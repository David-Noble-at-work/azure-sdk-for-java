// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.implementation.base.Preconditions.checkArgument;

/**
 * Describes the layout of an Int64 field.
 */
public final class LayoutInt64 extends LayoutTypePrimitive<Long> {

    /**
     * Initializes a new Int64 layout.
     */
    public LayoutInt64() {
        super(LayoutCode.INT_64, Long.BYTES);
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    @NotNull
    public String name() {
        return "int64";
    }

    @Override
    @NotNull
    public Result readFixed(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull LayoutColumn column,
        @NotNull Out<Long> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(0L);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readInt64(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result readSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Out<Long> value) {

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(0L);
            return result;
        }

        value.set(buffer.readSparseInt64(edit));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeFixed(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull LayoutColumn column,
        @NotNull Long value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeInt64(scope.start() + column.offset(), value);
        buffer.setBit(scope.start(), column.nullBit());

        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor edit,
        @NotNull Long value,
        @NotNull UpdateOptions options) {

        final Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseInt64(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Long value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}
