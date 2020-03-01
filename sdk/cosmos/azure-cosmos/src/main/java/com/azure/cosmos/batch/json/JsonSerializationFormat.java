// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.json;

enum JsonSerializationFormat {
    /// <summary>
    /// Plain text
    /// </summary>
    TEXT((byte) 0),

    /// <summary>
    /// Binary Encoding
    /// </summary>
    BINARY((byte) 0x80),

    /// <summary>
    /// HybridRow Binary Encoding
    /// </summary>
    HYBRID_ROW((byte) 0x81);

    // All other format values need to be > 127,
    // otherwise a valid JSON starting character (0-9, f[alse], t[rue], n[ull],{,[,") might be interpreted as a
    // serialization format.

    private final byte id;

    JsonSerializationFormat(final byte id) {
        this.id = id;
    }

    public byte id() {
        return this.id;
    }
}
