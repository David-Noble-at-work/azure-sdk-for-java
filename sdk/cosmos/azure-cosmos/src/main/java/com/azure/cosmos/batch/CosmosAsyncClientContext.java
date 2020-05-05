// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.ConnectionPolicy;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.batch.serializer.CosmosSerializerCore;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.ResourceType;
import com.azure.cosmos.models.PartitionKey;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

interface CosmosAsyncClientContext {
    /**
     * Gets the {@link CosmosAsyncClient Cosmos client} that is used for a request.
     *
     * @return the {@link CosmosAsyncClient Cosmos client} that is used for a request.
     */
    @NotNull
    CosmosAsyncClient getClient();

    /**
     * Gets the {@link ConnectionPolicy Cosmos connection policy} that is used for a request.
     *
     * @return the {@link ConnectionPolicy Cosmos connection policy}  that is used for a request.
     */
    @NotNull
    ConnectionPolicy getConnectionPolicy();

    /**
     * Gets the {@link CosmosSerializerCore Cosmos serializer} that is used for a request.
     *
     * @return the  {@link CosmosSerializerCore Cosmos serializer}  that is used for a request.
     */
    CosmosSerializerCore getSerializerCore();

    /**
     * This is a wrapper around the request invoker method.
     * <p>
     * This allows the calls to be mocked so logic done in a resource can be unit tested.
     */
    @NotNull
    <T> CompletableFuture<T> processResourceOperationAsync(
        @NotNull String resourceUri,
        @NotNull ResourceType resourceType,
        @NotNull OperationType operationType,
        @Nullable RequestOptions requestOptions,
        @NotNull CosmosAsyncContainer container,
        PartitionKey partitionKey,
        InputStream streamPayload,
        @NotNull Consumer<BatchRequestMessage> requestEnricher,
        @NotNull Function<BatchResponseMessage, T> responseCreator,
        CosmosDiagnosticsContext diagnosticsScope);

    /**
     * This is a wrapper around the request invoker method.
     * <p>
     * This allows the calls to be mocked so logic done in a resource can be unit tested.
     */
    @NotNull
    CompletableFuture<BatchResponseMessage> processResourceOperationStreamAsync(
        @NotNull String resourceUri,
        @NotNull ResourceType resourceType,
        @NotNull OperationType operationType,
        @Nullable RequestOptions requestOptions,
        CosmosAsyncContainer container,
        @Nullable PartitionKey partitionKey,
        @NotNull InputStream streamPayload,
        @NotNull Consumer<BatchRequestMessage> requestEnricher,
        CosmosDiagnosticsContext diagnosticsScope);
}
