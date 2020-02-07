// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Delegate to process a request for retry an operation
 */
@FunctionalInterface
public interface BatchAsyncBatcherRetrier extends Function<ItemBatchOperation<?>, CompletableFuture<Void>> {
}
