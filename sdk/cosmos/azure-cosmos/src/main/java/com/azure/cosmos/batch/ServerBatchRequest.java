// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.ResultValue;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.cosmos.serialization.hybridrow.recordio.RecordIOStream;
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

    public final List<ItemBatchOperation<?>> getOperations() {
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
    protected final CompletableFuture<List<ItemBatchOperation<?>>> createBodyStreamAsync(
        @Nonnull final List<ItemBatchOperation<?>> operations) {
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

            future = future == null
                ? operation.materializeResource(this.serializerCore)
                : future.thenComposeAsync((Void r) -> operation.materializeResource(this.serializerCore));

            future = future.thenApplyAsync((Void r) -> {

                final int approximateSerializedLength = operation.GetApproximateSerializedLength();
                track.estimatedMaxOperationLength = max(approximateSerializedLength, track.estimatedMaxOperationLength);
                track.approximateTotalSerializedLength += approximateSerializedLength;
                track.materializedCount++;

                if (track.approximateTotalSerializedLength > this.maxBodyLength
                    || track.materializedCount == this.maxOperationCount) {
                    throw BatchOverflowException.INSTANCE;
                }

                return r;
            })
            ;
        }

        assert future != null : "expected non-null future";

        return future.thenComposeAsync((Void result) -> {

            track.approximateTotalSerializedLength += track.materializedCount * SERIALIZATION_OVERHEAD_ESTIMATE_IN_BYTES;
            this.operations = operations.subList(0, track.materializedCount);
            this.bodyStream = new ByteBufOutputStream(Unpooled.buffer(track.approximateTotalSerializedLength));

            return RecordIOStream.writeRecordIOAsync(this.bodyStream, null, this::writeOperation);

        }).thenApplyAsync((Result result) -> {

            if (this.shouldDeleteLastWrittenRecord) {
                this.bodyStream.buffer().writerIndex((int) this.bodyStreamPositionBeforeWritingCurrentRecord);
                this.operations = operations.subList(0, this.lastWrittenOperationIndex);
            } else {
                this.operations = operations.subList(0, this.lastWrittenOperationIndex + 1);
            }

            return operations.subList(track.materializedCount, operations.size());
        });
    }

    private ResultValue<ByteBuf> writeOperation(final long index) {

        checkArgument(0 <= index && index < Integer.MAX_VALUE, "expected 0 <= index && index <= %s, not %s",
            Integer.MAX_VALUE,
            index);

        int start = this.bodyStream.buffer().writerIndex();

        if (this.bodyStream.buffer().writerIndex() > this.maxBodyLength) {
            // If there is just one operation in the request, keep it even if it exceeds the maximum size allowed
            if (index > 1) {
                this.shouldDeleteLastWrittenRecord = true;
            }
            return new ResultValue<>(Result.SUCCESS, null);
        }

        this.bodyStreamPositionBeforeWritingCurrentRecord = start;

        if (index >= this.operations.size()) {
            return new ResultValue<>(Result.SUCCESS, null);
        }

        ItemBatchOperation<?> operation = this.operations.get((int) index);

        RowBuffer rowBuffer = new RowBuffer(1024).initLayout(
            HybridRowVersion.V1,
            BatchSchemaProvider.getBatchOperationLayout(),
            BatchSchemaProvider.getBatchLayoutResolverNamespace());

        Result result = RowWriter.writeBuffer(rowBuffer, operation, ItemBatchOperation::writeOperation);

        if (result != Result.SUCCESS) {
            return new ResultValue<>(Result.SUCCESS, null);
        }

        this.lastWrittenOperationIndex = (int) index;
        return new ResultValue<>(Result.SUCCESS, rowBuffer.buffer());
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
