// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Executor implementation that processes a list of operations.
 */
@FunctionalInterface
public interface BatchAsyncBatcherExecutor extends
    Function<PartitionKeyRangeServerBatchRequest, CompletableFuture<PartitionKeyRangeBatchExecutionResult>> {
}
