// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.core.Out;

import org.jetbrains.annotations.NotNull;;

import static com.azure.cosmos.base.Preconditions.checkArgument;

public final class LayoutFloat64 extends LayoutTypePrimitive<Double> {

    public LayoutFloat64() {
        super(LayoutCode.FLOAT_64, Double.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    @NotNull
    public String name() {
        return "float64";
    }

    @Override
    @NotNull
    public Result readFixed(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn column,
                            @NotNull Out<Double> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(0D);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readFloat64(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result readSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Out<Double> value) {

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(0D);
            return result;
        }

        value.set(buffer.readSparseFloat64(edit));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeFixed(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn col,
                             @NotNull Double value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeFloat64(scope.start() + col.offset(), value);
        buffer.setBit(scope.start(), col.nullBit());
        return Result.SUCCESS;
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public override Result WriteSparse(ref RowBuffer b, ref RowCursor edit, double value,
    // UpdateOptions options = UpdateOptions.Upsert)
    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Double value,
                              @NotNull UpdateOptions options) {

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseFloat64(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Double value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}
