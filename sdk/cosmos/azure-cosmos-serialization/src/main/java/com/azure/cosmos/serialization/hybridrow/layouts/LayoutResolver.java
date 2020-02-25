// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import com.azure.cosmos.serialization.hybridrow.SchemaId;

import org.jetbrains.annotations.NotNull;;

public abstract class LayoutResolver {
    @NotNull
    public abstract Layout resolve(@NotNull SchemaId schemaId);
}
