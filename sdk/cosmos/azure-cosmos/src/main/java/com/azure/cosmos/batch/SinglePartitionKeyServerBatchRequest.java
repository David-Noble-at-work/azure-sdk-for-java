// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.serializer.CosmosSerializerCore;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class SinglePartitionKeyServerBatchRequest extends ServerBatchRequest {

    private final PartitionKey partitionKey;

    /**
     * Initializes a new instance of the {@link SinglePartitionKeyServerBatchRequest} class. Single partition key server
     * request.
     *
     * @param partitionKey Partition key that applies to all operations in this request.
     * @param serializerCore Serializer to serialize user provided objects to JSON.
     */
    private SinglePartitionKeyServerBatchRequest(PartitionKey partitionKey, CosmosSerializerCore serializerCore) {
        super(Integer.MAX_VALUE, Integer.MAX_VALUE, serializerCore);
        this.partitionKey = partitionKey;
    }

    /**
     * PartitionKey that applies to all operations in this request.
     */
    public PartitionKey getPartitionKey() {
        return this.partitionKey;
    }

    /**
     * Creates an instance of {@link SinglePartitionKeyServerBatchRequest}. The body of the request is populated with
     * operations till it reaches the provided maxBodyLength.
     *
     * @param partitionKey Partition key of the request.
     * @param operations Operations to be added into this batch request.
     * @param serializerCore Serializer to serialize user provided objects to JSON.
     *
     * @return A newly created instance of {@link SinglePartitionKeyServerBatchRequest}.
     */
    public static CompletableFuture<SinglePartitionKeyServerBatchRequest> CreateAsync(
        PartitionKey partitionKey,
        List<ItemBatchOperation> operations,
        CosmosSerializerCore serializerCore) {

        final SinglePartitionKeyServerBatchRequest request = new SinglePartitionKeyServerBatchRequest(
            partitionKey,
            serializerCore);
        /*await*/ request.createBodyStreamAsync(operations);
        return request;
    }
}
