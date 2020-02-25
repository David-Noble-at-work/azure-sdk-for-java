// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.core.Out;

import org.jetbrains.annotations.NotNull;;

import static com.azure.cosmos.base.Preconditions.checkArgument;

public final class LayoutVarInt extends LayoutTypePrimitive<Long> {

    public LayoutVarInt() {
        super(LayoutCode.VAR_INT, 0);
    }

    public boolean isFixed() {
        return false;
    }

    public boolean isVarint() {
        return true;
    }

    @NotNull
    public String name() {
        return "varint";
    }

    @Override
    @NotNull
    public Result readFixed(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn column,
                            @NotNull Out<Long> value) {
        assert false : "not implemented";
        value.set(0L);
        return Result.FAILURE;
    }

    @Override
    @NotNull
    public Result readSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Out<Long> value) {

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(0L);
            return result;
        }

        value.set(buffer.readSparseVarInt(edit));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result readVariable(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn column,
                               @NotNull Out<Long> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(0L);
            return Result.NOT_FOUND;
        }

        int varOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
        value.set(buffer.readVariableInt(varOffset));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeFixed(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn column,
                             @NotNull Long value) {
        assert false : "not implemented";
        return Result.FAILURE;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Long value,
                              @NotNull UpdateOptions options) {

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseVarInt(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Long value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }

    @Override
    @NotNull
    public Result writeVariable(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn column,
                                @NotNull Long value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        boolean exists = buffer.readBit(scope.start(), column.nullBit());
        int varOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
        int shift = buffer.writeVariableInt(varOffset, value, exists);

        buffer.setBit(scope.start(), column.nullBit());
        scope.metaOffset(scope.metaOffset() + shift);
        scope.valueOffset(scope.valueOffset() + shift);

        return Result.SUCCESS;
    }
}
