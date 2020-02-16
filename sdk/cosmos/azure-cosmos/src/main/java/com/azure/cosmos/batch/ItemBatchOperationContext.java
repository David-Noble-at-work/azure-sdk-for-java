// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.implementation.DocumentClientRetryPolicy;
import com.azure.cosmos.implementation.IRetryPolicy.ShouldRetryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Context for a particular Batch operation.
 */
public class ItemBatchOperationContext implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(ItemBatchOperationContext.class);

    private BatchAsyncBatcher currentBatcher;
    private final String partitionKeyRangeId;
    private final DocumentClientRetryPolicy retryPolicy;

    public ItemBatchOperationContext(@Nonnull final String partitionKeyRangeId) {
        this(partitionKeyRangeId, null);
    }

    public ItemBatchOperationContext(
        @Nonnull final String partitionKeyRangeId, @Nullable final DocumentClientRetryPolicy retryPolicy) {

        checkNotNull(partitionKeyRangeId, "expected non-null partitionKeyRangeId");
        this.partitionKeyRangeId = partitionKeyRangeId;
        this.retryPolicy = retryPolicy;
    }

    public final BatchAsyncBatcher getCurrentBatcher() {
        return currentBatcher;
    }

    public final void setCurrentBatcher(BatchAsyncBatcher value) {
        currentBatcher = value;
    }

    public final CompletableFuture<TransactionalBatchOperationResult> getOperationTask() {
        return this.taskCompletionSource.Task;
    }

    public final String getPartitionKeyRangeId() {
        return partitionKeyRangeId;
    }

    public final void Complete(BatchAsyncBatcher completer, TransactionalBatchOperationResult result) {
        if (this.AssertBatcher(completer)) {
            this.taskCompletionSource.SetResult(result);
        }
        this.close();
    }

    public final void Fail(BatchAsyncBatcher completer, Throwable error) {
        if (this.AssertBatcher(completer, error)) {
            this.taskCompletionSource.SetException(error);
        }
        this.close();
    }

    /**
     * Based on the Retry Policy, if a failed response should retry.
     *
     * @param result result of batch operation.
     */
    public final CompletableFuture<ShouldRetryResult> ShouldRetryAsync(
        @Nonnull final TransactionalBatchOperationResult<?> result) {

        checkNotNull(result, "expected non-null result");

        if (this.retryPolicy == null || result.isSuccessStatusCode()) {
            return CompletableFuture.completedFuture(ShouldRetryResult.noRetry());
        }

        ResponseMessage responseMessage = result.ToResponseMessage();
        return this.retryPolicy.ShouldRetryAsync(responseMessage);
    }

    public final void close() {
        this.setCurrentBatcher(null);
    }

    private boolean AssertBatcher(BatchAsyncBatcher completer) {
        return AssertBatcher(completer, null);
    }

    private boolean AssertBatcher(BatchAsyncBatcher completer, Throwable error) {
        if (completer != this.getCurrentBatcher()) {
            logger.error("Operation was completed by incorrect batcher");
            this.taskCompletionSource.SetException(
                new RuntimeException("Operation was completed by incorrect batcher."),
                error);
            return false;
        }
        return true;
    }
}
