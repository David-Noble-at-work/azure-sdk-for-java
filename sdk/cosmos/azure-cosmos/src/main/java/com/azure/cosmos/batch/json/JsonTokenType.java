// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.json;

public enum JsonTokenType {

    /// <summary>
    /// Reserved for no other value
    /// </summary>
    NOT_STARTED,

    /// <summary>
    /// Corresponds to the beginning of a JSON array ('[')
    /// </summary>
    BEGIN_ARRAY,

    /// <summary>
    /// Corresponds to the end of a JSON array (']')
    /// </summary>
    END_ARRAY,

    /// <summary>
    /// Corresponds to the beginning of a JSON object ('{')
    /// </summary>
    BEGIN_OBJECT,

    /// <summary>
    /// Corresponds to the end of a JSON object ('}')
    /// </summary>
    END_OBJECT,

    /// <summary>
    /// Corresponds to a JSON string.
    /// </summary>
    STRING,

    /// <summary>
    /// Corresponds to a JSON number.
    /// </summary>
    NUMBER,

    /// <summary>
    /// Corresponds to the JSON 'true' value.
    /// </summary>
    TRUE,

    /// <summary>
    /// Corresponds to the JSON 'false' value.
    /// </summary>
    FALSE,

    /// <summary>
    /// Corresponds to the JSON 'null' value.
    /// </summary>
    NULL,

    /// <summary>
    /// Corresponds to the JSON fieldname in a JSON object.
    /// </summary>
    FIELD_NAME,

    /// <summary>
    /// Corresponds to a signed 1 byte integer.
    /// </summary>
    INT_8,

    /// <summary>
    /// Corresponds to a signed 2 byte integer.
    /// </summary>
    INT_16,

    /// <summary>
    /// Corresponds to a signed 4 byte integer.
    /// </summary>
    INT_32,

    /// <summary>
    /// Corresponds to a signed 8 byte integer.
    /// </summary>
    INT_64,

    /// <summary>
    /// Corresponds to an unsigned 4 byte integer
    /// </summary>
    UINT_32,

    /// <summary>
    /// Corresponds to a single precision floating point.
    /// </summary>
    FLOAT_32,

    /// <summary>
    /// Corresponds to a double precision floating point.
    /// </summary>
    FLOAT_64,

    /// <summary>
    /// Corresponds to a GUID.
    /// </summary>
    GUID,

    /// <summary>
    /// Corresponds to an arbitrary sequence of bytes in an object.
    /// </summary>
    BINARY
}
