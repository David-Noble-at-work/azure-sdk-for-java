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
 * Describes the layout of a Nullable field
 */
public final class LayoutNullable extends LayoutIndexedScope {

    /**
     * Initializes a new Nullable field.
     *
     * @param immutable {@code true} if the Nullable field is immutable and {@code false}, if it is not.
     */
    public LayoutNullable(boolean immutable) {
        super(
            immutable ? LayoutCode.IMMUTABLE_NULLABLE_SCOPE : LayoutCode.NULLABLE_SCOPE, immutable,
            true, true, false, true
        );
    }

    @Override
    public int countTypeArgument(@NotNull final TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkArgument(value.count() == 1);
        return LayoutCode.BYTES + value.get(0).type().countTypeArgument(value.get(0).typeArgs());
    }

    @Override
    public boolean hasImplicitTypeCode(@NotNull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        checkArgument(edit.index() >= 0);
        checkArgument(edit.scopeTypeArgs().count() == 1);
        checkArgument(edit.index() == 1);
        return !LayoutCodeTraits.alwaysRequiresTypeCode(edit.scopeTypeArgs().get(0).type().layoutCode());
    }

    /**
     * Has value result.
     *
     * @param buffer the buffer
     * @param scope the scope
     *
     * @return the result
     */
    public static Result hasValue(@NotNull final RowBuffer buffer, @NotNull final RowCursor scope) {
        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkArgument(scope.scopeType() instanceof LayoutNullable);
        checkArgument(scope.index() == 1 || scope.index() == 2);
        checkArgument(scope.scopeTypeArgs().count() == 1);
        boolean hasValue = buffer.readInt8(scope.start()) != 0;
        return hasValue ? Result.SUCCESS : Result.NOT_FOUND;
    }

    @Override
    @NotNull
    public String name() {
        return this.isImmutable() ? "im_nullable" : "nullable";
    }

    @NotNull
    @Override
    public TypeArgumentList readTypeArgumentList(
        @NotNull final RowBuffer buffer, final int offset, @NotNull final Out<Integer> lengthInBytes) {
        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");
        return new TypeArgumentList(readTypeArgument(buffer, offset, lengthInBytes));
    }

    @Override
    public void setImplicitTypeCode(@NotNull RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        checkState(edit.index() == 1);
        edit.cellType(edit.scopeTypeArgs().get(0).type());
        edit.cellTypeArgs(edit.scopeTypeArgs().get(0).typeArgs());
    }

    /**
     * Write scope result.
     *
     * @param buffer the buffer
     * @param edit the edit
     * @param typeArgs the type args
     * @param hasValue the has value
     * @param value the value
     *
     * @return the result
     */
    @NotNull
    public Result writeScope(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor edit,
        @NotNull final TypeArgumentList typeArgs,
        boolean hasValue,
        @NotNull final Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, hasValue, UpdateOptions.UPSERT, value);
    }

    /**
     * Write scope result.
     *
     * @param buffer the buffer
     * @param edit the edit
     * @param typeArgs the type args
     * @param hasValue the has value
     * @param options the options
     * @param value the value
     *
     * @return the result
     */
    @NotNull
    public Result writeScope(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor edit,
        @NotNull final TypeArgumentList typeArgs,
        boolean hasValue,
        @NotNull final UpdateOptions options,
        @NotNull final Out<RowCursor> value) {

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

        buffer.writeNullable(edit, this, typeArgs, options, hasValue);
        return Result.SUCCESS;
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
        return this.writeScope(buffer, edit, typeArgs, true, options, value);
    }

    @Override
    public int writeTypeArgument(@NotNull final RowBuffer buffer, int offset, @NotNull final TypeArgumentList value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(value, "expected non-null value");
        checkArgument(offset >= 0);
        checkArgument(value.count() == 1);

        final TypeArgument typeArg = value.get(0);
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        return LayoutCode.BYTES + typeArg.type().writeTypeArgument(buffer, offset + LayoutCode.BYTES,
            typeArg.typeArgs());
    }
}
