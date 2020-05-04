// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.network.models;

import com.azure.core.annotation.Immutable;
import com.azure.core.annotation.JsonFlatten;
import com.azure.core.management.Resource;
import com.azure.core.management.SubResource;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** The DdosProtectionPlan model. */
@JsonFlatten
@Immutable
public class DdosProtectionPlanInner extends Resource {
    /*
     * A unique read-only string that changes whenever the resource is updated.
     */
    @JsonProperty(value = "etag", access = JsonProperty.Access.WRITE_ONLY)
    private String etag;

    /*
     * The resource GUID property of the DDoS protection plan resource. It
     * uniquely identifies the resource, even if the user changes its name or
     * migrate the resource across subscriptions or resource groups.
     */
    @JsonProperty(value = "properties.resourceGuid", access = JsonProperty.Access.WRITE_ONLY)
    private String resourceGuid;

    /*
     * The provisioning state of the DDoS protection plan resource. Possible
     * values are: 'Succeeded', 'Updating', 'Deleting', and 'Failed'.
     */
    @JsonProperty(value = "properties.provisioningState", access = JsonProperty.Access.WRITE_ONLY)
    private String provisioningState;

    /*
     * The list of virtual networks associated with the DDoS protection plan
     * resource. This list is read-only.
     */
    @JsonProperty(value = "properties.virtualNetworks", access = JsonProperty.Access.WRITE_ONLY)
    private List<SubResource> virtualNetworks;

    /**
     * Get the etag property: A unique read-only string that changes whenever the resource is updated.
     *
     * @return the etag value.
     */
    public String etag() {
        return this.etag;
    }

    /**
     * Get the resourceGuid property: The resource GUID property of the DDoS protection plan resource. It uniquely
     * identifies the resource, even if the user changes its name or migrate the resource across subscriptions or
     * resource groups.
     *
     * @return the resourceGuid value.
     */
    public String resourceGuid() {
        return this.resourceGuid;
    }

    /**
     * Get the provisioningState property: The provisioning state of the DDoS protection plan resource. Possible values
     * are: 'Succeeded', 'Updating', 'Deleting', and 'Failed'.
     *
     * @return the provisioningState value.
     */
    public String provisioningState() {
        return this.provisioningState;
    }

    /**
     * Get the virtualNetworks property: The list of virtual networks associated with the DDoS protection plan resource.
     * This list is read-only.
     *
     * @return the virtualNetworks value.
     */
    public List<SubResource> virtualNetworks() {
        return this.virtualNetworks;
    }
}
