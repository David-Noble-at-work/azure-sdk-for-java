// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.containerservice;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The ContainerServiceVMDiagnostics model. */
@Fluent
public final class ContainerServiceVMDiagnostics {
    /*
     * Whether the VM diagnostic agent is provisioned on the VM.
     */
    @JsonProperty(value = "enabled", required = true)
    private boolean enabled;

    /*
     * The URI of the storage account where diagnostics are stored.
     */
    @JsonProperty(value = "storageUri", access = JsonProperty.Access.WRITE_ONLY)
    private String storageUri;

    /**
     * Get the enabled property: Whether the VM diagnostic agent is provisioned on the VM.
     *
     * @return the enabled value.
     */
    public boolean enabled() {
        return this.enabled;
    }

    /**
     * Set the enabled property: Whether the VM diagnostic agent is provisioned on the VM.
     *
     * @param enabled the enabled value to set.
     * @return the ContainerServiceVMDiagnostics object itself.
     */
    public ContainerServiceVMDiagnostics withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Get the storageUri property: The URI of the storage account where diagnostics are stored.
     *
     * @return the storageUri value.
     */
    public String storageUri() {
        return this.storageUri;
    }
}
