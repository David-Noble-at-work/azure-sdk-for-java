// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning disable SA1402 // File may only contain a single type
public class ItemBatchOperation<T> extends ItemBatchOperation
    //C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
    ///#pragma warning restore SA1402 // File may only contain a single type
{

    private T Resource;

    public ItemBatchOperation(OperationType operationType, int operationIndex, PartitionKey partitionKey, T resource,
                              String id) {
        this(operationType, operationIndex, partitionKey, resource, id, null);
    }

    public ItemBatchOperation(OperationType operationType, int operationIndex, PartitionKey partitionKey, T resource) {
        this(operationType, operationIndex, partitionKey, resource, null, null);
    }


    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public ItemBatchOperation(OperationType operationType, int operationIndex, PartitionKey
    // partitionKey, T resource, string id = null, TransactionalBatchItemRequestOptions requestOptions = null)
    public ItemBatchOperation(OperationType operationType, int operationIndex, PartitionKey partitionKey, T resource,
                              String id, TransactionalBatchItemRequestOptions requestOptions) {
        super(operationType, operationIndex, partitionKey, , id, requestOptions);
        this.setResource(resource);
    }

    public ItemBatchOperation(OperationType operationType, int operationIndex, T resource, String id) {
        this(operationType, operationIndex, resource, id, null);
    }

    public ItemBatchOperation(OperationType operationType, int operationIndex, T resource) {
        this(operationType, operationIndex, resource, null, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public ItemBatchOperation(OperationType operationType, int operationIndex, T resource, string id
    // = null, TransactionalBatchItemRequestOptions requestOptions = null)
    public ItemBatchOperation(OperationType operationType, int operationIndex, T resource, String id,
                              TransactionalBatchItemRequestOptions requestOptions) {
        super(operationType, operationIndex, , , id, requestOptions);
        this.setResource(resource);
    }

    public final T getResource() {
        return Resource;
    }

    private void setResource(T value) {
        Resource = value;
    }

    /**
     * Materializes the operation's resource into a Memory{byte} wrapping a byte array.
     *
     * @param serializerCore Serializer to serialize user provided objects to JSON.
     * @param cancellationToken {@link CancellationToken} for cancellation.
     */
    @Override
    public CompletableFuture<Void>MaterializeResourceAsync(CosmosSerializerCore serializerCore, CancellationToken cancellationToken) {
        if (this.body.IsEmpty && this.getResource() != null) {
            this.setResourceStream(serializerCore.ToStream(this.getResource()));
            return super.MaterializeResourceAsync(serializerCore, cancellationToken);
        }

        return Task.FromResult(true);
    }
}
