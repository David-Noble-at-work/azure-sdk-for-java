// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.serializer;

import com.azure.cosmos.batch.json.JsonNavigator;
import com.azure.cosmos.batch.json.JsonWriter;
import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

public final class CosmosSerializationFormatOptions {

    private final String contentSerializationFormat;
    private final Function<ByteBuf, JsonNavigator> createCustomNavigator;
    private final Supplier<JsonWriter> createCustomWriter;

    public CosmosSerializationFormatOptions(
        @NotNull final String contentSerializationFormat,
        @NotNull final Function<ByteBuf, JsonNavigator> createCustomNavigator,
        @NotNull final Supplier<JsonWriter> createCustomWriter) {

        checkNotNull(contentSerializationFormat, "expected non-null contentSerializationFormat");
        checkNotNull(createCustomNavigator, "expected non-null createCustomNavigator");
        checkNotNull(createCustomWriter, "expected non-null createCustomWriter");

        this.contentSerializationFormat = contentSerializationFormat;
        this.createCustomNavigator = createCustomNavigator;
        this.createCustomWriter = createCustomWriter;
    }

    /**
     * Gets the content serialization format to request in the response from a Cosmos DB backend.
     *
     * @return the content serialization format to request in the response from a Cosmos DB backend.
     */
    public String getContentSerializationFormat() {
        return this.contentSerializationFormat;
    }

    /**
     * Gets the function for creating a {@link JsonNavigator} over the contents of a {@link ByteBuf}.
     * <p>
     * Any {@link ByteBuf} provided as input to this function must be in this {@link #getContentSerializationFormat
     * contentSeralizationFormat}.
     *
     * @return the function for creating a {@link JsonNavigator} over the contents of a {@link ByteBuf}.
     */
    public Function<ByteBuf, JsonNavigator> getCreateCustomNavigator() {
        return this.createCustomNavigator;
    }

    /**
     * Get the supplier of a {@link JsonWriter}.
     * <p>
     * The writer produces content in this {@link #getContentSerializationFormat contentSerializationFormat}.
     *
     * @return the function for creating a {@link JsonWriter}.
     */
    public Supplier<JsonWriter> getCreateCustomWriter() {
        return this.createCustomWriter;
    }
}
