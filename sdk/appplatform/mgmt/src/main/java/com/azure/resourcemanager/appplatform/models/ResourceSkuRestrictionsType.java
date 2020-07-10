// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appplatform.models;

import com.azure.core.util.ExpandableStringEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Collection;

/** Defines values for ResourceSkuRestrictionsType. */
public final class ResourceSkuRestrictionsType extends ExpandableStringEnum<ResourceSkuRestrictionsType> {
    /** Static value Location for ResourceSkuRestrictionsType. */
    public static final ResourceSkuRestrictionsType LOCATION = fromString("Location");

    /** Static value Zone for ResourceSkuRestrictionsType. */
    public static final ResourceSkuRestrictionsType ZONE = fromString("Zone");

    /**
     * Creates or finds a ResourceSkuRestrictionsType from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding ResourceSkuRestrictionsType.
     */
    @JsonCreator
    public static ResourceSkuRestrictionsType fromString(String name) {
        return fromString(name, ResourceSkuRestrictionsType.class);
    }

    /** @return known ResourceSkuRestrictionsType values. */
    public static Collection<ResourceSkuRestrictionsType> values() {
        return values(ResourceSkuRestrictionsType.class);
    }
}
