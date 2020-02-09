// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public final class BatchExecutor {
    private RequestOptions batchOptions;
    private CosmosClientContext clientContext;
    private ContainerCore container;
    private CosmosDiagnosticsContext diagnosticsContext;
    private List<ItemBatchOperation> inputOperations;
    private PartitionKey partitionKey;

    public BatchExecutor(
        final ContainerCore container,
        final PartitionKey partitionKey,
        final List<ItemBatchOperation> operations,
        final RequestOptions batchOptions,
        final CosmosDiagnosticsContext diagnosticsContext) {

        this.container = container;
        this.clientContext = this.container.ClientContext;
        this.inputOperations = operations;
        this.partitionKey = partitionKey;
        this.batchOptions = batchOptions;
        this.diagnosticsContext = diagnosticsContext;
    }

    public CompletableFuture<TransactionalBatchResponse> ExecuteAsync() {

        try (this.diagnosticsContext.CreateOverallScope("BatchExecuteAsync")) {

            BatchExecUtils.ensureValid(this.inputOperations, this.batchOptions);
            PartitionKey serverRequestPartitionKey = this.partitionKey;

            if (this.batchOptions != null && this.batchOptions.IsEffectivePartitionKeyRouting) {
                serverRequestPartitionKey = null;
            }

            SinglePartitionKeyServerBatchRequest serverRequest;

            try (this.diagnosticsContext.CreateScope("CreateBatchRequest")) {
                serverRequest = /*await*/SinglePartitionKeyServerBatchRequest.CreateAsync(serverRequestPartitionKey,
                    new ArrayList<ItemBatchOperation>(this.inputOperations),
                    this.clientContext.SerializerCore);
            }

            return /*await*/this.ExecuteServerRequestAsync(serverRequest);
        }
    }

    /**
     * Makes a single batch request to the server.
     *
     * @param serverRequest A server request with a set of operations on items.
     *
     * @return Response from the server.
     */
    private CompletableFuture<TransactionalBatchResponse> ExecuteServerRequestAsync(SinglePartitionKeyServerBatchRequest serverRequest) {

        try (Stream serverRequestPayload = serverRequest.TransferBodyStream()) {
            assert serverRequestPayload != null : "expected non-null serverRequestPayload";
            ResponseMessage responseMessage = /*await*/
                this.clientContext.ProcessResourceOperationStreamAsync(this.container.LinkUri, ResourceType.Document,
                    OperationType.Batch, this.batchOptions, this.container, serverRequest.getPartitionKey(),
                    serverRequestPayload, requestMessage -> {
                        requestMessage.Headers.Add(HttpHeaders.IS_BATCH_REQUEST, Boolean.TRUE.toString());
                        requestMessage.Headers.Add(HttpHeaders.IS_BATCH_ATOMIC, Boolean.TRUE.toString());
                        requestMessage.Headers.Add(HttpHeaders.IS_BATCH_ORDERED, Boolean.TRUE.toString());
                    }, this.diagnosticsContext);

            try (this.diagnosticsContext.CreateScope("TransactionalBatchResponse")) {
                return /*await*/
                TransactionalBatchResponse.FromResponseMessageAsync(responseMessage, serverRequest, this.clientContext.SerializerCore);
            }
        }
    }
}
