// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.SchemaId;
import com.azure.cosmos.serialization.hybridrow.schemas.StorageKind;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Stack;

import static com.azure.cosmos.implementation.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * The type Layout builder.
 */
public final class LayoutBuilder {

    private final String name;
    private final SchemaId schemaId;
    private LayoutBit.Allocator bitAllocator;
    private ArrayList<LayoutColumn> fixedColumns;
    private int fixedCount;
    private int fixedSize;
    private Stack<LayoutColumn> scope;
    private ArrayList<LayoutColumn> sparseColumns;
    private int sparseCount;
    private ArrayList<LayoutColumn> varColumns;
    private int varCount;

    /**
     * Instantiates a new Layout builder.
     *
     * @param name the name
     * @param schemaId the schema id
     */
    // [ <present bits>
    //   <bool bits>
    //   <fixed_1> <fixed_2> ... <fixed_n>
    //   <var_1> <var_2> ... <var_n>
    //   <sparse_1> <sparse_2> ... <sparse_o>
    // ]
    public LayoutBuilder(String name, SchemaId schemaId) {
        this.name = name;
        this.schemaId = schemaId;
        this.reset();
    }

    /**
     * End object scope.
     */
    public void endObjectScope() {
        checkArgument(this.scope.size() > 0);
        this.scope.pop();
    }

    /**
     * Add fixed column.
     *
     * @param path the path
     * @param type the type
     * @param nullable the nullable
     * @param length the length
     */
    public void addFixedColumn(@NotNull String path, @NotNull LayoutType type, boolean nullable, int length) {

        checkNotNull(path, "expected non-null path");
        checkNotNull(type, "expected non-null type");
        checkArgument(length >= 0);
        checkArgument(!type.isVarint());

        final LayoutColumn column;

        if (type.isNull()) {
            checkArgument(nullable);
            LayoutBit boolBit = this.bitAllocator.allocate();
            LayoutBit nullBit = LayoutBit.INVALID;
            int offset = 0;
            column = new LayoutColumn(
                path, type, TypeArgumentList.EMPTY, StorageKind.FIXED, this.parent(), this.fixedCount, offset,
                boolBit, nullBit, 0);
        } else if (type.isBoolean()) {
            LayoutBit nullBit = nullable ? this.bitAllocator.allocate() : LayoutBit.INVALID;
            LayoutBit boolBit = this.bitAllocator.allocate();
            int offset = 0;
            column = new LayoutColumn(
                path, type, TypeArgumentList.EMPTY, StorageKind.FIXED, this.parent(), this.fixedCount, offset,
                nullBit, boolBit, 0);
        } else {
            LayoutBit boolBit = LayoutBit.INVALID;
            LayoutBit nullBit = nullable ? this.bitAllocator.allocate() : LayoutBit.INVALID;
            int offset = this.fixedSize;
            column = new LayoutColumn(
                path, type, TypeArgumentList.EMPTY, StorageKind.FIXED, this.parent(), this.fixedCount, offset,
                nullBit, boolBit, length);
            this.fixedSize += type.isFixed() ? type.size() : length;
        }

        this.fixedCount++;
        this.fixedColumns.add(column);
    }

    /**
     * Add object scope.
     *
     * @param path the path
     * @param type the type
     */
    public void addObjectScope(String path, LayoutType type) {

        LayoutColumn column = new LayoutColumn(path, type, TypeArgumentList.EMPTY, StorageKind.SPARSE, this.parent(),
            this.sparseCount, -1, LayoutBit.INVALID, LayoutBit.INVALID, 0);

        this.sparseCount++;
        this.sparseColumns.add(column);
        this.scope.push(column);
    }

    /**
     * Add sparse column.
     *
     * @param path the path
     * @param type the type
     */
    public void addSparseColumn(@NotNull final String path, @NotNull final LayoutType type) {

        checkNotNull(path, "expected non-null path");
        checkNotNull(type, "expected non-null type");

        final LayoutColumn column = new LayoutColumn(
            path, type, TypeArgumentList.EMPTY, StorageKind.SPARSE, this.parent(), this.sparseCount, -1,
            LayoutBit.INVALID, LayoutBit.INVALID, 0);

        this.sparseCount++;
        this.sparseColumns.add(column);
    }

    /**
     * Add typed scope.
     *
     * @param path the path
     * @param type the type
     * @param typeArgs the type args
     */
    public void addTypedScope(
        @NotNull final String path, @NotNull final LayoutType type, @NotNull final TypeArgumentList typeArgs) {

        final LayoutColumn column = new LayoutColumn(
            path, type, typeArgs, StorageKind.SPARSE, this.parent(), this.sparseCount, -1, LayoutBit.INVALID,
            LayoutBit.INVALID, 0);

        this.sparseCount++;
        this.sparseColumns.add(column);
    }

    /**
     * Add variable column.
     *
     * @param path the path
     * @param type the type
     * @param length the length
     */
    public void addVariableColumn(String path, LayoutType type, int length) {

        checkNotNull(path, "expected non-null path");
        checkNotNull(type, "expected non-null type");
        checkArgument(length >= 0);
        checkArgument(type.allowVariable());

        final LayoutColumn column = new LayoutColumn(
            path, type, TypeArgumentList.EMPTY, StorageKind.VARIABLE, this.parent(), this.varCount, this.varCount,
            this.bitAllocator.allocate(), LayoutBit.INVALID, length);

        this.varCount++;
        this.varColumns.add(column);
    }

    /**
     * Build layout.
     *
     * @return the layout
     */
    public Layout build() {

        // Compute offset deltas. Offset boolean values by the present byte count and fixed fields by the sum of the
        // present and boolean value count.

        int fixedDelta = this.bitAllocator.numBytes();
        int varIndexDelta = this.fixedCount;

        // Update the fixedColumns with the delta before freezing them.

        ArrayList<LayoutColumn> updatedColumns =
            new ArrayList<LayoutColumn>(this.fixedColumns.size() + this.varColumns.size());

        for (LayoutColumn column : this.fixedColumns) {
            column.offset(column.offset() + fixedDelta);
            updatedColumns.add(column);
        }

        for (LayoutColumn column : this.varColumns) {
            // Adjust variable column indexes such that they begin immediately following the last fixed column.
            column.index(column.index() + varIndexDelta);
            updatedColumns.add(column);
        }

        updatedColumns.addAll(this.sparseColumns);

        Layout layout = new Layout(
            this.name,
            this.schemaId,
            this.bitAllocator.numBytes(),
            this.fixedSize + fixedDelta,
            updatedColumns);

        this.reset();
        return layout;
    }

    private LayoutColumn parent() {
        if (this.scope.empty()) {
            return null;
        }
        return this.scope.peek();
    }

    private void reset() {
        this.bitAllocator = new LayoutBit.Allocator();
        this.fixedSize = 0;
        this.fixedCount = 0;
        this.fixedColumns = new ArrayList<LayoutColumn>();
        this.varCount = 0;
        this.varColumns = new ArrayList<LayoutColumn>();
        this.sparseCount = 0;
        this.sparseColumns = new ArrayList<LayoutColumn>();
        this.scope = new Stack<LayoutColumn>();
    }
}
