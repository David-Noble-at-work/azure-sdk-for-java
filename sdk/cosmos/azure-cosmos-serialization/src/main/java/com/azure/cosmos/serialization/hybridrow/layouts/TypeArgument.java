// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.implementation.Json;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * The type Type argument.
 */
public final class TypeArgument {

    /**
     * The constant NONE.
     */
    public static final TypeArgument NONE = new TypeArgument();

    private final LayoutType type;

    @JsonProperty
    private final TypeArgumentList typeArgs;

    /**
     * Initializes a new instance of the {@link TypeArgument} struct.
     *
     * @param type the type of the constraint.
     */
    public TypeArgument(@NotNull LayoutType type) {
        checkNotNull(type, "expected non-null type");
        this.type = type;
        this.typeArgs = TypeArgumentList.EMPTY;
    }

    /**
     * Initializes a new instance of the {@link TypeArgument} struct.
     *
     * @param type The type of the constraint.
     * @param typeArgs For generic types the type parameters.
     */
    public TypeArgument(@NotNull LayoutType type, @NotNull TypeArgumentList typeArgs) {
        checkNotNull(type, "expected non-null type");
        checkNotNull(type, "expected non-null typeArgs");
        this.type = type;
        this.typeArgs = typeArgs;
    }

    private TypeArgument() {
        this.type = null;
        this.typeArgs = null;
    }

    @Override
    public boolean equals(Object other) {
        if (null == other) {
            return false;
        }
        return other.getClass() == TypeArgument.class && this.equals((TypeArgument) other);
    }

    /**
     * Equals boolean.
     *
     * @param other the other
     *
     * @return the boolean
     */
    public boolean equals(TypeArgument other) {
        return this.type.equals(other.type) && this.typeArgs.equals(other.typeArgs);
    }

    @Override
    public int hashCode() {
        return (this.type.hashCode() * 397) ^ this.typeArgs.hashCode();
    }

    @Override
    public String toString() {
        return Json.toString(this);
    }

    /**
     * The physical layout type.
     *
     * @return the physical layout type.
     */
    public LayoutType type() {
        return this.type;
    }

    /**
     * If the type argument is itself generic, then its type arguments.
     *
     * @return it the type argument is itself generic, then its type arguments.
     */
    public TypeArgumentList typeArgs() {
        return this.typeArgs;
    }
}
