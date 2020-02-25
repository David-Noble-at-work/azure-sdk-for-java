// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.core.Out;

import org.jetbrains.annotations.NotNull;;

import static com.azure.cosmos.base.Preconditions.checkNotNull;
import static com.azure.cosmos.base.Preconditions.checkState;

public final class LayoutTypedArray extends LayoutIndexedScope {

    public LayoutTypedArray(boolean immutable) {
        super(
            immutable ? LayoutCode.IMMUTABLE_TYPED_ARRAY_SCOPE : LayoutCode.TYPED_ARRAY_SCOPE, immutable,
            true, false, false, true
        );
    }

    @Override
    public int countTypeArgument(@NotNull TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkState(value.count() == 1);
        return LayoutCode.BYTES + value.get(0).type().countTypeArgument(value.get(0).typeArgs());
    }

    @Override
    public boolean hasImplicitTypeCode(@NotNull RowCursor edit) {
        checkState(edit.index() >= 0);
        checkState(edit.scopeTypeArgs().count() == 1);
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(0).type().layoutCode());
    }

    @Override
    @NotNull
    public String name() {
        return this.isImmutable() ? "im_array_t" : "array_t";
    }

    @Override
    @NotNull
    public TypeArgumentList readTypeArgumentList(@NotNull RowBuffer buffer, int offset,
                                                 @NotNull Out<Integer> lenInBytes) {
        return new TypeArgumentList(readTypeArgument(buffer, offset, lenInBytes));
    }

    @Override
    public void setImplicitTypeCode(@NotNull final RowCursor edit) {
        edit.cellType(edit.scopeTypeArgs().get(0).type());
        edit.cellTypeArgs(edit.scopeTypeArgs().get(0).typeArgs());
    }

    @Override
    @NotNull
    public Result writeScope(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor edit,
        @NotNull final TypeArgumentList typeArgs,
        @NotNull final Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @NotNull
    public Result writeScope(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor edit,
        @NotNull final TypeArgumentList typeArgs,
        @NotNull final UpdateOptions options,
        @NotNull final Out<RowCursor> value) {

        final TypeArgument typeArg = new TypeArgument(this, typeArgs);
        final Result result = prepareSparseWrite(buffer, edit, typeArg, options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.writeTypedArray(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(
        @NotNull final RowBuffer buffer, final int offset, @NotNull final TypeArgumentList value) {

        checkState(value.count() == 1);

        TypeArgument typeArg = value.get(0);
        buffer.writeSparseTypeCode(offset, this.layoutCode());

        return LayoutCode.BYTES + typeArg.type().writeTypeArgument(
            buffer, offset + LayoutCode.BYTES, typeArg.typeArgs()
        );
    }
}
