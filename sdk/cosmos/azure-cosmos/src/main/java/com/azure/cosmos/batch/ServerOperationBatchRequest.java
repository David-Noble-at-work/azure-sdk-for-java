// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

final class ServerOperationBatchRequest implements Serializable {

    private static final long serialVersionUID = 1899495696133611673L;
    private final PartitionKeyRangeServerBatchRequest batchRequest;
    private final List<ItemBatchOperation<?>> operations;

    /**
     * Gets the key for this pair.
     * @return key for this pair
     */
    public PartitionKeyRangeServerBatchRequest getBatchRequest() {
        return this.batchRequest;
    }

    /**
     * Gets the value for this pair.
     * @return value for this pair
     */
    public List<ItemBatchOperation<?>> getBatchOperations() {
        return this.operations;
    }

    /**
     * Creates a new pair
     * @param batchRequest the {@link ServerBatchRequest batch request}
     * @param operations the {@link List list} of {@link ItemBatchOperation operations} for the batch request.
     */
    ServerOperationBatchRequest(
        @Nonnull final PartitionKeyRangeServerBatchRequest batchRequest,
        @Nonnull final List<ItemBatchOperation<?>> operations) {

        checkNotNull(batchRequest, "expected non-null batchRequest");
        checkNotNull(operations, "expected non-null operations");
        this.batchRequest = batchRequest;
        this.operations = operations;
    }

    /**
     * {@link String} representation of this {@link ServerOperationBatchRequest}.
     *
     *  @return {@link String} representation of this {@link ServerOperationBatchRequest}.
     */
    @Override
    public String toString() {
        return batchRequest + "=" + operations;
    }

    /**
     * Calculates a hash code for this {@link ServerOperationBatchRequest}.
     *
     * @return hash code for this {@link ServerOperationBatchRequest}.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (batchRequest != null ? batchRequest.hashCode() : 0);
        hash = 31 * hash + (operations != null ? operations.hashCode() : 0);
        return hash;
    }

    /**
     * Tests this {@link ServerOperationBatchRequest} for equality with another {@link Object}.
     * <p>
     * Two {@link ServerOperationBatchRequest} instances are considered equal if and only if both the batch request and
     * operation lists are equal.
     *
     * @param other the {@link Object} to test for equality with this {@link ServerOperationBatchRequest}.
     *
     * @return {@code true} if the given {@link Object} is equal to this {@link ServerOperationBatchRequest};
     * {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        ServerOperationBatchRequest that = (ServerOperationBatchRequest) other;

        if (!batchRequest.equals(that.batchRequest)) {
            return false;
        }

        return operations.equals(that.operations);
    }
}
