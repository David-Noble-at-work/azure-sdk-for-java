// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.schemas;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Versions of the HybridRow Schema Description Language.
 */
public enum SchemaLanguageVersion {
    /**
     * Initial version of the HybridRow Schema Description Lanauge.
     */
    V1((byte) 0, "v1");

    /**
     * The constant BYTES.
     */
    public static final int BYTES = Byte.BYTES;

    private String friendlyName;
    private byte value;

    SchemaLanguageVersion(byte value, String text) {
        this.value = value;
        this.friendlyName = text;
    }

    /**
     * Returns the friendly name of this enum constant.
     *
     * @return the friendly name of this enum constant.
     *
     * @see #toString() #toString()
     */
    @NotNull
    public String friendlyName() {
        return this.friendlyName;
    }

    /**
     * Gets the {@link SchemaLanguageVersion schema language version} with the given {@code byte} value.
     *
     * @param value the value.
     *
     * @return the {@link SchemaLanguageVersion schema language version} or {@code null}, if {@code value} does not map
     * to a {@link SchemaLanguageVersion schema language version}.
     */
    @Nullable
    public static SchemaLanguageVersion from(final byte value) {
        return value == 0x00 ? V1 : null;
    }

    /**
     * Returns the friendly name of this enum constant.
     *
     * @return the friendly name of this enum constant.
     * @see #friendlyName()
     */
    @Override
    public String toString() {
        return this.friendlyName;
    }

    /**
     * Value byte.
     *
     * @return the byte
     */
    public byte value() {
        return this.value;
    }
}
