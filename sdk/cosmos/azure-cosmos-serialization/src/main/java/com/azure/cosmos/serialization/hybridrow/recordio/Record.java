// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.recordio;

import static com.azure.cosmos.base.Preconditions.checkArgument;

/**
 * The type Record.
 */
public final class Record {

    /**
     * The constant EMPTY.
     */
    public static final Record EMPTY = new Record(0, 0);

    private final long crc32;
    private final int length;

    /**
     * Instantiates a new Record.
     *
     * @param length the length
     * @param crc32 the crc 32
     */
    public Record(final int length, final long crc32) {
        checkArgument(length >= 0, "expected non-negative length");
        this.length = length;
        this.crc32 = crc32;
    }

    /**
     * Crc 32 long.
     *
     * @return the long
     */
    public long crc32() {
        return this.crc32;
    }

    /**
     * Length int.
     *
     * @return the int
     */
    public int length() {
        return this.length;
    }
}
