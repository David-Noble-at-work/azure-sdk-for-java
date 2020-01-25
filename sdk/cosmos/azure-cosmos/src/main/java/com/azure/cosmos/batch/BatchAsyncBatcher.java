// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Maintains a batch of operations and dispatches it as a unit of work.
 * <p>
 * The dispatch process involves:
 * <ol>
 * <li>Creating a {@link PartitionKeyRangeServerBatchRequest}.
 * <li>Verifying overflow that might happen due to HybridRow serialization. Any operations that did not fit, get sent
 * to the {@link BatchAsyncBatcherRetryDelegate}.
 * <li>Delegating to {@link BatchAsyncBatcherExecuteDelegate} to execute a request.
 * <li>Delegating to {@link BatchAsyncBatcherRetryDelegate} to retry a request, if a split is detected. In this case
 * all operations in the request are sent to the {@link BatchAsyncBatcherRetryDelegate} for re-queueing.
 * </ol>
 * The result of the request is used to wire up all responses with the original tasks for each operation.
 * @see ItemBatchOperation
 */
public class BatchAsyncBatcher {

    private static final Logger logger = LoggerFactory.getLogger(BatchAsyncBatcher.class);

    private final InterlockIncrementCheck interlockIncrementCheck = new InterlockIncrementCheck();
    private final ArrayList<ItemBatchOperation> batchOperations;
    private long currentSize = 0;
    private boolean dispatched = false;
    private final BatchAsyncBatcherExecuteDelegate executor;
    private final int maxBatchByteSize;
    private final int maxBatchOperationCount;
    private final BatchAsyncBatcherRetryDelegate retrier;
    private final CosmosSerializerCore serializerCore;

    public BatchAsyncBatcher(
        final int maxBatchOperationCount,
        final int maxBatchByteSize,
        @Nonnull final CosmosSerializerCore serializerCore,
        @Nonnull final BatchAsyncBatcherExecuteDelegate executor,
        @Nonnull final BatchAsyncBatcherRetryDelegate retrier) {

        checkArgument(maxBatchOperationCount > 0,
            "expected maxBatchOperationCount > 0, not %s",
            maxBatchOperationCount);

        checkArgument(maxBatchByteSize > 0,
            "expected maxBatchByteSize > 0, not %s",
            maxBatchByteSize);

        checkNotNull(executor, "expected non-null executor");
        checkNotNull(retrier, "expected non-null retrier");
        checkNotNull(serializerCore, "expected non-null serializerCore");

        this.executor = (PartitionKeyRangeServerBatchRequest request) -> executor.invoke(request);
        this.retrier = (ItemBatchOperation operation) -> retrier.invoke(operation);
        this.batchOperations = new ArrayList<>(maxBatchOperationCount);
        this.maxBatchOperationCount = maxBatchOperationCount;
        this.maxBatchByteSize = maxBatchByteSize;
        this.serializerCore = serializerCore;
    }

    public final boolean isEmpty() {
        return this.batchOperations.isEmpty();
    }

    //C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: internal virtual async Task<Tuple<PartitionKeyRangeServerBatchRequest,
    // ArraySegment<ItemBatchOperation>>> CreateServerRequestAsync(CancellationToken cancellationToken)
    public Task<Tuple<PartitionKeyRangeServerBatchRequest, List<ItemBatchOperation>>> CreateServerRequestAsync() {

        // All operations must be for the same partition key range

        @SuppressWarnings("unchecked") final List<ItemBatchOperation> operationsArraySegment = Collections.unmodifiableList(new ArrayList<>(this.batchOperations));
        final String partitionKeyRangeId = this.batchOperations.get(0).getContext().getPartitionKeyRangeId();

        //C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:

        PartitionKeyRangeServerBatchRequest.CreateAsync(
            partitionKeyRangeId,
            operationsArraySegment,
            this.maxBatchByteSize,
            this.maxBatchOperationCount,
            false,
            this.serializerCore);
    }

    public CompletableFuture<Void> DispatchAsync() {

        this.interlockIncrementCheck.EnterLockCheck();

        ArrayList<ItemBatchOperation> pendingOperations = new ArrayList<>();
        PartitionKeyRangeServerBatchRequest serverRequest = null;

        try {
            try {
                // HybridRow serialization might leave some pending operations out of the batch
                //C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
                Tuple<PartitionKeyRangeServerBatchRequest, List<ItemBatchOperation>> createRequestResponse =
                    /*await*/
                this.CreateServerRequestAsync();
                serverRequest = createRequestResponse.Item1;
                pendingOperations = createRequestResponse.Item2;
                // Any overflow goes to a new batch
                for (ItemBatchOperation operation : pendingOperations) {
                    //C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
                    /*await*/ this.retrier.invoke(operation);
                }
            } catch (RuntimeException ex) {
                // Exceptions happening during request creation, fail the entire list
                for (ItemBatchOperation itemBatchOperation : this.batchOperations) {
                    itemBatchOperation.getContext().Fail(this, ex);
                }

                throw ex;
            }

            try {
                PartitionKeyRangeBatchExecutionResult result = /*await*/
                this.executor.invoke(serverRequest);
                try (PartitionKeyRangeBatchResponse batchResponse =
                         new PartitionKeyRangeBatchResponse(serverRequest.getOperations().Count,
                             result.getServerResponse(), this.serializerCore)) {
                    for (ItemBatchOperation itemBatchOperation : batchResponse.getOperations()) {
                        TransactionalBatchOperationResult response =
                            batchResponse.get(itemBatchOperation.getOperationIndex());

                        // Bulk has diagnostics per a item operation.
                        // Batch has a single diagnostics for the execute operation
                        if (itemBatchOperation.getDiagnosticsContext() != null) {
                            response.setDiagnosticsContext(itemBatchOperation.getDiagnosticsContext());
                            response.getDiagnosticsContext().Append(batchResponse.getDiagnosticsContext());
                        } else {
                            response.setDiagnosticsContext(batchResponse.getDiagnosticsContext());
                        }

                        if (!response.getIsSuccessStatusCode()) {
                            //C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
                            Documents.ShouldRetryResult shouldRetry = /*await*/
                            itemBatchOperation.getContext().ShouldRetryAsync(response);
                            if (shouldRetry.ShouldRetry) {
                                //C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
                                /*await*/ this.retrier.invoke(itemBatchOperation);
                                continue;
                            }
                        }

                        itemBatchOperation.getContext().Complete(this, response);
                    }
                }
            } catch (RuntimeException error) {
                // Exceptions happening during execution fail all the Tasks part of the request (excluding overflow)
                for (ItemBatchOperation itemBatchOperation : serverRequest.getOperations()) {
                    itemBatchOperation.getContext().Fail(this, error);
                }

                throw error;
            }

        } catch (RuntimeException error) {
            logger.error("Exception during BatchAsyncBatcher: ", error);
        } finally {
            this.batchOperations.clear();
            this.dispatched = true;
        }
    }

    public boolean TryAdd(ItemBatchOperation operation) {

        checkNotNull(operation, "expected non-null operation");
        checkNotNull(operation.getContext(), "expected non-null operation context");

        if (this.dispatched) {
            logger.error("Add operation attempted on dispatched batch.");
            return false;
        }

        if (this.batchOperations.size() == this.maxBatchOperationCount) {
            logger.info("Batch is full - Max operation count {} reached.", this.maxBatchOperationCount);
            return false;
        }

        int itemByteSize = operation.GetApproximateSerializedLength();

        if (!this.batchOperations.isEmpty() && itemByteSize + this.currentSize > this.maxBatchByteSize) {
            logger.info("Batch is full - Max byte size {} reached.", this.maxBatchByteSize);
            return false;
        }

        this.currentSize += itemByteSize;

        // Operation index is in the scope of the current batch
        operation.setOperationIndex(this.batchOperations.size());
        operation.getContext().setCurrentBatcher(this);
        this.batchOperations.add(operation);

        return true;
    }
}
