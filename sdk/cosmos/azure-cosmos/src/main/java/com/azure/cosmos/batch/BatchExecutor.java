// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.batch.serializer.CosmosSerializerCore;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticScope;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
import com.azure.cosmos.core.Out;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.ResourceType;
import com.azure.cosmos.models.PartitionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.azure.cosmos.batch.TransactionalBatchResponse.fromResponseMessageAsync;

public final class BatchExecutor {

    private static final Logger logger = LoggerFactory.getLogger(BatchExecutor.class);

    private final CosmosAsyncClientContext clientContext;
    private final CosmosAsyncContainer container;
    private final CosmosDiagnosticsContext diagnosticsContext;
    private final List<ItemBatchOperation<?>> operations;
    private final RequestOptions options;
    private final PartitionKey partitionKey;

    public BatchExecutor(
        final CosmosAsyncClientContext clientContext,
        final CosmosAsyncContainer container,
        final PartitionKey partitionKey,
        final List<ItemBatchOperation<?>> operations,
        final RequestOptions options,
        final CosmosDiagnosticsContext diagnosticsContext) {

        this.clientContext = clientContext;
        this.container = container;
        this.operations = operations;
        this.partitionKey = partitionKey;
        this.options = options;
        this.diagnosticsContext = diagnosticsContext;
    }

    public CompletableFuture<TransactionalBatchResponse> executeAsync() {

        BatchExecUtils.ensureValid(this.operations, this.options);

        final CosmosDiagnosticScope executeScope = this.diagnosticsContext.createOverallScope("BatchExecuteAsync");
        final ArrayList<ItemBatchOperation<?>> operations = new ArrayList<>(this.operations);
        final CosmosSerializerCore serializerCore = this.clientContext.getSerializerCore();

        final PartitionKey partitionKey = this.options != null && this.options.isEffectivePartitionKeyRouting()
            ? null
            : this.partitionKey;

        final CosmosDiagnosticScope requestScope = this.diagnosticsContext.createScope("CreateBatchRequest");

        return SinglePartitionKeyServerBatchRequest.createAsync(partitionKey, operations, serializerCore)
            .whenCompleteAsync((request, error) -> requestScope.close())
            .thenComposeAsync(this::executeBatchRequestAsync)
            .whenCompleteAsync((response, error) -> executeScope.close());
    }

    /**
     * Makes a single batch request to the server.
     *
     * @param request A server request with a set of operations on items.
     *
     * @return Response from the server.
     */
    private CompletableFuture<TransactionalBatchResponse> executeBatchRequestAsync(
        @Nonnull final SinglePartitionKeyServerBatchRequest request) {

        final CosmosSerializerCore serializerCore = this.clientContext.getSerializerCore();
        final Out<CosmosDiagnosticScope> responseScope = new Out<>();
        final InputStream payload = request.transferBodyStream();

        return this.clientContext.processResourceOperationStreamAsync(
            this.container.getLink(),
            ResourceType.Document,
            OperationType.Batch,
            options,
            this.container,
            request.getPartitionKey(),
            payload,
            requestMessage -> {
                //requestMessage.Headers.Add(HttpHeaders.IS_BATCH_REQUEST, Boolean.TRUE.toString());
                //requestMessage.Headers.Add(HttpHeaders.IS_BATCH_ATOMIC, Boolean.TRUE.toString());
                //requestMessage.Headers.Add(HttpHeaders.IS_BATCH_ORDERED, Boolean.TRUE.toString());
            },
            this.diagnosticsContext)

            .thenComposeAsync((BatchResponseMessage message) -> {
                responseScope.set(this.diagnosticsContext.createScope("TransactionalBatchResponse"));
                return fromResponseMessageAsync(message, request, serializerCore);
            })

            .whenCompleteAsync((response, error) -> {
                if (responseScope.get() != null) {
                    responseScope.get().close();
                }
                try {
                    payload.close();
                } catch (IOException e) {
                    logger.error("failed to close payload input stream due to ", e);
                }
            });
    }
}
