// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.codecs;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;

/**
 * Tests the DateTimeCodec using data generated from C# code.
 * <p>
 * Test data was generated from code that looks like this:<pre>{@code
 * using System.Runtime.InteropServices;
 * var buffer = new byte[8];
 * var value = DateTime.Now;
 * MemoryMarshal.Write(buffer, ref value);
 * Console.WriteLine($"new DateTimeItem(new byte[] {{ {string.Join(", ", (sbyte[])(Array)buffer )} }}, OffsetDateTime.parse(\"{value.ToString("o")}\"))");
 * }</pre>
 */
@Test(groups = "unit")
public class DateTimeCodecTest {

    @Test(dataProvider = "dateTimeDataProvider")
    public void testDecodeByteArray(byte[] buffer, OffsetDateTime value) {
        OffsetDateTime actual = DateTimeCodec.decode(buffer);
        OffsetDateTime adjusted = OffsetDateTime.of(actual.toLocalDateTime(), value.getOffset());
        assertEquals(adjusted, value);
    }

    @Test(dataProvider = "dateTimeDataProvider")
    public void testDecodeByteBuf(byte[] buffer, OffsetDateTime value) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(buffer);
        OffsetDateTime actual = DateTimeCodec.decode(byteBuf);
        OffsetDateTime adjusted = OffsetDateTime.of(actual.toLocalDateTime(), value.getOffset());
        assertEquals(adjusted, value);
    }

    @Test(dataProvider = "dateTimeDataProvider")
    public void testEncodeByteArray(byte[] buffer, OffsetDateTime value) {

        byte[] actual = DateTimeCodec.encode(value);

        if (value.getOffset().getTotalSeconds() != 0) {
            if (!value.getOffset().equals(ZoneId.systemDefault().getRules().getOffset(value.toInstant()))) {
                assertEquals(actual[7] & 0b11000000, 0b11000000);
                actual[7] = (byte) ((actual[7] & 0b00111111) | 0b10000000);
            }
        }

        assertEquals(actual, buffer);
    }

    @Test(dataProvider = "dateTimeDataProvider")
    public void testEncodeByteBuf(byte[] buffer, OffsetDateTime value) {

        ByteBuf actual = Unpooled.wrappedBuffer(new byte[DateTimeCodec.BYTES]).clear();
        DateTimeCodec.encode(value, actual);

        if (value.getOffset().getTotalSeconds() != 0) {
            if (!value.getOffset().equals(ZoneId.systemDefault().getRules().getOffset(value.toInstant()))) {
                assertEquals(actual.getByte(7) & 0b11000000, 0b11000000);
                actual.setByte(7, (actual.getByte(7) & 0b00111111) | 0b10000000);
            }
        }

        assertEquals(actual.array(), buffer);
    }

    @DataProvider(name = "dateTimeDataProvider")
    private static Iterator<Object[]> dateTimeData() {

        ImmutableList<DateTimeItem> items = ImmutableList.of(
            // PDT, DateTimeCodec.KIND_LOCAL
            new DateTimeItem(
                new byte[] { 120, -44, 106, -5, 105, 48, -41, -120 },
                OffsetDateTime.parse("2019-09-03T12:26:44.3996280-07:00")),
            // PST, DateTimeCodec.KIND_LOCAL
            new DateTimeItem(
                new byte[] { 84, -5, 108, 5, -31, -107, -41, -120 },
                OffsetDateTime.parse("2020-01-10T15:23:18.7423060-08:00")),
            // UTC, DateTimeCodec.KIND_UTC
            new DateTimeItem(
                new byte[] { 48, -121, 27, 8, 44, -106, -41, 72 },
                OffsetDateTime.parse("2020-01-11T00:20:15.4963760Z"))
        );

        return items.stream().map(item -> new Object[] { item.buffer, item.value }).iterator();
    }

    private static class DateTimeItem {

        private final byte[] buffer;
        private final OffsetDateTime value;

        DateTimeItem(byte[] buffer, OffsetDateTime value) {
            this.buffer = buffer;
            this.value = value;
        }

        public byte[] buffer() {
            return this.buffer;
        }

        public OffsetDateTime value() {
            return this.value;
        }
    }
}
