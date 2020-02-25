// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;

import org.jetbrains.annotations.NotNull;;

import static com.azure.cosmos.base.Preconditions.checkNotNull;

public abstract class LayoutIndexedScope extends LayoutTypeScope {

    protected LayoutIndexedScope(
        @NotNull final LayoutCode code,
        final boolean immutable,
        final boolean isSizedScope,
        final boolean isFixedArity,
        final boolean isUniqueScope,
        final boolean isTypedScope) {
        super(code, immutable, isSizedScope, true, isFixedArity, isUniqueScope, isTypedScope);
    }

    @Override
    public void readSparsePath(@NotNull final RowBuffer buffer, @NotNull final RowCursor edit) {
        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        edit.pathToken(0);
        edit.pathOffset(0);
    }
}
