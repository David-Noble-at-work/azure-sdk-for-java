// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import org.jetbrains.annotations.NotNull;

import static com.azure.cosmos.base.Preconditions.checkArgument;
import static com.azure.cosmos.base.Preconditions.checkNotNull;
import static com.azure.cosmos.base.Preconditions.checkState;

/**
 * Describes the layout of a TypeMap field.
 */
public final class LayoutTypedMap extends LayoutUniqueScope {

    /**
     * Initializes a new TypedMap layout.
     *
     * @param immutable {@code true} if the TypedMap field is immutable and {@code false}, if it is not.
     */
    public LayoutTypedMap(boolean immutable) {
        super(
            immutable ? LayoutCode.IMMUTABLE_TYPED_MAP_SCOPE : LayoutCode.TYPED_MAP_SCOPE, immutable,
            true, true);
    }

    @Override
    public int countTypeArgument(@NotNull final TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        checkState(value.count() == 2);
        return value.stream()
            .map(arg -> arg.type().countTypeArgument(arg.typeArgs()))
            .reduce(LayoutCode.BYTES, Integer::sum);
    }

    @NotNull
    @Override
    public TypeArgument fieldType(@NotNull final RowCursor scope) {
        checkNotNull(scope, "expected non-null scope");
        return new TypeArgument(
            scope.scopeType().isImmutable() ? LayoutTypes.IMMUTABLE_TYPED_TUPLE : LayoutTypes.TYPED_TUPLE,
            scope.scopeTypeArgs());
    }

    @Override
    public boolean hasImplicitTypeCode(@NotNull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        return true;
    }

    @NotNull
    public String name() {
        return this.isImmutable() ? "im_map_t" : "map_t";
    }

    @Override
    @NotNull
    public TypeArgumentList readTypeArgumentList(
        @NotNull final RowBuffer buffer, final int offset, @NotNull final Out<Integer> lengthInBytes) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(lengthInBytes, "expected non-null lengthInBytes");

        TypeArgument[] typeArguments = new TypeArgument[2];
        Out<Integer> length = new Out<>();
        int index = 0;

        for (int i = 0; i < 2; i++) {
            typeArguments[i] = readTypeArgument(buffer, offset + index, length);
            index += length.get();
        }

        lengthInBytes.set(index);
        return new TypeArgumentList(typeArguments);
    }

    @Override
    public void setImplicitTypeCode(@NotNull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        edit.cellType(edit.scopeType().isImmutable() ? LayoutTypes.IMMUTABLE_TYPED_TUPLE : LayoutTypes.TYPED_TUPLE);
        edit.cellTypeArgs(edit.scopeTypeArgs());
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

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(typeArgs, "expected non-null typeArgs");
        checkNotNull(options, "expected non-null options");
        checkNotNull(value, "expected non-null value");

        final Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.setAndGet(null);
            return result;
        }

        value.set(buffer.writeTypedMap(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(
        @NotNull final RowBuffer buffer, final int offset, @NotNull final TypeArgumentList value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(value, "expected non-null value");
        checkArgument(value.count() == 2, "expected value count of 2, not %s", value.count());

        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lengthInBytes = LayoutCode.BYTES;

        for (TypeArgument arg : value.list()) {
            lengthInBytes += arg.type().writeTypeArgument(buffer, offset + lengthInBytes, arg.typeArgs());
        }

        return lengthInBytes;
    }
}
