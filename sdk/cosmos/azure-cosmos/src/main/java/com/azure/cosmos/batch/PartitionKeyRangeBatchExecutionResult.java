// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.implementation.HttpConstants.StatusCodes;
import com.azure.cosmos.implementation.HttpConstants.SubStatusCodes;

public final class PartitionKeyRangeBatchExecutionResult {

    private final Iterable<ItemBatchOperation> operations;
    private final String partitionKeyRangeId;
    private final TransactionalBatchResponse serverResponse;

    public PartitionKeyRangeBatchExecutionResult(
        final String pkRangeId,
        final Iterable<ItemBatchOperation> operations,
        final TransactionalBatchResponse serverResponse) {

        this.partitionKeyRangeId = pkRangeId;
        this.serverResponse = serverResponse;
        this.operations = operations;
    }

    public Iterable<ItemBatchOperation> getOperations() {
        return operations;
    }

    public String getPartitionKeyRangeId() {
        return partitionKeyRangeId;
    }

    public TransactionalBatchResponse getServerResponse() {
        return serverResponse;
    }

    public boolean isSplit() {
        return this.getServerResponse() != null && this.getServerResponse().getResponseStatus() == StatusCodes.GONE && (
            this.getServerResponse().getSubStatusCode() == SubStatusCodes.COMPLETING_SPLIT
                || this.getServerResponse().getSubStatusCode() == SubStatusCodes.COMPLETING_PARTITION_MIGRATION
                || this.getServerResponse().getSubStatusCode() == SubStatusCodes.PARTITION_KEY_RANGE_GONE);
    }
}
