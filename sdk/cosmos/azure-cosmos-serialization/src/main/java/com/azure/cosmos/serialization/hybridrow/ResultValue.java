// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow;

public final class ResultValue<T> {

    private final Result result;
    private final T value;

    public ResultValue(Result result, T value) {
        this.result = result;
        this.value = value;
    }

    public Result getResult() {
        return this.result;
    }

    public T getValue() {
        return this.value;
    }
}
