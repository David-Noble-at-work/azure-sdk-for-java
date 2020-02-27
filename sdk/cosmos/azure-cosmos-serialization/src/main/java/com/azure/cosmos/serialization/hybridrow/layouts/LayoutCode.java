// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.implementation.base.Suppliers;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Type coded used in the binary encoding to indicate the formatting of succeeding bytes.
 */
public enum LayoutCode {

    /**
     * Invalid layout code.
     */
    INVALID((byte) 0),

    /**
     * Null layout code.
     */
    NULL((byte) 1),

    /**
     * Boolean false layout code.
     */
    BOOLEAN_FALSE((byte) 2),
    /**
     * Boolean layout code.
     */
    BOOLEAN((byte) 3),

    /**
     * Int 8 layout code.
     */
    INT_8((byte) 5),
    /**
     * Int 16 layout code.
     */
    INT_16((byte) 6),
    /**
     * Int 32 layout code.
     */
    INT_32((byte) 7),
    /**
     * Int 64 layout code.
     */
    INT_64((byte) 8),
    /**
     * Uint 8 layout code.
     */
    UINT_8((byte) 9),
    /**
     * Uint 16 layout code.
     */
    UINT_16((byte) 10),
    /**
     * Uint 32 layout code.
     */
    UINT_32((byte) 11),
    /**
     * Uint 64 layout code.
     */
    UINT_64((byte) 12),
    /**
     * Var int layout code.
     */
    VAR_INT((byte) 13),
    /**
     * Var uint layout code.
     */
    VAR_UINT((byte) 14),

    /**
     * Float 32 layout code.
     */
    FLOAT_32((byte) 15),
    /**
     * Float 64 layout code.
     */
    FLOAT_64((byte) 16),
    /**
     * Decimal layout code.
     */
    DECIMAL((byte) 17),

    /**
     * Date time layout code.
     */
    DATE_TIME((byte) 18),
    /**
     * Guid layout code.
     */
    GUID((byte) 19),

    /**
     * Utf 8 layout code.
     */
    UTF_8((byte) 20),
    /**
     * Binary layout code.
     */
    BINARY((byte) 21),

    /**
     * Float 128 layout code.
     */
    FLOAT_128((byte) 22),
    /**
     * Unix date time layout code.
     */
    UNIX_DATE_TIME((byte) 23),
    /**
     * Mongodb object id layout code.
     */
    MONGODB_OBJECT_ID((byte) 24),

    /**
     * Object scope layout code.
     */
    OBJECT_SCOPE((byte) 30),
    /**
     * Immutable object scope layout code.
     */
    IMMUTABLE_OBJECT_SCOPE((byte) 31),

    /**
     * Array scope layout code.
     */
    ARRAY_SCOPE((byte) 32),
    /**
     * Immutable array scope layout code.
     */
    IMMUTABLE_ARRAY_SCOPE((byte) 33),

    /**
     * Typed array scope layout code.
     */
    TYPED_ARRAY_SCOPE((byte) 34),
    /**
     * Immutable typed array scope layout code.
     */
    IMMUTABLE_TYPED_ARRAY_SCOPE((byte) 35),

    /**
     * Tuple scope layout code.
     */
    TUPLE_SCOPE((byte) 36),
    /**
     * Immutable tuple scope layout code.
     */
    IMMUTABLE_TUPLE_SCOPE((byte) 37),

    /**
     * Typed tuple scope layout code.
     */
    TYPED_TUPLE_SCOPE((byte) 38),
    /**
     * Immutable typed tuple scope layout code.
     */
    IMMUTABLE_TYPED_TUPLE_SCOPE((byte) 39),

    /**
     * Map scope layout code.
     */
    MAP_SCOPE((byte) 40),
    /**
     * Immutable map scope layout code.
     */
    IMMUTABLE_MAP_SCOPE((byte) 41),

    /**
     * Typed map scope layout code.
     */
    TYPED_MAP_SCOPE((byte) 42),
    /**
     * Immutable typed map scope layout code.
     */
    IMMUTABLE_TYPED_MAP_SCOPE((byte) 43),

    /**
     * Set scope layout code.
     */
    SET_SCOPE((byte) 44),
    /**
     * Immutable set scope layout code.
     */
    IMMUTABLE_SET_SCOPE((byte) 45),

    /**
     * Typed set scope layout code.
     */
    TYPED_SET_SCOPE((byte) 46),
    /**
     * Immutable typed set scope layout code.
     */
    IMMUTABLE_TYPED_SET_SCOPE((byte) 47),

    /**
     * Nullable scope layout code.
     */
    NULLABLE_SCOPE((byte) 48),
    /**
     * Immutable nullable scope layout code.
     */
    IMMUTABLE_NULLABLE_SCOPE((byte) 49),

    /**
     * Tagged scope layout code.
     */
    TAGGED_SCOPE((byte) 50),
    /**
     * Immutable tagged scope layout code.
     */
    IMMUTABLE_TAGGED_SCOPE((byte) 51),

    /**
     * Tagged 2 scope layout code.
     */
    TAGGED2_SCOPE((byte) 52),
    /**
     * Immutable tagged 2 scope layout code.
     */
    IMMUTABLE_TAGGED2_SCOPE((byte) 53),

    /**
     * Nested row.
     */
    SCHEMA((byte) 68),
    /**
     * Immutable schema layout code.
     */
    IMMUTABLE_SCHEMA((byte) 69),

    /**
     * End scope layout code.
     */
    END_SCOPE((byte) 70);

    /**
     * The constant BYTES.
     */
    public static final int BYTES = Byte.BYTES;

    private static final Supplier<Byte2ReferenceMap<LayoutCode>> mappings = Suppliers.memoize(() -> {
        final LayoutCode[] constants = LayoutCode.class.getEnumConstants();
        final byte[] values = new byte[constants.length];
        for (int i = 0; i < constants.length; i++) {
            values[i] = constants[i].value();
        }
        return new Byte2ReferenceOpenHashMap<>(values, constants);
    });

    private final byte value;

    LayoutCode(final byte value) {
        this.value = value;
    }

    /**
     * From layout code.
     *
     * @param value the value
     *
     * @return the layout code
     */
    @Nullable
    public static LayoutCode from(final byte value) {
        return mappings.get().get(value);
    }

    /**
     * Value byte.
     *
     * @return the byte
     */
    public byte value() {
        return this.value;
    }
}
