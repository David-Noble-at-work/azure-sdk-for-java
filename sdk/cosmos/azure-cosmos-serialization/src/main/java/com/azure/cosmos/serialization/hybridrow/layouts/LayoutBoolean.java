// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.base.Preconditions.checkArgument;
import static com.azure.cosmos.base.Preconditions.checkNotNull;

/**
 * The type Layout boolean.
 */
public final class LayoutBoolean extends LayoutTypePrimitive<Boolean> implements ILayoutType {

    /**
     * Instantiates a new Layout boolean.
     *
     * @param value the value
     */
    public LayoutBoolean(boolean value) {
        super(value ? LayoutCode.BOOLEAN : LayoutCode.BOOLEAN_FALSE, 0);
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    @NotNull
    public String name() {
        return "bool";
    }

    @Override
    @NotNull
    public Result readFixed(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor scope,
        @NotNull final LayoutColumn column,
        @NotNull final Out<Boolean> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(false);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readBit(scope.start(), column.booleanBit()));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result readSparse(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor edit,
        @NotNull final Out<Boolean> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());
        if (result != Result.SUCCESS) {
            value.set(false);
            return result;
        }

        value.set(buffer.readSparseBoolean(edit));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeFixed(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor scope,
        @NotNull final LayoutColumn column,
        @NotNull final Boolean value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT,
            "expected scope of %s, not %s", LayoutUDT.class, scope.scopeType().getClass());

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if (value) {
            buffer.setBit(scope.start(), column.booleanBit());
        } else {
            buffer.unsetBit(scope.start(), column.booleanBit());
        }

        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor edit,
        @NotNull final Boolean value,
        @NotNull final UpdateOptions options) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");
        checkNotNull(options, "expected non-null options");

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseBoolean(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Boolean value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}
