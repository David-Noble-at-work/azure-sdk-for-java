// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.core.Out;

import org.jetbrains.annotations.NotNull;;

public final class LayoutEndScope extends LayoutTypeScope {

    public LayoutEndScope() {
        super(LayoutCode.END_SCOPE, false, false, false, false, false, false);
    }

    @NotNull
    public String name() {
        return "end";
    }

    @Override
    @NotNull
    public Result writeScope(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor scope,
        @NotNull final TypeArgumentList typeArgs,
        @NotNull final Out<RowCursor> value) {
        return this.writeScope(buffer, scope, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @NotNull
    public Result writeScope(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor scope,
        @NotNull final TypeArgumentList typeArgs,
        @NotNull final UpdateOptions options,
        @NotNull final Out<RowCursor> value) {

        assert false : "cannot write an EndScope directly";
        value.set(null);

        return Result.FAILURE;
    }
}
