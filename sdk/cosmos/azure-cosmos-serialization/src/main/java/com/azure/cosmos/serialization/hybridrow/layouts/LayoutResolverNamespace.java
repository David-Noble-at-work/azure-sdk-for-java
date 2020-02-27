// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.SchemaId;
import com.azure.cosmos.serialization.hybridrow.schemas.Namespace;
import com.azure.cosmos.serialization.hybridrow.schemas.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.base.Preconditions.checkState;

/**
 * An implementation of {@link LayoutResolver} which dynamically compiles schema from a {@link Namespace}.
 * <p>
 * This resolver assumes that {@link Schema} within the {@link Namespace} have their {@link Schema#schemaId()} properly
 * populated. The resolver caches compiled schema.
 * <p>
 * All members of this class are multi-thread safe.
 */
public final class LayoutResolverNamespace extends LayoutResolver {

    private final ConcurrentHashMap<SchemaId, Layout> layoutCache;
    private final LayoutResolver parent;
    private final Namespace schemaNamespace;

    /**
     * Instantiates a new Layout resolver namespace.
     *
     * @param namespace the namespace
     */
    public LayoutResolverNamespace(@NotNull final Namespace namespace) {
        this(namespace, null);
    }

    /**
     * Instantiates a new Layout resolver namespace.
     *
     * @param schemaNamespace the schema namespace
     * @param parent the parent
     */
    public LayoutResolverNamespace(@NotNull final Namespace schemaNamespace, @Nullable final LayoutResolver parent) {
        checkNotNull(schemaNamespace, "expected non-null schemaNamespace");
        this.schemaNamespace = schemaNamespace;
        this.parent = parent;
        this.layoutCache = new ConcurrentHashMap<>();
    }

    /**
     * Namespace namespace.
     *
     * @return the namespace
     */
    public Namespace namespace() {
        return this.schemaNamespace;
    }

    @NotNull
    @Override
    public Layout resolve(@NotNull SchemaId schemaId) {

        checkNotNull(schemaId, "expected non-null schemaId");

        Layout layout = this.layoutCache.computeIfAbsent(schemaId, id -> {
            for (Schema schema : this.namespace().schemas()) {
                if (schema.schemaId().equals(id)) {
                    return schema.compile(this.schemaNamespace);
                }
            }
            return this.parent == null ? null : this.parent.resolve(schemaId);
        });

        checkState(layout != null, "failed to resolve schema %s", schemaId);
        return layout;
    }
}
