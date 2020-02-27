// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Json;
import com.azure.cosmos.serialization.hybridrow.SchemaId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.azure.cosmos.base.Preconditions.checkNotNull;

/**
 * The type Type argument list.
 */
@JsonSerialize(using = TypeArgumentList.JsonSerializer.class)
public final class TypeArgumentList {

    /**
     * The constant EMPTY.
     */
    public static final TypeArgumentList EMPTY = new TypeArgumentList();

    private final TypeArgument[] args;
    private final SchemaId schemaId;

    /**
     * Initializes a new instance of the {@link TypeArgumentList} class.
     *
     * @param args arguments in the list.
     */
    public TypeArgumentList(@NotNull final TypeArgument... args) {
        checkNotNull(args);
        this.args = args;
        this.schemaId = SchemaId.INVALID;
    }

    /**
     * Initializes a new instance of the {@link TypeArgumentList} class
     *
     * @param schemaId for UDT fields, the schema id of the nested layout
     */
    public TypeArgumentList(@NotNull final SchemaId schemaId) {
        checkNotNull(schemaId);
        this.args = EMPTY.args;
        this.schemaId = schemaId;
    }

    private TypeArgumentList() {
        this.args = new TypeArgument[] {};
        this.schemaId = SchemaId.INVALID;
    }

    /**
     * Number of elements in this {@link TypeArgumentList}
     * <p>
     *
     * @return number of arguments in the list
     */
    public int count() {
        return this.args.length;
    }

    /**
     * Equals boolean.
     *
     * @param other the other
     *
     * @return the boolean
     */
    public boolean equals(TypeArgumentList other) {
        if (null == other) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return this.schemaId().equals(other.schemaId()) && Arrays.equals(this.args, other.args);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TypeArgumentList && this.equals((TypeArgumentList) other);
    }

    /**
     * Element at the specified position in this {@link TypeArgumentList}
     * <p>
     *
     * @param index index of the element to return
     *
     * @return element at the specified position in this {@link TypeArgumentList}
     */
    public TypeArgument get(int index) {
        return this.args[index];
    }

    @Override
    public int hashCode() {

        int hash = 19;
        hash = (hash * 397) ^ this.schemaId().hashCode();

        for (TypeArgument a : this.args) {
            hash = (hash * 397) ^ a.hashCode();
        }

        return hash;
    }

    /**
     * List list.
     *
     * @return the list
     */
    public List<TypeArgument> list() {
        return Collections.unmodifiableList(Arrays.asList(this.args));
    }

    /**
     * For UDT fields, the schema id of the nested layout.
     *
     * @return for UDT fields, the Schema ID of the nested layout.
     */
    public SchemaId schemaId() {
        return this.schemaId;
    }

    /**
     * Stream for iterating over elements in this {@link TypeArgumentList}
     * <p>
     *
     * @return a stream for iterating over elements in this {@link TypeArgumentList}
     */
    public Stream<TypeArgument> stream() {
        if (this.args.length == 0) {
            return Stream.empty();
        }
        return StreamSupport.stream(Arrays.spliterator(this.args), false);
    }

    @Override
    public String toString() {
        return Json.toString(this);
    }

    /**
     * The type Json serializer.
     */
    static final class JsonSerializer extends StdSerializer<TypeArgumentList> {

        private static final long serialVersionUID = 3342591303413986726L;

        private JsonSerializer() {
            super(TypeArgumentList.class);
        }

        @Override
        public void serialize(
            final TypeArgumentList value,
            final JsonGenerator generator,
            final SerializerProvider provider) throws IOException {

            generator.writeStartObject();
            generator.writeObjectField("schemaId", value.schemaId);
            generator.writeArrayFieldStart("args");

            for (TypeArgument element : value.args) {
                generator.writeString(element.toString());
            }

            generator.writeEndArray();
        }
    }
}
