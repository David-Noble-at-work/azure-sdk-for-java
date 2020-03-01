// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow;

import com.azure.cosmos.implementation.Json;
import com.azure.cosmos.implementation.base.Strings;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.implementation.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * An immutable 128-bit hash code.
 * <p>
 * The hash code is represented by two {@code long} values: {@link #low()} and {@link #high()}.
 */
public final class HashCode128 {

    public static final int BYTES = 2 * Long.BYTES;

    @JsonProperty
    private final long high;

    @JsonProperty
    private final long low;

    private HashCode128(final long high, final long low) {
        this.high = high;
        this.low = low;
    }

    private HashCode128(ByteBuf buffer) {
        this.low = buffer.readLongLE();
        this.high = buffer.readLongLE();
    }

    // region Accessors

    /**
     * Returns the first 8 bytes (64-bits) of this {@link HashCode128}.
     *
     * @return the first 8 bytes (64-bits) of this {@link HashCode128}.
     */
    public long high() {
        return this.high;
    }

    /**
     * Returns the second 8 bytes (64-bits) of this {@link HashCode128}.
     *
     * @return the second 8 bytes (64-bits) of this {@link HashCode128}.
     */
    public long low() {
        return this.low;
    }

    // endregion

    /**
     * Extracts {@link HashCode128 128-bit hash code} from the first 16 bytes of a byte array.
     *
     * @param buffer a {@code byte} array containing a 128-bit hash code.
     *
     * @return a new {@link HashCode128} instance representing the value extracted from {@code buffer}.
     */
    @NotNull
    public static HashCode128 decode(@NotNull final byte[] buffer) {
        return decode(buffer, 0);
    }

    /**
     * Extracts {@link HashCode128 128-bit hash code} from the first 16 bytes of a byte array.
     *
     * @param buffer a {@code byte} array containing a 128-bit hash code.
     * @param offset an offset into {@code buffer}.
     *
     * @return a new {@link HashCode128} instance representing the value extracted from {@code buffer}.
     */
    @NotNull
    public static HashCode128 decode(@NotNull final byte[] buffer, int offset) {
        checkNotNull(buffer, "expected non-null buffer");
        return new HashCode128(Unpooled.wrappedBuffer(buffer, offset, offset + BYTES));
    }

    // region Methods

    /**
     * Extracts a {@link HashCode128 128-bit hash code} from the first 16 bytes of a {@link ByteBuf}.
     * <p>
     * The hash code is read as a pair of long values serialized in little-endian format. The values are read from the
     * current reader index of the input {@link ByteBuf}. On return the current the current reader index is advanced by
     * 16 bytes: the length of two {@code long} values.
     *
     * @param in the {@link ByteBuf} from which to read the {@link HashCode128 128-bit hash code} .
     *
     * @return a new {@link HashCode128} instance representing the value extracted from {@code in}.
     */
    @NotNull
    public static HashCode128 decode(@NotNull final ByteBuf in) {

        checkNotNull(in, "expected non-null in");
        checkArgument(in.readableBytes() >= BYTES, "expected at least 16 readable bytes in in, not %s",
            in.readableBytes());

        return new HashCode128(in);
    }

    /**
     * Writes this {@link HashCode128} to a {@code byte} array in little-endian byte order.
     *
     * @param buffer a {@link byte} array.
     * @param offset offset into {@code buffer}.
     *
     * @return a reference to this {@link HashCode128} instance.
     */
    @NotNull
    public HashCode128 encode(@NotNull final byte[] buffer, final int offset) {

        checkNotNull(buffer, "expected non-null out");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        checkArgument(offset + BYTES <= buffer.length, "expected buffer length >= %s, not %s",
            offset + BYTES,
            buffer.length);

        this.encode(Unpooled.wrappedBuffer(buffer, offset, offset + BYTES));
        return this;
    }

    /**
     * Writes this {@link HashCode128} to a {@link ByteBuf} in little-endian byte order.
     *
     * @param out the {@link ByteBuf}.
     *
     * @return a reference to {@code out}.
     */
    @NotNull
    public ByteBuf encode(@NotNull final ByteBuf out) {
        checkNotNull(out, "expected non-null out");
        return out.writeLongLE(this.low()).writeLongLE(this.high());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HashCode128 that = (HashCode128) o;

        if (high != that.high) {
            return false;
        }
        return low == that.low;
    }

    @Override
    public int hashCode() {
        int result = (int) (high ^ (high >>> 32));
        result = 31 * result + (int) (low ^ (low >>> 32));
        return result;
    }

    /**
     * Creates a new {@link HashCode128} from two {@code long} values.
     *
     * @param low the least significant 64-bits of the {@link HashCode128}.
     * @param high the most significant 64-bits of the {@link HashCode128}.
     *
     * @return a new {@link HashCode128} instance created from {@code low} and {@code high}.
     */
    @NotNull
    public static HashCode128 of(long high, long low) {
        return new HashCode128(high, low);
    }

    @NotNull
    public String toHexString() {
        return Long.toHexString(this.high) + Strings.padStart(Long.toHexString(this.low), 16, '0');
    }

    @NotNull
    @Override
    public String toString() {
        return Json.toString(this);
    }

    @NotNull byte[] asBytes() {
        return new byte[] {
            (byte) this.low,
            (byte) (this.low >> 8),
            (byte) (this.low >> 16),
            (byte) (this.low >> 24),
            (byte) (this.low >> 32),
            (byte) (this.low >> 40),
            (byte) (this.low >> 48),
            (byte) (this.low >> 56),
            (byte) this.high,
            (byte) (this.high >> 8),
            (byte) (this.high >> 16),
            (byte) (this.high >> 24),
            (byte) (this.high >> 32),
            (byte) (this.high >> 40),
            (byte) (this.high >> 48),
            (byte) (this.high >> 56)
        };
    }

    // endregion
}
