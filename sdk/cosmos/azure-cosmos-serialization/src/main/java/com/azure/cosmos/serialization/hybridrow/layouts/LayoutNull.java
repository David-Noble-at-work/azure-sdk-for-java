// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.NullValue;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.core.Out;

import org.jetbrains.annotations.NotNull;;

import static com.azure.cosmos.base.Preconditions.checkArgument;

public final class LayoutNull extends LayoutTypePrimitive<NullValue> implements ILayoutType {

    public LayoutNull() {
        super(LayoutCode.NULL, 0);
    }

    public boolean isFixed() {
        return true;
    }

    public boolean isNull() {
        return true;
    }

    @NotNull
    public String name() {
        return "null";
    }

    @Override
    @NotNull
    public Result readFixed(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn column,
                            @NotNull Out<NullValue> value) {
        checkArgument(scope.scopeType() instanceof LayoutUDT);
        value.set(NullValue.DEFAULT);
        if (!buffer.readBit(scope.start(), column.nullBit())) {
            return Result.NOT_FOUND;
        }
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result readSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Out<NullValue> value) {
        Result result = prepareSparseRead(buffer, edit, this.layoutCode());
        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }
        value.set(buffer.readSparseNull(edit));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeFixed(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn column,
                             @NotNull NullValue value) {
        checkArgument(scope.scopeType() instanceof LayoutUDT);
        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull NullValue value,
                              @NotNull UpdateOptions options) {
        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);
        if (result != Result.SUCCESS) {
            return result;
        }
        buffer.writeSparseNull(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull NullValue value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}
