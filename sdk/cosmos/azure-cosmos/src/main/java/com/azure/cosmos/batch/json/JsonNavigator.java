// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.json;

import com.azure.cosmos.core.Out;

import java.nio.ByteBuffer;
import java.util.UUID;

public interface JsonNavigator {
    /// <summary>
    /// Gets the <see cref="JsonSerializationFormat"/> for the IJsonNavigator.
    /// </summary>
    JsonSerializationFormat getSerializationFormat();

    /// <summary>
    /// Gets the root node.
    /// </summary>
    /// <returns>The root node.</returns>
    JsonNavigatorNode getRootNode();

    /// <summary>
    /// Gets the <see cref="JsonNodeType"/> type for a particular node
    /// </summary>
    /// <param name="node">The the node you want to know the type of</param>
    /// <returns><see cref="JsonNodeType"/> for the node</returns>
    JsonNodeType getNodeType(JsonNavigatorNode node);

    /// <summary>
    /// Gets the numeric value for a node
    /// </summary>
    /// <param name="node">The node you want the number value from.</param>
    /// <returns>A double that represents the number value in the node.</returns>
    Number getNumberValue(JsonNavigatorNode node);

    /// <summary>
    /// Tries to get the buffered string value from a node.
    /// </summary>
    /// <param name="stringNode">The node to get the buffered string from.</param>
    /// <param name="out">The buffered string value if possible</param>
    /// <returns><code>true</code> if the JsonNavigator successfully got the buffered string value; <code>false</code> if the JsonNavigator failed to get the buffered string value.</returns>
    boolean tryGetBufferedUtf8StringValue(JsonNavigatorNode stringNode, Out<ByteBuffer> out);

    /// <summary>
    /// Gets a string value from a node.
    /// </summary>
    /// <param name="node">The node to get the string value from.</param>
    /// <returns>The string value from the node.</returns>
    String getStringValue(JsonNavigatorNode node);

    /// <summary>
    /// Gets the numeric value for a node as a signed byte.
    /// </summary>
    /// <param name="node">The node you want the number value from.</param>
    /// <returns>A sbyte value that represents the number value in the node.</returns>
    byte getInt8Value(JsonNavigatorNode node);

    /// <summary>
    /// Gets the numeric value for a node as a 16-bit signed integer.
    /// </summary>
    /// <param name="node">The node you want the number value from.</param>
    /// <returns>A short value that represents the number value in the node.</returns>
    short getInt16Value(JsonNavigatorNode node);

    /// <summary>
    /// Gets the numeric value for a node as a 32-bit signed integer.
    /// </summary>
    /// <param name="node">The node you want the number value from.</param>
    /// <returns>An int value that represents the number value in the node.</returns>
    int getInt32Value(JsonNavigatorNode node);

    /// <summary>
    /// Gets the numeric value for a node as a 64-bit signed integer.
    /// </summary>
    /// <param name="node">The node you want the number value from.</param>
    /// <returns>A long value that represents the number value in the node.</returns>
    long getInt64Value(JsonNavigatorNode node);

    /// <summary>
    /// Gets the numeric value for a node as a single precision number if the number is expressed as a floating point.
    /// </summary>
    /// <param name="node">The node you want the number value from.</param>
    /// <returns>A double that represents the number value in the node.</returns>
    float getFloat32Value(JsonNavigatorNode node);

    /// <summary>
    /// Gets the numeric value for a node as double precision number if the number is expressed as a floating point.
    /// </summary>
    /// <param name="node">The node you want the number value from.</param>
    /// <returns>A double that represents the number value in the node.</returns>
    double getFloat64Value(JsonNavigatorNode node);

    /// <summary>
    /// Gets the numeric value for a node as an unsigned 32 bit integer if the node is expressed as an uint32.
    /// </summary>
    /// <param name="node">The node you want the number value from.</param>
    /// <returns>An unsigned integer that represents the number value in the node.</returns>
    long getUInt32Value(JsonNavigatorNode node);

    /// <summary>
    /// Gets the Guid value for a node.
    /// </summary>
    /// <param name="node">The node you want the guid value from.</param>
    /// <returns>A guid read from the node.</returns>
    UUID getGuidValue(JsonNavigatorNode node);

    /// <summary>
    /// Gets a binary value for a given node from the input.
    /// </summary>
    /// <param name="node">The node to get the binary value from.</param>
    /// <returns>The binary value from the node</returns>
    ByteBuffer getBinaryValue(JsonNavigatorNode node);

    /// <summary>
    /// Tries to get the buffered binary value from a node.
    /// </summary>
    /// <param name="node">The node to get the buffered binary from.</param>
    /// <param name="out">The buffered binary value if possible</param>
    /// <returns><code>true</code> if the JsonNavigator successfully got the buffered binary value; <code>false</code> if the JsonNavigator failed to get the buffered binary value.</returns>
    boolean tryGetBufferedBinaryValue(JsonNavigatorNode node, Out<ByteBuffer> out);

    /// <summary>
    /// Gets the number of elements in an array node.
    /// </summary>
    /// <param name="node">The (array) node to get the count of.</param>
    /// <returns>The number of elements in the array node.</returns>
    int getArrayItemCount(JsonNavigatorNode node);

    /// <summary>
    /// Gets the node at a particular index of an array node
    /// </summary>
    /// <param name="node">The (array) node to index from.</param>
    /// <param name="index">The offset into the array</param>
    /// <returns>The node at a particular index of an array node</returns>
    JsonNavigatorNode getArrayItemAt(JsonNavigatorNode node, int index);

    /// <summary>
    /// Gets the array item nodes of the array node.
    /// </summary>
    /// <param name="node">The array to get the items from.</param>
    /// <returns>The array item nodes of the array node</returns>
    Iterable<JsonNavigatorNode> getArrayItems(JsonNavigatorNode node);

    /// <summary>
    /// Gets the number of properties in an object node.
    /// </summary>
    /// <param name="node">The node to get the property count from.</param>
    /// <returns>The number of properties in an object node.</returns>
    int getObjectPropertyCount(JsonNavigatorNode node);

    /// <summary>
    /// Tries to get a object property from an object with a particular property name.
    /// </summary>
    /// <param name="node">The object node to get a property from.</param>
    /// <param name="name">The name of the property to search for.</param>
    /// <param name="out">The <see cref="ObjectProperty"/> with the specified property name if it exists.</param>
    /// <returns><code>true</code> if the JsonNavigator successfully found the <see cref="ObjectProperty"/> with the specified property name; <code>false</code> otherwise.</returns>
    boolean tryGetObjectProperty(JsonNavigatorNode node, String name, Out<JsonObjectProperty> out);

    /// <summary>
    /// Gets the <see cref="ObjectProperty"/> properties from an object node.
    /// </summary>
    /// <param name="node">The object node to get the properties from.</param>
    /// <returns>The <see cref="ObjectProperty"/> properties from an object node.</returns>
    Iterable<JsonObjectProperty> getObjectProperties(JsonNavigatorNode node);

    /// <summary>
    /// Tries to get the buffered raw json
    /// </summary>
    /// <param name="node">The json node of interest</param>
    /// <param name="out">The raw json.</param>
    /// <returns>True if out was set. False otherwise.</returns>
    boolean tryGetBufferedRawJson(JsonNavigatorNode node, Out<ByteBuffer> out);
}
