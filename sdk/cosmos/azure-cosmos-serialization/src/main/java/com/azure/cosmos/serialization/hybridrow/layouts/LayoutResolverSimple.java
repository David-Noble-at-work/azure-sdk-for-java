// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.SchemaId;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * The type Layout resolver simple.
 */
public final class LayoutResolverSimple extends LayoutResolver {

    private final Function<SchemaId, Layout> resolver;

    /**
     * Instantiates a new Layout resolver simple.
     *
     * @param resolver the resolver
     */
    public LayoutResolverSimple(Function<SchemaId, Layout> resolver) {
        this.resolver = resolver;
    }

    @NotNull
    @Override
    public Layout resolve(@NotNull SchemaId schemaId) {
        checkNotNull(schemaId, "expected non-null schemaId");
        return this.resolver.apply(schemaId);
    }
}
