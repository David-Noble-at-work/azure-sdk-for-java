// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

/**
 * Layout type definitions
 */
public abstract class LayoutTypes {
    /**
     * The constant ARRAY.
     */
    public static final LayoutArray ARRAY = new LayoutArray(false);
    /**
     * The constant BINARY.
     */
    public static final LayoutBinary BINARY = new LayoutBinary();
    /**
     * The constant BOOLEAN.
     */
    public static final LayoutBoolean BOOLEAN = new LayoutBoolean(true);
    /**
     * The constant BOOLEAN_FALSE.
     */
    public static final LayoutBoolean BOOLEAN_FALSE = new LayoutBoolean(false);
    /**
     * The constant DATE_TIME.
     */
    public static final LayoutDateTime DATE_TIME = new LayoutDateTime();
    /**
     * The constant DECIMAL.
     */
    public static final LayoutDecimal DECIMAL = new LayoutDecimal();
    /**
     * The constant END_SCOPE.
     */
    public static final LayoutEndScope END_SCOPE = new LayoutEndScope();
    /**
     * The constant FLOAT_128.
     */
    public static final LayoutFloat128 FLOAT_128 = new LayoutFloat128();
    /**
     * The constant FLOAT_32.
     */
    public static final LayoutFloat32 FLOAT_32 = new LayoutFloat32();
    /**
     * The constant FLOAT_64.
     */
    public static final LayoutFloat64 FLOAT_64 = new LayoutFloat64();
    /**
     * The constant GUID.
     */
    public static final LayoutGuid GUID = new LayoutGuid();
    /**
     * The constant IMMUTABLE_ARRAY.
     */
    public static final LayoutArray IMMUTABLE_ARRAY = new LayoutArray(true);
    /**
     * The constant IMMUTABLE_NULLABLE.
     */
    public static final LayoutNullable IMMUTABLE_NULLABLE = new LayoutNullable(true);
    /**
     * The constant IMMUTABLE_OBJECT.
     */
    public static final LayoutObject IMMUTABLE_OBJECT = new LayoutObject(true);
    /**
     * The constant IMMUTABLE_TAGGED.
     */
    public static final LayoutTagged IMMUTABLE_TAGGED = new LayoutTagged(true);
    /**
     * The constant IMMUTABLE_TAGGED_2.
     */
    public static final LayoutTagged2 IMMUTABLE_TAGGED_2 = new LayoutTagged2(true);
    /**
     * The constant IMMUTABLE_TYPED_ARRAY.
     */
    public static final LayoutTypedArray IMMUTABLE_TYPED_ARRAY = new LayoutTypedArray(true);
    /**
     * The constant IMMUTABLE_TYPED_MAP.
     */
    public static final LayoutTypedMap IMMUTABLE_TYPED_MAP = new LayoutTypedMap(true);
    /**
     * The constant IMMUTABLE_TYPED_SET.
     */
    public static final LayoutTypedSet IMMUTABLE_TYPED_SET = new LayoutTypedSet(true);
    /**
     * The constant IMMUTABLE_TYPED_TUPLE.
     */
    public static final LayoutTypedTuple IMMUTABLE_TYPED_TUPLE = new LayoutTypedTuple(true);
    /**
     * The constant IMMUTABLE_UDT.
     */
    public static final LayoutUDT IMMUTABLE_UDT = new LayoutUDT(true);
    /**
     * The constant INT_16.
     */
    public static final LayoutInt16 INT_16 = new LayoutInt16();
    /**
     * The constant INT_32.
     */
    public static final LayoutInt32 INT_32 = new LayoutInt32();
    /**
     * The constant INT_64.
     */
    public static final LayoutInt64 INT_64 = new LayoutInt64();
    /**
     * The constant INT_8.
     */
    public static final LayoutInt8 INT_8 = new LayoutInt8();
    /**
     * The constant IMMUTABLE_TUPLE.
     */
    public static final LayoutTuple IMMUTABLE_TUPLE = new LayoutTuple(true);
    /**
     * The constant NULL.
     */
    public static final LayoutNull NULL = new LayoutNull();
    /**
     * The constant NULLABLE.
     */
    public static final LayoutNullable NULLABLE = new LayoutNullable(false);
    /**
     * The constant OBJECT.
     */
    public static final LayoutObject OBJECT = new LayoutObject(false);
    /**
     * The constant TAGGED.
     */
    public static final LayoutTagged TAGGED = new LayoutTagged(false);
    /**
     * The constant TAGGED_2.
     */
    public static final LayoutTagged2 TAGGED_2 = new LayoutTagged2(false);
    /**
     * The constant TUPLE.
     */
    public static final LayoutTuple TUPLE = new LayoutTuple(false);
    /**
     * The constant TYPED_ARRAY.
     */
    public static final LayoutTypedArray TYPED_ARRAY = new LayoutTypedArray(false);
    /**
     * The constant TYPED_MAP.
     */
    public static final LayoutTypedMap TYPED_MAP = new LayoutTypedMap(false);
    /**
     * The constant TYPED_SET.
     */
    public static final LayoutTypedSet TYPED_SET = new LayoutTypedSet(false);
    /**
     * The constant TYPED_TUPLE.
     */
    public static final LayoutTypedTuple TYPED_TUPLE = new LayoutTypedTuple(false);
    /**
     * The constant UDT.
     */
    public static final LayoutUDT UDT = new LayoutUDT(false);
    /**
     * The constant UINT_16.
     */
    public static final LayoutUInt16 UINT_16 = new LayoutUInt16();
    /**
     * The constant UINT_32.
     */
    public static final LayoutUInt32 UINT_32 = new LayoutUInt32();
    /**
     * The constant UINT_64.
     */
    public static final LayoutUInt64 UINT_64 = new LayoutUInt64();
    /**
     * The constant UINT_8.
     */
    public static final LayoutUInt8 UINT_8 = new LayoutUInt8();
    /**
     * The constant UNIX_DATE_TIME.
     */
    public static final LayoutUnixDateTime UNIX_DATE_TIME = new LayoutUnixDateTime();
    /**
     * The constant UTF_8.
     */
    public static final LayoutUtf8 UTF_8 = new LayoutUtf8();
    /**
     * The constant VAR_INT.
     */
    public static final LayoutVarInt VAR_INT = new LayoutVarInt();
    /**
     * The constant VAR_UINT.
     */
    public static final LayoutVarUInt VAR_UINT = new LayoutVarUInt();
}
