// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.io.RowWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning disable CA1001 // Types that own disposable fields should be disposable
public abstract class ServerBatchRequest
    //C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
    ///#pragma warning restore CA1001 // Types that own disposable fields should be disposable
{
    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.MemoryStream is
    // input or output:
    private MemoryStream bodyStream;
    private long bodyStreamPositionBeforeWritingCurrentRecord;
    private int lastWrittenOperationIndex;
    private final int maxBodyLength;
    private final int maxOperationCount;
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private MemorySpanResizer<byte> operationResizableWriteBuffer;
    private MemorySpanResizer<Byte> operationResizableWriteBuffer;
    private ArrayList<ItemBatchOperation> operations = new ArrayList<ItemBatchOperation>();
    private final CosmosSerializerCore serializerCore;
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
    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.MemoryStream is
    // input or output:
    public final MemoryStream TransferBodyStream() {
        //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.MemoryStream
        // is input or output:
        MemoryStream bodyStream = this.bodyStream;
        this.bodyStream = null;
        return bodyStream;
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

    protected final CompletableFuture<List<ItemBatchOperation>> CreateBodyStreamAsync(List<ItemBatchOperation> operations) {
        return CreateBodyStreamAsync(operations, false);
    }

    protected final CompletableFuture<List<ItemBatchOperation>> CreateBodyStreamAsync(
        final List<ItemBatchOperation> operations,
        final boolean ensureContinuousOperationIndexes) {

        int estimatedMaxOperationLength = 0;
        int approximateTotalLength = 0;

        int previousOperationIndex = -1;
        int materializedCount = 0;

        for (ItemBatchOperation operation : operations) {

            if (ensureContinuousOperationIndexes && previousOperationIndex != -1 && operation.getOperationIndex() != previousOperationIndex + 1) {
                break;
            }

            /*await*/ operation.materializeResource(this.serializerCore);
            materializedCount++;

            previousOperationIndex = operation.getOperationIndex();

            int currentLength = operation.GetApproximateSerializedLength();
            estimatedMaxOperationLength = Math.max(currentLength, estimatedMaxOperationLength);

            approximateTotalLength += currentLength;
            if (approximateTotalLength > this.maxBodyLength) {
                break;
            }

            if (materializedCount == this.maxOperationCount) {
                break;
            }
        }

        this.operations = new List<ItemBatchOperation>(operations.Array, operations.Offset, materializedCount);

        final int operationSerializationOverheadOverEstimateInBytes = 200;
        //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.MemoryStream
        // is input or output:
        this.bodyStream =
            new MemoryStream(approximateTotalLength + (operationSerializationOverheadOverEstimateInBytes * materializedCount));
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: this.operationResizableWriteBuffer = new MemorySpanResizer<byte>(estimatedMaxOperationLength
        // + operationSerializationOverheadOverEstimateInBytes);
        this.operationResizableWriteBuffer =
            new MemorySpanResizer<Byte>(estimatedMaxOperationLength + operationSerializationOverheadOverEstimateInBytes);

        Result r = /*await*/ this.bodyStream.WriteRecordIOAsync(null, this.WriteOperation);
        assert r == Result.SUCCESS : "Failed to serialize batch request";

        this.bodyStream.Position = 0;

        if (this.shouldDeleteLastWrittenRecord) {
            this.bodyStream.SetLength(this.bodyStreamPositionBeforeWritingCurrentRecord);
            this.operations = new ArraySegment<ItemBatchOperation>(operations.Array, operations.Offset,
                this.lastWrittenOperationIndex);
        } else {
            this.operations = new ArraySegment<ItemBatchOperation>(operations.Array, operations.Offset,
                this.lastWrittenOperationIndex + 1);
        }

        int overflowOperations = operations.Count - this.operations.Count;
        return new ArrayList<ItemBatchOperation>(operations.Array, this.operations.Count + operations.Offset,
            overflowOperations);
    }

    private Result WriteOperation(long index, tangible.OutObject<ReadOnlyMemory<Byte>> buffer) {

        if (this.bodyStream.Length > this.maxBodyLength) {
            // If there is only one operation within the request, we will keep it even if it exceeds the maximum size
            // allowed for the body.
            if (index > 1) {
                this.shouldDeleteLastWrittenRecord = true;
            }
            buffer.argValue = null;
            return Result.SUCCESS;
        }

        this.bodyStreamPositionBeforeWritingCurrentRecord = this.bodyStream.Length;

        if (index >= this.operations.Count) {
            buffer.argValue = null;
            return Result.SUCCESS;
        }

        ItemBatchOperation operation = this.operations.Array[this.operations.Offset + (int) index];

        RowBuffer rowBuffer = new RowBuffer(this.operationResizableWriteBuffer.Memory.Length, this.operationResizableWriteBuffer);
        rowBuffer.initLayout(HybridRowVersion.V1, BatchSchemaProvider.getBatchOperationLayout(), BatchSchemaProvider.getBatchLayoutResolverNamespace());
        Result result = RowWriter.writeBuffer(rowBuffer, operation, ItemBatchOperation.WriteOperation);

        if (result != Result.SUCCESS) {
            return result;
        }

        this.lastWrittenOperationIndex = (int) index;
        buffer.argValue = this.operationResizableWriteBuffer.Memory.Slice(0, rowBuffer.Length);
        return Result.SUCCESS;
    }
}
