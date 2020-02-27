// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow;

import com.azure.cosmos.implementation.base.Suppliers;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Describes the desired behavior when mutating a hybrid row.
 */
public enum RowOptions {

    /**
     * None row options.
     */
    NONE(0),

    /**
     * Overwrite an existing value.
     * <p>
     * An existing value is assumed to exist at the offset provided.  The existing value is replaced inline.  The
     * remainder of the row is resized to accomodate either an increase or decrease in required space.
     */
    UPDATE(1),

    /**
     * Insert a new value.
     * <p>
     * An existing value is assumed NOT to exist at the offset provided.  The new value is inserted immediately at the
     * offset.  The remainder of the row is resized to accomodate either an increase or decrease in required space.
     */
    INSERT(2),

    /**
     * Update an existing value or insert a new value, if no value exists.
     * <p>
     * If a value exists, then this operation becomes {@link #UPDATE}, otherwise it becomes {@link #INSERT}.
     */
    UPSERT(3),

    /**
     * Insert a new value moving existing values to the right.
     * <p>
     * Within an array scope, inserts a new value immediately at the index moving all subsequent items to the right. In
     * any other scope behaves the same as {@link #UPSERT}.
     */
    INSERT_AT(4),

    /**
     * Delete an existing value.
     * <p>
     * If a value exists, then it is removed.  The remainder of the row is resized to accommodate a decrease in required
     * space.  If no value exists this operation is a no-op.
     */
    DELETE(5);

    /**
     * The constant BYTES.
     */
    public static final int BYTES = Integer.BYTES;

    private static final Supplier<Int2ReferenceMap<RowOptions>> mappings = Suppliers.memoize(() -> {
        RowOptions[] constants = RowOptions.class.getEnumConstants();
        int[] values = new int[constants.length];
        Arrays.setAll(values, index -> constants[index].value);
        return new Int2ReferenceArrayMap<>(values, constants);
    });

    private final int value;

    RowOptions(int value) {
        this.value = value;
    }

    /**
     * From row options.
     *
     * @param value the value
     *
     * @return the row options
     */
    public static RowOptions from(int value) {
        return mappings.get().get(value);
    }

    /**
     * Value int.
     *
     * @return the int
     */
    public int value() {
        return this.value;
    }
}
