// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.core.Out;

import org.jetbrains.annotations.NotNull;;

import static com.azure.cosmos.base.Preconditions.checkNotNull;

public final class LayoutTuple extends LayoutIndexedScope {

    public LayoutTuple(boolean immutable) {
        super(
            immutable ? LayoutCode.IMMUTABLE_TUPLE_SCOPE : LayoutCode.TUPLE_SCOPE,
            immutable, false, true, false, false
        );
    }

    @Override
    public int countTypeArgument(@NotNull final TypeArgumentList value) {
        checkNotNull(value, "expected non-null value");
        return value.stream()
            .map(arg -> arg.type().countTypeArgument(arg.typeArgs()))
            .reduce(LayoutCode.BYTES + RowBuffer.count7BitEncodedUInt(value.count()), Integer::sum);
    }

    @Override
    @NotNull
    public String name() {
        return this.isImmutable() ? "im_tuple" : "tuple";
    }

    @Override
    @NotNull
    public TypeArgumentList readTypeArgumentList(
        @NotNull final RowBuffer buffer, final int offset, @NotNull final Out<Integer> lengthInBytes) {

        final int numTypeArgs = (int) buffer.readVariableUInt(offset, lengthInBytes);
        final TypeArgument[] typeArgs = new TypeArgument[numTypeArgs];
        final Out<Integer> len = new Out<>();

        int sum = lengthInBytes.get();

        for (int i = 0; i < numTypeArgs; i++) {
            typeArgs[i] = readTypeArgument(buffer, offset + sum, len);
            sum += len.get();
        }

        return new TypeArgumentList(typeArgs);
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

        Result result = prepareSparseWrite(buffer, edit, new TypeArgument(this, typeArgs), options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        value.set(buffer.writeSparseTuple(edit, this, typeArgs, options));
        return Result.SUCCESS;
    }

    @Override
    public int writeTypeArgument(@NotNull RowBuffer buffer, int offset, @NotNull TypeArgumentList value) {
        buffer.writeSparseTypeCode(offset, this.layoutCode());
        int lengthInBytes = LayoutCode.BYTES;
        lengthInBytes += buffer.writeVariableUInt(offset + lengthInBytes, value.count());
        for (TypeArgument arg : value.list()) {
            lengthInBytes += arg.type().writeTypeArgument(buffer, offset + lengthInBytes, arg.typeArgs());
        }

        return lengthInBytes;
    }
}
