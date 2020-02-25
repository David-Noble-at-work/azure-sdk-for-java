// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.core.Out;

import org.jetbrains.annotations.NotNull;;

import static com.azure.cosmos.base.Preconditions.checkNotNull;

public final class LayoutObject extends LayoutPropertyScope {

    private final TypeArgument typeArg;

    public LayoutObject(boolean immutable) {
        super(immutable ? LayoutCode.IMMUTABLE_OBJECT_SCOPE : LayoutCode.OBJECT_SCOPE, immutable);
        this.typeArg = new TypeArgument(this);
    }

    @Override
    @NotNull
    public String name() {
        return this.isImmutable() ? "im_object" : "object";
    }

    public TypeArgument typeArg() {
        return this.typeArg;
    }


    @Override
    @NotNull
    public Result writeScope(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor edit,
        @NotNull TypeArgumentList typeArgs, @NotNull Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @NotNull
    public Result writeScope(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor edit,
        @NotNull TypeArgumentList typeArgs,
        @NotNull UpdateOptions options, @NotNull Out<RowCursor> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(typeArgs, "expected non-null typeArgs");
        checkNotNull(options, "expected non-null options");
        checkNotNull(value, "expected non-null value");

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.set(buffer.writeSparseObject(edit, this, options));
        return Result.SUCCESS;
    }
}
