// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.implementation.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * Describes the layout of a Tagged field.
 */
public final class LayoutTagged extends LayoutIndexedScope {

    /**
     * Initializes a new Tagged layout.
     *
     * @param immutable {@code true} if the Tagged field is immutable and {@code false}, if it is not.
     */
    public LayoutTagged(boolean immutable) {
        super(immutable ? LayoutCode.IMMUTABLE_TAGGED_SCOPE : LayoutCode.TAGGED_SCOPE, immutable,
            true, true, false, true
        );
    }

    @Override
    public int countTypeArgument(@NotNull final TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkArgument(value.count() == 2);
        return LayoutCode.BYTES + value.get(1).type().countTypeArgument(value.get(1).typeArgs());
    }

    @Override
    public boolean hasImplicitTypeCode(@NotNull RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        checkArgument(edit.index() >= 0);
        checkArgument(edit.scopeTypeArgs().count() > edit.index());
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(edit.index()).type().layoutCode());
    }

    @Override
    @NotNull
    public String name() {
        return this.isImmutable() ? "im_tagged_t" : "tagged_t";
    }

    @Override
    @NotNull
    public TypeArgumentList readTypeArgumentList(
        @NotNull final RowBuffer buffer, final int offset, @NotNull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        return new TypeArgumentList(
            new TypeArgument(LayoutTypes.UINT_8, TypeArgumentList.EMPTY),
            readTypeArgument(buffer, offset, lengthInBytes)
        );
    }

    @Override
    public void setImplicitTypeCode(@NotNull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        edit.cellType(edit.scopeTypeArgs().get(edit.index()).type());
        edit.cellTypeArgs(edit.scopeTypeArgs().get(edit.index()).typeArgs());
    }

    @Override
    @NotNull
    public Result writeScope(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor edit,
        @NotNull final TypeArgumentList typeArgs, @NotNull final Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @NotNull
    public Result writeScope(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor edit,
        @NotNull final TypeArgumentList typeArgs,
        @NotNull final UpdateOptions options, final @NotNull Out<RowCursor> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(typeArgs, "expected non-null typeArgs");
        checkNotNull(options, "expected non-null options");
        checkNotNull(value, "expected non-null value");

        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.writeTypedTuple(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(
        @NotNull final RowBuffer buffer, final int offset, @NotNull final TypeArgumentList value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(value, "expected non-null value");
        checkArgument(value.count() == 2);

        buffer.writeSparseTypeCode(offset, this.layoutCode());
        final TypeArgument typeArg = value.get(1);
        int lengthInBytes = LayoutCode.BYTES;
        lengthInBytes += typeArg.type().writeTypeArgument(buffer, offset + lengthInBytes, typeArg.typeArgs());

        return lengthInBytes;
    }
}
