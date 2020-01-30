// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serializer;

/**
 * Determines the naming policy used to convert a string-based name to another format, such as a camel-casing where the
 * first letter is lower case.
 */
public enum CosmosPropertyNamingPolicy {
    /**
     * No custom naming policy. The property name will be the same as the source.
     */
    Default(0),

    /**
     * First letter in the property name is lower case.
     */
    CamelCase(1);

    public static final int SIZE = java.lang.Integer.SIZE;
    private static java.util.HashMap<Integer, CosmosPropertyNamingPolicy> mappings;
    private int intValue;

    CosmosPropertyNamingPolicy(int value) {
        intValue = value;
        getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static CosmosPropertyNamingPolicy forValue(int value) {
        return getMappings().get(value);
    }

    private static java.util.HashMap<Integer, CosmosPropertyNamingPolicy> getMappings() {
        if (mappings == null) {
            synchronized (CosmosPropertyNamingPolicy.class) {
                if (mappings == null) {
                    mappings = new java.util.HashMap<Integer, CosmosPropertyNamingPolicy>();
                }
            }
        }
        return mappings;
    }
}
