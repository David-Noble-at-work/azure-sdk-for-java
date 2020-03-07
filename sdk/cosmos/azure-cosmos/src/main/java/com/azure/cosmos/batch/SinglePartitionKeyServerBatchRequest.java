// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.batch.serializer.CosmosSerializerCore;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

public final class SinglePartitionKeyServerBatchRequest extends ServerBatchRequest {

    private final PartitionKey partitionKey;

    /**
     * Initializes a new instance of the {@link SinglePartitionKeyServerBatchRequest} class. Single partition key server
     * request.
     *
     * @param partitionKey Partition key that applies to all operations in this request.
     * @param serializerCore Serializer to serialize user provided objects to JSON.
     */
    private SinglePartitionKeyServerBatchRequest(
        @Nonnull final PartitionKey partitionKey,
        @Nonnull final CosmosSerializerCore serializerCore) {
        super(Integer.MAX_VALUE, Integer.MAX_VALUE, serializerCore);
        this.partitionKey = partitionKey;
    }

    /**
     * Returns the {@link PartitionKey partition key} that applies to all operations in this {@link
     * SinglePartitionKeyServerBatchRequest batch request}.
     *
     * @return the {@link PartitionKey partition key} that applies to all operations in this {@link
     * SinglePartitionKeyServerBatchRequest batch request}.
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
    public static CompletableFuture<SinglePartitionKeyServerBatchRequest> createAsync(
        @Nonnull final PartitionKey partitionKey,
        @Nonnull final List<ItemBatchOperation<?>> operations,
        @Nonnull final CosmosSerializerCore serializerCore) {

        checkNotNull(partitionKey, "expected non-null partitionKey");
        checkNotNull(operations, "expected non-null operations");
        checkNotNull(serializerCore, "expected non-null serializerCore");

        final SinglePartitionKeyServerBatchRequest request = new SinglePartitionKeyServerBatchRequest(
            partitionKey,
            serializerCore);

        return request.createBodyStreamAsync(operations).thenApplyAsync(pendingOperations -> request);
    }
}
