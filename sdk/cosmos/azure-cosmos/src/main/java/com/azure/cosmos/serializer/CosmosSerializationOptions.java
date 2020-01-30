// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serializer;

/**
 * This class provides a way to configure basic serializer settings.
 */
public final class CosmosSerializationOptions {
    /**
     * Gets or sets if the serializer should ignore null properties
     * <p>
     * <p>
     * The default value is false
     */
    private boolean IgnoreNullValues;
    /**
     * Gets or sets if the serializer should use indentation
     * <p>
     * <p>
     * The default value is false
     */
    private boolean Indented;
    /**
     * Gets or sets whether the naming policy used to convert a string-based name to another format, such as a
     * camel-casing format.
     * <p>
     * <p>
     * The default value is CosmosPropertyNamingPolicy.Default
     */
    private CosmosPropertyNamingPolicy PropertyNamingPolicy = CosmosPropertyNamingPolicy.values()[0];

    /**
     * Create an instance of CosmosSerializationOptions with the default values for the Cosmos SDK
     */
    public CosmosSerializationOptions() {
        this.setIgnoreNullValues(false);
        this.setIndented(false);
        this.setPropertyNamingPolicy(CosmosPropertyNamingPolicy.Default);
    }

    public boolean getIgnoreNullValues() {
        return IgnoreNullValues;
    }

    public void setIgnoreNullValues(boolean value) {
        IgnoreNullValues = value;
    }

    public boolean getIndented() {
        return Indented;
    }

    public void setIndented(boolean value) {
        Indented = value;
    }

    public CosmosPropertyNamingPolicy getPropertyNamingPolicy() {
        return PropertyNamingPolicy;
    }

    public void setPropertyNamingPolicy(CosmosPropertyNamingPolicy value) {
        PropertyNamingPolicy = value;
    }
}
