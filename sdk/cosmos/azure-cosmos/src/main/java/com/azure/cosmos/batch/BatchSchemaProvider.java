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

    // region Fields

    private static final String SCHEMAS = "HybridRowBatchSchemas.json";

    private static final Logger logger = LoggerFactory.getLogger(BatchSchemaProvider.class);

    private static final Supplier<Namespace> namespace = Suppliers.memoize(() -> {

        final Optional<Namespace> namespace;

        try (InputStream stream = getResourceAsStream(SCHEMAS)) {
            namespace = Namespace.parse(stream);
        } catch (IOException cause) {
            String message = lenientFormat("failed to initialize %s due to %s", BatchSchemaProvider.class, cause);
            logger.error(message);
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

    private static final Supplier<LayoutResolverNamespace> layoutResolverNamespaceSupplier = Suppliers.memoize(() ->
        new LayoutResolverNamespace(namespace.get()));

    private static final Supplier<Layout> batchOperationLayoutSupplier = Suppliers.memoize(() ->
        getLayout("BatchOperation"));

    private static final Supplier<Layout> batchResultLayoutSupplier = Suppliers.memoize(() ->
        getLayout("BatchResult"));

    // endregion

    // region Accessors

    public static LayoutResolverNamespace getBatchLayoutResolverNamespace() {
        return layoutResolverNamespaceSupplier.get();
    }

    public static Layout getBatchOperationLayout() {
        return batchOperationLayoutSupplier.get();
    }

    public static Layout getBatchResultLayout() {
        return batchResultLayoutSupplier.get();
    }

    public static Namespace getBatchNamespace() {
        return namespace.get();
    }

    // endregion

    // region Privates

    private BatchSchemaProvider() {

    }

    private static Layout getLayout(String schemaName) {
        return layoutResolverNamespaceSupplier.get().resolve(namespace.get().schemas().stream().filter(x ->
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

    // endregion
}
