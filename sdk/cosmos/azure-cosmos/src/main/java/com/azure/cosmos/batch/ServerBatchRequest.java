// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.cosmos.serializer.CosmosSerializerCore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.azure.cosmos.base.Preconditions.checkArgument;
import static com.azure.cosmos.base.Preconditions.checkNotNull;
import static java.lang.Math.max;

public abstract class ServerBatchRequest {

    private static final int SERIALIZATION_OVERHEAD_ESTIMATE_IN_BYTES = 200;  // an over estimate

    private final int maxBodyLength;
    private final int maxOperationCount;
    private final CosmosSerializerCore serializerCore;
    private ByteBufOutputStream bodyStream;
    private long bodyStreamPositionBeforeWritingCurrentRecord;
    private int lastWrittenOperationIndex;
    private List<ItemBatchOperation<?>> operations;
    private boolean shouldDeleteLastWrittenRecord;

    /**
     * Initializes a new instance of the {@link ServerBatchRequest} class.
     *
     * @param maxBodyLength Maximum length allowed for the request body.
     * @param maxOperationCount Maximum number of operations allowed in the request.
     * @param serializerCore Serializer to serialize user provided objects to JSON.
     */
    protected ServerBatchRequest(int maxBodyLength, int maxOperationCount, CosmosSerializerCore serializerCore) {
        this.maxBodyLength = maxBodyLength;
        this.maxOperationCount = maxOperationCount;
        this.serializerCore = serializerCore;
    }

    public final List<ItemBatchOperation> getOperations() {
        return this.operations;
    }

    /**
     * Returns the body Stream. Caller is responsible for disposing it after use.
     *
     * @return Body stream.
     */
    public final InputStream transferBodyStream() {
        ByteBufInputStream inputStream = new ByteBufInputStream(this.bodyStream.buffer(), true);
        this.bodyStream = null;
        return inputStream;
    }

    /**
     * Adds as many operations as possible from the provided list of operations in the list order while having the body
     * stream not exceed maxBodySize.
     *
     * @param operations Operations to be added; read-only.
     *
     * @return Any pending operations that were not included in the request.
     */
    protected final CompletableFuture<List<ItemBatchOperation>> createBodyStreamAsync(
        @Nonnull final List<ItemBatchOperation> operations) {
        return createBodyStreamAsync(operations, false);
    }

    /**
     * Adds as many operations as possible from the provided list of operations in the list order while having the body
     * stream not exceed maxBodySize.
     *
     * @param operations operations to be added; read-only.
     * @param ensureContinuousOperationIndexes specifies whether to stop adding operations to the request once there is
     * non-continuity in the operation indexes.
     *
     * @return Any pending operations that were not included in the request.
     */
    protected final CompletableFuture<List<ItemBatchOperation<?>>> createBodyStreamAsync(
        @Nonnull final List<ItemBatchOperation<?>> operations,
        final boolean ensureContinuousOperationIndexes) {

        checkNotNull(operations, "expected non-null operations");

        CompletableFuture<Void> future = null;
        final Track track = new Track();

        int previousOperationIndex = -1;

        for (ItemBatchOperation<?> operation : operations) {

            if (ensureContinuousOperationIndexes) {
                final int operationIndex = operation.getOperationIndex();
                if (previousOperationIndex != -1 && operationIndex != previousOperationIndex + 1) {
                    break;
                }
                previousOperationIndex = operationIndex;
            }

            if (future == null) {
                future = operation.materializeResource(this.serializerCore);
            } else {
                future.thenComposeAsync((Void result) -> operation.materializeResource(this.serializerCore));
            }

            future.thenApplyAsync((Void result) -> {

                final int approximateSerializedLength = operation.GetApproximateSerializedLength();
                track.estimatedMaxOperationLength = max(approximateSerializedLength, track.estimatedMaxOperationLength);
                track.approximateTotalSerializedLength += approximateSerializedLength;
                track.materializedCount++;

                if (track.approximateTotalSerializedLength > this.maxBodyLength
                    || track.materializedCount == this.maxOperationCount) {
                    throw BatchOverflowException.INSTANCE;
                }

                return result;
            })
            ;
        }

        future.thenApplyAsync((Void result) -> {

            track.approximateTotalSerializedLength += track.materializedCount * SERIALIZATION_OVERHEAD_ESTIMATE_IN_BYTES;
            this.operations = operations.subList(0, track.materializedCount);
            this.bodyStream = new ByteBufOutputStream(Unpooled.buffer(track.approximateTotalSerializedLength));
            return result;

        }).thenComposeAsync((Void result) -> {

            Result r = /*await*/ this.bodyStream.WriteRecordIOAsync(null, this::WriteOperation);
            assert r == Result.SUCCESS : "Failed to serialize batch request";

            this.bodyStream.Position = 0;

            if (this.shouldDeleteLastWrittenRecord) {
                this.bodyStream.SetLength(this.bodyStreamPositionBeforeWritingCurrentRecord);
                this.operations = new ArraySegment<ItemBatchOperation>(
                    operations.Array, operations.Offset, this.lastWrittenOperationIndex);
            } else {
                this.operations = new ArraySegment<ItemBatchOperation>(
                    operations.Array, operations.Offset, this.lastWrittenOperationIndex + 1);
            }

            ////

            return operations.subList(materializedCount, operations.size());
        });

        ////

        this.bodyStream = new MemoryStream(
            approximateTotalSerializedLength + (SERIALIZATION_OVERHEAD_ESTIMATE_IN_BYTES * materializedCount));

        this.operationResizableWriteBuffer = new MemorySpanResizer<Byte>(
            estimatedMaxOperationLength + SERIALIZATION_OVERHEAD_ESTIMATE_IN_BYTES);

        Result r = /*await*/ this.bodyStream.WriteRecordIOAsync(null, this::WriteOperation);
        assert r == Result.SUCCESS : "Failed to serialize batch request";

        this.bodyStream.Position = 0;

        if (this.shouldDeleteLastWrittenRecord) {
            this.bodyStream.SetLength(this.bodyStreamPositionBeforeWritingCurrentRecord);
            this.operations = new ArraySegment<ItemBatchOperation>(
                operations.Array, operations.Offset, this.lastWrittenOperationIndex);
        } else {
            this.operations = new ArraySegment<ItemBatchOperation>(
                operations.Array, operations.Offset, this.lastWrittenOperationIndex + 1);
        }

        ////

        return operations.subList(materializedCount, operations.size());
    }

    private Result WriteOperation(final long index, @Nonnull final Out<ByteBuf> buffer) {

        checkArgument(0 <= index && index < Integer.MAX_VALUE, "expected 0 <= index && index <= %s, not %s",
            Integer.MAX_VALUE,
            index);

        int start = this.bodyStream.buffer().writerIndex();

        if (this.bodyStream.buffer().writerIndex() > this.maxBodyLength) {
            // If there is just one operation in the request, keep it even if it exceeds the maximum size allowed
            if (index > 1) {
                this.shouldDeleteLastWrittenRecord = true;
            }
            buffer.set(null);
            return Result.SUCCESS;
        }

        this.bodyStreamPositionBeforeWritingCurrentRecord = start;

        if (index >= this.operations.size()) {
            buffer.set(null);
            return Result.SUCCESS;
        }

        ItemBatchOperation operation = this.operations.get((int) index);

        RowBuffer rowBuffer = new RowBuffer(1024).initLayout(
            HybridRowVersion.V1,
            BatchSchemaProvider.getBatchOperationLayout(),
            BatchSchemaProvider.getBatchLayoutResolverNamespace());

        Result result = RowWriter.writeBuffer(rowBuffer, operation, ItemBatchOperation::writeOperation);

        if (result != Result.SUCCESS) {
            buffer.set(null);
            return result;
        }

        this.lastWrittenOperationIndex = (int) index;
        buffer.set(rowBuffer.buffer());

        return Result.SUCCESS;
    }

    private static final class BatchOverflowException extends RuntimeException {

        static BatchOverflowException INSTANCE = new BatchOverflowException();

        private BatchOverflowException() {
            super(null, null, false, false);
        }
    }

    private static final class Track {
        int approximateTotalSerializedLength = 0;
        int estimatedMaxOperationLength = 0;
        int materializedCount = 0;
    }
}
