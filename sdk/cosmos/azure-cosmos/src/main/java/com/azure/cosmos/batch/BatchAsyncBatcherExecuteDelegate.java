// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import java.util.concurrent.CompletableFuture;

/**
 * Executor implementation that processes a list of operations.
 */
@FunctionalInterface
public interface BatchAsyncBatcherExecuteDelegate {
    CompletableFuture<PartitionKeyRangeBatchExecutionResult> invoke(PartitionKeyRangeServerBatchRequest request);
}
