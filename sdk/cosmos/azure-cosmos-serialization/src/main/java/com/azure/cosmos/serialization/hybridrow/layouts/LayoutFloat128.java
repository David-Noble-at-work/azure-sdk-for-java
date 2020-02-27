// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Float128;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.base.Preconditions.checkArgument;

/**
 * Describes the layout of a Float128 field.
 */
public final class LayoutFloat128 extends LayoutTypePrimitive<Float128> {

    /**
     * Initialize a new Float128 layout.
     */
    public LayoutFloat128() {
        super(LayoutCode.FLOAT_128, Float128.BYTES);
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    @NotNull
    public String name() {
        return "float128";
    }

    @Override
    @NotNull
    public Result readFixed(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull LayoutColumn column,
        @NotNull Out<Float128> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.setAndGet(null);
            return Result.NOT_FOUND;
        }

        value.setAndGet(buffer.readFloat128(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result readSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Out<Float128> value) {

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.setAndGet(buffer.readSparseFloat128(edit));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeFixed(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull LayoutColumn column,
        @NotNull Float128 value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeFloat128(scope.start() + column.offset(), value);
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor edit,
        @NotNull Float128 value,
        @NotNull UpdateOptions options) {

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseFloat128(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Float128 value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}
