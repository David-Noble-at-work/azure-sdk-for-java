// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.resources.fluent.inner;

import com.azure.core.annotation.Fluent;
import com.azure.core.management.exception.ManagementError;
import com.azure.core.util.logging.ClientLogger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The ResourceGroupExportResult model. */
@Fluent
public final class ResourceGroupExportResultInner {
    @JsonIgnore private final ClientLogger logger = new ClientLogger(ResourceGroupExportResultInner.class);

    /*
     * The template content.
     */
    @JsonProperty(value = "template")
    private Object template;

    /*
     * The template export error.
     */
    @JsonProperty(value = "error")
    private ManagementError error;

    /**
     * Get the template property: The template content.
     *
     * @return the template value.
     */
    public Object template() {
        return this.template;
    }

    /**
     * Set the template property: The template content.
     *
     * @param template the template value to set.
     * @return the ResourceGroupExportResultInner object itself.
     */
    public ResourceGroupExportResultInner withTemplate(Object template) {
        this.template = template;
        return this;
    }

    /**
     * Get the error property: The template export error.
     *
     * @return the error value.
     */
    public ManagementError error() {
        return this.error;
    }

    /**
     * Set the error property: The template export error.
     *
     * @param error the error value to set.
     * @return the ResourceGroupExportResultInner object itself.
     */
    public ResourceGroupExportResultInner withError(ManagementError error) {
        this.error = error;
        return this;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }
}
