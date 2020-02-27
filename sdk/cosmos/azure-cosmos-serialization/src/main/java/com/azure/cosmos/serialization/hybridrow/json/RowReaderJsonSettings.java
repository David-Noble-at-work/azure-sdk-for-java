// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.json;

/**
 * The type Row reader json settings.
 */
public final class RowReaderJsonSettings {

    private final String indentChars;
    private final char quoteChar;

    /**
     * Instantiates a new Row reader json settings.
     *
     * @param indentChars the indent chars
     */
    public RowReaderJsonSettings(String indentChars) {
        this(indentChars, '"');
    }

    /**
     * Instantiates a new Row reader json settings.
     */
    public RowReaderJsonSettings() {
        this("  ", '"');
    }

    /**
     * Instantiates a new Row reader json settings.
     *
     * @param indentChars the indent chars
     * @param quoteChar the quote char
     */
    public RowReaderJsonSettings(String indentChars, char quoteChar) {
        this.indentChars = indentChars;
        this.quoteChar = quoteChar;
    }

    /**
     * If non-null then child objects are indented by one copy of this string per level.
     *
     * @return indentation characters.
     */
    public String indentChars() {
        return this.indentChars;
    }

    /**
     * The current quote character.
     * <p>
     * May be double or single quote.
     *
     * @return quote character.
     */
    public char quoteChar() {
        return this.quoteChar;
    }
}
