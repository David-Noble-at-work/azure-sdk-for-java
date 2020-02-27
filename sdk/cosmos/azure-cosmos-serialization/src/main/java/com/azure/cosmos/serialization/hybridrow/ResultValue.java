// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow;

/**
 * The type Result value.
 *
 * @param <T> the type parameter
 */
public final class ResultValue<T> {

    private final Result result;
    private final T value;

    /**
     * Instantiates a new Result value.
     *
     * @param result the result
     * @param value the value
     */
    public ResultValue(Result result, T value) {
        this.result = result;
        this.value = value;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public Result getResult() {
        return this.result;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public T getValue() {
        return this.value;
    }
}
