// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.base.Suppliers;
import com.azure.cosmos.serialization.hybridrow.SchemaId;
import com.azure.cosmos.serialization.hybridrow.schemas.Namespace;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

import static com.azure.cosmos.base.Strings.lenientFormat;

/**
 * The type System schema.
 */
public final class SystemSchema {

    /**
     * SchemaId of the empty schema. This schema has no defined cells but can accomodate unschematized sparse content.
     */
    public static final SchemaId EMPTY_SCHEMA_ID = SchemaId.from(2147473650);

    /**
     * SchemaId of HybridRow RecordIO Record Headers.
     */
    public static final SchemaId RECORD_SCHEMA_ID = SchemaId.from(2147473649);

    /**
     * SchemaId of HybridRow RecordIO Segments.
     */
    public static final SchemaId SEGMENT_SCHEMA_ID = SchemaId.from(2147473648);

    /**
     * Title of the HybridRow serialization library
     */
    public static final String SPECIFICATION_TITLE = "HybridRow serialization library";

    private static final Supplier<LayoutResolver> layoutResolver = Suppliers.memoize(() -> {

        final Optional<Namespace> namespace;

        try (InputStream stream = getResourceAsStream("SystemSchema.json")) {
            namespace = Namespace.parse(stream);
        } catch (IOException cause) {
            String message = lenientFormat("failed to initialize %s due to %s", SystemSchema.class, cause);
            throw new IllegalStateException(message, cause);
        }

        return new LayoutResolverNamespace(namespace.orElseThrow(() -> {
            String message = lenientFormat(
                "failed to initialize %s due to system schema parse error",
                SystemSchema.class);
            return new IllegalStateException(message);
        }));
    });

    private SystemSchema() {
    }

    /**
     * Layout resolver layout resolver.
     *
     * @return the layout resolver
     */
    public static LayoutResolver layoutResolver() {
        return layoutResolver.get();
    }

    private static InputStream getResourceAsStream(final String name) throws IOException {

        InputStream inputStream = SystemSchema.class.getClassLoader().getResourceAsStream(name);

        if (inputStream != null) {
            return inputStream;
        }

        throw new FileNotFoundException(lenientFormat("cannot find %s", name));
    }
}
