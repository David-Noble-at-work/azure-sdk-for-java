// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.core.Utf8String;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.RowCursor;
import org.jetbrains.annotations.NotNull;

/**
 * An optional interface that indicates a {@link LayoutType} can also be read as a {@link Utf8String}.
 */
public interface LayoutUtf8Readable extends ILayoutType {

    /**
     * Read fixed span result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param column the column
     * @param value the value
     *
     * @return the result
     */
    @NotNull
    Result readFixedSpan(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<Utf8String> value);

    /**
     * Read sparse span result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param value the value
     *
     * @return the result
     */
    @NotNull
    Result readSparseSpan(RowBuffer buffer, RowCursor scope, Out<Utf8String> value);

    /**
     * Read variable span result.
     *
     * @param buffer the buffer
     * @param scope the scope
     * @param column the column
     * @param value the value
     *
     * @return the result
     */
    @NotNull
    Result readVariableSpan(RowBuffer buffer, RowCursor scope, LayoutColumn column, Out<Utf8String> value);
}
