// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.search.documents.indexes.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Emits the entire input as a single token. This tokenizer is implemented
 * using Apache Lucene.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@odata.type")
@JsonTypeName("#Microsoft.Azure.Search.KeywordTokenizer")
@Fluent
public final class KeywordTokenizer extends LexicalTokenizer {
    /*
     * The read buffer size in bytes. Default is 256.
     */
    @JsonProperty(value = "bufferSize")
    private Integer bufferSize;

    /**
     * Get the bufferSize property: The read buffer size in bytes. Default is
     * 256.
     *
     * @return the bufferSize value.
     */
    public Integer getBufferSize() {
        return this.bufferSize;
    }

    /**
     * Set the bufferSize property: The read buffer size in bytes. Default is
     * 256.
     *
     * @param bufferSize the bufferSize value to set.
     * @return the KeywordTokenizer object itself.
     */
    public KeywordTokenizer setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }
}
