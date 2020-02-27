// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;

import java.util.List;

/**
 * An optional interface that indicates a {@link LayoutType} can also read using a read-only {@link List}
 *
 * @param <TElement> The sub-element type to be written
 */
public interface LayoutListReadable<TElement> extends ILayoutType {

    /**
     * Read fixed list result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param column the column
     * @param value the value
     *
     * @return the result
     */
    Result readFixedList(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<List<TElement>> value);

    /**
     * Read sparse list result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param value the value
     *
     * @return the result
     */
    Result readSparseList(RowBuffer buffer, RowCursor scope, Out<List<TElement>> value);

    /**
     * Read variable list result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param column the column
     * @param value the value
     *
     * @return the result
     */
    Result readVariableList(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<List<TElement>> value);
}
