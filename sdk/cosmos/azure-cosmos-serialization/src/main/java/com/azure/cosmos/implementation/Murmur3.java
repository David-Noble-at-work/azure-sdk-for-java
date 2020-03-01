// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation;

import com.azure.cosmos.core.Utf8String;
import com.azure.cosmos.implementation.base.Utf8;
import com.azure.cosmos.serialization.hybridrow.HashCode128;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.base.Strings.lenientFormat;
import static com.google.common.primitives.UnsignedBytes.toInt;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Murmur3 hashing for x86_64 (little-endian).
 *
 * @see <a href="https://en.wikipedia.org/wiki/MurmurHash">MurmurHash</a>
 */
public final class Murmur3 {

    private Murmur3() {
    }

    /**
     * Computes a 128-bit MurmurHash3 value for a {@code boolean} data item.
     *
     * @param item the data to hash.
     * @param seed the seed with which to initialize the hash value.
     *
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance.
     */
    @NotNull
    public static HashCode128 hash128(final boolean item, @NotNull final HashCode128 seed) {
        return hash128(item ? Constant.TRUE : Constant.FALSE, seed);
    }

    /**
     * Computes a 128-bit MurmurHash3 value for a {@code byte} data item.
     *
     * @param item the data to hash.
     * @param seed the seed with which to initialize the hash value.
     *
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance.
     */
    @NotNull
    public static HashCode128 hash128(final byte item,  @NotNull final HashCode128 seed) {
        return hash128(Constant.BYTE[item & 0xFF].readerIndex(0), seed);
    }

    /**
     * Computes a 128-bit MurmurHash3 value for a {@code int} data item.
     *
     * @param item the data to hash.
     * @param seed the seed with which to initialize the hash value.
     *
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance.
     */
    @NotNull
    public static HashCode128 hash128(final int item,  @NotNull final HashCode128 seed) {
        final ByteBuf buffer = Unpooled.wrappedBuffer(new byte[Integer.BYTES]).writerIndex(0).writeIntLE(item);
        return hash128(buffer, seed);
    }

    /**
     * Computes a 128-bit MurmurHash3 value for a {@code short} data item.
     *
     * @param item the data to hash.
     * @param seed the seed with which to initialize the hash value.
     *
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance.
     */
    @NotNull
    public static HashCode128 hash128(final short item,  @NotNull final HashCode128 seed) {
        final ByteBuf buffer = Unpooled.wrappedBuffer(new byte[Short.BYTES]).writerIndex(0).writeShortLE(item);
        return hash128(buffer, seed);
    }

    /**
     * Computes a 128-bit MurmurHash3 value for a {@link String} data item.
     *
     * @param item the data to hash.
     * @param seed the seed with which to initialize the hash value.
     *
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance
     */
    @NotNull
    public static HashCode128 hash128(@Nullable final String item, @NotNull final HashCode128 seed) {

        checkNotNull(seed, "expected non-null seed");
        final Utf8String value = Utf8String.transcodeUtf16(item);

        try {
            return hash128(value.content(), seed);
        } finally {
            value.release();
        }
    }

    /**
     * Computes a 128-bit MurmurHash3 value for a {@link ByteBuf} data item.
     *
     * This method does not advance the item's {@link ByteBuf#readerIndex}.
     *
     * @param item the data to hash.
     * @param seed the seed with which to initialize the hash value.
     *
     * @return The 128-bit hash represented as two 64-bit words encapsulated by a {@link HashCode128} instance.
     */
    @NotNull
    public static HashCode128 hash128(@Nullable ByteBuf item, @NotNull HashCode128 seed) {

        checkNotNull(seed, "expected non-null seed");
        final Hasher hasher = new Hasher(seed);

        if (item != null && item.readableBytes() > 0) {

            int start = item.readerIndex();

            while (item.readableBytes() >= Hasher.CHUNK_SIZE) {
                hasher.process(item);
            }

            hasher.processRemaining(item);
            item.readerIndex(start);
        }

        return hasher.complete();
    }

    // region Types

    private static final class Constant {

        private Constant() {
        }

        private static final ByteBuf CONSTANTS = ByteBufAllocator.DEFAULT.heapBuffer(256, 256);

        static final ByteBuf[] BYTE = new ByteBuf[256];

        static {
            Arrays.setAll(BYTE, index -> CONSTANTS.setByte(index, (byte) index).slice(index, Byte.BYTES).asReadOnly());
        }

        static final ByteBuf FALSE = BYTE[0];
        static final ByteBuf TRUE = BYTE[1];
    }

    private static final class Hasher {

        private static final long C1 = 0x87c37b91114253d5L;
        private static final long C2 = 0x4cf5ad432745937fL;
        private static final int CHUNK_SIZE = 16;

        private long h1;
        private long h2;
        private int length;

        /**
         * Instantiates a new Murmur 3 128 hasher.
         *
         * @param seed the seed
         */
        Hasher(HashCode128 seed) {
            this.h2 = seed.high();
            this.h1 = seed.low();
            this.length = 0;
        }

        public HashCode128 complete() {

            h1 ^= length;
            h2 ^= length;

            h1 += h2;
            h2 += h1;

            h1 = fmix64(h1);
            h2 = fmix64(h2);

            h1 += h2;
            h2 += h1;

            return HashCode128.of(h1, h2);
        }

        void process(ByteBuf in) {
            bmix64(in.readLongLE(), in.readLongLE());
            length += CHUNK_SIZE;
        }

        @SuppressFBWarnings("SF_SWITCH_FALLTHROUGH")
        @SuppressWarnings("fallthrough")
        void processRemaining(@NotNull final ByteBuf in) {

            final int readableBytes = in.readableBytes();

            if (readableBytes <= 0) {
                return;
            }

            final int readerIndex = in.readerIndex();
            this.length += readableBytes;

            long k1 = 0;
            long k2 = 0;

            switch (in.readableBytes()) {
                case 15:
                    k2 ^= (long) toInt(in.getByte(readerIndex + 14)) << 48; // fall through
                case 14:
                    k2 ^= (long) toInt(in.getByte(readerIndex + 13)) << 40; // fall through
                case 13:
                    k2 ^= (long) toInt(in.getByte(readerIndex + 12)) << 32; // fall through
                case 12:
                    k2 ^= (long) toInt(in.getByte(readerIndex + 11)) << 24; // fall through
                case 11:
                    k2 ^= (long) toInt(in.getByte(readerIndex + 10)) << 16; // fall through
                case 10:
                    k2 ^= (long) toInt(in.getByte(readerIndex + 9)) << 8; // fall through
                case 9:
                    k2 ^= (long) toInt(in.getByte(readerIndex + 8)); // fall through
                case 8:
                    k1 ^= in.getLong(readerIndex);
                    break;
                case 7:
                    k1 ^= (long) toInt(in.getByte(readerIndex + 6)) << 48; // fall through
                case 6:
                    k1 ^= (long) toInt(in.getByte(readerIndex + 5)) << 40; // fall through
                case 5:
                    k1 ^= (long) toInt(in.getByte(readerIndex + 4)) << 32; // fall through
                case 4:
                    k1 ^= (long) toInt(in.getByte(readerIndex + 3)) << 24; // fall through
                case 3:
                    k1 ^= (long) toInt(in.getByte(readerIndex + 2)) << 16; // fall through
                case 2:
                    k1 ^= (long) toInt(in.getByte(readerIndex + 1)) << 8; // fall through
                case 1:
                    k1 ^= (long) toInt(in.getByte(readerIndex));
                    break;
                default:
                    throw new AssertionError("Should never get here.");
            }

            h1 ^= mixK1(k1);
            h2 ^= mixK2(k2);
        }

        private void bmix64(long k1, long k2) {

            h1 ^= mixK1(k1);

            h1 = Long.rotateLeft(h1, 27);
            h1 += h2;
            h1 = h1 * 5 + 0x52dce729;

            h2 ^= mixK2(k2);

            h2 = Long.rotateLeft(h2, 31);
            h2 += h1;
            h2 = h2 * 5 + 0x38495ab5;
        }

        private static long fmix64(long k) {
            k ^= k >>> 33;
            k *= 0xff51afd7ed558ccdL;
            k ^= k >>> 33;
            k *= 0xc4ceb9fe1a85ec53L;
            k ^= k >>> 33;
            return k;
        }

        private static long mixK1(long k1) {
            k1 *= C1;
            k1 = Long.rotateLeft(k1, 31);
            k1 *= C2;
            return k1;
        }

        private static long mixK2(long k2) {
            k2 *= C2;
            k2 = Long.rotateLeft(k2, 33);
            k2 *= C1;
            return k2;
        }
    }

    // endregion
}
