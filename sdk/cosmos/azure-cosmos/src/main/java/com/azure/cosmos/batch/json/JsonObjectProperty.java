// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.json;

public class JsonObjectProperty {

    private final JsonNavigatorNode nameNode;
    private final JsonNavigatorNode valueNode;

    /// <summary>
    /// Initializes a new instance of the ObjectProperty struct.
    /// </summary>
    /// <param name="nameNode">The IJsonNavigatorNode to the node that holds the object property name.</param>
    /// <param name="valueNode">The IJsonNavigatorNode to the node that holds the object property value.</param>
    public JsonObjectProperty(JsonNavigatorNode nameNode, JsonNavigatorNode valueNode) {
        this.nameNode = nameNode;
        this.valueNode = valueNode;
    }

    /// <summary>
    /// The node that holds the object property name.
    /// </summary>
    public JsonNavigatorNode getNameNode() {
        return this.nameNode;
    }

    /// <summary>
    /// The node that holds the object property value.
    /// </summary>
    public JsonNavigatorNode getValueNode() {
        return this.valueNode;
    }
}
