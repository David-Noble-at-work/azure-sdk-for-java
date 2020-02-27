// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import static com.azure.cosmos.base.Preconditions.checkArgument;

/**
 * The type Layout bit.
 */
public final class LayoutBit {
    /**
     * The empty bit.
     */
    public static final LayoutBit INVALID = new LayoutBit(-1);

    private final int index;

    /**
     * Initializes a new instance of the {@link LayoutBit} class.
     *
     * @param index The zero-based offset into the layout bitmask.
     */
    public LayoutBit(int index) {
        checkArgument(index >= -1);
        this.index = index;
    }

    /**
     * Is invalid boolean.
     *
     * @return the boolean
     */
    public boolean isInvalid() {
        return this.index == INVALID.index;
    }

    /**
     * Zero-based bit from the beginning of the byte that contains this bit.
     * <p>
     * Also see {@link #offset(int)} to identify relevant byte.
     *
     * @return The bit of the byte within the bitmask.
     */
    public int bit() {
        return this.index() % Byte.SIZE;
    }

    /**
     * Compute the division rounding up to the next whole number
     *
     * @param numerator the numerator to divide.
     * @param divisor the divisor to divide by.
     *
     * @return the {@code ceiling(numerator/divisor)}.
     */
    public static int divCeiling(int numerator, int divisor) {
        return (numerator + (divisor - 1)) / divisor;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof LayoutBit && this.equals((LayoutBit) other);
    }

    /**
     * Equals boolean.
     *
     * @param other the other
     *
     * @return the boolean
     */
    public boolean equals(LayoutBit other) {
        return other != null && this.index() == other.index();
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(this.index()).hashCode();
    }

    /**
     * Zero-based offset into the layout bitmask.
     *
     * @return zero -based offset into the layout bitmask.
     */
    public int index() {
        return this.index;
    }

    /**
     * Returns the zero-based byte offset from the beginning of the row or scope that contains the bit from the
     * bitmask.
     * <p>
     * Also see {@link #bit()} to identify.
     *
     * @param offset The byte offset from the beginning of the row where the scope begins.
     *
     * @return The byte offset containing this bit.
     */
    public int offset(int offset) {
        return offset + (this.index() / Byte.SIZE);
    }

    /**
     * Allocates layout bits from a bitmask.
     */
    static class Allocator {
        /**
         * The next bit to allocate.
         */
        private int next;

        /**
         * Initializes a new instance of the {@link Allocator} class.
         */
        Allocator() {
            this.next = 0;
        }

        /**
         * Allocates a new bit from the bitmask.
         *
         * @return The allocated bit.
         */
        final LayoutBit allocate() {
            return new LayoutBit(this.next++);
        }

        /**
         * The number of bytes needed to hold all bits so far allocated.
         *
         * @return the int
         */
        final int numBytes() {
            return LayoutBit.divCeiling(this.next, Byte.SIZE);
        }
    }
}
