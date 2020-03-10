// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.CosmosClientException;
import com.azure.cosmos.batch.BatchResponseMessage;
import com.azure.cosmos.implementation.DocumentClientRetryPolicy;
import com.azure.cosmos.implementation.HttpConstants.StatusCodes;
import com.azure.cosmos.implementation.HttpConstants.SubStatusCodes;
import com.azure.cosmos.implementation.RxDocumentServiceRequest;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.time.Duration;

public final class BatchPartitionKeyRangeGoneRetryPolicy extends DocumentClientRetryPolicy {

    private static final int MAX_RETRIES = 1;

    private final DocumentClientRetryPolicy nextRetryPolicy;
    private int attemptedRetries;

    public BatchPartitionKeyRangeGoneRetryPolicy(DocumentClientRetryPolicy nextRetryPolicy) {
        this.nextRetryPolicy = nextRetryPolicy;
    }

    @Override
    public Mono<ShouldRetryResult> shouldRetry(@Nullable final Exception exception) {

        if (exception instanceof CosmosClientException) {

            final CosmosClientException clientException = (CosmosClientException) exception;

            final ShouldRetryResult result = this.shouldRetryInternal(
                clientException.getStatusCode(),
                clientException.getSubStatusCode(),
                clientException.getResourceAddress());

            if (result != null) {
                return Mono.just(result);
            }
        }

        if (this.nextRetryPolicy == null) {
            return Mono.just(ShouldRetryResult.noRetry());
        }

        return this.nextRetryPolicy.shouldRetry(exception);
    }

    public Mono<ShouldRetryResult> shouldRetry(@Nullable final BatchResponseMessage message) {

        if (message != null) {

            final ShouldRetryResult result = this.shouldRetryInternal(
                message.getStatusCode(),
                message.getHeaders().getSubStatusCode(),
                message.getResourceAddress());

            if (result != null) {
                return Mono.just(result);
            }
        }

        // We know the answer for null batch response messages (no need to chain)
        return Mono.just(ShouldRetryResult.noRetry());
    }

    public void onBeforeSendRequest(RxDocumentServiceRequest request) {
        this.nextRetryPolicy.onBeforeSendRequest(request);
    }

    private ShouldRetryResult shouldRetryInternal(int statusCode, int subStatusCode, String resourceAddress) {

        if (statusCode == StatusCodes.GONE && (subStatusCode == SubStatusCodes.PARTITION_KEY_RANGE_GONE
            || subStatusCode == SubStatusCodes.NAME_CACHE_IS_STALE) && this.attemptedRetries < MAX_RETRIES) {
            this.attemptedRetries++;
            return ShouldRetryResult.retryAfter(Duration.ZERO);
        }

        return null;
    }
}
