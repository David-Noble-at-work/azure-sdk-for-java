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
 * Pipeline reference type.
 */
public class PipelineReference {
    /**
     * Pipeline reference type.
     */
    @JsonProperty(value = "type", required = true)
    private String type;

    /**
     * Reference pipeline name.
     */
    @JsonProperty(value = "referenceName", required = true)
    private String referenceName;

    /**
     * Reference name.
     */
    @JsonProperty(value = "name")
    private String name;

    /**
     * Creates an instance of PipelineReference class.
     * @param referenceName reference pipeline name.
     */
    public PipelineReference() {
        type = "PipelineReference";
    }

    /**
     * Get pipeline reference type.
     *
     * @return the type value
     */
    public String type() {
        return this.type;
    }

    /**
     * Set pipeline reference type.
     *
     * @param type the type value to set
     * @return the PipelineReference object itself.
     */
    public PipelineReference withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Get reference pipeline name.
     *
     * @return the referenceName value
     */
    public String referenceName() {
        return this.referenceName;
    }

    /**
     * Set reference pipeline name.
     *
     * @param referenceName the referenceName value to set
     * @return the PipelineReference object itself.
     */
    public PipelineReference withReferenceName(String referenceName) {
        this.referenceName = referenceName;
        return this;
    }

    /**
     * Get reference name.
     *
     * @return the name value
     */
    public String name() {
        return this.name;
    }

    /**
     * Set reference name.
     *
     * @param name the name value to set
     * @return the PipelineReference object itself.
     */
    public PipelineReference withName(String name) {
        this.name = name;
        return this;
    }

}
