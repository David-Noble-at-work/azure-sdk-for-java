// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.base.Preconditions.checkArgument;

/**
 * The type Layout var u int.
 */
public final class LayoutVarUInt extends LayoutTypePrimitive<Long> {

    /**
     * Instantiates a new Layout var u int.
     */
    public LayoutVarUInt() {
        super(LayoutCode.VAR_UINT, 0);
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    public boolean isVarint() {
        return true;
    }

    @Override
    @NotNull
    public String name() {
        return "varuint";
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

        value.set(buffer.readSparseVarUInt(edit));
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
        value.set(buffer.readVariableUInt(varOffset));
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

        buffer.writeSparseVarUInt(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Long value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }

    @Override
    @NotNull
    public Result writeVariable(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn col,
                                @NotNull Long value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        final boolean exists = buffer.readBit(scope.start(), col.nullBit());
        final int varOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), col.offset());
        final int shift = buffer.writeVariableUInt(varOffset, value, exists);

        buffer.setBit(scope.start(), col.nullBit());
        scope.metaOffset(scope.metaOffset() + shift);
        scope.valueOffset(scope.valueOffset() + shift);

        return Result.SUCCESS;
    }
}
