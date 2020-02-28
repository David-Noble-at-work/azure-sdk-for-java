// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.core;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.core.Utf8String.transcodeUtf16;

/**
 * A string whose memory representation may be either UTF-8 or UTF-16.
 * <p>
 * This type supports polymorphic use of {@link String} and {@link Utf8String} when equality, hashing, and comparison
 * are needed against either encoding. An API leveraging {@link UtfAnyString} can avoid separate method overloads while
 * still accepting either encoding without imposing additional allocations.
 */
public final class UtfAnyString implements CharSequence, Comparable<UtfAnyString> {

    /**
     * The empty {@link UtfAnyString}.
     */
    public static final UtfAnyString EMPTY = new UtfAnyString("");

    /**
     * The null {@link UtfAnyString}.
     */
    public static final UtfAnyString NULL = new UtfAnyString();

    private static final int NULL_HASHCODE = reduceHashCode(5_381, 5_381);

    private final CharSequence buffer;

    /**
     * Instantiates a new {@link UtfAnyString} from a {@link String string} value.
     *
     * @param value the value.
     */
    public UtfAnyString(final String value) {
        this.buffer = value;
    }

    /**
     * Instantiates a new {@link UtfAnyString} from a {@link Utf8String UTF-8 string} value.
     *
     * @param value the value.
     */
    public UtfAnyString(final Utf8String value) {
        this.buffer = value;
    }

    private UtfAnyString() {
        this.buffer = null;
    }

    /**
     * {@code true} if the {@link UtfAnyString} is empty.
     *
     * @return {@code true} if the {@link UtfAnyString} is empty.
     */
    public boolean isEmpty() {
        return this.buffer != null && this.buffer.length() == 0;
    }

    /**
     * {@code true} if the {@link UtfAnyString} is {@code null}.
     *
     * @return {@code true} if the {@link UtfAnyString} is {@code null}.
     */
    public boolean isNull() {
        return null == this.buffer;
    }

    /**
     * {@code true} if the underlying representation of the {@link UtfAnyString} is a {@link String}.
     *
     * @return {@code true} if the underlying representation of the {@link UtfAnyString} is a {@link String}.
     */
    public boolean isUtf16() {
        return this.buffer instanceof String;
    }

    /**
     * {@code true} if the underlying representation of the {@link UtfAnyString} is a {@link Utf8String}.
     *
     * @return {@code true} if the underlying representation of the {@link UtfAnyString} is a {@link Utf8String}.
     */
    public boolean isUtf8() {
        return this.buffer instanceof Utf8String;
    }

    /**
     * Returns the {@code char} value at the specified {@code index}.
     * <p>
     * An index ranges from zero to {@link UtfAnyString#length()} minus one. The first {@code char} value of the
     * sequence is at index zero, the next at index one, and so on, as for array indexing. If the {@code char} value
     * specified by the {@code index} is a surrogate, the surrogate (not the surrogate pair) is returned.
     *
     * @param index the index of the {@code char} value to be returned.
     *
     * @return the specified {@code char} value.
     *
     * @throws IndexOutOfBoundsException if the {@code index} argument is negative or not less than {@link
     * UtfAnyString#length()}
     * @throws UnsupportedOperationException if this {@link UtfAnyString} is {@code null}.
     */
    @Override
    public char charAt(final int index) {
        if (this.buffer == null) {
            throw new UnsupportedOperationException("String is null");
        }
        return this.buffer.charAt(index);
    }

    /**
     * Compares this {@link UtfAnyString} to a {@link String} value.
     *
     * @param value the {@link String} value.
     *
     * @return a negative integer, zero, or a positive integer as this {@link UtfAnyString} is less than, equal to, or
     * greater than {@code value}.
     */
    @SuppressFBWarnings("ES_COMPARING_PARAMETER_STRING_WITH_EQ")
    public int compareTo(@NotNull final String value) {

        checkNotNull(value, "expected non-null value");

        if (this.buffer == value) {  // ES_COMPARING_PARAMETER_STRING_WITH_EQ is intentional
            return 0;
        }

        if (this.buffer == null) {
            return -1;
        }

        return this.buffer instanceof String
            ? ((String) this.buffer).compareTo(value)
            : ((Utf8String) this.buffer).compareTo(value);
    }

    /**
     * Compares this {@link UtfAnyString} to a {@link Utf8String} value.
     *
     * @param value the {@link Utf8String} value.
     *
     * @return a negative integer, zero, or a positive integer as this {@link UtfAnyString} is less than, equal to, or
     * greater than {@code value}.
     */
    @SuppressFBWarnings("RV_NEGATING_RESULT_OF_COMPARETO")
    public int compareTo(@NotNull final Utf8String value) {

        checkNotNull(value, "expected non-null value");

        if (value == this.buffer) {
            return 0;
        }

        if (this.buffer == null) {
            return -1;
        }

        return this.buffer instanceof String  // RV_NEGATING_RESULT_OF_COMPARETO is intentional
            ? -value.compareTo((String) this.buffer)
            : ((Utf8String) this.buffer).compareTo(value);
    }

    /**
     * Compares this {@link UtfAnyString} to another {@link UtfAnyString} instance.
     *
     * @param other the {@link String} value.
     *
     * @return a negative integer, zero, or a positive integer as this {@link UtfAnyString} is less than, equal to, or
     * greater than the {@code other} one.
     */
    @Override
    @SuppressFBWarnings("RV_NEGATING_RESULT_OF_COMPARETO")
    public int compareTo(@NotNull final UtfAnyString other) {

        checkNotNull(other, "expected non-null other");

        if (other.buffer == this.buffer) {
            return 0;
        }

        if (other.buffer == null) {
            return 1;
        }

        if (this.buffer == null) {
            return -1;
        }

        if (this.buffer instanceof String) {
            return other.buffer instanceof String  // RV_NEGATING_RESULT_OF_COMPARETO is intentional
                ? ((String) this.buffer).compareTo((String) other.buffer)
                : -((Utf8String) other.buffer).compareTo((String) this.buffer);
        }

        return ((Utf8String) this.buffer).compareTo((Utf8String) other.buffer);
    }

    /**
     * Returns the empty {@link UtfAnyString}.
     *
     * The value returned is a singleton. There is one and only one empty {@link UtfAnyString}.
     *
     * @return the empty {@link UtfAnyString}.
     */
    public static UtfAnyString empty() {
        return EMPTY;
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (null == other) {
            return false;
        }

        if (other.getClass() == UtfAnyString.class) {
            return this.equals((UtfAnyString) other);
        }

        return false;
    }

    /**
     * Indicates whether the value of some {@link String} is equal to the value of this {@link UtfAnyString}.
     *
     * @param value the {@link String} to compare for equality.
     *
     * @return {@code true} if the value of this {@link UtfAnyString} equals {@code value}.
     */
    public boolean equals(final String value) {

        if (null == this.buffer) {
            return null == value;
        }

        if (this.buffer instanceof String) {
            return value.contentEquals(this.buffer);  // skips the type check that String.equals performs
        }

        return ((Utf8String) this.buffer).equals(value);
    }

    /**
     * Indicates whether the value of some {@link Utf8String} is equal to the value of this {@link UtfAnyString}.
     *
     * @param value the {@link Utf8String} to compare for equality.
     *
     * @return {@code true} if the value of this {@link UtfAnyString} equals {@code value}.
     */
    public boolean equals(final Utf8String value) {

        if (null == value) {
            return null == this.buffer;
        }

        return value.equals(this.buffer);
    }

    /**
     * Indicates whether some other {@link UtfAnyString} is equal to this one.
     *
     * @param other the other.
     *
     * @return {@code true} if this {@link UtfAnyString} equals {@code other}.
     */
    public boolean equals(final UtfAnyString other) {

        if (null == other) {
            return false;
        }

        if (null == this.buffer) {
            return null == other.buffer;
        }

        return this.buffer instanceof String ? other.buffer.equals(this.buffer) : this.buffer.equals(other.buffer);
    }

    @Override
    public int hashCode() {

        if (this.buffer == null) {
            return NULL_HASHCODE;
        }

        if (this.buffer instanceof String) {

            final long[] hash = { 5_381, 5_381 };

            this.buffer.codePoints().reduce(0, (index, codePoint) -> {
                if (index % 2 == 0) {
                    hash[0] = ((hash[0] << 5) + hash[0]) ^ codePoint;
                } else {
                    hash[1] = ((hash[1] << 5) + hash[1]) ^ codePoint;
                }
                return index;
            });

            return reduceHashCode(hash[0], hash[1]);
        }

        return this.buffer.hashCode();
    }

    /**
     * Returns the length of this character sequence.
     * <p>
     * The length is the number of 16-bit {@code char}s in the sequence.
     *
     * @return the number of {@code char}s in this sequence.
     *
     * @throws UnsupportedOperationException if this {@link UtfAnyString} is {@code null}.
     */
    @Override
    public int length() {
        if (this.buffer == null) {
            throw new UnsupportedOperationException("String is null");
        }
        return this.buffer.length();
    }

    /**
     * Returns a {@code CharSequence} that is a subsequence of this sequence.
     * <p>
     * The subsequence starts with the {@code char} value at the specified index and ends with the{@code char} value at
     * index {@code end - 1}. The length (in {@code char}s) of the returned sequence is {@code end - start}, so if
     * {@code start == end}, an empty sequence is returned.
     *
     * @param start the start index, inclusive.
     * @param end the end index, exclusive.
     *
     * @return the specified subsequence.
     *
     * @throws IndexOutOfBoundsException if {@code start} or {@code end} are negative, {@code end} is greater than
     * {@link UtfAnyString#length}, or {@code start} is greater than {@code end}.
     * @throws UnsupportedOperationException if string is {@code null}.
     */
    @Override
    @NotNull
    public CharSequence subSequence(final int start, final int end) {
        if (this.buffer == null) {
            throw new UnsupportedOperationException("String is null");
        }
        return this.buffer.subSequence(start, end);
    }

    @Override
    @NotNull
    public String toString() {
        return String.valueOf(this.buffer);
    }

    /**
     * To utf 16 string.
     *
     * @return the string
     */
    public String toUtf16() {
        if (null == this.buffer) {
            return null;
        }
        return this.buffer instanceof String ? (String) this.buffer : this.buffer.toString();
    }

    /**
     * To utf 8 utf 8 string.
     *
     * @return the utf 8 string
     */
    public Utf8String toUtf8() {
        if (null == this.buffer) {
            return null;
        }
        return this.buffer instanceof String ? transcodeUtf16((String) this.buffer) : (Utf8String) this.buffer;
    }

    private static int reduceHashCode(final long h1, final long h2) {
        return Long.valueOf(h1 + (h2 * 1_566_083_941L)).intValue();
    }
}
