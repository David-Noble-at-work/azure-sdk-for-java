// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.search.documents.indexes.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Base type for character filters.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@odata.type",
    defaultImpl = CharFilter.class)
@JsonTypeName("CharFilter")
@JsonSubTypes({
    @JsonSubTypes.Type(name = "#Microsoft.Azure.Search.MappingCharFilter", value = MappingCharFilter.class),
    @JsonSubTypes.Type(name = "#Microsoft.Azure.Search.PatternReplaceCharFilter",
        value = PatternReplaceCharFilter.class)
})
@Fluent
public abstract class CharFilter {
    /*
     * The name of the char filter. It must only contain letters, digits,
     * spaces, dashes or underscores, can only start and end with alphanumeric
     * characters, and is limited to 128 characters.
     */
    @JsonProperty(value = "name", required = true)
    private String name;

    /**
     * Get the name property: The name of the char filter. It must only contain
     * letters, digits, spaces, dashes or underscores, can only start and end
     * with alphanumeric characters, and is limited to 128 characters.
     *
     * @return the name value.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name property: The name of the char filter. It must only contain
     * letters, digits, spaces, dashes or underscores, can only start and end
     * with alphanumeric characters, and is limited to 128 characters.
     *
     * @param name the name value to set.
     * @return the CharFilter object itself.
     */
    public CharFilter setName(String name) {
        this.name = name;
        return this;
    }
}
