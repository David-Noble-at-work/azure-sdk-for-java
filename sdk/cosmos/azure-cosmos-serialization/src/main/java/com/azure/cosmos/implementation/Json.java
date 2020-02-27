// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.base.Strings.lenientFormat;

/**
 * This class consists of static utility methods for reading and writing JSON values.
 */
public final class Json {

    private Json() {
    }

    // region Fields

    private static final Logger logger = LoggerFactory.getLogger(Json.class);

    private static final ObjectMapper mapper = new ObjectMapper(new JsonFactory()
        .enable(JsonParser.Feature.ALLOW_COMMENTS));

    private static final ObjectReader reader = mapper.reader()
        .withFeatures(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

    private static final ObjectWriter writer = mapper.writer()
        .withFeatures(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

    // endregion

    // region Methods

    /**
     * Reads an {@link Optional optional} JSON value from a {@link File file}.
     *
     * @param <T> the type of the value to be read.
     * @param file a file containing the value to be read.
     * @param type the {@link Class class} representing the type of the value to be read.
     *
     * @return the {@link Optional optional} value read.
     */
    public static <T> Optional<T> parse(File file, Class<T> type) {

        checkNotNull(file, "expected non-null file");
        checkNotNull(type, "expected non-null type");

        try {
            return Optional.of(reader.forType(type).readValue(file));
        } catch (IOException error) {
            logger.error("failed to parse {} due to ", type.getName(), error);
            return Optional.empty();
        }
    }

    /**
     * Reads an {@link Optional optional} JSON value from a {@link File file}.
     *
     * @param <T> the type of the value to be read.
     * @param stream the {@link InputStream input stream} from which to read the value.
     * @param type the {@link Class class} representing the type of the value to be read.
     *
     * @return the {@link Optional optional} value read.
     */
    @NotNull
    public static <T> Optional<T> parse(@NotNull InputStream stream, @NotNull Class<T> type) {

        checkNotNull(stream, "expected non-null stream");
        checkNotNull(type, "expected non-null type");

        try {
            return Optional.of(reader.forType(type).readValue(stream));
        } catch (IOException error) {
            logger.error("failed to parse {} due to ", type.getName(), error);
            return Optional.empty();
        }
    }

    /**
     * Reads an {@link Optional optional} JSON value from a {@link String string}.
     *
     * @param <T> the type of the value to be read.
     * @param value the {@link String string} from which to read the value.
     * @param type the {@link Class class} representing the type of the value to be read.
     *
     * @return the {@link Optional optional} value read.
     */
    @NotNull
    public static <T> Optional<T> parse(@NotNull String value, @NotNull Class<T> type) {

        checkNotNull(value, "expected non-null value");
        checkNotNull(type, "expected non-null type");

        try {
            return Optional.of(reader.forType(type).readValue(value));
        } catch (IOException error) {
            logger.error("", error);
            return Optional.empty();
        }
    }

    /**
     * Returns a JSON string representation of an {@link Object object}.
     *
     * @param object an {@link Object object}
     *
     * @return a JSON string representation of {@code object}.
     */
    @NotNull
    public static String toString(@Nullable Object object) {
        try {
            return writer.writeValueAsString(object);
        } catch (JsonProcessingException error) {
            return lenientFormat("{\"error\": \"%s\"}", error);
        }
    }

    // endregion
}
