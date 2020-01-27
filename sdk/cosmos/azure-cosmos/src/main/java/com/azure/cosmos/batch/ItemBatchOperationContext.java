// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.implementation.IDocumentClientRetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Context for a particular Batch operation.
 */
public class ItemBatchOperationContext implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(ItemBatchOperationContext.class);

    private BatchAsyncBatcher CurrentBatcher;
    private final String PartitionKeyRangeId;
    private final IDocumentClientRetryPolicy retryPolicy;

    public ItemBatchOperationContext(@Nonnull final String partitionKeyRangeId) {
        this(partitionKeyRangeId, null);
    }

    public ItemBatchOperationContext(
        @Nonnull final String partitionKeyRangeId, final IDocumentClientRetryPolicy retryPolicy) {

        checkNotNull(partitionKeyRangeId, "expected non-null partitionKeyRangeId");
        this.PartitionKeyRangeId = partitionKeyRangeId;
        this.retryPolicy = retryPolicy;
    }

    public final BatchAsyncBatcher getCurrentBatcher() {
        return CurrentBatcher;
    }

    public final void setCurrentBatcher(BatchAsyncBatcher value) {
        CurrentBatcher = value;
    }

    public final Task<TransactionalBatchOperationResult> getOperationTask() {
        return this.taskCompletionSource.Task;
    }

    public final String getPartitionKeyRangeId() {
        return PartitionKeyRangeId;
    }

    public final void Complete(BatchAsyncBatcher completer, TransactionalBatchOperationResult result) {
        if (this.AssertBatcher(completer)) {
            this.taskCompletionSource.SetResult(result);
        }

        this.close();
    }

    public final void Fail(BatchAsyncBatcher completer, RuntimeException exception) {
        if (this.AssertBatcher(completer, exception)) {
            this.taskCompletionSource.SetException(exception);
        }

        this.close();
    }

    /**
     * Based on the Retry Policy, if a failed response should retry.
     */
    public final Task<ShouldRetryResult> ShouldRetryAsync(TransactionalBatchOperationResult batchOperationResult,
                                                          CancellationToken cancellationToken) {
        if (this.retryPolicy == null || batchOperationResult.isSuccessStatusCode()) {
            return Task.FromResult(ShouldRetryResult.NoRetry());
        }

        ResponseMessage responseMessage = batchOperationResult.ToResponseMessage();
        return this.retryPolicy.ShouldRetryAsync(responseMessage, cancellationToken);
    }

    public final void close() throws IOException {
        this.setCurrentBatcher(null);
    }


    private boolean AssertBatcher(BatchAsyncBatcher completer) {
        return AssertBatcher(completer, null);
    }

    private boolean AssertBatcher(BatchAsyncBatcher completer, RuntimeException innerException) {
        if (completer != this.getCurrentBatcher()) {
            logger.error("Operation was completed by incorrect batcher");
            this.taskCompletionSource.SetException(new RuntimeException(String.format("Operation was completed by " +
                "incorrect batcher."), innerException));
            return false;
        }
        return true;
    }
}
