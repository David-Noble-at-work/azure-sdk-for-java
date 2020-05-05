// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow;

import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.implementation.base.Preconditions.throwIllegalArgumentException;

/**
 * Versions of HybridRow.
 * <p>
 * A version from this list MUST be inserted in the version BOM at the beginning of all rows.
 */
public enum HybridRowVersion {

    /**
     * Invalid hybrid row version.
     */
    INVALID((byte) 0),

    /**
     * Initial version of the HybridRow format.
     */
    V1((byte) 0x81);

    /**
     * The number of bytes in the {@link #value} of this {@link HybridRowVersion HybridRow version}.
     */
    public static final int BYTES = Byte.BYTES;
    private final byte value;

    HybridRowVersion(final byte value) {
        this.value = value;
    }

    /**
     * Returns the {@link HybridRowVersion HybridRow version} associated with the given {@code byte} value.
     *
     * @param value the value.
     *
     * @return a {@link HybridRowVersion HybridRow version}.
     *
     * @throws IllegalArgumentException if {@code value} does not map to a a {@link HybridRowVersion HybridRow version}.
     */
    @NotNull
    public static HybridRowVersion from(final byte value) {
        switch (value) {
            case (byte) 0x00:
                return INVALID;
            case (byte) 0x81:
                return V1;
            default:
                throwIllegalArgumentException("unrecognized HybridRowVersion number: %s", value);
                return null;
        }
    }

    /**
     * Returns the {@code byte} value of this {@link HybridRowVersion HybridRow version}.
     *
     * @return the the {@code byte} value of this {@link HybridRowVersion HybridRow version}.
     */
    public byte value() {
        return this.value;
    }
}
