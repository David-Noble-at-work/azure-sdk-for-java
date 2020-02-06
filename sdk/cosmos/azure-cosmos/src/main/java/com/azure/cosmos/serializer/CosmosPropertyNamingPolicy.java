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
    DEFAULT,

    /**
     * First letter in the property name is lower case.
     */
    CAMEL_CASE;
}
