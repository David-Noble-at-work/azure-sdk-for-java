// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.core.Out;

import org.jetbrains.annotations.NotNull;;

import static com.azure.cosmos.base.Preconditions.checkArgument;

public final class LayoutUInt16 extends LayoutTypePrimitive<Integer> {

    public LayoutUInt16() {
        super(LayoutCode.UINT_16, Short.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    @NotNull
    public String name() {
        return "uint16";
    }

    @Override
    @NotNull
    public Result readFixed(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn column,
                            @NotNull Out<Integer> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(0);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readUInt16(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result readSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Out<Integer> value) {

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(0);
            return result;
        }

        value.set(buffer.readSparseUInt16(edit));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeFixed(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn column,
                             @NotNull Integer value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeUInt16(scope.start() + column.offset(), value.shortValue());
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Integer value,
                              @NotNull UpdateOptions options) {

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseUInt16(edit, value.shortValue(), options);
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Integer value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}
