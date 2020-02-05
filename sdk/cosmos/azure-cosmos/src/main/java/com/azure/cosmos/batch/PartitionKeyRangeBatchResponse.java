// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.Resource;
import com.azure.cosmos.serializer.CosmosSerializerCore;
import io.netty.handler.codec.http.HttpResponseStatus;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Response of a cross partition key batch request.
 */
public class PartitionKeyRangeBatchResponse extends TransactionalBatchResponse {

    // region Fields

    // Results sorted in the order operations had been added.

    private final TransactionalBatchOperationResult[] resultsByOperationIndex;
    private final TransactionalBatchResponse serverResponse;

    // endregion

    // region Constructors

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

    // endregion

    // region Accessors

    /**
     * Gets the ActivityId that identifies the server request made to execute the batch request.
     */
    @Override
    public String getActivityId() {
        return this.serverResponse.getActivityId();
    }

    @Override
    public CosmosDiagnostics getDiagnostics() {
        return this.serverResponse.getDiagnostics();
    }

    @Override
    public CosmosDiagnosticsContext getDiagnosticsContext() {
        return this.serverResponse.getDiagnosticsContext();
    }

    /**
     * Gets the result of the operation at the provided index in the batch
     * <p>
     * The returned result has a {@link Resource} of the specified type.
     *
     * @param <T> type to which the {@link Resource} in the operation result needs to be deserialized, when present.
     * @param index 0-based index of the operation in the batch whose result needs to be returned.
     *
     * @return Result of batch operation that contains a Resource deserialized to specified type.
     */
    @Override
    public <T> TransactionalBatchOperationResult<T> GetOperationResultAtIndex(
        final int index, @Nonnull final Class<T> type) {

        checkArgument(0 <= index && index < this.size(), "expected index in range [0, %s), not %s",
            this.size(),
            index);

        checkNotNull(type, "expected non-null type");

        final TransactionalBatchOperationResult result = this.resultsByOperationIndex[index];
        T resource = null;

        if (result.getResourceStream() != null) {
            resource = this.getSerializer().<T>FromStream(result.getResourceStream(), type);
        }

        return new TransactionalBatchOperationResult<T>(result, resource);
    }

    // endregion

    // region Methods

    @Override
    public void close() throws Exception {
        if (this.serverResponse != null) {
            this.serverResponse.close();
        }
        super.close();
    }

    @Override
    public TransactionalBatchOperationResult get(int index) {
        return this.resultsByOperationIndex[index];
    }

    /**
     * Gets an enumerator over the operation results.
     *
     * @return Enumerator over the operation results.
     */
    @Override
    public Iterator<TransactionalBatchOperationResult> iterator() {
        return Stream.of(this.resultsByOperationIndex).iterator();
    }

    /**
     * Gets the number of operation results.
     */
    @Override
    public int size() {
        return this.resultsByOperationIndex.length;
    }

    // endregion
}
