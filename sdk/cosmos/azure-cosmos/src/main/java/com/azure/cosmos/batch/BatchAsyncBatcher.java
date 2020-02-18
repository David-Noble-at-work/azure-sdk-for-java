// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.batch.serializer.CosmosSerializerCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import static com.azure.cosmos.batch.PartitionKeyRangeServerBatchRequest.createAsync;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Maintains a batch of operations and dispatches it as a unit of work.
 * <p>
 * The dispatch process involves:
 * <ol>
 * <li>Creating a {@link PartitionKeyRangeServerBatchRequest}.
 * <li>Verifying overflow that might happen due to HybridRow serialization. Any operations that did not fit, get sent
 * to the {@link BatchAsyncBatcherRetrier}.
 * <li>Delegating to {@link BatchAsyncBatcherExecutor} to execute a request.
 * <li>Delegating to {@link BatchAsyncBatcherRetrier} to retry a request, if a split is detected. In this case
 * all operations in the request are sent to the {@link BatchAsyncBatcherRetrier} for re-queueing.
 * </ol>
 * The result of the request is used to wire up all responses with the original tasks for each operation.
 * @see ItemBatchOperation
 */
public class BatchAsyncBatcher {

    private static final Logger logger = LoggerFactory.getLogger(BatchAsyncBatcher.class);

    private final Semaphore interlockIncrementCheck = new Semaphore(1);
    private final ArrayList<ItemBatchOperation> batchOperations;
    private long currentSize = 0;
    private boolean dispatched = false;
    private final BatchAsyncBatcherExecutor executor;
    private final int maxBatchByteSize;
    private final int maxBatchOperationCount;
    private final BatchAsyncBatcherRetrier retrier;
    private final CosmosSerializerCore serializerCore;

    public BatchAsyncBatcher(
        final int maxBatchOperationCount,
        final int maxBatchByteSize,
        @Nonnull final CosmosSerializerCore serializerCore,
        @Nonnull final BatchAsyncBatcherExecutor executor,
        @Nonnull final BatchAsyncBatcherRetrier retrier) {

        checkArgument(maxBatchOperationCount > 0,
            "expected maxBatchOperationCount > 0, not %s",
            maxBatchOperationCount);

        checkArgument(maxBatchByteSize > 0,
            "expected maxBatchByteSize > 0, not %s",
            maxBatchByteSize);

        checkNotNull(serializerCore, "expected non-null serializerCore");
        checkNotNull(executor, "expected non-null executor");
        checkNotNull(retrier, "expected non-null retrier");

        this.batchOperations = new ArrayList<>(maxBatchOperationCount);
        this.maxBatchOperationCount = maxBatchOperationCount;
        this.maxBatchByteSize = maxBatchByteSize;
        this.serializerCore = serializerCore;
        this.executor = executor;
        this.retrier = retrier;
    }

    public final boolean isEmpty() {
        return this.batchOperations.isEmpty();
    }

    public CompletableFuture<ServerOperationBatchRequest> createServerRequestAsync() {

        // All operations must be for the same partition key range

        final String partitionKeyRangeId = this.batchOperations.get(0).getContext().getPartitionKeyRangeId();

        final List<ItemBatchOperation<?>> itemBatchOperations = Collections.unmodifiableList(
            new ArrayList<>(this.batchOperations)
        );

        return createAsync(
            partitionKeyRangeId,
            itemBatchOperations,
            this.maxBatchByteSize,
            this.maxBatchOperationCount,
            false,
            this.serializerCore);
    }

    public CompletableFuture<Void> dispatchAsync() {

        checkState(interlockIncrementCheck.tryAcquire(), "failed to acquire dispatch permit");
        final CompletableFuture<Void> dispatchFuture = new CompletableFuture<>();

        this.createServerRequestAsync().whenComplete((batchRequest, creationError) -> {

            if (creationError != null) {
                dispatchFuture.completeExceptionally(creationError);
                return;
            }

            final List<ItemBatchOperation<?>> batchOperations = batchRequest.getBatchOperations();
            final CompletableFuture<?>[] futures = new CompletableFuture[batchOperations.size()];
            int i = -1;

            final AtomicReference<RuntimeException> aggregateExceptionReference = new AtomicReference<>();

            for (ItemBatchOperation<?> batchOperation : batchOperations) {
                futures[++i] = this.retrier.apply(batchOperation).whenComplete((r, e) -> {
                    if (e != null) {
                        aggregateExceptionReference.accumulateAndGet(null, (current, x) ->
                            current == null
                                ? new RuntimeException()
                                : null
                        ).addSuppressed(e);
                    }
                });
            }

            CompletableFuture.allOf(futures);
            final RuntimeException aggregateException = aggregateExceptionReference.get();

            if (aggregateException != null) {

                for (ItemBatchOperation<?> batchOperation : batchOperations) {
                    batchOperation.getContext().Fail(this, aggregateException);
                }

                dispatchFuture.completeExceptionally(aggregateException);
            }

        }).thenApply((ServerOperationBatchRequest request) ->

            this.executor.apply(request.getBatchRequest()).whenComplete((executionResult, executionError) -> {

                if (executionError != null) {
                    dispatchFuture.completeExceptionally(executionError);
                    return;
                }

                final TransactionalBatchResponse batchResponse = executionResult.getServerResponse();
                final int operationCount = request.getBatchOperations().size();
                final CompletableFuture<?>[] futures = new CompletableFuture<?>[operationCount];

                Arrays.setAll(futures, future -> new CompletableFuture<Void>());

                CompletableFuture.allOf(futures).whenComplete((voidResult, error) -> {
                    if (error == null) {
                        dispatchFuture.complete(null);
                    } else {
                        for (ItemBatchOperation<?> itemBatchOperation : request.getBatchOperations()) {
                            itemBatchOperation.getContext().Fail(this, error);
                        }
                        dispatchFuture.completeExceptionally(error);
                    }
                });

                try (PartitionKeyRangeBatchResponse response = new PartitionKeyRangeBatchResponse(
                    operationCount, batchResponse, this.serializerCore)) {

                    // TODO (DANOBLE) Is it OK to auto-close a PartitionKeyRangeBatchResponse before all responses are
                    //  complete or should we close at some point in the future? What should happen when
                    //  PartitionKeyRangeBatchResponse.close throws?

                    int i = -1;

                    for (ItemBatchOperation<?> operation : response.getBatchOperations()) {

                        final CompletableFuture<?> future = futures[++i];
                        final int operationIndex = operation.getOperationIndex();
                        final TransactionalBatchOperationResult<?> result = response.get(operationIndex);

                        // TODO (DANOBLE) wire up diagnostics
                        // Bulk has diagnostics per item operation.
                        // Batch has a single diagnostics for the execute operation
//
//                        if (operation.getDiagnosticsContext() != null) {
//                            result.setDiagnosticsContext(operation.getDiagnosticsContext());
//                            result.getDiagnosticsContext().Append(result.getDiagnosticsContext());
//                        } else {
//                            result.setDiagnosticsContext(result.getDiagnosticsContext());
//                        }

                        final ItemBatchOperationContext context = operation.getContext();

                        if (result.isSuccessStatusCode()) {

                            context.Complete(this, result);
                            future.complete(null);

                        } else {

                            context.ShouldRetryAsync(result).whenComplete((shouldRetry, error) -> {
                                if (error != null) {
                                    future.completeExceptionally(error);
                                }
                            }).thenApply(shouldRetry -> {
                                if (shouldRetry.shouldRetry) {
                                    this.retrier.apply(operation).whenComplete((voidResult, error) -> {
                                        if (error == null) {
                                            context.Complete(this, result);
                                            future.complete(null);
                                        } else {
                                            future.completeExceptionally(error);
                                        }
                                    });
                                }
                            });
                        }
                    }

                } catch (final Exception error) {
                }
            }));

        return dispatchFuture.whenComplete((Void dispatch, Throwable error) -> {
            if (error != null) {
                logger.error("dispatch failed due to ", error);
            }
            this.batchOperations.clear();
            this.dispatched = true;
        });
    }

    public boolean tryAdd(ItemBatchOperation operation) {

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

        operation.setOperationIndex(this.batchOperations.size()).getContext().setCurrentBatcher(this);
        this.batchOperations.add(operation);

        return true;
    }
}
