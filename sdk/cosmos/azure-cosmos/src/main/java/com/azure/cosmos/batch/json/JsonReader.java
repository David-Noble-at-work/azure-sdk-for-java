// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.json;

import com.azure.cosmos.core.Out;

import java.nio.ByteBuffer;
import java.util.UUID;

public interface JsonReader {
    /// <summary>
    /// Gets the <see cref="JsonSerializationFormat"/> for the JsonReader
    /// </summary>
    JsonSerializationFormat getSerializationFormat();

    /// <summary>
    /// Gets the current level of nesting of the JSON that the JsonReader is reading.
    /// </summary>
    int getCurrentDepth();

    /// <summary>
    /// Gets the <see cref="JsonTokenType"/> of the current token that the JsonReader is about to read.
    /// </summary>
    JsonTokenType getCurrentTokenType();

    /// <summary>
    /// Advances the JsonReader by one token.
    /// </summary>
    /// <returns><code>true</code> if the JsonReader successfully advanced to the next token; <code>false</code> if the JsonReader has passed the end of the JSON.</returns>
    boolean read();

    /// <summary>
    /// Gets the next JSON token from the JsonReader as a double.
    /// </summary>
    /// <returns>The next JSON token from the JsonReader as a double.</returns>
    Number getNumberValue();

    /// <summary>
    /// Gets the next JSON token from the JsonReader as a string.
    /// </summary>
    /// <returns>The next JSON token from the JsonReader as a string.</returns>
    String getStringValue();

    /// <summary>
    /// Tries to get the buffered UTF-8 string value.
    /// </summary>
    /// <param name="out">The buffered UTF-8 string value if found.</param>
    /// <returns>true if the buffered UTF-8 string value was retrieved; false otherwise.</returns>
    boolean tryGetBufferedUtf8StringValue(Out<ByteBuffer> out);

    /// <summary>
    /// Tries to get the current JSON token from the JsonReader as a raw series of bytes that is buffered.
    /// </summary>
    /// <returns>true if the current JSON token was retrieved; false otherwise.</returns>
    boolean tryGetBufferedRawJsonToken(Out<ByteBuffer> out);

    /// <summary>
    /// Gets the next JSON token from the JsonReader as a 1 byte signed integer.
    /// </summary>
    /// <returns>The next JSON token from the JsonReader as a 1 byte signed integer.</returns>
    byte getInt8Value();

    /// <summary>
    /// Gets the next JSON token from the JsonReader as a 2 byte signed integer.
    /// </summary>
    /// <returns>The next JSON token from the JsonReader as a 2 byte signed integer.</returns>
    short getInt16Value();

    /// <summary>
    /// Gets the next JSON token from the JsonReader as a 4 byte signed integer.
    /// </summary>
    /// <returns>The next JSON token from the JsonReader as a 4 byte signed integer.</returns>
    int getInt32Value();

    /// <summary>
    /// Gets the next JSON token from the JsonReader as a 8 byte signed integer.
    /// </summary>
    /// <returns>The next JSON token from the JsonReader as a 8 byte signed integer.</returns>
    long getInt64Value();

    /// <summary>
    /// Gets the next JSON token from the JsonReader as a 4 byte unsigned integer.
    /// </summary>
    /// <returns>The next JSON token from the JsonReader as a 4 byte unsigned integer.</returns>
    long getUInt32Value();

    /// <summary>
    /// Gets the next JSON token from the JsonReader as a single precision floating point.
    /// </summary>
    /// <returns>The next JSON token from the JsonReader as a single precision floating point.</returns>
    float getFloat32Value();

    /// <summary>
    /// Gets the next JSON token from the JsonReader as a double precision floating point.
    /// </summary>
    /// <returns>The next JSON token from the JsonReader as a double precision floating point.</returns>
    double getFloat64Value();

    /// <summary>
    /// Gets the next JSON token from the JsonReader as a GUID.
    /// </summary>
    /// <returns>The next JSON token from the JsonReader as a GUID.</returns>
    UUID getGuidValue();

    /// <summary>
    /// Gets the next JSON token from the JsonReader as a binary list.
    /// </summary>
    /// <returns>The next JSON token from the JsonReader as a binary list.</returns>
    ByteBuffer getBinaryValue();
}
