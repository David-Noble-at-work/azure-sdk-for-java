// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import com.azure.cosmos.serialization.hybridrow.RowCursors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.azure.cosmos.base.Preconditions.checkNotNull;

/**
 * Describes the layout of a TypeScope field.
 */
public abstract class LayoutTypeScope extends LayoutType {

    private final boolean isFixedArity;
    private final boolean isIndexedScope;
    private final boolean isSizedScope;
    private final boolean isTypedScope;
    private final boolean isUniqueScope;

    /**
     * Instantiates a new Layout type scope.
     *
     * @param code the code
     * @param immutable {@code true} if the TypeScope field is immutable and {@code false}, if it is not.
     * @param isSizedScope the is sized scope
     * @param isIndexedScope the is indexed scope
     * @param isFixedArity the is fixed arity
     * @param isUniqueScope the is unique scope
     * @param isTypedScope the is typed scope
     */
    protected LayoutTypeScope(
        @NotNull final LayoutCode code,
        final boolean immutable,
        final boolean isSizedScope,
        final boolean isIndexedScope,
        final boolean isFixedArity,
        final boolean isUniqueScope,
        final boolean isTypedScope) {

        super(code, immutable, 0);
        this.isSizedScope = isSizedScope;
        this.isIndexedScope = isIndexedScope;
        this.isFixedArity = isFixedArity;
        this.isUniqueScope = isUniqueScope;
        this.isTypedScope = isTypedScope;
    }

    /**
     * {@code true} if the {@link LayoutTypeScope} has a fixed-, not variable-length layout type.
     *
     * @return {@code true} if the {@link LayoutTypeScope} has a fixed-, not variable-length layout type.
     */
    @Override
    public boolean isFixed() {
        return false;
    }

    /**
     * {@code true} if this is a fixed arity scope.
     *
     * @return {@code true} if this is a fixed arity scope.
     */
    public boolean isFixedArity() {
        return this.isFixedArity;
    }

    /**
     * {@code true} if this is an indexed scope.
     *
     * @return {@code true} if this is an indexed scope.
     */
    public boolean isIndexedScope() {
        return this.isIndexedScope;
    }

    /**
     * {@code true} if this is a sized scope.
     *
     * @return {@code true} if this is a sized scope.
     */
    public boolean isSizedScope() {
        return this.isSizedScope;
    }

    /**
     * {@code true} if this is a typed scope.
     *
     * @return {@code true} if this is a typed scope.
     */
    public boolean isTypedScope() {
        return this.isTypedScope;
    }

    /**
     * {@code true} if the scope's elements cannot be updated directly.
     *
     * @return {@code true} if the scope's elements cannot be updated directly.
     */
    public boolean isUniqueScope() {
        return this.isUniqueScope;
    }

    /**
     * Delete scope result.
     *
     * @param buffer the buffer
     * @param edit the edit
     *
     * @return the result
     */
    @NotNull
    public final Result deleteScope(@NotNull final RowBuffer buffer, @NotNull final RowCursor edit) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");

        Result result = prepareSparseDelete(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            return result;
        }

        buffer.deleteSparse(edit);
        return Result.SUCCESS;
    }

    /**
     * {@code true} if writing an item in the specified typed scope would elide the type code because it is implied by
     * the type arguments
     *
     * @param edit a non-null {@link RowCursor} specifying a typed scope.
     *
     * @return {@code true} if the type code is implied (not written); {@code false} otherwise.
     */
    public boolean hasImplicitTypeCode(@NotNull final RowCursor edit) {
        checkNotNull(edit, "expected non-null edit");
        return false;
    }

    /**
     * Read scope result.
     *
     * @param buffer the buffer
     * @param edit the edit
     * @param value the value
     *
     * @return the result
     */
    @NotNull
    public final Result readScope(
        @NotNull final RowBuffer buffer, @NotNull final RowCursor edit, @NotNull final Out<RowCursor> value) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(edit, "expected non-null edit");
        checkNotNull(value, "expected non-null value");

        Result result = prepareSparseRead(buffer, edit, this.layoutCode());

        if (result != Result.SUCCESS) {
            value.set(null);
            return result;
        }

        boolean immutable = this.isImmutable() || edit.immutable() || edit.scopeType().isUniqueScope();
        value.set(buffer.sparseIteratorReadScope(edit, immutable));
        return Result.SUCCESS;
    }

    /**
     * Read sparse path.
     *
     * @param buffer the buffer
     * @param edit the edit
     */
    public void readSparsePath(@NotNull final RowBuffer buffer, @NotNull final RowCursor edit) {
        Out<Integer> pathLengthInBytes = new Out<>();
        Out<Integer> pathOffset = new Out<>();
        edit.pathToken(buffer.readSparsePathLen(edit.layout(), edit.valueOffset(), pathOffset, pathLengthInBytes));
        edit.pathOffset(pathOffset.get());
        edit.valueOffset(edit.valueOffset() + pathLengthInBytes.get());
    }

    /**
     * Sets implicit type code.
     *
     * @param edit the edit
     *
     * @throws UnsupportedOperationException because this method is not supported by this abstract type.
     */
    public void setImplicitTypeCode(@NotNull final RowCursor edit) {
        throw new UnsupportedOperationException();
    }

    /**
     * Write scope result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param typeArgs the type args
     * @param value the value
     *
     * @return the result
     */
    @NotNull
    public abstract Result writeScope(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull TypeArgumentList typeArgs, @NotNull Out<RowCursor> value);

    /**
     * Write scope result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param typeArgs the type args
     * @param options the options
     * @param value the value
     *
     * @return the result
     */
    @NotNull
    public abstract Result writeScope(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull TypeArgumentList typeArgs,
        @NotNull UpdateOptions options, @NotNull Out<RowCursor> value);

    /**
     * Write scope result.
     *
     * @param <TContext> the type parameter
     * @param buffer the buffer
     * @param scope the scope
     * @param typeArgs the type args
     * @param context the context
     * @param func the func
     *
     * @return the result
     */
    @NotNull
    public <TContext> Result writeScope(
        @NotNull RowBuffer buffer,
        @NotNull RowCursor scope,
        @NotNull TypeArgumentList typeArgs,
        @NotNull TContext context, @Nullable WriterFunc<TContext> func) {
        return this.writeScope(buffer, scope, typeArgs, context, func, UpdateOptions.UPSERT);
    }

    /**
     * Write scope result.
     *
     * @param <TContext> the type parameter
     * @param buffer the buffer
     * @param scope the scope
     * @param typeArgs the type args
     * @param context the context
     * @param func the func
     * @param options the options
     *
     * @return the result
     */
    @NotNull
    public <TContext> Result writeScope(
        @NotNull final RowBuffer buffer,
        @NotNull final RowCursor scope,
        @NotNull final TypeArgumentList typeArgs,
        @Nullable TContext context,
        @Nullable WriterFunc<TContext> func,
        @NotNull UpdateOptions options) {

        final Out<RowCursor> out = new Out<>();
        Result result = this.writeScope(buffer, scope, typeArgs, options, out);

        if (result != Result.SUCCESS) {
            return result;
        }

        final RowCursor childScope = out.get();

        if (func != null) {
            result = func.invoke(buffer, childScope, context);
            if (result != Result.SUCCESS) {
                this.deleteScope(buffer, scope);
                return result;
            }
        }

        RowCursors.skip(scope, buffer, childScope);
        return Result.SUCCESS;
    }

    /**
     * A functional interfaced that can be used to write content to a {@link RowBuffer}
     *
     * @param <TContext> The type of the context value passed by the caller
     */
    @FunctionalInterface
    public interface WriterFunc<TContext> {
        /**
         * Writes content to a {@link RowBuffer}.
         *
         * @param buffer The row to write to.
         * @param scope The type of the scope to write into.
         * @param context A context value provided by the caller.
         *
         * @return The result
         */
        @NotNull
        Result invoke(@NotNull RowBuffer buffer, @NotNull RowCursor scope, @Nullable TContext context);
    }
}
