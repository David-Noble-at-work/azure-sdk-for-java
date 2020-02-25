// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.serialization.hybridrow.codecs.DateTimeCodec;
import com.azure.cosmos.core.Out;

import org.jetbrains.annotations.NotNull;;
import java.time.OffsetDateTime;

import static com.azure.cosmos.base.Preconditions.checkArgument;

public final class LayoutDateTime extends LayoutTypePrimitive<OffsetDateTime> {

    public LayoutDateTime() {
        super(LayoutCode.DATE_TIME, DateTimeCodec.BYTES);
    }

    public boolean isFixed() {
        return true;
    }

    @NotNull
    public String name() {
        return "datetime";
    }

    @Override
    @NotNull
    public Result readFixed(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn column,
                            @NotNull Out<OffsetDateTime> value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            value.set(OffsetDateTime.MIN);
            return Result.NOT_FOUND;
        }

        value.set(buffer.readDateTime(scope.start() + column.offset()));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result readSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull Out<OffsetDateTime> value) {

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(OffsetDateTime.MIN);
            return result;
        }

        value.set(buffer.readSparseDateTime(edit));
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeFixed(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @NotNull LayoutColumn column,
                             @NotNull OffsetDateTime value) {

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        buffer.writeDateTime(scope.start() + column.offset(), value);
        buffer.setBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull OffsetDateTime value,
                              @NotNull UpdateOptions options) {

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.writeSparseDateTime(edit, value, options);
        return Result.SUCCESS;
    }

    @Override
    @NotNull
    public Result writeSparse(@NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull OffsetDateTime value) {
        return this.writeSparse(buffer, edit, value, UpdateOptions.UPSERT);
    }
}
