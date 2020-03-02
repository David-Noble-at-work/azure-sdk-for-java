// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.serializer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * {@link CosmosSerializer} implementation for Jackson.
 * <p>
 * This is an internal class designed to reduce exposure of Jackson types to make it is easier to utilize alternative
 * serialization libraries.
 */
final class CosmosJacksonSerializer implements CosmosSerializer {

    final ObjectMapper objectMapper;
    final ObjectReader reader;
    final ObjectWriter writer;

    /**
     * Constructs a new {@link CosmosJacksonSerializer}.
     *
     * @param serializationOptions reference to serialization options or {@code null}.
     *
     * @see PropertyNamingStrategy
     */
    CosmosJacksonSerializer(@Nullable final CosmosSerializationOptions serializationOptions) {

        this.objectMapper = new ObjectMapper();

        if (serializationOptions != null) {

            if (serializationOptions.getIgnoreNullValues()) {
                this.objectMapper.setSerializationInclusion(Include.NON_NULL);
            }

            if (serializationOptions.getIndented()) {
                this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            }

            switch (serializationOptions.getPropertyNamingPolicy()) {
                case DEFAULT:
                    break;
                case CAMEL_CASE:
                    this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
                    break;
            }
        }

        this.reader = this.objectMapper.reader();
        this.writer = this.objectMapper.writer();
    }

    /**
     * Constructs a new {@link CosmosJacksonSerializer}.
     *
     * @param objectMapper reference to a custom {@link ObjectMapper object mapper}.
     *
     * @see PropertyNamingStrategy
     */
    CosmosJacksonSerializer(@Nonnull final ObjectMapper objectMapper) {
        checkNotNull(objectMapper, "expected non-null objectMapper");
        this.objectMapper = objectMapper;
        this.reader = this.objectMapper.reader();
        this.writer = this.objectMapper.writer();
    }

    /**
     * Deserializes an object from a stream of UTF-8 encoded bytes.
     *
     * @param <T> the type of object to be deserialized.
     * @param inputStream a stream of UTF-8 encoded bytes.
     * @param type a class representing the type of object to be deserialized.
     *
     * @return The object deserialized from the {@code inputStream}.
     *
     * @throws IOException if an input error occurs.
     */
    @Override
    public <T> T fromStream(@Nonnull final InputStream inputStream, @Nonnull final  Class<T> type) throws IOException {
        checkNotNull(inputStream, "expected non-null inputStream");
        checkNotNull(type, "expected non-null type");
        return this.reader.forType(type).readValue(inputStream);
    }

    /**
     * Serializes an object to an {@link OutputStream output stream}.
     *
     * @param <T> the type of object to be serialized.
     * @param object the object to be serialized.
     *
     * @return an {@link InputStream input stream} for reading the serialized {@code object}.
     *
     * @throws IOException if an output error occurs.
     */
    @Override
    public <T> InputStream toStream(@Nonnull T object) throws IOException {

        checkNotNull(object, "expected non-null object");

        ByteBuf buffer = Unpooled.buffer(1024);
        OutputStream outputStream = new ByteBufOutputStream(buffer);
        this.writer.forType(object.getClass()).writeValue(outputStream, object);

        return new ByteBufInputStream(buffer, true);
    }
}
