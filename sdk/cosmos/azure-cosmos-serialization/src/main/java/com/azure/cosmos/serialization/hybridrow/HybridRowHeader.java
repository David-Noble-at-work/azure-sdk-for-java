// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.base.Preconditions.checkArgument;
import static com.azure.cosmos.base.Preconditions.checkNotNull;

/**
 * Describes the header that precedes all valid Hybrid Rows.
 */
public final class HybridRowHeader {
    /**
     * Size (in bytes) of a serialized header.
     */
    public static final int BYTES = HybridRowVersion.BYTES + SchemaId.BYTES;

    private final SchemaId schemaId;
    private final HybridRowVersion version;

    /**
     * Initializes a new instance of a {@link HybridRowHeader}.
     *
     * @param version the version of the HybridRow library used to write this row.
     * @param schemaId the unique identifier of the schema whose layout was used to write this row.
     */
    public HybridRowHeader(@NotNull final HybridRowVersion version, @NotNull SchemaId schemaId) {

        checkNotNull(version, "expected non-null version");
        checkNotNull(schemaId, "expected non-null schemaId");

        this.version = version;
        this.schemaId = schemaId;
    }

    /**
     * The unique identifier of the schema whose layout was used to write this {@link HybridRowHeader}.
     *
     * @return unique identifier of the schema whose layout was used to write this {@link HybridRowHeader}.
     */
    @NotNull
    public SchemaId schemaId() {
        return this.schemaId;
    }

    /**
     * The version of the HybridRow serialization library used to write this {@link HybridRowHeader}.
     *
     * @return version of the HybridRow serialization library used to write this {@link HybridRowHeader}.
     */
    @NotNull
    public HybridRowVersion version() {
        return this.version;
    }

    /**
     * Extracts a {@link HybridRowHeader HybridRow header} from a {@link ByteBuf}.
     * <p>
     * The decode operation starts at the current reader index and increases the reader index by the length of the
     * {@link HybridRowHeader HybridRow header}.
     *
     * @param buffer the {@link ByteBuf} to decode.
     *
     * @return a new {@link HybridRowHeader} instance.
     */
    @NotNull
    public static HybridRowHeader decode(@NotNull final ByteBuf buffer) {

        checkNotNull(buffer, "expected non-null buffer");

        checkArgument(buffer.readableBytes() >= HybridRowVersion.BYTES,
            "expected buffer with at least %s readable bytes",
            buffer.readableBytes());

        return new HybridRowHeader(HybridRowVersion.from(buffer.readByte()), SchemaId.from(buffer.readIntLE()));
    }
}
