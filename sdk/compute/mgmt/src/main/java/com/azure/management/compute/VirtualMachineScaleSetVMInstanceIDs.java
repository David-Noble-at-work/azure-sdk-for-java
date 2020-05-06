// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.compute;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** The VirtualMachineScaleSetVMInstanceIDs model. */
@Fluent
public final class VirtualMachineScaleSetVMInstanceIDs {
    /*
     * The virtual machine scale set instance ids. Omitting the virtual machine
     * scale set instance ids will result in the operation being performed on
     * all virtual machines in the virtual machine scale set.
     */
    @JsonProperty(value = "instanceIds")
    private List<String> instanceIds;

    /**
     * Get the instanceIds property: The virtual machine scale set instance ids. Omitting the virtual machine scale set
     * instance ids will result in the operation being performed on all virtual machines in the virtual machine scale
     * set.
     *
     * @return the instanceIds value.
     */
    public List<String> instanceIds() {
        return this.instanceIds;
    }

    /**
     * Set the instanceIds property: The virtual machine scale set instance ids. Omitting the virtual machine scale set
     * instance ids will result in the operation being performed on all virtual machines in the virtual machine scale
     * set.
     *
     * @param instanceIds the instanceIds value to set.
     * @return the VirtualMachineScaleSetVMInstanceIDs object itself.
     */
    public VirtualMachineScaleSetVMInstanceIDs withInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
        return this;
    }
}
