// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.json;

import java.nio.ByteBuffer;
import java.util.UUID;

public interface JsonWriter {
    /// <summary>
    /// Gets the SerializationFormat of the JsonWriter.
    /// </summary>
    JsonSerializationFormat getSerializationFormat();

    /// <summary>
    /// Gets the current length of the internal buffer.
    /// </summary>
    long getCurrentLength();

    /// <summary>
    /// Writes the object start symbol to internal buffer.
    /// </summary>
    void writeObjectStart();

    /// <summary>
    /// Writes the object end symbol to the internal buffer.
    /// </summary>
    void writeObjectEnd();

    /// <summary>
    /// Writes the array start symbol to the internal buffer.
    /// </summary>
    void writeArrayStart();

    /// <summary>
    /// Writes the array end symbol to the internal buffer.
    /// </summary>
    void writeArrayEnd();

    /// <summary>
    /// Writes a field name to the the internal buffer.
    /// </summary>
    /// <param name="name">The name of the field to write.</param>
    void writeFieldName(String name);

    /// <summary>
    /// Writes a UTF-8 field name to the internal buffer.
    /// </summary>
    /// <param name="name"></param>
    void writeFieldName(ByteBuffer name);

    /// <summary>
    /// Writes a string to the internal buffer.
    /// </summary>
    /// <param name="value">The value of the string to write.</param>
    void writeStringValue(String value);

    /// <summary>
    /// Writes a UTF-8 string value to the internal buffer.
    /// </summary>
    /// <param name="value"></param>
    void writeStringValue(ByteBuffer value);

    /// <summary>
    /// Writes a number to the internal buffer.
    /// </summary>
    /// <param name="value">The value of the number to write.</param>
    void writeNumberValue(Number value);

    /// <summary>
    /// Writes a boolean to the internal buffer.
    /// </summary>
    /// <param name="value">The value of the boolean to write.</param>
    void writeBooleanValue(boolean value);

    /// <summary>
    /// Writes a null to the internal buffer.
    /// </summary>
    void writeNullValue();

    /// <summary>
    /// Writes an single signed byte integer to the internal buffer.
    /// </summary>
    /// <param name="value">The value of the integer to write.</param>
    void writeInt8Value(byte value);

    /// <summary>
    /// Writes an signed 2-byte integer to the internal buffer.
    /// </summary>
    /// <param name="value">The value of the integer to write.</param>
    void writeInt16Value(short value);

    /// <summary>
    /// Writes an signed 4-byte integer to the internal buffer.
    /// </summary>
    /// <param name="value">The value of the integer to write.</param>
    void writeInt32Value(int value);

    /// <summary>
    /// Writes an signed 8-byte integer to the internal buffer.
    /// </summary>
    /// <param name="value">The value of the integer to write.</param>
    void writeInt64Value(long value);

    /// <summary>
    /// Writes a single precision floating point number into the internal buffer.
    /// </summary>
    /// <param name="value">The value of the integer to write.</param>
    void writeFloat32Value(float value);

    /// <summary>
    /// Writes a double precision floating point number into the internal buffer.
    /// </summary>
    /// <param name="value">The value of the integer to write.</param>
    void writeFloat64Value(double value);

    /// <summary>
    /// Writes a 4 byte unsigned integer into the internal buffer.
    /// </summary>
    /// <param name="value">The value of the integer to write.</param>
    void writeUInt32Value(int value);

    /// <summary>
    /// Writes a Guid value into the internal buffer.
    /// </summary>
    /// <param name="value">The value of the guid to write.</param>
    void writeGuidValue(UUID value);

    /// <summary>
    /// Writes a Binary value into the internal buffer.
    /// </summary>
    /// <param name="value">The value of the bytes to write.</param>
    void writeBinaryValue(ByteBuffer value);

    /// <summary>
    /// Writes current token from a json reader to the internal buffer.
    /// </summary>
    /// <param name="reader">The JsonReader to the get the current token from.</param>
    void writeCurrentToken(JsonReader reader);

    /// <summary>
    /// Writes every token from the JsonReader to the internal buffer.
    /// </summary>
    /// <param name="reader">The JsonReader to get the tokens from.</param>
    void writeAll(JsonReader reader);

    /// <summary>
    /// Writes a fragment of a json to the internal buffer
    /// </summary>
    /// <param name="value">A section of a valid json</param>
    void writeJsonFragment(ByteBuffer value);

    /// <summary>
    /// Writes a json node to the internal buffer.
    /// </summary>
    /// <param name="navigator">The navigator to use to navigate the node</param>
    /// <param name="node">The node to write.</param>
    void writeJsonNode(JsonNavigator navigator, JsonNavigatorNode node);

    /// <summary>
    /// Gets the result of the JsonWriter.
    /// </summary>
    /// <returns>The result of the JsonWriter as an array of bytes.</returns>
    ByteBuffer getResult();
}
