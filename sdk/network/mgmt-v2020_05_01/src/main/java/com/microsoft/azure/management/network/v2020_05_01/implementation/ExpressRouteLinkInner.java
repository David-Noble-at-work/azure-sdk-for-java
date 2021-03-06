/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.network.v2020_05_01.implementation;

import com.microsoft.azure.management.network.v2020_05_01.ExpressRouteLinkConnectorType;
import com.microsoft.azure.management.network.v2020_05_01.ExpressRouteLinkAdminState;
import com.microsoft.azure.management.network.v2020_05_01.ProvisioningState;
import com.microsoft.azure.management.network.v2020_05_01.ExpressRouteLinkMacSecConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.rest.serializer.JsonFlatten;
import com.microsoft.azure.SubResource;

/**
 * ExpressRouteLink.
 * ExpressRouteLink child resource definition.
 */
@JsonFlatten
public class ExpressRouteLinkInner extends SubResource {
    /**
     * Name of Azure router associated with physical port.
     */
    @JsonProperty(value = "properties.routerName", access = JsonProperty.Access.WRITE_ONLY)
    private String routerName;

    /**
     * Name of Azure router interface.
     */
    @JsonProperty(value = "properties.interfaceName", access = JsonProperty.Access.WRITE_ONLY)
    private String interfaceName;

    /**
     * Mapping between physical port to patch panel port.
     */
    @JsonProperty(value = "properties.patchPanelId", access = JsonProperty.Access.WRITE_ONLY)
    private String patchPanelId;

    /**
     * Mapping of physical patch panel to rack.
     */
    @JsonProperty(value = "properties.rackId", access = JsonProperty.Access.WRITE_ONLY)
    private String rackId;

    /**
     * Physical fiber port type. Possible values include: 'LC', 'SC'.
     */
    @JsonProperty(value = "properties.connectorType", access = JsonProperty.Access.WRITE_ONLY)
    private ExpressRouteLinkConnectorType connectorType;

    /**
     * Administrative state of the physical port. Possible values include:
     * 'Enabled', 'Disabled'.
     */
    @JsonProperty(value = "properties.adminState")
    private ExpressRouteLinkAdminState adminState;

    /**
     * The provisioning state of the express route link resource. Possible
     * values include: 'Succeeded', 'Updating', 'Deleting', 'Failed'.
     */
    @JsonProperty(value = "properties.provisioningState", access = JsonProperty.Access.WRITE_ONLY)
    private ProvisioningState provisioningState;

    /**
     * MacSec configuration.
     */
    @JsonProperty(value = "properties.macSecConfig")
    private ExpressRouteLinkMacSecConfig macSecConfig;

    /**
     * Name of child port resource that is unique among child port resources of
     * the parent.
     */
    @JsonProperty(value = "name")
    private String name;

    /**
     * A unique read-only string that changes whenever the resource is updated.
     */
    @JsonProperty(value = "etag", access = JsonProperty.Access.WRITE_ONLY)
    private String etag;

    /**
     * Get name of Azure router associated with physical port.
     *
     * @return the routerName value
     */
    public String routerName() {
        return this.routerName;
    }

    /**
     * Get name of Azure router interface.
     *
     * @return the interfaceName value
     */
    public String interfaceName() {
        return this.interfaceName;
    }

    /**
     * Get mapping between physical port to patch panel port.
     *
     * @return the patchPanelId value
     */
    public String patchPanelId() {
        return this.patchPanelId;
    }

    /**
     * Get mapping of physical patch panel to rack.
     *
     * @return the rackId value
     */
    public String rackId() {
        return this.rackId;
    }

    /**
     * Get physical fiber port type. Possible values include: 'LC', 'SC'.
     *
     * @return the connectorType value
     */
    public ExpressRouteLinkConnectorType connectorType() {
        return this.connectorType;
    }

    /**
     * Get administrative state of the physical port. Possible values include: 'Enabled', 'Disabled'.
     *
     * @return the adminState value
     */
    public ExpressRouteLinkAdminState adminState() {
        return this.adminState;
    }

    /**
     * Set administrative state of the physical port. Possible values include: 'Enabled', 'Disabled'.
     *
     * @param adminState the adminState value to set
     * @return the ExpressRouteLinkInner object itself.
     */
    public ExpressRouteLinkInner withAdminState(ExpressRouteLinkAdminState adminState) {
        this.adminState = adminState;
        return this;
    }

    /**
     * Get the provisioning state of the express route link resource. Possible values include: 'Succeeded', 'Updating', 'Deleting', 'Failed'.
     *
     * @return the provisioningState value
     */
    public ProvisioningState provisioningState() {
        return this.provisioningState;
    }

    /**
     * Get macSec configuration.
     *
     * @return the macSecConfig value
     */
    public ExpressRouteLinkMacSecConfig macSecConfig() {
        return this.macSecConfig;
    }

    /**
     * Set macSec configuration.
     *
     * @param macSecConfig the macSecConfig value to set
     * @return the ExpressRouteLinkInner object itself.
     */
    public ExpressRouteLinkInner withMacSecConfig(ExpressRouteLinkMacSecConfig macSecConfig) {
        this.macSecConfig = macSecConfig;
        return this;
    }

    /**
     * Get name of child port resource that is unique among child port resources of the parent.
     *
     * @return the name value
     */
    public String name() {
        return this.name;
    }

    /**
     * Set name of child port resource that is unique among child port resources of the parent.
     *
     * @param name the name value to set
     * @return the ExpressRouteLinkInner object itself.
     */
    public ExpressRouteLinkInner withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get a unique read-only string that changes whenever the resource is updated.
     *
     * @return the etag value
     */
    public String etag() {
        return this.etag;
    }

}
