/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.storagecache.v2020_03_01;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines values for CacheIdentityType.
 */
public enum CacheIdentityType {
    /** Enum value SystemAssigned. */
    SYSTEM_ASSIGNED("SystemAssigned"),

    /** Enum value None. */
    NONE("None");

    /** The actual serialized value for a CacheIdentityType instance. */
    private String value;

    CacheIdentityType(String value) {
        this.value = value;
    }

    /**
     * Parses a serialized value to a CacheIdentityType instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed CacheIdentityType object, or null if unable to parse.
     */
    @JsonCreator
    public static CacheIdentityType fromString(String value) {
        CacheIdentityType[] items = CacheIdentityType.values();
        for (CacheIdentityType item : items) {
            if (item.toString().equalsIgnoreCase(value)) {
                return item;
            }
        }
        return null;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
