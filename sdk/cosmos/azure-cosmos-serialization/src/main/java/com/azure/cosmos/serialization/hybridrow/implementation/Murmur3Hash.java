// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.implementation;

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
    public static HashCode128 hash128(final boolean item, @NotNull final HashCode128 seed) {
        return Murmur3Hash.hash128(item ? Constant.TRUE : Constant.FALSE, seed);
    }

    public static HashCode128 hash128(final byte item,  @NotNull final HashCode128 seed) {
        final ByteBuf buffer = Unpooled.wrappedBuffer(new byte[Byte.BYTES]).writeByte(item);
        return Murmur3Hash.hash128(Constant.BYTE[item & 0xFF], seed);
    }

    public static HashCode128 hash128(final int item,  @NotNull final HashCode128 seed) {
        final ByteBuf buffer = Unpooled.wrappedBuffer(new byte[Integer.BYTES]).writeIntLE(item);
        return Murmur3Hash.hash128(buffer, seed);
    }

    public static HashCode128 hash128(final short item,  @NotNull final HashCode128 seed) {
        final ByteBuf buffer = Unpooled.wrappedBuffer(new byte[Short.BYTES]).writeShortLE(item);
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
        final Hasher hasher = new Hasher(seed);

        if (item != null && item.writerIndex() != 0) {

            item.markReaderIndex();

            while (item.readableBytes() >= Hasher.CHUNK_SIZE) {
                hasher.process(item);
            }

            hasher.processRemaining(item);
            item.resetReaderIndex();
        }

        return hasher.complete();
    }

    @SuppressWarnings("SameParameterValue")
    private static final class Constant {

        private static final ByteBuf CONSTANTS = allocator.heapBuffer();
        private static final ByteBuf EMPTY_STRING = add("");
        private static final ByteBuf FALSE = add(false);
        private static final ByteBuf TRUE = add(true);

        private static final ByteBuf[] BYTE = new ByteBuf[256];
        static {
            Arrays.setAll(BYTE, index -> add((byte) index));
        }

        private Constant() {
        }

        static ByteBuf add(final boolean value) {
            final int start = CONSTANTS.writerIndex();
            CONSTANTS.writeByte(value ? 1 : 0);
            return CONSTANTS.slice(start, Byte.BYTES).asReadOnly();
        }

        static ByteBuf add(final byte value) {
            final int start = CONSTANTS.writerIndex();
            CONSTANTS.writeByte(value);
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
}
