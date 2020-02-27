// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.SchemaId;
import org.jetbrains.annotations.NotNull;

/**
 * The type Layout resolver.
 */
public abstract class LayoutResolver {
    /**
     * Resolve layout.
     *
     * @param schemaId the schema id
     *
     * @return the layout
     */
    @NotNull
    public abstract Layout resolve(@NotNull SchemaId schemaId);
}
