// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.
// Changes may cause incorrect behavior and will be lost if the code is
// regenerated.

package com.azure.search.documents.implementation.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines values for AutocompleteMode.
 */
public enum AutocompleteMode {
    /**
     * Enum value oneTerm.
     */
    ONE_TERM("oneTerm"),

    /**
     * Enum value twoTerms.
     */
    TWO_TERMS("twoTerms"),

    /**
     * Enum value oneTermWithContext.
     */
    ONE_TERM_WITH_CONTEXT("oneTermWithContext");

    /**
     * The actual serialized value for a AutocompleteMode instance.
     */
    private final String value;

    AutocompleteMode(String value) {
        this.value = value;
    }

    /**
     * Parses a serialized value to a AutocompleteMode instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed AutocompleteMode object, or null if unable to parse.
     */
    @JsonCreator
    public static AutocompleteMode fromString(String value) {
        AutocompleteMode[] items = AutocompleteMode.values();
        for (AutocompleteMode item : items) {
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
