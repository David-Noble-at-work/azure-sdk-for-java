// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.json;

public enum JsonNodeType {
    /// <summary>
    /// Corresponds to the 'null' value in JSON.
    /// </summary>
    NULL,

    /// <summary>
    /// Corresponds to the 'false' value in JSON.
    /// </summary>
    FALSE,

    /// <summary>
    /// Corresponds to the 'true' value in JSON.
    /// </summary>
    TRUE,

    /// <summary>
    /// Corresponds to the number type in JSON (number = [ minus ] integer [ fraction ] [ exponent ])
    /// </summary>
    NUMBER,

    /// <summary>
    /// Corresponds to the string type in JSON (string = quotation-mark *char quotation-mark)
    /// </summary>
    STRING,

    /// <summary>
    /// Corresponds to the array type in JSON ( begin-array [ value *( value-separator value ) ] end-array)
    /// </summary>
    ARRAY,

    /// <summary>
    /// Corresponds to the object type in JSON (begin-object [ member *( value-separator member ) ] end-object)
    /// </summary>
    OBJECT,

    /// <summary>
    /// Corresponds to the property name of a JSON object property (which is also a string).
    /// </summary>
    FIELD_NAME,

    /// <summary>
    /// Corresponds to the sbyte type in C# for the extended types.
    /// </summary>
    INT_8,

    /// <summary>
    /// Corresponds to the short type in C# for the extended types.
    /// </summary>
    INT_16,

    /// <summary>
    /// Corresponds to the int type in C# for the extended types.
    /// </summary>
    INT_32,

    /// <summary>
    /// Corresponds to the long type in C# for the extended types.
    /// </summary>
    INT_64,

    /// <summary>
    /// Corresponds to the uint type in C# for the extended types.
    /// </summary>
    UINT_32,

    /// <summary>
    /// Corresponds to the float type in C# for the extended types.
    /// </summary>
    FLOAT_32,

    /// <summary>
    /// Corresponds to the double type in C# for the extended types.
    /// </summary>
    FLOAT_64,

    /// <summary>
    /// Corresponds to an arbitrary sequence of bytes (equivalent to a byte[] in C#)
    /// </summary>
    BINARY,

    /// <summary>
    /// Corresponds to a GUID type in C# for teh extended types.
    /// </summary>
    GUID,

    /// <summary>
    /// Unknown JsonNodeType.
    /// </summary>
    UNKNOWN,
}
