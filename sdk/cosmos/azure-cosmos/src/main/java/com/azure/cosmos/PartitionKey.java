// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos;

import com.azure.cosmos.implementation.Utils;
import com.azure.cosmos.implementation.routing.PartitionKeyInternal;

/**
 * Represents a partition key value in the Azure Cosmos DB database service. A
 * partition key identifies the partition where the document is stored in.
 */
public class PartitionKey {

    public static final PartitionKey EMPTY = new PartitionKey(PartitionKeyInternal.Empty);
    public static final PartitionKey NONE = new PartitionKey(PartitionKeyInternal.None);
    public static final PartitionKey UNDEFINED = new PartitionKey(PartitionKeyInternal.UndefinedPartitionKey);

    private final PartitionKeyInternal internalPartitionKey;
    private Object keyObject;

    PartitionKey(PartitionKeyInternal partitionKeyInternal) {
        this.internalPartitionKey = partitionKeyInternal;
    }

    /**
     * Constructor. CREATE a new instance of the PartitionKey object.
     *
     * @param key the value of the partition key.
     */
    @SuppressWarnings("serial")
    public PartitionKey(final Object key) {
        this.keyObject = key;
        this.internalPartitionKey = PartitionKeyInternal.fromObjectArray(new Object[] {key}, true);
    }

    /**
     * Gets the object used to create partition key
     * @return the partition key object
     */
    Object getKeyObject() {
        return keyObject;
    }

    /**
     * Create a new instance of the PartitionKey object from a serialized JSON
     * partition key.
     *
     * @param jsonString the JSON string representation of this PartitionKey object.
     * @return the PartitionKey instance.
     */
    static PartitionKey fromJsonString(String jsonString) {
        return new PartitionKey(PartitionKeyInternal.fromJsonString(jsonString));
    }

    /**
     * Serialize the current {@link PartitionKey partition key} to a JSON string.
     *
     * @return a JSON string representation of the current {@link PartitionKey partition key}.
     */
    public String toJsonString() {
        return internalPartitionKey.toJson();
    }

    /**
     * Serialize the PartitionKey object to a JSON string.
     *
     * @return the string representation of this PartitionKey object.
     */
    public String toString() {
        return this.internalPartitionKey.toJson();
    }

    PartitionKeyInternal getInternalPartitionKey() {
        return internalPartitionKey;
    }

    /**
     * Overrides the Equal operator for object comparisons between two instances of
     * {@link PartitionKey}
     *
     * @param other The object to compare with.
     * @return True if two object instance are considered equal.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        PartitionKey otherKey = Utils.as(other, PartitionKey.class);
        return otherKey != null && this.internalPartitionKey.equals(otherKey.internalPartitionKey);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
