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

public abstract class LayoutTypePrimitive<T> extends LayoutType implements ILayoutType {
    /**
     * Initializes a new instance of the {@link LayoutTypePrimitive} class.
     *
     * @param code the {@link LayoutCode} of this layout type.
     * @param immutable indicates whether edits of instances fields with this layout type are permitted.
     * @param size the size of fields with this layout type in bytes.
     */
    protected LayoutTypePrimitive(@NotNull LayoutCode code, boolean immutable, int size) {
        super(code, immutable, size);
    }

    /**
     * Initializes a new instance of the {@link LayoutTypePrimitive} class.
     *
     * @param code the {@link LayoutCode} of this layout type.
     * @param size the size of fields with this layout type in bytes.
     */
    protected LayoutTypePrimitive(LayoutCode code, int size) {
        super(code, size);
    }

    // TODO: DANOBLE: move methods implemented by the C# code LayoutType<T> to this type from LayoutType<T>
    // Also:
    // * Convert LayoutType<T> to a non-generic type (LayoutType, not LayoutType<T>)
    // * Ensure that all primitive types inherit from this type

    @NotNull
    public final Result deleteFixed(
        @NotNull final RowBuffer buffer, @NotNull final RowCursor scope, @NotNull final LayoutColumn column) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");

        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        if (column.nullBit().isInvalid()) {
            // Cannot delete a non-nullable fixed column.
            return Result.TYPE_MISMATCH;
        }

        buffer.unsetBit(scope.start(), column.nullBit());
        return Result.SUCCESS;
    }

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed.  The remainder of the row is resized to accommodate a decrease in required
     * space.  If no value exists this operation is a no-op.
     *
     * @param buffer target {@link RowBuffer}.
     * @param edit a {@link RowCursor} that identifies and locates the field to be deleted.
     *
     * @return {@link Result#SUCCESS} if the value was deleted; otherwise an error {@link Result}.
     */
    @NotNull
    public final Result deleteSparse(RowBuffer buffer, RowCursor edit) {

        Result result = prepareSparseDelete(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.deleteSparse(edit);
        return Result.SUCCESS;
    }

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed. The remainder of the row is resized to accommodate a decrease in required
     * space. If no value exists this operation is a no-op.
     *
     * @param buffer the target {@link RowBuffer}.
     * @param scope a {@linkplain RowCursor cursor} that identifies and locates the scope of the deletion.
     * @param column identifies and locates the value within the scope to be deleted.
     *
     * @return {@link Result#SUCCESS} if the value was deleted; otherwise an error {@link Result}.
     */
    @NotNull
    public final Result deleteVariable(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor scope,
        @NotNull final LayoutColumn column) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkArgument(scope.scopeType() instanceof LayoutUDT);

        if (scope.immutable()) {
            return Result.INSUFFICIENT_PERMISSIONS;
        }

        boolean exists = buffer.readBit(scope.start(), column.nullBit());

        if (exists) {
            int varOffset = buffer.computeVariableValueOffset(scope.layout(), scope.start(), column.offset());
            buffer.deleteVariable(varOffset, this.isVarint());
            buffer.unsetBit(scope.start(), column.nullBit());
        }

        return Result.SUCCESS;
    }

    @NotNull
    public final Result hasValue(
        @NotNull final RowBuffer buffer, @NotNull final RowCursor scope, @NotNull final LayoutColumn column) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");

        if (!buffer.readBit(scope.start(), column.nullBit())) {
            return Result.NOT_FOUND;
        }
        return Result.SUCCESS;
    }

    @NotNull
    public abstract Result readFixed(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull LayoutColumn column,
        @NotNull Out<T> value);

    @NotNull
    public abstract Result readSparse(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor edit,
        @NotNull Out<T> value);

    @NotNull
    public Result readVariable(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor scope,
        @NotNull final LayoutColumn column,
        @NotNull final Out<T> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(scope, "expected non-null scope");
        checkNotNull(column, "expected non-null column");
        checkNotNull(value, "expected non-null value");

        value.set(null);
        return Result.FAILURE;
    }

    @NotNull
    public abstract Result writeFixed(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull LayoutColumn column,
        @NotNull T value);

    @NotNull
    public abstract Result writeSparse(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor edit,
        @NotNull T value);

    @NotNull
    public abstract Result writeSparse(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor edit,
        @NotNull T value,
        @NotNull UpdateOptions options);

    @NotNull
    public Result writeVariable(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull LayoutColumn column,
        @NotNull T value) {
        return Result.FAILURE;
    }
}
