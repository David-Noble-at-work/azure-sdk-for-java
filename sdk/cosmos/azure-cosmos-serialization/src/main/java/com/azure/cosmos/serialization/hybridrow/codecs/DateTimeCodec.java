// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static com.azure.cosmos.base.Preconditions.checkArgument;
import static com.azure.cosmos.base.Preconditions.checkNotNull;

/**
 * Provides static methods for encoding and decoding {@link OffsetDateTime}s serialized as {@code System.DateTime}s
 * <p>
 * {@link OffsetDateTime} values are serialized as unsigned 64-bit integers:
 *
 * <table summary="Layout of field value">
 *     <tbody>
 *      <tr><td>
 *          Bits 01-62
 *      </td><td>
 *          Contain the number of 100-nanosecond ticks where 0 represents {@code 1/1/0001 12:00am}, up until the value
 *          {@code 12/31/9999 23:59:59.9999999}.
 *      </td></tr>
 *      <tr><td>
 *          Bits 63-64
 *      </td><td>
 *          Contain a four-state value that describes the {@code System.DateTimeKind} value of the date time, with a
 *          2nd value for the rare case where the date time is local. It is in an overlapped daylight savings time
 *          hour and it is in daylight savings time. This representation allows distinction of these otherwise ambiguous
 *          local times and prevents data loss when round tripping from Local to UTC time.
 *      </td></tr>
 *   </tbody>
 * </table>
 *
 * @see <a href="https://referencesource.microsoft.com/mscorlib/a.html#df6b1eba7461813b">struct DateTime source</a>
 */
public final class DateTimeCodec {

    public static final int BYTES = Long.BYTES;

    private static final long FLAGS_MASK = 0xC000000000000000L;
    private static final long KIND_AMBIGUOUS = 0xC000000000000000L;
    private static final long KIND_LOCAL = 0x8000000000000000L;
    private static final long KIND_UTC = 0x4000000000000000L;
    private static final long TICKS_MASK = 0x3FFFFFFFFFFFFFFFL;

    private static final long UNIX_EPOCH_TICKS = 0x89F7FF5F7B58000L;

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private static final int ZONE_OFFSET_LOCAL_DAYLIGHT_TOTAL_SECONDS;
    private static final int ZONE_OFFSET_LOCAL_STANDARD_TOTAL_SECONDS;

    static {
        final ZoneOffset zoneOffset = ZONE_ID.getRules().getStandardOffset(Instant.now());
        ZONE_OFFSET_LOCAL_STANDARD_TOTAL_SECONDS = zoneOffset.getTotalSeconds();
        ZONE_OFFSET_LOCAL_DAYLIGHT_TOTAL_SECONDS = ZONE_OFFSET_LOCAL_STANDARD_TOTAL_SECONDS + 3_600;
    }

    private DateTimeCodec() {
    }

    /**
     * Decode an {@link OffsetDateTime} serialized like a {@code System.DateTime} by {@code MemoryMarshal.Write}.
     *
     * @param bytes an array containing the serialized value to be decoded.
     * @return a new {@link OffsetDateTime}.((byte[])((UnpooledHeapByteBuf)in).array)[0]
     * @see <a href="https://referencesource.microsoft.com/mscorlib/a.html#df6b1eba7461813b">struct DateTime source</a>
     */
    public static OffsetDateTime decode(@Nonnull final byte[] bytes) {
        checkNotNull(bytes, "expected non-null bytes");
        return decode(Unpooled.wrappedBuffer(bytes));
    }

    /**
     * Decode an {@link OffsetDateTime} serialized like a {@code System.DateTime} by {@code MemoryMarshal.Write}.
     *
     * @param in a {@link ByteBuf} containing the serialized value to be decoded.
     * @return a new {@link OffsetDateTime}.
     * @see <a href="https://referencesource.microsoft.com/mscorlib/a.html#df6b1eba7461813b">struct DateTime source</a>
     */
    public static OffsetDateTime decode(@Nonnull final ByteBuf in) {

        checkNotNull(in, "expected non-null in");

        checkArgument(in.readableBytes() >= BYTES, "expected %s readable bytes, not %s",
            BYTES,
            in.readableBytes());

        final long data = in.readLongLE();
        final long ticks = data & TICKS_MASK;
        final long epochSecond = ((ticks - UNIX_EPOCH_TICKS) / 10_000_000L);
        final int nanoSecond = (int) (100L * (ticks % 10_000_000L));

        if ((data & FLAGS_MASK) == KIND_UTC) {
            return OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSecond, nanoSecond), ZoneOffset.UTC);
        }

        // KIND_LOCAL or KIND_AMBIGUOUS

        final Instant instant = Instant.ofEpochSecond(epochSecond, nanoSecond);

        return ZONE_ID.getRules().isDaylightSavings(instant)
            ? OffsetDateTime.ofInstant(instant.minusSeconds(ZONE_OFFSET_LOCAL_STANDARD_TOTAL_SECONDS + 3_600L), ZONE_ID)
            : OffsetDateTime.ofInstant(instant.minusSeconds(ZONE_OFFSET_LOCAL_STANDARD_TOTAL_SECONDS), ZONE_ID);
    }

    /**
     * Encode an {@link OffsetDateTime} like a {@code System.DateTime} serialized by {@code MemoryMarshal.Write}.
     *
     * @param dateTime an {@link OffsetDateTime} to be encoded.
     * @return a new byte array containing the encoded {@code offsetDateTime}.
     * @see <a href="https://referencesource.microsoft.com/mscorlib/a.html#df6b1eba7461813b">struct DateTime source</a>
     */
    public static byte[] encode(@Nonnull final OffsetDateTime dateTime) {
        checkNotNull(dateTime, "expected non-null dateTime");
        final byte[] bytes = new byte[BYTES];
        encode(dateTime, Unpooled.wrappedBuffer(bytes).clear());
        return bytes;
    }

    /**
     * Encode an {@link OffsetDateTime} like a {@code System.DateTime} produced by {@code MemoryMarshal.Write}.
     *
     * @param dateTime an {@link OffsetDateTime} to be encoded.
     * @param out an output {@link ByteBuf}.
     * @see <a href="https://referencesource.microsoft.com/mscorlib/a.html#df6b1eba7461813b">struct DateTime source</a>
     */
    public static void encode(@Nonnull final OffsetDateTime dateTime, @Nonnull final ByteBuf out) {

        checkNotNull(dateTime, "expected non-null dateTime");
        checkNotNull(out, "expected non-null out");

        final Instant instant = dateTime.toInstant();

        final long zoneOffsetTotalSeconds = dateTime.getOffset().getTotalSeconds();
        final long epochSecond = instant.getEpochSecond() + zoneOffsetTotalSeconds;

        final long ticks = UNIX_EPOCH_TICKS + 10_000_000L * epochSecond + instant.getNano() / 100L;

        checkArgument(ticks <= TICKS_MASK, "expected dateTime epoch second in range [0, %s], not %s",
            TICKS_MASK,
            ticks);

        final long value;

        if (zoneOffsetTotalSeconds == 0) {

            value = ticks | KIND_UTC;

        } else {

            final long daylightSavingsAdjustment = ZONE_ID.getRules().isDaylightSavings(instant) ? 0L : 3_600L;

            if (zoneOffsetTotalSeconds + daylightSavingsAdjustment == ZONE_OFFSET_LOCAL_DAYLIGHT_TOTAL_SECONDS) {
                value = ticks | KIND_LOCAL;
            } else {
                value = ticks | KIND_AMBIGUOUS;
            }
        }

        out.writeLongLE(value);
    }
}
