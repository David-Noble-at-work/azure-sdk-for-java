// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serializer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class provides a way to configure basic serializer settings.
 */
public final class CosmosSerializationOptions {

    private static final CosmosSerializationOptions DEFAULT = new CosmosSerializationOptions();

    private final boolean ignoreNullValues;
    private final boolean indented;
    private final CosmosPropertyNamingPolicy propertyNamingPolicy;

    private CosmosSerializationOptions(Builder builder) {
        this.indented = builder.indented;
        this.ignoreNullValues = builder.ignoreNullValues;
        this.propertyNamingPolicy = builder.propertyNamingPolicy;
    }

    private CosmosSerializationOptions() {
        this.ignoreNullValues = false;
        this.indented = false;
        this.propertyNamingPolicy = CosmosPropertyNamingPolicy.DEFAULT;
    }

    public Builder builder() {
        return new Builder();
    }

    /**
     * {@code true} if the serializer should ignore null properties.
     * <p>
     * The default value is false.
     *
     * @return {@code true} if the serializer should ignore null properties.
     */
    public boolean getIgnoreNullValues() {
        return ignoreNullValues;
    }

    /**
     * {@code true} if the serializer should use indentation.
     * <p>
     * The default value is false.
     *
     * @return {@code true} if the serializer should use indentation.
     */
    public boolean getIndented() {
        return indented;
    }

    /**
     * Gets the naming policy used to convert a string-based name to another format, such as a camel-casing format.
     * <p>
     * The default value is {@link CosmosPropertyNamingPolicy#DEFAULT}.
     *
     * @return {@code }
     */
    public CosmosPropertyNamingPolicy getPropertyNamingPolicy() {
        return propertyNamingPolicy;
    }

    public final class Builder {

        private boolean ignoreNullValues;
        private boolean indented;
        private CosmosPropertyNamingPolicy propertyNamingPolicy;

        private Builder() {
            this.ignoreNullValues = DEFAULT.ignoreNullValues;
            this.indented = DEFAULT.indented;
            this.propertyNamingPolicy = DEFAULT.propertyNamingPolicy;
        }

        public CosmosSerializationOptions build() {
            return new CosmosSerializationOptions(this);
        }

        public Builder ignoreNullValues(boolean value) {
            this.ignoreNullValues = value;
            return this;
        }

        public Builder indented(boolean value) {
            this.indented = value;
            return this;
        }

        public Builder propertyNamingPolicy(CosmosPropertyNamingPolicy value) {
            checkNotNull(value, "expected non-null value");
            this.propertyNamingPolicy = value;
            return this;
        }
    }
}
