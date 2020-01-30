// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.implementation.Strings;
import com.azure.cosmos.serializer.CosmosSerializerCore;
import io.netty.handler.codec.http.HttpResponseStatus;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Response of a cross partition key batch request.
 */
public class PartitionKeyRangeBatchResponse extends TransactionalBatchResponse {

    // Results sorted in the order operations had been added.

    private final TransactionalBatchOperationResult[] resultsByOperationIndex;
    private final TransactionalBatchResponse serverResponse;

    /**
     * Initializes a new instance of the {@link PartitionKeyRangeBatchResponse} class.
     *
     * @param originalOperationsCount Original operations that generated the server responses.
     * @param serverResponse Response from the server.
     * @param serializer Serializer to deserialize response resource body streams.
     */
    public PartitionKeyRangeBatchResponse(
        final int originalOperationsCount,
        @Nonnull final TransactionalBatchResponse serverResponse,
        @Nonnull final CosmosSerializerCore serializer) {

        super(
            serverResponse.getResponseStatus(),
            serverResponse.getSubStatusCode(),
            serverResponse.getErrorMessage(),
            serverResponse.getRequestCharge(),
            serverResponse.getRetryAfter(),
            serverResponse.getActivityId(),
            serverResponse.getDiagnosticsContext(),
            new ArrayList<>(serverResponse.getOperations()),
            serializer);

        this.serverResponse = serverResponse;

        // We expect number of results == number of operations here

        this.resultsByOperationIndex = new TransactionalBatchOperationResult[originalOperationsCount];

        for (int index = 0; index < serverResponse.getOperations().size(); index++) {

            final int operationIndex = serverResponse.getOperations().get(index).getOperationIndex();
            final TransactionalBatchOperationResult result = this.resultsByOperationIndex[operationIndex];

            if (result == null || result.getResponseStatus() == HttpResponseStatus.TOO_MANY_REQUESTS) {
                this.resultsByOperationIndex[operationIndex] = serverResponse.get(index);
            }
        }
    }

    /**
     * Gets the ActivityId that identifies the server request made to execute the batch request.
     */
    @Override
    public String getActivityId() {
        return this.serverResponse.getActivityId();
    }

    /**
     * Gets the number of operation results.
     */
    @Override
    public int size() {
        return this.resultsByOperationIndex.length;
    }

    /**
     * <inheritdoc />
     */
    @Override
    public CosmosDiagnostics getDiagnostics() {
        return this.serverResponse.getDiagnostics();
    }

    @Override
    public CosmosDiagnosticsContext getDiagnosticsContext() {
        return this.serverResponse.getDiagnosticsContext();
    }

    @Override
    public CosmosSerializerCore getSerializer() {
        return serializerCore;
    }

    /**
     * Gets an enumerator over the operation results.
     *
     * @return Enumerator over the operation results.
     */
    @Override
    public Iterator<TransactionalBatchOperationResult> GetEnumerator() {
        for (TransactionalBatchOperationResult result : this.resultsByOperationIndex) {
            //C# TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to the C# 'yield' keyword:
            yield return result;
        }
    }

    /**
     * Gets the result of the operation at the provided index in the batch - the returned result has a Resource of
     * provided type.
     *
     * <typeparam name="T">Type to which the Resource in the operation result needs to be deserialized to, when
     * present.</typeparam>
     *
     * @param index 0-based index of the operation in the batch whose result needs to be returned.
     *
     * @return Result of batch operation that contains a Resource deserialized to specified type.
     */
    @Override
    public <T> TransactionalBatchOperationResult<T> GetOperationResultAtIndex(int index) {

        if (index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }

        TransactionalBatchOperationResult result = this.resultsByOperationIndex[index];

        T resource = null;
        if (result.getResourceStream() != null) {
            resource = this.getSerializer().<T>FromStream(result.getResourceStream());
        }

        return new TransactionalBatchOperationResult<T>(result, resource);
    }

    /**
     * <inheritdoc />
     */
    @Override
    public TransactionalBatchOperationResult get(int index) {
        return this.resultsByOperationIndex[index];
    }

    /**
     * Disposes the disposable members held.
     *
     * @param disposing Indicates whether to dispose managed resources or not.
     */
    @Override
    protected void Dispose(boolean disposing) {
        if (disposing && !this.isDisposed) {
            this.isDisposed = true;
            this.serverResponse == null ? null : this.serverResponse.close();
        }

        super.Dispose(disposing);
    }
}
