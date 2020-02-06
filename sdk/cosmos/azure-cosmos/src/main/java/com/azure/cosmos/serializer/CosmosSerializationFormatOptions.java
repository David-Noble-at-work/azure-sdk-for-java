// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serializer;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public final class CosmosSerializationFormatOptions {

    private final String contentSerializationFormat;
    private final Function<ByteBuf, IJsonNavigator> createCustomNavigator;
    private final Supplier<IJsonWriter> createCustomWriter;

    public CosmosSerializationFormatOptions(
        @Nonnull final String contentSerializationFormat,
        @Nonnull final Function<ByteBuf, IJsonNavigator> createCustomNavigator,
        @Nonnull final Supplier<IJsonWriter> createCustomWriter) {

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
     * Gets the function for creating a {@link IJsonNavigator} over the contents of a {@link ByteBuf}.
     * <p>
     * Any {@link ByteBuf} provided as input to this function must be in this {@link #getContentSerializationFormat
     * contentSeralizationFormat}.
     *
     * @return the function for creating a {@link IJsonNavigator} over the contents of a {@link ByteBuf}.
     */
    public Function<ByteBuf, IJsonNavigator> getCreateCustomNavigator() {
        return this.createCustomNavigator;
    }

    /**
     * Get the supplier of a {@link IJsonWriter}.
     * <p>
     * The writer produces content in this {@link #getContentSerializationFormat contentSerializationFormat}.
     *
     * @return the function for creating a {@link IJsonWriter}.
     */
    public Supplier<IJsonWriter> getCreateCustomWriter() {
        return this.createCustomWriter;
    }
}
