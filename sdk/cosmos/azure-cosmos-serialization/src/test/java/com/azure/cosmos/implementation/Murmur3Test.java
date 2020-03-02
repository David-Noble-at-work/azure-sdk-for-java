// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation;

import com.azure.cosmos.serialization.hybridrow.HashCode128;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.function.BiFunction;

import static com.azure.cosmos.implementation.base.Strings.lenientFormat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Tests the Murmur3 class
 */
@Test(groups = "unit")
public class Murmur3Test {

    private static Logger logger = LoggerFactory.getLogger(Murmur3Test.class);

    @Test(dataProvider = "Items")
    public void testHash128(final Datum datum) {

        final HashCode128 observed;

        try {
            observed = datum.hash128.apply(datum.value, datum.seed);
        } catch (IndexOutOfBoundsException error) {
            fail("unexpected exception", error);
            throw error;
        }

        if (datum.expected != null) {
            System.out.println(datum.toString());
            assertEquals(observed, datum.expected);
        }
    }

    @DataProvider(name = "Items")
    private static Iterator<Object[]> Items() {

        ImmutableList<Datum> items = ImmutableList.of(

            // Boolean

            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((boolean) value, seed),
                HashCode128.of(0xcfdde5952cbed0a8L, 0xff62a726496298f9L),   // expected
                HashCode128.of(0x00000000c1a7b159L , 0x00000000c1a7b159L),  // seed
                false),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((boolean) value, seed),
                HashCode128.of(0x33bd43460de8c7a6L, 0x3b41ee3be33c9801L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                true),

            // Byte

            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((byte) value, seed),
                HashCode128.of(0x94308d7cc70ab466L, 0x4904de0bd0aad4abL),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                (byte) 0x7f),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((byte) value, seed),
                HashCode128.of(0x33bd43460de8c7a6L, 0x3b41ee3be33c9801L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                (byte) 0x1),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((byte) value, seed),
                HashCode128.of(0xcfdde5952cbed0a8L, 0xff62a726496298f9L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                (byte) 0x0),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((byte) value, seed),
                HashCode128.of(0xffe4a17c046feabcL, 0x496258effa3b27d4L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                (byte) 0xff),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((byte) value, seed),
                HashCode128.of(0xab491b34c0ba9c98L, 0xe0689e9e2e35e66dL),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                (byte) 0x80),

            // ByteBuf

            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((ByteBuf) value, seed),
                HashCode128.of(0xb42fb74c54504e9cL, 0x15bf63bf39efc115L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                (ByteBuf) null),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((ByteBuf) value, seed),
                HashCode128.of(0xb42fb74c54504e9cL, 0x15bf63bf39efc115L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                Unpooled.EMPTY_BUFFER),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((ByteBuf) value, seed),
                HashCode128.of(0xfcbf2ae1c9593e2fL, 0xb5b1a0221289fc5L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                Unpooled.wrappedBuffer(new byte[] {
                    (byte) 0x37, (byte) 0xa2, (byte) 0x4a, (byte) 0x3c, (byte) 0x44, (byte) 0x26, (byte) 0x2e,
                    (byte) 0xe5, (byte) 0xa1, (byte) 0xa3, (byte) 0xc1, (byte) 0xf1, (byte) 0x1e, (byte) 0x02,
                    (byte) 0x20, (byte) 0x54, (byte) 0x6a, (byte) 0xca, (byte) 0xaa, (byte) 0x9e, (byte) 0x55,
                    (byte) 0x8e, (byte) 0x88, (byte) 0xf2, (byte) 0xb9, (byte) 0x57, (byte) 0xc9, (byte) 0xfc,
                    (byte) 0x15, (byte) 0xf9, (byte) 0xee, (byte) 0x42, (byte) 0x46, (byte) 0x61, (byte) 0x28,
                    (byte) 0x50, (byte) 0x21, (byte) 0x5f, (byte) 0x65, (byte) 0x42, (byte) 0x30, (byte) 0x0a,
                    (byte) 0xc0, (byte) 0xce, (byte) 0x09, (byte) 0x38, (byte) 0x9a, (byte) 0x2c, (byte) 0x4e,
                    (byte) 0x16, (byte) 0x01, (byte) 0x82, (byte) 0xfe, (byte) 0x8c, (byte) 0x62, (byte) 0x55,
                    (byte) 0x93, (byte) 0x3f, (byte) 0x7f, (byte) 0xc3, (byte) 0xeb, (byte) 0x92, (byte) 0x44,
                    (byte) 0xd4, (byte) 0x9f, (byte) 0x5a, (byte) 0xb0, (byte) 0x80, (byte) 0x2e, (byte) 0x57,
                    (byte) 0xd7, (byte) 0xbc, (byte) 0x99, (byte) 0xb7, (byte) 0x08, (byte) 0xec, (byte) 0x96,
                    (byte) 0xaf, (byte) 0x3d, (byte) 0x40, (byte) 0x88, (byte) 0x4a, (byte) 0x26, (byte) 0x0a,
                    (byte) 0x27, (byte) 0xe7, (byte) 0x17, (byte) 0x90, (byte) 0x0d, (byte) 0xb6, (byte) 0xba,
                    (byte) 0xe4, (byte) 0x52, (byte) 0x56, (byte) 0x32, (byte) 0x32, (byte) 0x10, (byte) 0x01,
                    (byte) 0x63, (byte) 0xca, (byte) 0xae, (byte) 0x60, (byte) 0xd8, (byte) 0xa0, (byte) 0xe0,
                    (byte) 0x37, (byte) 0x1f, (byte) 0x4b, (byte) 0x1a, (byte) 0x21, (byte) 0x00, (byte) 0x93,
                    (byte) 0xbc, (byte) 0x67, (byte) 0x13, (byte) 0x51, (byte) 0xaf, (byte) 0xf5, (byte) 0xb2,
                    (byte) 0x6b, (byte) 0xb6, (byte) 0xb1, (byte) 0x53, (byte) 0x82, (byte) 0xc3, (byte) 0x31,
                    (byte) 0x1b, (byte) 0x91, (byte) 0x93, (byte) 0xe7, (byte) 0xc9, (byte) 0x74, (byte) 0x74,
                    (byte) 0x5c, (byte) 0x01, (byte) 0xea, (byte) 0x2f, (byte) 0x25, (byte) 0x47, (byte) 0x24,
                    (byte) 0x0e, (byte) 0xae, (byte) 0x3e, (byte) 0x6c, (byte) 0x5a, (byte) 0xce, (byte) 0x03,
                    (byte) 0x83, (byte) 0x48, (byte) 0x67, (byte) 0xc3, (byte) 0x31, (byte) 0x27, (byte) 0x37,
                    (byte) 0xec, (byte) 0x3b, (byte) 0xb4, (byte) 0x7e, (byte) 0x0d, (byte) 0x06, (byte) 0xb4,
                    (byte) 0x15, (byte) 0x39, (byte) 0x32, (byte) 0xfe, (byte) 0x79, (byte) 0x6b, (byte) 0x07,
                    (byte) 0x95, (byte) 0xfd, (byte) 0x36, (byte) 0x9a, (byte) 0xfa, (byte) 0x89, (byte) 0x5c,
                    (byte) 0xf7, (byte) 0x1b, (byte) 0x2e, (byte) 0xe2, (byte) 0x2c, (byte) 0x7e, (byte) 0xa1,
                    (byte) 0x50, (byte) 0xf6, (byte) 0x14, (byte) 0x78, (byte) 0xeb, (byte) 0xa4, (byte) 0x9d,
                    (byte) 0xda, (byte) 0xa9, (byte) 0x6b, (byte) 0xc0, (byte) 0x9d, (byte) 0x6b, (byte) 0x35,
                    (byte) 0x10, (byte) 0x16, (byte) 0x1e, (byte) 0x81, (byte) 0x88, (byte) 0x78, (byte) 0x00,
                    (byte) 0x71, (byte) 0xc0, (byte) 0xf7, (byte) 0xcc, (byte) 0x3c, (byte) 0x20, (byte) 0xdf,
                    (byte) 0x11, (byte) 0xaf, (byte) 0x95, (byte) 0x63, (byte) 0xb3, (byte) 0x72, (byte) 0x2d,
                    (byte) 0x90, (byte) 0x90, (byte) 0x02, (byte) 0xee, (byte) 0x1a, (byte) 0x2d, (byte) 0xde,
                    (byte) 0x04, (byte) 0xa2, (byte) 0x4f, (byte) 0x3d, (byte) 0x53, (byte) 0x07, (byte) 0x8f,
                    (byte) 0x19, (byte) 0xd9, (byte) 0xc1, (byte) 0xf1, (byte) 0xb8, (byte) 0x05, (byte) 0xf0,
                    (byte) 0xc1, (byte) 0x94, (byte) 0x5c, (byte) 0x15, (byte) 0x4e, (byte) 0x10, (byte) 0xc5,
                    (byte) 0x7c, (byte) 0x30, (byte) 0xa8, (byte) 0x8c, (byte) 0x80, (byte) 0xcf, (byte) 0xd1,
                    (byte) 0x7e, (byte) 0xae, (byte) 0x2b, (byte) 0x3a, (byte) 0x4c, (byte) 0xad, (byte) 0x88,
                    (byte) 0xcb, (byte) 0x45, (byte) 0xa3, (byte) 0xb5, (byte) 0x10, (byte) 0xb1, (byte) 0x3f,
                    (byte) 0xe7, (byte) 0xc4, (byte) 0xfc, (byte) 0x53, (byte) 0x9a, (byte) 0xf4, (byte) 0x8e,
                    (byte) 0x67, (byte) 0x71, (byte) 0x41, (byte) 0xd3, (byte) 0x23, (byte) 0x61, (byte) 0x8e,
                    (byte) 0x03, (byte) 0x33, (byte) 0x07, (byte) 0xff, (byte) 0x7a, (byte) 0xa3, (byte) 0xb6,
                    (byte) 0x2e, (byte) 0x39, (byte) 0x1a, (byte) 0xf3, (byte) 0xd9, (byte) 0xba, (byte) 0x1c,
                    (byte) 0x8f, (byte) 0xfe, (byte) 0x39, (byte) 0xdd, (byte) 0xc2, (byte) 0xbf, (byte) 0x35,
                    (byte) 0xed, (byte) 0x45, (byte) 0x38, (byte) 0x07, (byte) 0xa7, (byte) 0xc9, (byte) 0x68,
                    (byte) 0xf1, (byte) 0x1a, (byte) 0xaa, (byte) 0x60, (byte) 0x3d, (byte) 0x82, (byte) 0xa8,
                    (byte) 0xfa, (byte) 0xcf, (byte) 0xb7, (byte) 0x82, (byte) 0x3a, (byte) 0x6f, (byte) 0x43,
                    (byte) 0xf8, (byte) 0x74, (byte) 0x0d, (byte) 0x38, (byte) 0xaf, (byte) 0x30, (byte) 0x2a,
                    (byte) 0xe3, (byte) 0x76, (byte) 0x49, (byte) 0xf6, (byte) 0x7b, (byte) 0x41, (byte) 0x98,
                    (byte) 0xa2, (byte) 0x93, (byte) 0x83, (byte) 0x2f, (byte) 0x04, (byte) 0xae, (byte) 0x8d,
                    (byte) 0x83, (byte) 0x23, (byte) 0xee, (byte) 0xd8, (byte) 0xed, (byte) 0x0a, (byte) 0x1b,
                    (byte) 0xd1, (byte) 0x8c, (byte) 0x85, (byte) 0x0a, (byte) 0x9d, (byte) 0x4a, (byte) 0x58,
                    (byte) 0x9b, (byte) 0x96, (byte) 0xea, (byte) 0xe7, (byte) 0xd6, (byte) 0xee, (byte) 0xa9,
                    (byte) 0x75, (byte) 0x75, (byte) 0xde, (byte) 0x71, (byte) 0x01, (byte) 0x07, (byte) 0xc2,
                    (byte) 0x1d, (byte) 0x0d, (byte) 0x43, (byte) 0x3b, (byte) 0xef, (byte) 0xbb, (byte) 0xfe,
                    (byte) 0x4e, (byte) 0xea, (byte) 0xae, (byte) 0x6d, (byte) 0x9c, (byte) 0xf8, (byte) 0xba,
                    (byte) 0x8d, (byte) 0xe9, (byte) 0x1e, (byte) 0xe0, (byte) 0x3b, (byte) 0x84, (byte) 0xe1,
                    (byte) 0x1c, (byte) 0xd9, (byte) 0x05, (byte) 0x71, (byte) 0x95, (byte) 0xed, (byte) 0xa9,
                    (byte) 0xf6, (byte) 0xb9, (byte) 0x92, (byte) 0x35, (byte) 0xec, (byte) 0x81, (byte) 0xd2,
                    (byte) 0x83, (byte) 0x3c, (byte) 0x62, (byte) 0xe6, (byte) 0x34, (byte) 0x10, (byte) 0x55,
                    (byte) 0xf6, (byte) 0x1e, (byte) 0x81, (byte) 0x9e, (byte) 0x9a, (byte) 0xb4, (byte) 0x67,
                    (byte) 0xdf, (byte) 0x65, (byte) 0x66, (byte) 0x6e, (byte) 0xb8, (byte) 0x6b, (byte) 0x8a,
                    (byte) 0xb8, (byte) 0xa3, (byte) 0x68, (byte) 0x0e, (byte) 0x5f, (byte) 0xf3, (byte) 0x32,
                    (byte) 0xf1, (byte) 0x2e, (byte) 0x67, (byte) 0xf4, (byte) 0xf1, (byte) 0xbc, (byte) 0xab,
                    (byte) 0x4d, (byte) 0xbf, (byte) 0x15, (byte) 0x1e, (byte) 0x97, (byte) 0x9c, (byte) 0xd9,
                    (byte) 0x9f, (byte) 0x89, (byte) 0x18, (byte) 0xdf, (byte) 0xa2, (byte) 0x51, (byte) 0x97,
                    (byte) 0x56, (byte) 0xa0, (byte) 0x21, (byte) 0xd5, (byte) 0x39, (byte) 0x60, (byte) 0x07,
                    (byte) 0x2b, (byte) 0x7d, (byte) 0x66, (byte) 0xc2, (byte) 0x2b, (byte) 0x90, (byte) 0x17,
                    (byte) 0xb9, (byte) 0xc1, (byte) 0x20, (byte) 0x54, (byte) 0x8c, (byte) 0x0d, (byte) 0x01,
                    (byte) 0x3f, (byte) 0x8c, (byte) 0x84, (byte) 0x71, (byte) 0x25, (byte) 0x55, (byte) 0xc3,
                    (byte) 0x62, (byte) 0x25, (byte) 0xb8, (byte) 0xed, (byte) 0xc8, (byte) 0x42, (byte) 0xdb,
                    (byte) 0xb1, (byte) 0x3d, (byte) 0x77, (byte) 0x5c, (byte) 0xe3, (byte) 0xd5, (byte) 0x61,
                    (byte) 0x86, (byte) 0xc7, (byte) 0x26, (byte) 0xfb, (byte) 0x2c, (byte) 0x89, (byte) 0xd8,
                    (byte) 0x95, (byte) 0x10, (byte) 0x05, (byte) 0x88, (byte) 0x42, (byte) 0xff, (byte) 0x68,
                    (byte) 0x92, (byte) 0x1d, (byte) 0xd6, (byte) 0x41, (byte) 0x69, (byte) 0xcc, (byte) 0xc6,
                    (byte) 0x81, (byte) 0xd5, (byte) 0xd0, (byte) 0x5c, (byte) 0x7e, (byte) 0xcd, (byte) 0x16,
                    (byte) 0x6e, (byte) 0x03, (byte) 0xe3, (byte) 0x66, (byte) 0x85, (byte) 0xfe, (byte) 0x2e,
                    (byte) 0x12, (byte) 0x73, (byte) 0x6a, (byte) 0x01, (byte) 0x1c, (byte) 0x7f, (byte) 0xf5,
                    (byte) 0x2d, (byte) 0x74, (byte) 0x1d, (byte) 0xf3, (byte) 0xbe, (byte) 0xba, (byte) 0xbf,
                    (byte) 0xb0, (byte) 0x7e, (byte) 0x25, (byte) 0x2d, (byte) 0x8c, (byte) 0x47, (byte) 0x6d,
                    (byte) 0x05, (byte) 0x82, (byte) 0x35, (byte) 0x65, (byte) 0x99, (byte) 0x0f, (byte) 0xd5,
                    (byte) 0xd0, (byte) 0xc5, (byte) 0x05, (byte) 0x7c, (byte) 0xe1, (byte) 0x3c, (byte) 0xa4,
                    (byte) 0x2f, (byte) 0x91, (byte) 0xcd, (byte) 0x2c, (byte) 0xf8, (byte) 0xfc, (byte) 0xc1,
                    (byte) 0xef, (byte) 0xc1, (byte) 0x3e, (byte) 0xba, (byte) 0xa5, (byte) 0x56, (byte) 0xd0,
                    (byte) 0x61, (byte) 0xab, (byte) 0x00, (byte) 0x50, (byte) 0xf9, (byte) 0x55, (byte) 0xa5,
                    (byte) 0x6c, (byte) 0x7b, (byte) 0xdb, (byte) 0x87, (byte) 0x48, (byte) 0xec, (byte) 0x61,
                    (byte) 0xf4, (byte) 0x96, (byte) 0x59, (byte) 0xf1, (byte) 0x89, (byte) 0xeb, (byte) 0x89,
                    (byte) 0x96, (byte) 0x07, (byte) 0x97, (byte) 0xd0, (byte) 0x63, (byte) 0x9f, (byte) 0x07,
                    (byte) 0xba, (byte) 0xe2, (byte) 0x9c, (byte) 0x72, (byte) 0x55, (byte) 0x1f, (byte) 0xbd,
                    (byte) 0x68, (byte) 0x4c, (byte) 0x3d, (byte) 0x72, (byte) 0xc7, (byte) 0x79, (byte) 0x10,
                    (byte) 0x2e, (byte) 0xc2, (byte) 0x0b, (byte) 0xea, (byte) 0xf5, (byte) 0x70, (byte) 0x1c,
                    (byte) 0x6b, (byte) 0x1f, (byte) 0x49, (byte) 0xd2, (byte) 0x04, (byte) 0x43, (byte) 0x85,
                    (byte) 0x21, (byte) 0x90, (byte) 0x04, (byte) 0x12, (byte) 0x59, (byte) 0x0b, (byte) 0xda,
                    (byte) 0xdc, (byte) 0xdb, (byte) 0xfd, (byte) 0x12, (byte) 0x1f, (byte) 0x50, (byte) 0xab,
                    (byte) 0xa0, (byte) 0x7b, (byte) 0xbc, (byte) 0xa2, (byte) 0x52, (byte) 0x7e, (byte) 0xa3,
                    (byte) 0x3e, (byte) 0xa9, (byte) 0x3f, (byte) 0x74, (byte) 0x27, (byte) 0xc0, (byte) 0xb3,
                    (byte) 0xd8, (byte) 0xed, (byte) 0x7a, (byte) 0xc9, (byte) 0xe4, (byte) 0x18, (byte) 0x7f,
                    (byte) 0xbc, (byte) 0x24, (byte) 0x75, (byte) 0x6c, (byte) 0xf5, (byte) 0xb0, (byte) 0x42,
                    (byte) 0xba, (byte) 0x8d, (byte) 0x3d, (byte) 0x5d, (byte) 0xc2, (byte) 0xac, (byte) 0x84,
                    (byte) 0x17, (byte) 0x8c, (byte) 0x4e, (byte) 0xb7, (byte) 0x9f, (byte) 0x1c, (byte) 0x98,
                    (byte) 0xe4, (byte) 0x8c, (byte) 0x48, (byte) 0xc9, (byte) 0x2f, (byte) 0x29, (byte) 0x76,
                    (byte) 0x83, (byte) 0xb7, (byte) 0x00, (byte) 0x15, (byte) 0xce, (byte) 0x97, (byte) 0x1d,
                    (byte) 0xbd, (byte) 0x12, (byte) 0xfd, (byte) 0x92, (byte) 0x29, (byte) 0x2d, (byte) 0xe0,
                    (byte) 0xe8, (byte) 0x1c, (byte) 0x2c, (byte) 0xae, (byte) 0x9b, (byte) 0xb7, (byte) 0x50,
                    (byte) 0x42, (byte) 0x6b, (byte) 0xd2, (byte) 0x9b, (byte) 0x1a, (byte) 0x19, (byte) 0xfe,
                    (byte) 0x82, (byte) 0x71, (byte) 0x29, (byte) 0x40, (byte) 0x4b, (byte) 0xaa, (byte) 0x63,
                    (byte) 0x12, (byte) 0x88, (byte) 0xfd, (byte) 0xd9, (byte) 0x99, (byte) 0x45, (byte) 0x38,
                    (byte) 0xd2, (byte) 0xb3, (byte) 0x5d, (byte) 0x6f, (byte) 0x3e, (byte) 0xfa, (byte) 0xb0,
                    (byte) 0x8a, (byte) 0xa7, (byte) 0x89, (byte) 0xf3, (byte) 0xd0, (byte) 0x6d, (byte) 0x9d,
                    (byte) 0x81, (byte) 0x1d, (byte) 0xa3, (byte) 0x37, (byte) 0xbb, (byte) 0x8d, (byte) 0x40,
                    (byte) 0x80, (byte) 0x36, (byte) 0xc8, (byte) 0x58, (byte) 0xf3, (byte) 0x91, (byte) 0x4c,
                    (byte) 0x44, (byte) 0xc9, (byte) 0x49, (byte) 0xb5, (byte) 0xb7, (byte) 0xaa, (byte) 0x04,
                    (byte) 0x5f, (byte) 0x80, (byte) 0x07, (byte) 0x1a, (byte) 0x9d, (byte) 0xfb, (byte) 0x2c,
                    (byte) 0x29, (byte) 0x1e, (byte) 0xfb, (byte) 0x16, (byte) 0xef, (byte) 0x30, (byte) 0x50,
                    (byte) 0xfd, (byte) 0x82, (byte) 0x78, (byte) 0x14, (byte) 0xae, (byte) 0xca, (byte) 0x1d,
                    (byte) 0x8b, (byte) 0x76, (byte) 0x32, (byte) 0x9b, (byte) 0x32, (byte) 0x3c, (byte) 0x2a,
                    (byte) 0x34, (byte) 0xec, (byte) 0x93, (byte) 0x82, (byte) 0xc6, (byte) 0x87, (byte) 0xad,
                    (byte) 0x7b, (byte) 0x44, (byte) 0xb7, (byte) 0x89, (byte) 0x2b, (byte) 0x67, (byte) 0xcc,
                    (byte) 0x60, (byte) 0x34, (byte) 0xb8, (byte) 0xbf, (byte) 0xeb, (byte) 0xd9, (byte) 0x9e,
                    (byte) 0xa7, (byte) 0xc5, (byte) 0x05, (byte) 0x55, (byte) 0x3c, (byte) 0x5f, (byte) 0x5b,
                    (byte) 0xf2, (byte) 0xdf, (byte) 0xdf, (byte) 0x12, (byte) 0x66, (byte) 0x96, (byte) 0x0d,
                    (byte) 0x36, (byte) 0xa5, (byte) 0xfb, (byte) 0x36, (byte) 0xff, (byte) 0xf0, (byte) 0x06,
                    (byte) 0x5b, (byte) 0x5a, (byte) 0x26, (byte) 0xd2, (byte) 0x1f, (byte) 0x30, (byte) 0x59,
                    (byte) 0x81, (byte) 0xc6, (byte) 0x04, (byte) 0x47, (byte) 0xa4, (byte) 0x71, (byte) 0x17,
                    (byte) 0xff, (byte) 0xb9, (byte) 0x3c, (byte) 0x62, (byte) 0xf4, (byte) 0xc9, (byte) 0xd7,
                    (byte) 0x8d, (byte) 0xbb, (byte) 0xd4, (byte) 0x15, (byte) 0xe0, (byte) 0x1c, (byte) 0x75,
                    (byte) 0xdc, (byte) 0x93, (byte) 0x30, (byte) 0xeb, (byte) 0xc1, (byte) 0x01, (byte) 0x12,
                    (byte) 0x34, (byte) 0x91, (byte) 0x90, (byte) 0x58, (byte) 0xa3, (byte) 0xcd, (byte) 0xb3,
                    (byte) 0xc3, (byte) 0xb4, (byte) 0xf1, (byte) 0xca, (byte) 0x58, (byte) 0x07, (byte) 0x59,
                    (byte) 0x03, (byte) 0x01, (byte) 0x91, (byte) 0x2d, (byte) 0x5f, (byte) 0x4d, (byte) 0x41,
                    (byte) 0x84, (byte) 0xf6, (byte) 0xeb, (byte) 0xeb, (byte) 0x97, (byte) 0x53, (byte) 0xfe,
                    (byte) 0xbb, (byte) 0x8b, (byte) 0x40, (byte) 0x2b, (byte) 0xed, (byte) 0x03, (byte) 0x14,
                    (byte) 0x4f, (byte) 0x1a, (byte) 0xfc, (byte) 0x1e, (byte) 0xa3, (byte) 0xc4, (byte) 0x06,
                    (byte) 0x0d, (byte) 0x22, (byte) 0xff, (byte) 0xa8, (byte) 0x2b, (byte) 0x74, (byte) 0x1f,
                    (byte) 0xac, (byte) 0x9d, (byte) 0x00, (byte) 0x29, (byte) 0xf2, (byte) 0x72, (byte) 0xc8,
                    (byte) 0xb5, (byte) 0xd3, (byte) 0x16, (byte) 0x70, (byte) 0x4c, (byte) 0x38, (byte) 0x2a,
                    (byte) 0xb5, (byte) 0xc1, (byte) 0x89, (byte) 0xbb, (byte) 0xcc, (byte) 0xda, (byte) 0xd7,
                    (byte) 0xd6, (byte) 0x47, (byte) 0x4f, (byte) 0xc3, (byte) 0xd5, (byte) 0xed, (byte) 0x68,
                    (byte) 0xee, (byte) 0x31, (byte) 0x6b, (byte) 0x94, (byte) 0xc0, (byte) 0x47, (byte) 0xbf,
                    (byte) 0x0f, (byte) 0x85, (byte) 0x19, (byte) 0x7c, (byte) 0x31, (byte) 0x23, (byte) 0x31,
                    (byte) 0x6f, (byte) 0x1c, (byte) 0x47, (byte) 0x64, (byte) 0x8a, (byte) 0x1c, (byte) 0x75,
                    (byte) 0xe6, (byte) 0x85, (byte) 0x8c, (byte) 0x2f, (byte) 0x0e, (byte) 0x5a, (byte) 0x0b,
                    (byte) 0x1f, (byte) 0xe4, (byte) 0x69, (byte) 0xe0, (byte) 0x08, (byte) 0x01, (byte) 0x39,
                    (byte) 0xad, (byte) 0x9d, (byte) 0xfa, (byte) 0x98, (byte) 0x2f, (byte) 0xdb, (byte) 0x79,
                    (byte) 0x16, (byte) 0x28, (byte) 0xdb, (byte) 0x0c, (byte) 0x4f, (byte) 0xd3, (byte) 0xd4,
                    (byte) 0x92, (byte) 0x53, (byte) 0x85, (byte) 0x19, (byte) 0x32, (byte) 0x56, (byte) 0x31,
                    (byte) 0x2e, (byte) 0x72, (byte) 0xb5, (byte) 0xdf, (byte) 0x04, (byte) 0x79, (byte) 0x16,
                    (byte) 0x4c, (byte) 0xf1,
                })),

            // Integer

            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((int) value, seed),
                HashCode128.of(0xd64e43fff4f9151fL, 0x4b7c62a50b489525L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                0x7fffffff),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((int) value, seed),
                HashCode128.of(0x6d72a7cdbc76580aL, 0x5136864cca81db21L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                0x80000000),

            // Short

            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((short) value, seed),
                HashCode128.of(0x781c1f3830af826dL, 0x7d0c5b5d31405590L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                (short) 0x7fff),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((short) value, seed),
                HashCode128.of(0x2e5561f709edb832L, 0x165e86e9c5f75246L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                (short) 0x8000),

            // String

            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((String) value, seed),
                HashCode128.of(0xb42fb74c54504e9cL, 0x15bf63bf39efc115L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                (String) null),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((String) value, seed),
                HashCode128.of(0xb42fb74c54504e9cL, 0x15bf63bf39efc115L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                ""),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((String) value, seed),
                HashCode128.of(0x9300c29be36170ebL, 0x1a38e7d2356f9feaL),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                "non-empty string"),
            new Datum(
                (Object value, HashCode128 seed) -> Murmur3.hash128((String) value, seed),
                HashCode128.of(0x290ec1d704f45b0cL, 0xe0d450e46690e8f1L),  // expected
                HashCode128.of(0x00000000c1a7b159L, 0x00000000c1a7b159L),  // seed
                "long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string long non-empty string ")
        );

        return items.stream().map(item -> new Object[] { item }).iterator();
    }

    private static class Datum {

        BiFunction<Object, HashCode128, HashCode128> hash128;
        HashCode128 expected;
        HashCode128 seed;
        Object value;

        Datum(BiFunction<Object, HashCode128, HashCode128> hash128, HashCode128 expected, HashCode128 seed, Object value) {
            this.hash128 = hash128;
            this.seed = seed;
            this.value = value;
            this.expected = expected;
        }

        @Override
        public String toString() {
            return lenientFormat("value : %s = %s",
                this.value == null
                    ? "Nothing"
                    : this.value.getClass().getSimpleName(),
                this.value == null
                    ? "null"
                    : Json.toString(this.value));
        }
    }
}
