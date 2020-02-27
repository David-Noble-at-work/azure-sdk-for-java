// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.serialization.hybridrow.SchemaId;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * Describes the layout of a UDT field.
 */
public final class LayoutUDT extends LayoutPropertyScope {

    /**
     * Initializes a new UDT layout.
     *
     * @param immutable {@code true} if the UDT field is immutable and {@code false}, if it is not.
     */
    public LayoutUDT(boolean immutable) {
        super(immutable ? LayoutCode.IMMUTABLE_SCHEMA : LayoutCode.SCHEMA, immutable);
    }

    @Override
    public int countTypeArgument(@NotNull TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        return LayoutCode.BYTES + SchemaId.BYTES;
    }

    @Override
    @NotNull
    public String name() {
        return this.isImmutable() ? "im_udt" : "udt";
    }

    @Override
    @NotNull
    public TypeArgumentList readTypeArgumentList(@NotNull RowBuffer row, int offset,
                                                 @NotNull Out<Integer> lengthInBytes) {
        SchemaId schemaId = row.readSchemaId(offset);
        lengthInBytes.set(SchemaId.BYTES);
        return new TypeArgumentList(schemaId);
    }

    @Override
    @NotNull
    public Result writeScope(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull TypeArgumentList typeArgs,
                             @NotNull Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @NotNull
    public Result writeScope(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull TypeArgumentList typeArgs,
                             @NotNull UpdateOptions options,
                             @NotNull Out<RowCursor> value) {

        Layout udt = buffer.resolver().resolve(typeArgs.schemaId());
        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.writeSparseUDT(edit, this, udt, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(@NotNull RowBuffer buffer, int offset, @NotNull TypeArgumentList value) {
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        buffer.writeSchemaId(offset + LayoutCode.BYTES, value.schemaId());
        return LayoutCode.BYTES + SchemaId.BYTES;
    }
}
