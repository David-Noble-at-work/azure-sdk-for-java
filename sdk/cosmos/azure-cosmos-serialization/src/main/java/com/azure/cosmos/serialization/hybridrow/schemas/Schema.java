// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.schemas;

import com.azure.cosmos.implementation.Json;
import com.azure.cosmos.serialization.hybridrow.SchemaId;
import com.azure.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.cosmos.serialization.hybridrow.layouts.LayoutCompiler;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.azure.cosmos.implementation.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * A schema describes either table or UDT metadata.
 * <p>
 * The schema of a table or UDT describes the structure of row (i.e. which columns and the types of those columns). A
 * table schema represents the description of the contents of a collection level row directly.  UDTs described nested
 * structured objects that may appear either within a table column or within another UDT (i.e. nested UDTs).
 */
public final class Schema {

    // Required fields

    @JsonProperty
    private String comment;
    @JsonProperty()
    private SchemaId id;

    // Optional fields
    @JsonProperty(required = true)
    private String name;
    @JsonProperty
    private SchemaOptions options;
    private List<PartitionKey> partitionKeys;
    private List<PrimarySortKey> primaryKeys;
    @JsonProperty
    private List<Property> properties;

    // TODO: DANOBLE: how do these properties serialize?
    private List<StaticKey> staticKeys;
    @JsonProperty(defaultValue = "schema", required = true)
    private TypeKind type;
    @JsonProperty
    private SchemaLanguageVersion version;

    /**
     * Initializes a new instance of the {@link Schema} class.
     */
    private Schema() {
        this.id = SchemaId.NONE;
        this.type = TypeKind.SCHEMA;
        this.partitionKeys = Collections.emptyList();
        this.primaryKeys = Collections.emptyList();
        this.staticKeys = Collections.emptyList();
    }

    /**
     * An (optional) comment describing the purpose of this schema.
     * <p>
     * Comments are for documentary purpose only and do not affect the schema at runtime.
     *
     * @return the comment on this {@linkplain Schema schema} or {@code null}, if there is no comment.
     */
    public String comment() {
        return this.comment;
    }

    /**
     * Sets the (optional) comment describing the purpose of this schema.
     * <p>
     * Comments are for documentary purpose only and do not affect the schema at runtime.
     *
     * @param value a comment on this {@linkplain Schema schema} or {@code null} to remove the comment, if any, on this
     * {@linkplain Schema schema}.
     *
     * @return a reference to this {@linkplain Schema schema}.
     */
    public Schema comment(String value) {
        this.comment = value;
        return this;
    }

    /**
     * Compiles this logical schema into a physical layout that can be used to read and write rows.
     *
     * @param namespace The namespace within which this schema is defined.
     *
     * @return The layout for the schema.
     */
    public Layout compile(Namespace namespace) {

        checkNotNull(namespace, "expected non-null ns");
        checkArgument(namespace.schemas().contains(this));

        return LayoutCompiler.compile(namespace, this);
    }

    /**
     * The name of this {@linkplain Schema schema}.
     * <p>
     * The name of a schema MUST be unique within its namespace. Names must begin with an alpha-numeric character and
     * can only contain alpha-numeric characters and underscores.
     *
     * @return the name of this {@linkplain Schema schema} or {@code null}, if the name has not yet been set.
     */
    public String name() {
        return this.name;
    }

    /**
     * Sets the name of this {@linkplain Schema schema}.
     * <p>
     * The name of a schema MUST be unique within its namespace. Names must begin with an alpha-numeric character and
     * can only contain alpha-numeric characters and underscores.
     *
     * @param value a name for this {@linkplain Schema schema}.
     *
     * @return a reference to this {@linkplain Schema schema}.
     */
    @NotNull
    public Schema name(@NotNull String value) {
        checkNotNull(value);
        this.name = value;
        return this;
    }

    /**
     * Schema-wide options.
     *
     * @return schema -wide options.
     */
    public SchemaOptions options() {
        return this.options;
    }

    /**
     * Options schema.
     *
     * @param value the value
     *
     * @return the schema
     */
    public Schema options(SchemaOptions value) {
        this.options = value;
        return this;
    }

    /**
     * Parse a JSON fragment and return a schema.
     *
     * @param value The JSON string value to parse
     *
     * @return A logical schema, if the value parses.
     */
    public static Optional<Schema> parse(String value) {
        return Json.parse(value, Schema.class);
        // TODO: DANOBLE: perform structural validation on the Schema after JSON parsing
    }

    /**
     * An (optional) list of zero or more logical paths that form the partition key.
     * <p>
     * All paths referenced MUST map to a property within the schema. This field is never null.
     *
     * @return list of zero or more logical paths that form the partition key
     */
    @Nullable
    public List<PartitionKey> partitionKeys() {
        return this.partitionKeys;
    }

    /**
     * Partition keys schema.
     *
     * @param value the value
     *
     * @return the schema
     */
    public Schema partitionKeys(@Nullable List<PartitionKey> value) {
        this.partitionKeys = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * An (optional) list of zero or more logical paths that form the primary sort key.
     * <p>
     * All paths referenced MUST map to a property within the schema. This field is never null.
     *
     * @return list of zero or more logical paths that form the partition key
     */
    @Nullable
    public List<PrimarySortKey> primarySortKeys() {
        return this.primaryKeys;
    }

    /**
     * Primary sort keys schema.
     *
     * @param value the value
     *
     * @return the schema
     */
    public Schema primarySortKeys(ArrayList<PrimarySortKey> value) {
        this.primaryKeys = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * A list of zero or more property definitions that define the columns within the schema.
     * <p>
     * This field is never null.
     *
     * @return list of zero or more property definitions that define the columns within the schema
     */
    @NotNull
    public List<Property> properties() {
        return this.properties;
    }

    /**
     * Properties schema.
     *
     * @param value the value
     *
     * @return the schema
     */
    public Schema properties(List<Property> value) {
        this.properties = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * The unique identifier for a schema.
     * <p>
     * Identifiers must be unique within the scope of the database in which they are used.
     *
     * @return the unique identifier for a schema.
     */
    public SchemaId schemaId() {
        return this.id;
    }

    /**
     * Schema id schema.
     *
     * @param value the value
     *
     * @return the schema
     */
    public Schema schemaId(SchemaId value) {
        this.id = value;
        return this;
    }

    /**
     * A list of zero or more logical paths that hold data shared by all documents with same partition key.
     * <p>
     * All paths referenced MUST map to a property within the schema.
     * <p>
     * This field is never null.
     *
     * @return A list of zero or more logical paths that hold data shared by all documents with same partition key.
     */
    @NotNull
    public List<StaticKey> staticKeys() {
        return this.staticKeys;
    }

    /**
     * Static keys schema.
     *
     * @param value the value
     *
     * @return the schema
     */
    public Schema staticKeys(List<StaticKey> value) {
        this.staticKeys = value != null ? value : Collections.emptyList();
        return this;
    }

    /**
     * Returns a JSON string representation of the current {@link Schema}.
     *
     * @return a JSON string representation of the current {@link Schema}
     */
    @Override
    public String toString() {
        return Json.toString(this);
    }

    /**
     * The type of this schema.
     * <p>
     * This value MUST be {@link TypeKind#SCHEMA}.
     *
     * @return the type of this schema.
     */
    public TypeKind type() {
        return this.type;
    }

    /**
     * Type schema.
     *
     * @param value the value
     *
     * @return the schema
     */
    public Schema type(TypeKind value) {
        this.type = value;
        return this;
    }

    /**
     * The version of the HybridRow Schema Definition Language used to encode this schema.
     *
     * @return the version of the HybridRow Schema Definition Language used to encode this schema.
     */
    public SchemaLanguageVersion version() {
        return this.version;
    }

    /**
     * Version schema.
     *
     * @param value the value
     *
     * @return the schema
     */
    public Schema version(SchemaLanguageVersion value) {
        this.version = value;
        return this;
    }
}
