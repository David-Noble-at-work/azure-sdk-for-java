// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import org.jetbrains.annotations.NotNull;

/**
 * Describes the layout of an Array field.
 */
public final class LayoutArray extends LayoutIndexedScope {

    /**
     * Instantiates a new Layout array.
     *
     * @param immutable {@code true} if the Array field is immutable and {@code false}, if it is not.
     */
    public LayoutArray(final boolean immutable) {
        super(immutable
            ? LayoutCode.IMMUTABLE_ARRAY_SCOPE
            : LayoutCode.ARRAY_SCOPE, immutable, false, false, false, false);
    }

    @NotNull
    public String name() {
        return this.isImmutable() ? "im_array" : "array";
    }

    @Override
    @NotNull
    public Result writeScope(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor edit,
        @NotNull final TypeArgumentList typeArgs,
        @NotNull Out<RowCursor> value) {
        return this.writeScope(buffer, edit, typeArgs, UpdateOptions.UPSERT, value);
    }

    @Override
    @NotNull
    public Result writeScope(
        @NotNull RowBuffer buffer, @NotNull RowCursor edit, @NotNull TypeArgumentList typeArgs,
        @NotNull UpdateOptions options, @NotNull Out<RowCursor> value) {

        Result result = prepareSparseWrite(buffer, edit, this.typeArg(), options);

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        buffer.writeSparseArray(edit, this, options);
        return Result.SUCCESS;
    }
}
