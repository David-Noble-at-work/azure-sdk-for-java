// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.cosmos.serialization.hybridrow.layouts.LayoutResolverNamespace;
import com.azure.cosmos.serialization.hybridrow.layouts.SystemSchema;
import com.azure.cosmos.serialization.hybridrow.schemas.Namespace;
import com.google.common.base.Suppliers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Strings.lenientFormat;

public final class BatchSchemaProvider {

    private static final String SCHEMAS = "HybridRowBatchSchemas.json";

    private static final Logger logger = LoggerFactory.getLogger(BatchSchemaProvider.class);

    private static final Supplier<Namespace> schemaNamespace = Suppliers.memoize(() -> {

        final Optional<Namespace> namespace;

        try (InputStream stream = getResourceAsStream(SCHEMAS)) {
            namespace = Namespace.parse(stream);
        } catch (IOException cause) {
            String message = lenientFormat("failed to initialize %s due to %s", BatchSchemaProvider.class, cause);
            throw new IllegalStateException(message, cause);
        }

        return namespace.orElseThrow(() -> {
            String message = lenientFormat(
                "failed to initialize %s due to %s parse error",
                BatchSchemaProvider.class,
                SCHEMAS);
            logger.error(message);
            return new IllegalStateException(message);
        });
    });

    private static final Supplier<LayoutResolverNamespace> layoutResolver = Suppliers.memoize(() ->
        new LayoutResolverNamespace(schemaNamespace.get()));

    private static final Supplier<Layout> batchOperationLayout = Suppliers.memoize(() -> getLayout("BatchOperation"));

    private static final Supplier<Layout> batchResultLayout = Suppliers.memoize(() -> getLayout("BatchResult"));

    public static LayoutResolverNamespace getBatchLayoutResolver() {
        return layoutResolver.get();
    }

    public static Layout getBatchOperationLayout() {
        return batchOperationLayout.get();
    }

    public static Layout getBatchResultLayout() {
        return batchResultLayout.get();
    }

    public static Namespace getBatchSchemaNamespace() {
        return schemaNamespace.get();
    }

    private static Layout getLayout(String schemaName) {
        return layoutResolver.get().resolve(schemaNamespace.get().schemas().stream().filter(x ->
            x.name().equals(schemaName)).findFirst().orElseThrow(() -> {
                String message = lenientFormat(
                    "failed to initialize %s because %s schema is not defined in %s",
                    BatchSchemaProvider.class,
                    schemaName,
                    SCHEMAS
                );
                logger.error(message);
                return new IllegalStateException(message);
            }
        ).schemaId());
    }

    private static InputStream getResourceAsStream(final String name) throws IOException {

        InputStream inputStream = SystemSchema.class.getClassLoader().getResourceAsStream(name);

        if (inputStream != null) {
            return inputStream;
        }

        throw new FileNotFoundException(lenientFormat("cannot find %s", name));
    }
}
