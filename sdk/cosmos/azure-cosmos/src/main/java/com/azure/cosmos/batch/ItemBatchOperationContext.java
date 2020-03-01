// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.batch.unimplemented.ResponseMessage;
import com.azure.cosmos.implementation.DocumentClientRetryPolicy;
import com.azure.cosmos.implementation.IRetryPolicy.ShouldRetryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Context for a particular Batch operation.
 */
public class ItemBatchOperationContext implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(ItemBatchOperationContext.class);

    private final CompletableFuture<TransactionalBatchOperationResult<?>> operationResultFuture;
    private BatchAsyncBatcher currentBatcher;
    private final String partitionKeyRangeId;
    private final DocumentClientRetryPolicy retryPolicy;

    public ItemBatchOperationContext(@Nonnull final String partitionKeyRangeId) {
        this(partitionKeyRangeId, null);
    }

    public ItemBatchOperationContext(
        @Nonnull final String partitionKeyRangeId, @Nullable final DocumentClientRetryPolicy retryPolicy) {

        checkNotNull(partitionKeyRangeId, "expected non-null partitionKeyRangeId");
        this.operationResultFuture = new CompletableFuture<>();
        this.partitionKeyRangeId = partitionKeyRangeId;
        this.retryPolicy = retryPolicy;
    }

    public final BatchAsyncBatcher getCurrentBatcher() {
        return currentBatcher;
    }

    public final void setCurrentBatcher(BatchAsyncBatcher value) {
        currentBatcher = value;
    }

    public final CompletableFuture<TransactionalBatchOperationResult<?>> getOperationResultFuture() {
        return this.operationResultFuture;
    }

    public final String getPartitionKeyRangeId() {
        return partitionKeyRangeId;
    }

    public final void complete(BatchAsyncBatcher completer, TransactionalBatchOperationResult<?> result) {
        if (this.assertBatcher(completer)) {
            this.operationResultFuture.complete(result);
        }
        this.close();
    }

    public final void fail(BatchAsyncBatcher completer, Throwable error) {
        if (this.assertBatcher(completer, error)) {
            this.operationResultFuture.completeExceptionally(error);
        }
        this.close();
    }

    /**
     * Based on the Retry Policy, if a failed response should retry.
     *
     * @param result result of batch operation.
     *
     * @return indicates whether a retry should be attempted.
     */
    public final Mono<ShouldRetryResult> shouldRetry(@Nonnull final TransactionalBatchOperationResult<?> result) {

        checkNotNull(result, "expected non-null result");

        if (this.retryPolicy == null || result.isSuccessStatusCode()) {
            return Mono.just(ShouldRetryResult.noRetry());
        }

        ResponseMessage responseMessage = result.toResponseMessage();
        return this.retryPolicy.shouldRetry(responseMessage);
    }

    public final void close() {
        this.operationResultFuture.cancel(true);
        this.setCurrentBatcher(null);
    }

    // region Privates

    private boolean assertBatcher(BatchAsyncBatcher completer) {
        return assertBatcher(completer, null);
    }

    private boolean assertBatcher(BatchAsyncBatcher completer, Throwable error) {
        if (completer != this.getCurrentBatcher()) {
            final String message = "operation was completed by incorrect batcher";
            logger.error(message);
            this.operationResultFuture.completeExceptionally(
                new RuntimeException(message, error));
            return false;
        }
        return true;
    }

    // endregion
}
