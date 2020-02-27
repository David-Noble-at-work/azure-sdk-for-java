// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Utf8String;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import org.jetbrains.annotations.NotNull;

/**
 * An optional interface that indicates a {@link LayoutType} can also write using a {@link Utf8String}
 */
public interface LayoutUtf8Writable extends ILayoutType {

    /**
     * Write fixed result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param column the column
     * @param value the value
     *
     * @return the result
     */
    @NotNull
    Result writeFixed(RowBuffer buffer, RowCursor scope, LayoutColumn column, Utf8String value);

    /**
     * Write sparse result.
     *
     * @param buffer the buffer
     * @param edit the edit
     * @param value the value
     *
     * @return the result
     */
    @NotNull
    Result writeSparse(RowBuffer buffer, RowCursor edit, Utf8String value);

    /**
     * Write sparse result.
     *
     * @param buffer the buffer
     * @param edit the edit
     * @param value the value
     * @param options the options
     *
     * @return the result
     */
    @NotNull
    Result writeSparse(RowBuffer buffer, RowCursor edit, Utf8String value, UpdateOptions options);

    /**
     * Write variable result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param column the column
     * @param value the value
     *
     * @return the result
     */
    @NotNull
    Result writeVariable(RowBuffer buffer, RowCursor scope, LayoutColumn column, Utf8String value);
}
