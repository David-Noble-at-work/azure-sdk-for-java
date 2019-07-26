/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.datafactoryv2.v2017_09_01_preview;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The entity reference.
 */
public class EntityReference {
    /**
     * The type of this referenced entity. Possible values include:
     * 'IntegrationRuntimeReference', 'LinkedServiceReference'.
     */
    @JsonProperty(value = "type")
    private IntegrationRuntimeEntityReferenceType type;

    /**
     * The name of this referenced entity.
     */
    @JsonProperty(value = "referenceName")
    private String referenceName;

    /**
     * Get the type of this referenced entity. Possible values include: 'IntegrationRuntimeReference', 'LinkedServiceReference'.
     *
     * @return the type value
     */
    public IntegrationRuntimeEntityReferenceType type() {
        return this.type;
    }

    /**
     * Set the type of this referenced entity. Possible values include: 'IntegrationRuntimeReference', 'LinkedServiceReference'.
     *
     * @param type the type value to set
     * @return the EntityReference object itself.
     */
    public EntityReference withType(IntegrationRuntimeEntityReferenceType type) {
        this.type = type;
        return this;
    }

    /**
     * Get the name of this referenced entity.
     *
     * @return the referenceName value
     */
    public String referenceName() {
        return this.referenceName;
    }

    /**
     * Set the name of this referenced entity.
     *
     * @param referenceName the referenceName value to set
     * @return the EntityReference object itself.
     */
    public EntityReference withReferenceName(String referenceName) {
        this.referenceName = referenceName;
        return this;
    }

}
