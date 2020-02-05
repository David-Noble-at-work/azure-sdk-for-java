// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.serializer.CosmosSerializerCore;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkNotNull;

final class PartitionKeyRangeServerBatchRequest extends ServerBatchRequest {

    private final String partitionKeyRangeId;

    /**
     * Initializes a new instance of the {@link PartitionKeyRangeServerBatchRequest} class.
     *
     * @param partitionKeyRangeId The partition key range id associated with all requests.
     * @param maxBodyLength Maximum length allowed for the request body.
     * @param maxOperationCount Maximum number of operations allowed in the request.
     * @param serializerCore Serializer to serialize user provided objects to JSON.
     */
    PartitionKeyRangeServerBatchRequest(
        @Nonnull final String partitionKeyRangeId,
        int maxBodyLength,
        int maxOperationCount,
        CosmosSerializerCore serializerCore) {

        super(maxBodyLength, maxOperationCount, serializerCore);
        checkNotNull(partitionKeyRangeId, "expected non-null partitionKeyRangeId");
        this.partitionKeyRangeId = partitionKeyRangeId;
    }

    /**
     * Gets the PartitionKeyRangeId that applies to all operations in this request.
     *
     * @return PartitionKeyRangeId that applies to all operations in this request.
     */
    public String getPartitionKeyRangeId() {
        return this.partitionKeyRangeId;
    }

    /**
     * Creates an instance of {@link PartitionKeyRangeServerBatchRequest}. In case of direct mode requests, all the
     * operations are expected to belong to the same PartitionKeyRange. The body of the request is populated with
     * operations till it reaches the provided maxBodyLength.
     *
     * @param partitionKeyRangeId The partition key range id associated with all requests.
     * @param operations Operations to be added into this batch request.
     * @param maxBodyLength Desired maximum length of the request body.
     * @param maxOperationCount Maximum number of operations allowed in the request.
     * @param ensureContinuousOperationIndexes Whether to stop adding operations to the request once there is
     * non-continuity in the operation indexes.
     * @param serializerCore Serializer to serialize user provided objects to JSON.
     *
     * @return A newly created instance of {@link PartitionKeyRangeServerBatchRequest} and the overflow
     * ItemBatchOperation not being processed.
     */
    public static CompletableFuture<ServerBatchOperationsRequest> createAsync(
        final String partitionKeyRangeId,
        final List<ItemBatchOperation> operations,
        final int maxBodyLength,
        final int maxOperationCount,
        final boolean ensureContinuousOperationIndexes,
        CosmosSerializerCore serializerCore) {

        final PartitionKeyRangeServerBatchRequest request = new PartitionKeyRangeServerBatchRequest(
            partitionKeyRangeId,
            maxBodyLength,
            maxOperationCount,
            serializerCore);

        List<ItemBatchOperation> pendingOperations = /*await*/request.CreateBodyStreamAsync(operations, ensureContinuousOperationIndexes);
        return new ServerBatchOperationsRequest(request, pendingOperations);
    }
}
