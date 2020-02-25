// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.recordio;

import static com.azure.cosmos.base.Preconditions.checkArgument;

public final class Record {

    public static final Record EMPTY = new Record(0, 0);

    private final long crc32;
    private final int length;

    public Record(final int length, final long crc32) {
        checkArgument(length >= 0, "expected non-negative length");
        this.length = length;
        this.crc32 = crc32;
    }

    public long crc32() {
        return this.crc32;
    }

    public int length() {
        return this.length;
    }
}
