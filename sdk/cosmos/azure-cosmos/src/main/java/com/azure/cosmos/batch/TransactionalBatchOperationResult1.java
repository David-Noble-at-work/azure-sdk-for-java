// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

/**
 * Represents a result for a specific operation that is part of a batch.
 *
 * <typeparam name="T">The type of the Resource which this class wraps.</typeparam>
 */
public class TransactionalBatchOperationResult1<T> extends TransactionalBatchOperationResult
    //C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
    ///#pragma warning restore SA1402 // File may only contain a single type
{
    /**
     * Gets the content of the resource.
     */
    private T Resource;

    /**
     * Initializes a new instance of the <see cref="TransactionalBatchOperationResult{T}"/> class.
     *
     * @param result BatchOperationResult with stream resource.
     * @param resource Deserialized resource.
     */
    public TransactionalBatchOperationResult1(TransactionalBatchOperationResult result, T resource) {
        super(result);
        this.setResource(resource);
    }

    /**
     * Initializes a new instance of the <see cref="TransactionalBatchOperationResult{T}"/> class.
     */
    protected TransactionalBatchOperationResult1() {
    }

    public T getResource() {
        return Resource;
    }

    public void setResource(T value) {
        Resource = value;
    }
}
