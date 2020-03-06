// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.ConnectionPolicy;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.ResourceType;
import com.azure.cosmos.batch.serializer.CosmosSerializerCore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class CosmosClientContext {
    /**
     * Gets the {@link CosmosAsyncClient Cosmos client} that is used for a request.
     *
     * @return the {@link CosmosAsyncClient Cosmos client} that is used for a request.
     */
    abstract CosmosAsyncClient getClient();

    /**
     * Gets the {@link ConnectionPolicy Cosmos connection policy} that is used for a request.
     *
     * @return the {@link ConnectionPolicy Cosmos connection policy}  that is used for a request.
     */
    abstract ConnectionPolicy getConnectionPolicy();

    /**
     * Gets the {@link CosmosSerializerCore Cosmos serializer} that is used for a request.
     *
     * @return the  {@link CosmosSerializerCore Cosmos serializer}  that is used for a request.
     */
    abstract CosmosSerializerCore getSerializerCore();

    /**
     * This is a wrapper around the request invoker method.
     * <p>
     * This allows the calls to be mocked so logic done in a resource can be unit tested.
     */
    @Nonnull
    abstract <T> CompletableFuture<T> processResourceOperationAsync(
        @Nonnull String resourceUri,
        @Nonnull ResourceType resourceType,
        @Nonnull OperationType operationType,
        @Nullable RequestOptions requestOptions,
        @Nonnull CosmosAsyncContainer container,
        PartitionKey partitionKey,
        InputStream streamPayload,
        @Nonnull Consumer<BatchRequestMessage> requestEnricher,
        @Nonnull Function<BatchResponseMessage, T> responseCreator,
        CosmosDiagnosticsContext diagnosticsScope);

    /**
     * This is a wrapper around the request invoker method.
     * <p>
     * This allows the calls to be mocked so logic done in a resource can be unit tested.
     */
    @Nonnull
    abstract CompletableFuture<BatchResponseMessage> processResourceOperationStreamAsync(
        @Nonnull String resourceUri,
        @Nonnull ResourceType resourceType,
        @Nonnull OperationType operationType,
        @Nullable RequestOptions requestOptions,
        CosmosAsyncContainer container,
        @Nullable PartitionKey partitionKey,
        @Nonnull InputStream streamPayload,
        @Nonnull Consumer<BatchRequestMessage> requestEnricher,
        CosmosDiagnosticsContext diagnosticsScope);
}
