// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.core;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A container object which may or may not contain a non-null value.
 *
 * @param <T> type of the referent.
 */
public final class Out<T> {

    private volatile T value;

    /**
     * If a value is present, invoke the specified consumer with the value, otherwise do nothing.
     *
     * @param consumer block to be executed if a value is present.
     */
    public void ifPresent(Consumer<T> consumer) {
        if (this.value != null) {
            consumer.accept(this.value);
        }
    }

    /**
     * {@code true} if there is a value present, otherwise {@code false}
     * <p>
     * This is equivalent to evaluating the expression {@code out.get() == null}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return this.value != null;
    }

    /**
     * Indicates whether some other object is equal to this {@link Out} value.
     * <p>
     * The other object is considered equal if:
     * <ul>
     * <li>it is also an {@link Out} and;
     * <li>both instances have no value present or;
     * <li>the present values are equal to each other as determined by {@code T.equals(Object)}}.
     * </ul>
     *
     * @param other an object to be tested for equality
     *
     * @return {code true} if the other object is equal to this object; otherwise {@code false}
     */
    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (other.getClass() != Out.class) {
            return false;
        }

        return Objects.equals(this.value, ((Out<?>) other).value);
    }

    /**
     * If a value is present in this {@link Out}, returns the value, otherwise throws {@link NoSuchElementException}.
     *
     * @return the value of this {@link Out}.
     *
     * @throws NoSuchElementException if the value of this {@link Out} is not present.
     */
    public T get() {
        if (this.value == null) {
            throw new NoSuchElementException("expected non-null value");
        }
        return this.value;
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    /**
     * Assigns a value to this {@link Out}.
     *
     * @param value a {@link Nullable nullable} value to assign to this {@link Out}.
     *
     * @return a reference to this {@link Out}.
     */
    public Out<T> set(@Nullable T value) {
        this.value = value;
        return this;
    }

    /**
     * Assigns a value to this {@link Out} and returns the value assigned.
     *
     * @param value a {@link Nullable nullable} value to assign to this {@link Out}.
     *
     * @return the {@code value} assigned, which may be {@code null}.
     */
    public T setAndGet(T value) {
        this.value = value;
        return value;
    }

    @Override
    public String toString() {
        return this.value == null ? "null" : this.value.toString();
    }
}
