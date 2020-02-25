// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow;

import com.azure.cosmos.core.UtfAnyString;
import com.azure.cosmos.serialization.hybridrow.layouts.LayoutCode;
import com.azure.cosmos.serialization.hybridrow.layouts.LayoutEndScope;
import com.azure.cosmos.serialization.hybridrow.layouts.StringToken;

import org.jetbrains.annotations.NotNull;;

import static com.azure.cosmos.base.Preconditions.checkArgument;
import static com.azure.cosmos.base.Preconditions.checkNotNull;

public final class RowCursors {

    private RowCursors() {
    }

    public static RowCursor find(@NotNull RowCursor edit, @NotNull RowBuffer row, @NotNull UtfAnyString path) {
        checkArgument(!edit.scopeType().isIndexedScope());

        if (!(edit.cellType() instanceof LayoutEndScope)) {
            while (row.sparseIteratorMoveNext(edit)) {
                if (path.equals(row.readSparsePath(edit))) {
                    edit.exists(true);
                    break;
                }
            }
        }

        edit.writePath(path);
        edit.writePathToken(null);

        return edit;
    }

    public static RowCursor find(@NotNull RowCursor edit, @NotNull RowBuffer row, @NotNull StringToken pathToken) {

        checkNotNull(edit);
        checkNotNull(row);
        checkNotNull(pathToken);

        checkArgument(!edit.scopeType().isIndexedScope());

        if (!(edit.cellType() instanceof LayoutEndScope)) {
            while (row.sparseIteratorMoveNext(edit)) {
                if (pathToken.id() == (long) edit.pathToken()) {
                    edit.exists(true);
                    break;
                }
            }
        }

        edit.writePath(new UtfAnyString(pathToken.path()));
        edit.writePathToken(pathToken);

        return edit;
    }

    /**
     * An equivalent scope that is read-only.
     *
     * @param source source scope.
     * @return an equivalent scope that is read-only.
     */
    public static RowCursor asReadOnly(RowCursor source) {
        return source.clone().immutable(true);
    }

    /**
     * Makes a copy of the current cursor.
     * <p>
     * The two cursors will have independent and unconnected lifetimes after cloning.  However, mutations to a
     * {@link RowBuffer} can invalidate any active cursors over the same row.
     *
     * @param source source cursor.
     * @return copy of the source cursor.
     */
    public static RowCursor copy(RowCursor source) {
        return source.clone();
    }

    public static boolean moveNext(RowCursor edit, RowBuffer row) {
        edit.writePath(null);
        edit.writePathToken(null);
        return row.sparseIteratorMoveNext(edit);
    }

    public static boolean moveNext(@NotNull RowCursor edit, @NotNull RowBuffer row, @NotNull RowCursor childScope) {
        if (childScope.scopeType() != null) {
            RowCursors.skip(edit.clone(), row, childScope);
        }
        return RowCursors.moveNext(edit.clone(), row);
    }

    public static boolean moveTo(@NotNull final RowCursor edit, @NotNull final RowBuffer row, final int index) {

        checkNotNull(row);
        checkNotNull(edit);
        checkArgument(edit.index() <= index);

        edit.writePath(null);
        edit.writePathToken(null);

        while (edit.index() < index) {
            if (!row.sparseIteratorMoveNext(edit)) {
                return false;
            }
        }

        return true;
    }

    public static void skip(
        @NotNull final RowCursor edit, @NotNull final RowBuffer buffer, @NotNull final RowCursor childScope) {

        checkNotNull(edit, "expected non-null edit");
        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(childScope, "expected non-null childScope");

        checkArgument(childScope.start() == edit.valueOffset());

        if (!(childScope.cellType() instanceof LayoutEndScope)) {
            while (buffer.sparseIteratorMoveNext(childScope)) {
            }
        }

        if (childScope.scopeType().isSizedScope()) {
            edit.endOffset(childScope.metaOffset());
        } else {
            edit.endOffset(childScope.metaOffset() + LayoutCode.BYTES); // move past end of scope marker
        }
    }
}
