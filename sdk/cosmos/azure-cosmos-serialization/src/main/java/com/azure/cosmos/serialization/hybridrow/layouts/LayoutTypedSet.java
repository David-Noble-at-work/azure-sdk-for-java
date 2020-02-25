// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.core.Out;

import org.jetbrains.annotations.NotNull;;

import static com.azure.cosmos.base.Preconditions.checkArgument;
import static com.azure.cosmos.base.Preconditions.checkNotNull;
import static com.azure.cosmos.base.Preconditions.checkState;

public final class LayoutTypedSet extends LayoutUniqueScope {

    public LayoutTypedSet(boolean immutable) {
        super(immutable ? LayoutCode.IMMUTABLE_TYPED_SET_SCOPE : LayoutCode.TYPED_SET_SCOPE, immutable, true, true);
    }

    @Override
    public int countTypeArgument(@NotNull final TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkState(value.count() == 1);
        return LayoutCode.BYTES + value.get(0).type().countTypeArgument(value.get(0).typeArgs());
    }

    @NotNull
    @Override
    public TypeArgument fieldType(@NotNull final RowCursor scope) {
        checkNotNull(scope, "expected non-null scope");
        return scope.scopeTypeArgs().get(0);
    }

    @Override
    public boolean hasImplicitTypeCode(@NotNull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        checkState(edit.index() >= 0);
        checkState(edit.scopeTypeArgs().count() == 1);
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(0).type().layoutCode());
    }

    @NotNull
    public String name() {
        return this.isImmutable() ? "im_set_t" : "set_t";
    }

    @Override
    @NotNull
    public TypeArgumentList readTypeArgumentList(
        @NotNull final RowBuffer buffer,
        final int offset,
        @NotNull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        return new TypeArgumentList(readTypeArgument(buffer, offset, lengthInBytes));
    }

    @Override
    public void setImplicitTypeCode(@NotNull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
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

        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.set(buffer.writeTypedSet(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(
        @NotNull final RowBuffer buffer,
        final int offset,
        @NotNull final TypeArgumentList value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(value, "expected non-null value");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);
        checkArgument(value.count() == 1, "expected a single value count, not %s", value.count());

        buffer.writeSparseTypeCode(offset, this.layoutCode());
        final TypeArgument typeArg = value.get(0);
        int lengthInBytes = LayoutCode.BYTES;
        lengthInBytes += typeArg.type().writeTypeArgument(buffer, offset + lengthInBytes, typeArg.typeArgs());

        return lengthInBytes;
    }
}
