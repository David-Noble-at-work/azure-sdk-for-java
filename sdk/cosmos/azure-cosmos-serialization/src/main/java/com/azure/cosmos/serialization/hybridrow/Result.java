// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow;

import com.azure.cosmos.implementation.base.Suppliers;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * The enum Result.
 */
public enum Result {

    /**
     * Success result.
     */
    SUCCESS(0),
    /**
     * Failure result.
     */
    FAILURE(1),
    /**
     * Not found result.
     */
    NOT_FOUND(2),
    /**
     * Exists result.
     */
    EXISTS(3),
    /**
     * Too big result.
     */
    TOO_BIG(4),

    /**
     * The type of an existing field does not match the expected type for this operation.
     */
    TYPE_MISMATCH(5),

    /**
     * An attempt to write in a read-only scope.
     */
    INSUFFICIENT_PERMISSIONS(6),

    /**
     * An attempt to write a field that did not match its (optional) type constraints.
     */
    TYPE_CONSTRAINT(7),

    /**
     * The byte sequence could not be parsed as a valid row.
     */
    INVALID_ROW(8),

    /**
     * The byte sequence was too short for the requested action.
     */
    INSUFFICIENT_BUFFER(9),

    /**
     * The operation was cancelled.
     */
    CANCELED(10);

    /**
     * The constant BYTES.
     */
    public static final int BYTES = Integer.BYTES;

    private static final Supplier<Int2ReferenceMap<Result>> mappings = Suppliers.memoize(() -> {
        Result[] constants = Result.class.getEnumConstants();
        int[] values = new int[constants.length];
        Arrays.setAll(values, index -> constants[index].value);
        return new Int2ReferenceOpenHashMap<>(values, constants);
    });

    private final int value;

    Result(int value) {
        this.value = value;
    }

    /**
     * From result.
     *
     * @param value the value
     *
     * @return the result
     */
    public static Result from(int value) {
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
