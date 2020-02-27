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
import static com.azure.cosmos.implementation.base.Preconditions.checkState;

/**
 * Describes the layout of a Tagged2 field.
 */
public final class LayoutTagged2 extends LayoutIndexedScope {

    /**
     * Initializes a new Tagged2 layout.
     *
     * @param immutable {@code true} if the Tagged field is immutable and {@code false}, if it is not.
     */
    public LayoutTagged2(boolean immutable) {
        super(
            immutable ? LayoutCode.IMMUTABLE_TAGGED2_SCOPE : LayoutCode.TAGGED2_SCOPE, immutable,
            true, true, false, true
        );
    }

    @Override
    public int countTypeArgument(@NotNull TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkState(value.count() == 3);
        return value.stream()
            .map(arg -> arg.type().countTypeArgument(arg.typeArgs()))
            .reduce(LayoutCode.BYTES, Integer::sum);
    }

    @Override
    public boolean hasImplicitTypeCode(@NotNull RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        checkState(edit.index() >= 0);
        checkState(edit.scopeTypeArgs().count() > edit.index());
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(edit.index()).type().layoutCode());
    }

    @Override
    @NotNull
    public String name() {
        return this.isImmutable() ? "im_tagged2_t" : "tagged2_t";
    }

    @Override
    @NotNull
    public TypeArgumentList readTypeArgumentList(
        @NotNull final RowBuffer buffer, final int offset, @NotNull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        checkArgument(offset >= 0, "expected non-negative offset, not %s", offset);

        final TypeArgument[] typeArgs = new TypeArgument[] {
            new TypeArgument(LayoutTypes.UINT_8, TypeArgumentList.EMPTY),
            null,
            null };

        final Out<Integer> len = new Out<>();
        int sum = 0;

        for (int i = 1; i < 3; i++) {
            typeArgs[i] = readTypeArgument(buffer, offset + sum, len);
            sum += len.get();
        }

        lengthInBytes.set(sum);
        return new TypeArgumentList(typeArgs);
    }

    @Override
    public void setImplicitTypeCode(@NotNull RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        edit.cellType(edit.scopeTypeArgs().get(edit.index()).type());
        edit.cellTypeArgs(edit.scopeTypeArgs().get(edit.index()).typeArgs());
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

        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.writeTypedTuple(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(@NotNull RowBuffer buffer, int offset, @NotNull TypeArgumentList value) {

        checkState(value.count() == 3);

        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lengthInBytes = LayoutCode.BYTES;

        for (int i = 1; i < value.count(); i++) {
            TypeArgument arg = value.get(i);
            lengthInBytes += arg.type().writeTypeArgument(buffer, offset + lengthInBytes, arg.typeArgs());
        }

        return lengthInBytes;
    }
}
