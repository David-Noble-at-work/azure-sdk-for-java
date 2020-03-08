// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.ConnectionPolicy;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.batch.serializer.CosmosSerializerCore;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.ResourceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

public class TransactionalBatchCore implements TransactionalBatch {

    private final CosmosAsyncClientContext clientContext;
    private final CosmosAsyncContainer container;
    private final ArrayList<ItemBatchOperation<?>> operations;
    private final PartitionKey partitionKey;

    /**
     * Initializes a new instance of the {@link TransactionalBatchCore} class.
     *
     * @param client the {@link CosmosAsyncClient client} for communicating with the Cosmos service. 
     * @param container a container of items on which the batch operations are to be performed.
     * @param partitionKey the partition key for all items on which batch operations are to be performed.
     * @param connectionPolicy the {@link ConnectionPolicy policy} for connections to the {@code container}.
     * @param serializerCore a {@link CosmosSerializerCore serializer} instance.
     */
    public TransactionalBatchCore(
        @Nonnull final CosmosAsyncClient client,
        @Nonnull final CosmosAsyncContainer container,
        @Nonnull final PartitionKey partitionKey,
        @Nonnull final ConnectionPolicy connectionPolicy,
        @Nonnull final CosmosSerializerCore serializerCore) {

        checkNotNull(container, "expected non-null container");
        checkNotNull(partitionKey, "expected non-null partitionKey");
        checkNotNull(client, "expected non-null client");
        checkNotNull(connectionPolicy, "expected non-null connectionPolicy");
        checkNotNull(serializerCore, "expected non-null serializerCore");

        this.clientContext = new CosmosAsyncClientContext() {

            @Override
            @Nonnull
            public CosmosAsyncClient getClient() {
                return client;
            }

            @Override
            @Nonnull
            public ConnectionPolicy getConnectionPolicy() {
                return connectionPolicy;
            }

            @Override
            @Nonnull
            public CosmosSerializerCore getSerializerCore() {
                return serializerCore;
            }

            @Override
            @Nonnull
            public <T> CompletableFuture<T> processResourceOperationAsync(
                @Nonnull String resourceUri,
                @Nonnull ResourceType resourceType,
                @Nonnull OperationType operationType,
                @Nullable RequestOptions requestOptions,
                @Nonnull CosmosAsyncContainer container,
                PartitionKey partitionKey,
                InputStream streamPayload,
                @Nonnull Consumer<BatchRequestMessage> requestEnricher,
                @Nonnull Function<BatchResponseMessage, T> responseCreator,
                CosmosDiagnosticsContext diagnosticsScope) {
                return null;
            }

            @Override
            @Nonnull
            public CompletableFuture<BatchResponseMessage> processResourceOperationStreamAsync(
                @Nonnull String resourceUri,
                @Nonnull ResourceType resourceType,
                @Nonnull OperationType operationType,
                @Nullable RequestOptions requestOptions,
                CosmosAsyncContainer container,
                @Nullable PartitionKey partitionKey,
                @Nonnull InputStream streamPayload,
                @Nonnull Consumer<BatchRequestMessage> requestEnricher,
                CosmosDiagnosticsContext diagnosticsScope) {
                return null;
            }
        };

        this.container = container;
        this.operations = new ArrayList<>();
        this.partitionKey = partitionKey;
    }


    @Override
    public <TItem> TransactionalBatch createItem(
        @Nonnull final TItem item,
        @Nullable final RequestOptions requestOptions) {

        checkNotNull(item, "expected non-null item");

        this.operations.add(new ItemBatchOperation.Builder<TItem>(OperationType.Create, this.operations.size())
            .requestOptions(requestOptions)
            .resource(item)
            .build());

        return this;
    }

    @Override
    public TransactionalBatch createItemStream(
        @Nonnull final InputStream inputStream,
        @Nullable final RequestOptions requestOptions) {

        checkNotNull(inputStream, "expected non-null inputStream");

        this.operations.add(new ItemBatchOperation.Builder<InputStream>(OperationType.Create, this.operations.size())
            .requestOptions(requestOptions)
            .resource(inputStream)
            .build());

        return this;
    }

    @Override
    public TransactionalBatch deleteItem(@Nonnull final String id, final RequestOptions requestOptions) {

        checkNotNull(id, "expected non-null id");

        this.operations.add(new ItemBatchOperation.Builder<Void>(OperationType.Delete, this.operations.size())
            .requestOptions(requestOptions)
            .id(id)
            .build());

        return this;
    }

    @Override
    public CompletableFuture<TransactionalBatchResponse> executeAsync() {
        return this.executeAsync(null);
    }

    /**
     * Executes the batch at the Azure Cosmos service as an asynchronous operation.
     *
     * @param requestOptions Options that apply to the batch. Used only for EPK routing.
     *
     * @return A completable future that will contain the completion status and results of each operation.
     */
    public CompletableFuture<TransactionalBatchResponse> executeAsync(RequestOptions requestOptions) {

        BatchExecutor executor = new BatchExecutor(
            this.clientContext,
            this.container,
            this.partitionKey,
            new ArrayList<>(this.operations),
            requestOptions,
            new CosmosDiagnosticsContext());

        this.operations.clear();
        return executor.executeAsync();
    }

    /**
     * Adds an operation to patch an item into the batch.
     *
     * @param id The cosmos item id.
     * @param inputStream A {@link Stream} containing the patch specification.
     * @param requestOptions (Optional) The options for the item request. {@link TransactionalBatchItemRequestOptions}.
     *
     * @return The {@link TransactionalBatch} instance with the operation added.
     */
    public TransactionalBatch patchItemStream(
        @Nonnull final String id,
        @Nonnull final InputStream inputStream,
        final RequestOptions requestOptions) {

        checkNotNull(id, "expected non-null id");
        checkNotNull(inputStream, "expected non-null inputStream");

        this.operations.add(new ItemBatchOperation.Builder<InputStream>(OperationType.Patch, this.operations.size())
            .requestOptions(requestOptions)
            .resource(inputStream)
            .id(id)
            .build());

        return this;
    }

    @Override
    public TransactionalBatch readItem(@Nonnull final String id, final RequestOptions requestOptions) {

        checkNotNull(id, "expected non-null id");

        this.operations.add(new ItemBatchOperation.Builder<Void>(OperationType.Read, this.operations.size())
            .requestOptions(requestOptions)
            .id(id)
            .build());

        return this;
    }

    @Override
    public <TItem> TransactionalBatch replaceItem(
        @Nonnull final String id,
        @Nonnull final TItem item,
        RequestOptions requestOptions) {

        checkNotNull(id, "expected non-null id");
        checkNotNull(item, "expected non-null item");

        this.operations.add(new ItemBatchOperation.Builder<TItem>(OperationType.Replace, this.operations.size())
            .requestOptions(requestOptions)
            .resource(item)
            .id(id)
            .build());

        return this;
    }

    @Override
    public TransactionalBatch replaceItemStream(
        @Nonnull final String id,
        @Nonnull final InputStream inputStream,
        final RequestOptions requestOptions) {

        checkNotNull(id, "expected non-null id");
        checkNotNull(inputStream, "expected non-null inputStream");

        this.operations.add(new ItemBatchOperation.Builder<InputStream>(OperationType.Replace, this.operations.size())
            .requestOptions(requestOptions)
            .resource(inputStream)
            .id(id)
            .build());

        return this;
    }

    @Override
    public <TItem> TransactionalBatch upsertItem(
        @Nonnull final TItem item,
        final RequestOptions requestOptions) {

        checkNotNull(item, "expected non-null item");

        this.operations.add(new ItemBatchOperation.Builder<TItem>(OperationType.Upsert, this.operations.size())
            .requestOptions(requestOptions)
            .resource(item)
            .build());

        return this;
    }

    @Override
    public TransactionalBatch upsertItemStream(
        @Nonnull final InputStream inputStream,
        @Nullable final RequestOptions requestOptions) {

        checkNotNull(inputStream, "expected non-null inputStream");

        this.operations.add(new ItemBatchOperation.Builder<InputStream>(OperationType.Upsert, this.operations.size())
            .requestOptions(requestOptions)
            .resource(inputStream)
            .build());

        return this;
    }
}
