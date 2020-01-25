// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import java.util.concurrent.CompletableFuture;

/**
 * Delegate to process a request for retry an operation
 */
@FunctionalInterface
public interface BatchAsyncBatcherRetryDelegate {
    CompletableFuture<Void> invoke(ItemBatchOperation operation);
}
