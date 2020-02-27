// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;

import java.util.List;

/**
 * An optional interface that indicates a {@link LayoutType} can also write using a {@link List}
 *
 * @param <TElement> The sub-element type to be written
 */
public interface LayoutListWritable<TElement> extends ILayoutType {

    /**
     * Write fixed list result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param column the column
     * @param value the value
     *
     * @return the result
     */
    Result writeFixedList(RowBuffer buffer, RowCursor scope, LayoutColumn column, List<TElement> value);

    /**
     * Write sparse list result.
     *
     * @param buffer the buffer
     * @param edit the edit
     * @param value the value
     *
     * @return the result
     */
    Result writeSparseList(RowBuffer buffer, RowCursor edit, TElement value);

    /**
     * Write sparse list result.
     *
     * @param buffer the buffer
     * @param edit the edit
     * @param value the value
     * @param options the options
     *
     * @return the result
     */
    Result writeSparseList(RowBuffer buffer, RowCursor edit, List<TElement> value, UpdateOptions options);

    /**
     * Write variable list result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param column the column
     * @param value the value
     *
     * @return the result
     */
    Result writeVariableList(RowBuffer buffer, RowCursor scope, LayoutColumn column, List<TElement> value);
}
