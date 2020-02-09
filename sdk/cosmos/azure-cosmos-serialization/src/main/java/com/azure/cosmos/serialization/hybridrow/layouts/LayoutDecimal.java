// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.serialization.hybridrow.codecs.DecimalCodec;
import com.azure.cosmos.core.Out;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

import static com.azure.cosmos.base.Preconditions.checkArgument;

public final class LayoutDecimal extends LayoutTypePrimitive<BigDecimal> {

    public LayoutDecimal() {
        super(LayoutCode.DECIMAL, DecimalCodec.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    @Nonnull
    public String name() {
        return "decimal";
    }

    @Override
    @Nonnull
    public Result readFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column,
                            @Nonnull Out<BigDecimal> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.setAndGet(new BigDecimal(0));
            return Result.NOT_FOUND;
        }

        value.setAndGet(buffer.readDecimal(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result readSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit,
                             @Nonnull Out<BigDecimal> value) {
        Result result = prepareSparseRead(buffer, edit, this.layoutCode());
        if (result != Result.SUCCESS) {
            value.setAndGet(new BigDecimal(0));
            return result;
        }

        value.setAndGet(buffer.readSparseDecimal(edit));
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeFixed(@Nonnull RowBuffer buffer, @Nonnull RowCursor scope, @Nonnull LayoutColumn column,
                             @Nonnull BigDecimal value) {
        checkArgument(scope.scopeType() instanceof LayoutUDT);
        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeDecimal(scope.start() + column.offset(), value);
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull BigDecimal value,
                              @Nonnull UpdateOptions options) {
        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);
        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseDecimal(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @Nonnull
    public Result writeSparse(@Nonnull RowBuffer buffer, @Nonnull RowCursor edit, @Nonnull BigDecimal value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}
