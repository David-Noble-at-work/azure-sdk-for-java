// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow;

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

    private final long high;
    private final long low;

    private HashCode128(final long low, final long high) {
        this.low = low;
        this.high = high;
    }

    private HashCode128(ByteBuf buffer) {
        this.low = buffer.readLongLE();
        this.high = buffer.readLongLE();
    }

    /**
     * Extracts {@link HashCode128 128-bit hash code} from the first 16 bytes of a byte array.
     *
     * @param buffer a {@code byte} array containing a 128-bit hash code.
     *
     * @return a new {@link HashCode128} instance representing the value extracted from {@code buffer}.
     */
    @NotNull
    public static HashCode128 decode(@NotNull final byte[] buffer) {

        checkNotNull(buffer, "expected non-null buffer");
        checkArgument(buffer.length >= 2 * Long.BYTES, "expected buffer length >= 16, not %s", buffer.length);

        return new HashCode128(Unpooled.wrappedBuffer(buffer));
    }

    /**
     * Extracts a {@link HashCode128 28-bit hash code} from the first 16 bytes of a {@link ByteBuf}.
     * <p>
     * The hash code is read as a pair of long values serialized in little-endian format. The values are read from the
     * buffer's current reader index which is advanced by 16 bytes: the length of two long values.
     *
     * @param buffer The buffer from which to read the hash code.
     *
     * @return a new {@link HashCode128} instance representing the value extracted from {@code buffer}.
     */
    @NotNull
    public static HashCode128 decode(@NotNull final ByteBuf buffer) {

        checkNotNull(buffer, "expected non-null buffer");

        final int length = buffer.writerIndex() - buffer.readerIndex();
        checkArgument(length >= 2 * Long.BYTES, "expected at least 16 readable bytes in buffer, not %s", length);

        return new HashCode128(buffer);
    }

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

    /**
     * Creates a new {@link HashCode128} from two {@code long} values.
     *
     * @param low the least significant 64-bits of the {@link HashCode128}.
     * @param high the most significant 64-bits of the {@link HashCode128}.
     *
     * @return a new {@link HashCode128} instance created from {@code low} and {@code high}.
     */
    @NotNull
    public static HashCode128 of(long low, long high) {
        return new HashCode128(low, high);
    }
}
