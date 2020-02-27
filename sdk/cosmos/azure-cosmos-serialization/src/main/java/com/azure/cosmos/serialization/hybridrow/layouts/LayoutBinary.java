// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.base.Preconditions.checkArgument;
import static com.azure.cosmos.base.Preconditions.checkNotNull;

/**
 * The type Layout binary.
 */
public final class LayoutBinary extends LayoutTypePrimitive<ByteBuf> {
    // implements
    // LayoutListWritable<Byte>,
    // LayoutListReadable<Byte>,
    // ILayoutSequenceWritable<Byte> {

    /**
     * Instantiates a new Layout binary.
     */
    public LayoutBinary() {
        super(LayoutCode.BINARY, 0);
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    @NotNull
    public String name() {
        return "binary";
    }

    @Override
    @NotNull
    public Result readFixed(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor scope,
        @NotNull final LayoutColumn column,
        @NotNull final Out<ByteBuf> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);
        checkArgument(column.size() >= 0);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(null);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readFixedBinary(scope.start() + column.offset(), column.size()));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result readSparse(
        @NotNull final RowBuffer buffer, @NotNull final RowCursor edit, @NotNull final Out<ByteBuf> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");

        final Result result = LayoutType.prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.readSparseBinary(edit));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result readVariable(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull LayoutColumn column,
        @NotNull Out<ByteBuf> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(null);
            return Result.NOT_FOUND;
        }

        final int valueOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
        value.set(buffer.readVariableBinary(valueOffset));

        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeFixed(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull LayoutColumn column,
        @NotNull ByteBuf value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);
        checkArgument(column.size() >= 0);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        final int valueOffset = scope.start() + column.offset();
        buffer.setBit(scope.start(), column.nullBit());

        buffer.writeFixedBinary(valueOffset, value, column.size());
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull ByteBuf value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }

    @Override
    @NotNull
    public Result writeSparse(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor edit,
        @NotNull ByteBuf value,
        @NotNull UpdateOptions options) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");
        checkNotNull(options, "expected non-null options");

        Result result = LayoutType.prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseBinary(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeVariable(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull LayoutColumn column,
        @NotNull ByteBuf value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if ((column.size() > 0) && (value.readableBytes() > column.size())) {
            return Result.TOO_BIG;
        }

        final boolean exists = buffer.readBit(scope.start(), column.nullBit());
        final int valueOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
        final Out<Integer> shift = new Out<>();

        buffer.writeVariableBinary(valueOffset, value, exists, shift);
        buffer.setBit(scope.start(), column.nullBit());
        scope.metaOffset(scope.metaOffset() + shift.get());
        scope.valueOffset(scope.valueOffset() + shift.get());

        return Result.SUCCESS;
    }
}
