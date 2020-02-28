// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.implementation;

import com.azure.cosmos.implementation.base.Utf8;
import com.azure.cosmos.core.Utf8String;
import com.azure.cosmos.implementation.hash.HashCode;
import com.azure.cosmos.implementation.hash.HashFunction;
import com.azure.cosmos.implementation.hash.Hasher;
import com.azure.cosmos.implementation.hash.Hashing;
import com.azure.cosmos.serialization.hybridrow.HashCode128;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.base.Strings.lenientFormat;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Murmur3Hash for x86_64 (little endian).
 *
 * @see <a href="https://en.wikipedia.org/wiki/MurmurHash">MurmurHash</a>
 */
public final class Murmur3Hash {

    private static final ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;

    /**
     * Computes a 128-bit Murmur3Hash 128-bit value for a data item.
     *
     * @param item The data to hash
     * @param seed The seed with which to initialize
     *
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance
     */
    public static HashCode128 hash128(@NotNull final String item, @NotNull final HashCode128 seed) {

        checkNotNull(item, "expected non-null item");
        checkNotNull(seed, "expected non-null seed");

        if (item.isEmpty()) {
            return hash128(Constant.EMPTY_STRING, seed);
        }

        Utf8String value = Utf8String.transcodeUtf16(item);

        try {
            return hash128(value.content(), seed);
        } finally {
            value.release();
        }
    }

    /**
     * Computes a 128-bit Murmur3Hash 128-bit value for a {@code boolean} data item.
     *
     * @param item The data to hash.
     * @param seed The seed with which to initialize.
     *
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance.
     */
    public static HashCode128 hash128(final boolean item, final HashCode128 seed) {
        return Murmur3Hash.hash128(item ? Constant.TRUE : Constant.FALSE, seed);
    }

    public static HashCode128 hash128(short item, HashCode128 seed) {
        ByteBuf buffer = Unpooled.wrappedBuffer(new byte[Integer.BYTES]).writeShortLE(item);
        return Murmur3Hash.hash128(buffer, seed);
    }

    public static HashCode128 hash128(byte item, HashCode128 seed) {
        ByteBuf buffer = Unpooled.wrappedBuffer(new byte[Integer.BYTES]).writeByte(item);
        return Murmur3Hash.hash128(buffer, seed);
    }

    public static HashCode128 hash128(int item, HashCode128 seed) {
        ByteBuf buffer = Unpooled.wrappedBuffer(new byte[Integer.BYTES]).writeIntLE(item);
        return Murmur3Hash.hash128(buffer, seed);
    }

    /**
     * Computes a 128-bit Murmur3Hash 128-bit value for a {@link ByteBuf} data item.
     *
     * @param item The data to hash
     * @param seed The seed with which to initialize
     *
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance.
     */
    public static HashCode128 hash128(@Nullable ByteBuf item, @NotNull HashCode128 seed) {

        checkNotNull(seed, "expected non-null seed");

        // TODO: DANOBLE: Support 128-bit hash code seeds by bringing in the murmur3 hash code from the Cosmos Java SDK

        final HashFunction hashFunction = Hashing.murmur3_128((int) (seed.high() & 0xFFFFFFFFL));
        final HashCode hashCode;

        if (item == null || item.writerIndex() == 0) {
            hashCode = hashFunction.newHasher().hash();
        } else if (item.hasArray()) {
            hashCode = hashFunction.hashBytes(item.array());
        } else {
            Hasher hasher = hashFunction.newHasher();
            item.forEachByte(b -> {
                hasher.putByte(b);
                return true;
            });
            hashCode = hasher.hash();
        }

        return HashCode128.from(hashCode.asBytes());
    }

    @SuppressWarnings("SameParameterValue")
    private static final class Constant {

        private static final ByteBuf CONSTANTS = allocator.heapBuffer();

        private static final ByteBuf FALSE = add(false);
        private static final ByteBuf TRUE = add(true);
        private static final ByteBuf EMPTY_STRING = add("");

        private Constant() {
        }

        static ByteBuf add(final boolean value) {
            final int start = CONSTANTS.writerIndex();
            CONSTANTS.writeByte(value ? 1 : 0);
            return CONSTANTS.slice(start, Byte.BYTES).asReadOnly();
        }

        static ByteBuf add(final String value) {

            final int start = CONSTANTS.writerIndex();
            final int encodedLength = Utf8.encodedLength(value);
            final ByteBuf buffer = allocator.buffer(encodedLength, encodedLength);

            final int count = buffer.writeCharSequence(value, UTF_8);
            assert count == encodedLength : lenientFormat("count: %s, encodedLength: %s");

            return CONSTANTS.slice(start, encodedLength);
        }
    }
}
