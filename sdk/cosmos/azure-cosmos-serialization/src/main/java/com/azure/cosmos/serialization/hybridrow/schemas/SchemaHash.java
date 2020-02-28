// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.schemas;

import com.azure.cosmos.serialization.hybridrow.HashCode128;
import com.azure.cosmos.serialization.hybridrow.SchemaId;
import com.azure.cosmos.serialization.hybridrow.implementation.Murmur3Hash;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.base.Preconditions.checkState;
import static com.azure.cosmos.implementation.base.Strings.lenientFormat;

/**
 * The type Schema hash.
 */
public final class SchemaHash {

    private SchemaHash() {
    }

    /**
     * Computes the logical hash for a logical schema.
     *
     * @param namespace The namespace within which {@code schema} is defined.
     * @param schema The logical schema to compute the hash of.
     * @param seed The seed to initialized the hash function.
     *
     * @return The logical 128-bit hash as a two-tuple (low, high).
     */
    @NotNull
    public static HashCode128 computeHash(
        @NotNull final Namespace namespace, @NotNull final Schema schema, @NotNull final HashCode128 seed) {

        checkNotNull(namespace, "expected non-null namespace");
        checkNotNull(schema, "expected non-null schema");
        checkNotNull(seed, "expected non-null seed");

        HashCode128 hash = seed;

        hash = Murmur3Hash.hash128(schema.schemaId().value(), hash);
        hash = Murmur3Hash.hash128(schema.type().value(), hash);
        hash = computeHash(namespace, schema.options(), hash);

        for (PartitionKey partitionKey : schema.partitionKeys()) {
            hash = SchemaHash.computeHash(namespace, partitionKey, hash);
        }

        for (PrimarySortKey p : schema.primarySortKeys()) {
            hash = SchemaHash.computeHash(namespace, p, hash);
        }

        for (StaticKey p : schema.staticKeys()) {
            hash = SchemaHash.computeHash(namespace, p, hash);
        }

        for (Property p : schema.properties()) {
            hash = SchemaHash.computeHash(namespace, p, hash);
        }

        return hash;
    }

    // region Privates

    @NotNull
    private static HashCode128 computeHash(
        @NotNull final Namespace namespace, @Nullable final SchemaOptions options, @NotNull final HashCode128 seed) {

        HashCode128 hash = seed;

        hash = Murmur3Hash.hash128(options != null && options.disallowUnschematized(), hash);
        hash = Murmur3Hash.hash128(options != null && options.enablePropertyLevelTimestamp(), hash);
        hash = Murmur3Hash.hash128(options != null && options.disableSystemPrefix(), hash);

        return hash;
    }

    @NotNull
    private static HashCode128 computeHash(
        @NotNull final Namespace namespace, @NotNull final Property property, @NotNull final HashCode128 seed) {

        HashCode128 hash = seed;

        hash = Murmur3Hash.hash128(property.path(), hash);
        hash = SchemaHash.computeHash(namespace, property.type(), hash);

        return hash;
    }

    @NotNull
    private static HashCode128 computeHash(
        @NotNull final Namespace namespace, @NotNull final PropertyType propertyType, @NotNull final HashCode128 seed) {

        HashCode128 hash = seed;

        hash = Murmur3Hash.hash128(propertyType.type().value(), hash);
        hash = Murmur3Hash.hash128(propertyType.nullable(), hash);

        if (propertyType.apiType() != null) {
            hash = Murmur3Hash.hash128(propertyType.apiType(), hash);
        }

        if (propertyType instanceof PrimitivePropertyType) {

            PrimitivePropertyType pp = (PrimitivePropertyType) propertyType;

            hash = Murmur3Hash.hash128(pp.storage().value(), hash);
            hash = Murmur3Hash.hash128(pp.length(), hash);

            return hash;
        }

        checkState(propertyType instanceof ScopePropertyType);
        ScopePropertyType pp = (ScopePropertyType) propertyType;
        hash = Murmur3Hash.hash128(pp.immutable(), hash);

        if (propertyType instanceof ArrayPropertyType) {
            ArrayPropertyType spp = (ArrayPropertyType) propertyType;
            if (spp.items() != null) {
                hash = computeHash(namespace, spp.items(), hash);
            }
            return hash;
        }

        if (propertyType instanceof ObjectPropertyType) {
            ObjectPropertyType spp = (ObjectPropertyType) propertyType;
            if (spp.properties() != null) {
                for (Property opp : spp.properties()) {
                    hash = SchemaHash.computeHash(namespace, opp, hash);
                }
            }
            return hash;
        }

        if (propertyType instanceof MapPropertyType) {

            MapPropertyType spp = (MapPropertyType) propertyType;

            if (spp.keys() != null) {
                hash = SchemaHash.computeHash(namespace, spp.keys(), hash);
            }

            if (spp.values() != null) {
                hash = SchemaHash.computeHash(namespace, spp.values(), hash);
            }

            return hash;
        }

        if (propertyType instanceof SetPropertyType) {

            SetPropertyType spp = (SetPropertyType) propertyType;

            if (spp.items() != null) {
                hash = computeHash(namespace, spp.items(), hash);
            }

            return hash;
        }

        if (propertyType instanceof TaggedPropertyType) {

            TaggedPropertyType spp = (TaggedPropertyType) propertyType;

            if (spp.items() != null) {
                for (PropertyType pt : spp.items()) {
                    hash = SchemaHash.computeHash(namespace, pt, hash);
                }
            }

            return hash;
        }

        if (propertyType instanceof TuplePropertyType) {

            TuplePropertyType spp = (TuplePropertyType) propertyType;

            if (spp.items() != null) {
                for (PropertyType pt : spp.items()) {
                    hash = SchemaHash.computeHash(namespace, pt, hash);
                }
            }

            return hash;
        }

        if (propertyType instanceof UdtPropertyType) {

            Stream<Schema> schemaStream = namespace.schemas().stream();
            UdtPropertyType spp = (UdtPropertyType) propertyType;
            Optional<Schema> udtSchema;

            if (spp.schemaId() == SchemaId.INVALID) {
                udtSchema = schemaStream.filter(schema -> schema.name().equals(spp.name())).findFirst();
            } else {
                udtSchema = schemaStream.filter(schema -> schema.schemaId().equals(spp.schemaId())).findFirst();
                udtSchema.ifPresent(schema -> checkState(schema.name().equals(spp.name()),
                    "Ambiguous schema reference: '%s:%s'", spp.name(), spp.schemaId()));
            }

            checkState(udtSchema.isPresent(), "Cannot resolve schema reference '{0}:{1}'", spp.name(), spp.schemaId());
            return SchemaHash.computeHash(namespace, udtSchema.get(), hash);
        }

        throw new IllegalStateException(lenientFormat("unrecognized property type: %s", propertyType.getClass()));
    }

    @NotNull
    private static HashCode128 computeHash(
        @NotNull final Namespace namespace, @NotNull final PartitionKey partitionKey, @NotNull final HashCode128 seed) {
        return partitionKey == null ? seed : Murmur3Hash.hash128(partitionKey.path(), seed);
    }

    @NotNull
    private static HashCode128 computeHash(
        @NotNull final Namespace namespace, @NotNull final PrimarySortKey primarySortKey, @NotNull HashCode128 seed) {
        HashCode128 hash = seed;
        if (primarySortKey != null) {
            hash = Murmur3Hash.hash128(primarySortKey.path(), hash);
            hash = Murmur3Hash.hash128(primarySortKey.direction().value(), hash);
        }
        return hash;
    }

    @NotNull
    private static HashCode128 computeHash(
        @NotNull final Namespace namespace, @NotNull final StaticKey staticKey, @NotNull final HashCode128 seed) {
        return staticKey == null ? seed : Murmur3Hash.hash128(staticKey.path(), seed);
    }

    // endregion
}
