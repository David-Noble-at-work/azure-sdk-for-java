// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.compute;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The AdditionalCapabilities model. */
@Fluent
public final class AdditionalCapabilities {
    /*
     * The flag that enables or disables a capability to have one or more
     * managed data disks with UltraSSD_LRS storage account type on the VM or
     * VMSS. Managed disks with storage account type UltraSSD_LRS can be added
     * to a virtual machine or virtual machine scale set only if this property
     * is enabled.
     */
    @JsonProperty(value = "ultraSSDEnabled")
    private Boolean ultraSSDEnabled;

    /**
     * Get the ultraSSDEnabled property: The flag that enables or disables a capability to have one or more managed data
     * disks with UltraSSD_LRS storage account type on the VM or VMSS. Managed disks with storage account type
     * UltraSSD_LRS can be added to a virtual machine or virtual machine scale set only if this property is enabled.
     *
     * @return the ultraSSDEnabled value.
     */
    public Boolean ultraSSDEnabled() {
        return this.ultraSSDEnabled;
    }

    /**
     * Set the ultraSSDEnabled property: The flag that enables or disables a capability to have one or more managed data
     * disks with UltraSSD_LRS storage account type on the VM or VMSS. Managed disks with storage account type
     * UltraSSD_LRS can be added to a virtual machine or virtual machine scale set only if this property is enabled.
     *
     * @param ultraSSDEnabled the ultraSSDEnabled value to set.
     * @return the AdditionalCapabilities object itself.
     */
    public AdditionalCapabilities withUltraSSDEnabled(Boolean ultraSSDEnabled) {
        this.ultraSSDEnabled = ultraSSDEnabled;
        return this;
    }
}
