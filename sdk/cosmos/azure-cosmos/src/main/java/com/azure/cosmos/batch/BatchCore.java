// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.implementation.RequestOptions;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public class BatchCore implements TransactionalBatch {

    private final ContainerCore container;
    private final ArrayList<ItemBatchOperation<?>> operations;
    private final PartitionKey partitionKey;

    /**
     * Initializes a new instance of the {@link BatchCore} class.
     *
     * @param container Container that has items on which batch operations are to be performed.
     * @param partitionKey The partition key for all items in the batch. {@link PartitionKey}.
     */
    public BatchCore(@Nonnull ContainerCore container, @Nonnull PartitionKey partitionKey) {

        checkNotNull(container, "expected non-null container");
        checkNotNull(partitionKey, "expected non-null partitionKey");

        this.container = container;
        this.partitionKey = partitionKey;
        this.operations = new ArrayList<>();
    }


    @Override
    public <TItem> TransactionalBatch createItem(@Nonnull final TItem item, final RequestOptions requestOptions) {

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
        final RequestOptions requestOptions) {

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
        final RequestOptions requestOptions) {

        checkNotNull(inputStream, "expected non-null inputStream");

        this.operations.add(new ItemBatchOperation.Builder<InputStream>(OperationType.Upsert, this.operations.size())
            .requestOptions(requestOptions)
            .resource(inputStream)
            .build());

        return this;
    }
}
