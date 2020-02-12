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
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ServerBatchRequest {

    static final int OPERATION_SERIALIZATION_OVERHEAD_OVER_ESTIMATE_IN_BYTES = 200;

    private final int maxBodyLength;
    private final int maxOperationCount;

    private final CosmosSerializerCore serializerCore;
    private MemoryStream bodyStream;
    private long bodyStreamPositionBeforeWritingCurrentRecord;
    private int lastWrittenOperationIndex;
    private MemorySpanResizer<Byte> operationResizableWriteBuffer;
    private List<ItemBatchOperation> operations;
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
    public final InputStream TransferBodyStream() {
        MemoryStream bodyStream = this.bodyStream;
        this.bodyStream = null;
        return bodyStream;
    }


    /**
     * Adds as many operations as possible from the provided list of operations in the list order while having the body
     * stream not exceed maxBodySize.
     *
     * @param operations Operations to be added; read-only.
     *
     * @return Any pending operations that were not included in the request.
     */
    protected final CompletableFuture<List<ItemBatchOperation>> CreateBodyStreamAsync(
        @Nonnull final List<ItemBatchOperation> operations) {
        return CreateBodyStreamAsync(operations, false);
    }

    /**
     * Adds as many operations as possible from the provided list of operations in the list order while having the body
     * stream not exceed maxBodySize.
     *
     * @param operations Operations to be added; read-only.
     * @param ensureContinuousOperationIndexes Whether to stop adding operations to the request once there is
     * non-continuity in the operation indexes.
     *
     * @return Any pending operations that were not included in the request.
     */
    protected final CompletableFuture<List<ItemBatchOperation>> CreateBodyStreamAsync(
        final List<ItemBatchOperation> operations,
        final boolean ensureContinuousOperationIndexes) {

        int estimatedMaxOperationLength = 0;
        int approximateTotalSerializedLength = 0;
        int materializedCount = 0;

        int previousOperationIndex = -1;

        for (ItemBatchOperation operation : operations) {

            if (ensureContinuousOperationIndexes) {
                final int operationIndex = operation.getOperationIndex();
                if (previousOperationIndex != -1 && operationIndex != previousOperationIndex + 1) {
                    break;
                }
                previousOperationIndex = operationIndex;
            }

            /*await*/ operation.materializeResource(this.serializerCore);

            final int approximateSerializedLength = operation.GetApproximateSerializedLength();

            estimatedMaxOperationLength = Math.max(approximateSerializedLength, estimatedMaxOperationLength);
            approximateTotalSerializedLength += approximateSerializedLength;
            materializedCount++;

            if (approximateTotalSerializedLength > this.maxBodyLength) {
                break;
            }

            if (materializedCount == this.maxOperationCount) {
                break;
            }
        }

        approximateTotalSerializedLength += materializedCount * OPERATION_SERIALIZATION_OVERHEAD_OVER_ESTIMATE_IN_BYTES;
        ByteBuf buffer = Unpooled.buffer(approximateTotalSerializedLength);
        this.operations = operations.subList(0, materializedCount);

        ////

        this.bodyStream = new MemoryStream(
            approximateTotalSerializedLength + (OPERATION_SERIALIZATION_OVERHEAD_OVER_ESTIMATE_IN_BYTES * materializedCount));

        this.operationResizableWriteBuffer = new MemorySpanResizer<Byte>(
            estimatedMaxOperationLength + OPERATION_SERIALIZATION_OVERHEAD_OVER_ESTIMATE_IN_BYTES);

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

    private Result WriteOperation(long index, Out<ReadOnlyMemory<Byte>> buffer) {

        if (this.bodyStream.Length > this.maxBodyLength) {
            // If there is only one operation within the request, we will keep it even if it exceeds the maximum size
            // allowed for the body.
            if (index > 1) {
                this.shouldDeleteLastWrittenRecord = true;
            }
            buffer.set(null);
            return Result.SUCCESS;
        }

        this.bodyStreamPositionBeforeWritingCurrentRecord = this.bodyStream.Length;

        if (index >= this.operations.Count) {
            buffer.set(null);
            return Result.SUCCESS;
        }

        ItemBatchOperation operation = this.operations.Array[this.operations.Offset + (int) index];

        RowBuffer rowBuffer = new RowBuffer(this.operationResizableWriteBuffer.Memory.Length, this.operationResizableWriteBuffer);
        rowBuffer.initLayout(HybridRowVersion.V1, BatchSchemaProvider.getBatchOperationLayout(), BatchSchemaProvider.getBatchLayoutResolverNamespace());
        Result result = RowWriter.writeBuffer(rowBuffer, operation, ItemBatchOperation.WriteOperation);

        if (result != Result.SUCCESS) {
            buffer.set(null);
            return result;
        }

        this.lastWrittenOperationIndex = (int) index;
        buffer.set(this.operationResizableWriteBuffer.Memory.Slice(0, rowBuffer.Length));

        return Result.SUCCESS;
    }
}
