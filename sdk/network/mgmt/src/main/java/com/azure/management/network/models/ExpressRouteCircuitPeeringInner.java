// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.network.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.annotation.JsonFlatten;
import com.azure.core.management.SubResource;
import com.azure.management.network.ExpressRouteCircuitPeeringConfig;
import com.azure.management.network.ExpressRouteConnectionId;
import com.azure.management.network.ExpressRoutePeeringState;
import com.azure.management.network.ExpressRoutePeeringType;
import com.azure.management.network.Ipv6ExpressRouteCircuitPeeringConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** The ExpressRouteCircuitPeering model. */
@JsonFlatten
@Fluent
public class ExpressRouteCircuitPeeringInner extends SubResource {
    /*
     * Gets name of the resource that is unique within a resource group. This
     * name can be used to access the resource.
     */
    @JsonProperty(value = "name")
    private String name;

    /*
     * A unique read-only string that changes whenever the resource is updated.
     */
    @JsonProperty(value = "etag", access = JsonProperty.Access.WRITE_ONLY)
    private String etag;

    /*
     * Type of the resource.
     */
    @JsonProperty(value = "type", access = JsonProperty.Access.WRITE_ONLY)
    private String type;

    /*
     * The peering type.
     */
    @JsonProperty(value = "properties.peeringType")
    private ExpressRoutePeeringType peeringType;

    /*
     * The peering state.
     */
    @JsonProperty(value = "properties.state")
    private ExpressRoutePeeringState state;

    /*
     * The Azure ASN.
     */
    @JsonProperty(value = "properties.azureASN")
    private Integer azureASN;

    /*
     * The peer ASN.
     */
    @JsonProperty(value = "properties.peerASN")
    private Long peerASN;

    /*
     * The primary address prefix.
     */
    @JsonProperty(value = "properties.primaryPeerAddressPrefix")
    private String primaryPeerAddressPrefix;

    /*
     * The secondary address prefix.
     */
    @JsonProperty(value = "properties.secondaryPeerAddressPrefix")
    private String secondaryPeerAddressPrefix;

    /*
     * The primary port.
     */
    @JsonProperty(value = "properties.primaryAzurePort")
    private String primaryAzurePort;

    /*
     * The secondary port.
     */
    @JsonProperty(value = "properties.secondaryAzurePort")
    private String secondaryAzurePort;

    /*
     * The shared key.
     */
    @JsonProperty(value = "properties.sharedKey")
    private String sharedKey;

    /*
     * The VLAN ID.
     */
    @JsonProperty(value = "properties.vlanId")
    private Integer vlanId;

    /*
     * The Microsoft peering configuration.
     */
    @JsonProperty(value = "properties.microsoftPeeringConfig")
    private ExpressRouteCircuitPeeringConfig microsoftPeeringConfig;

    /*
     * Gets peering stats.
     */
    @JsonProperty(value = "properties.stats")
    private ExpressRouteCircuitStatsInner stats;

    /*
     * Gets the provisioning state of the public IP resource. Possible values
     * are: 'Updating', 'Deleting', and 'Failed'.
     */
    @JsonProperty(value = "properties.provisioningState")
    private String provisioningState;

    /*
     * The GatewayManager Etag.
     */
    @JsonProperty(value = "properties.gatewayManagerEtag")
    private String gatewayManagerEtag;

    /*
     * Gets whether the provider or the customer last modified the peering.
     */
    @JsonProperty(value = "properties.lastModifiedBy")
    private String lastModifiedBy;

    /*
     * The reference of the RouteFilter resource.
     */
    @JsonProperty(value = "properties.routeFilter")
    private SubResource routeFilter;

    /*
     * The IPv6 peering configuration.
     */
    @JsonProperty(value = "properties.ipv6PeeringConfig")
    private Ipv6ExpressRouteCircuitPeeringConfig ipv6PeeringConfig;

    /*
     * The ExpressRoute connection.
     */
    @JsonProperty(value = "properties.expressRouteConnection")
    private ExpressRouteConnectionId expressRouteConnection;

    /*
     * The list of circuit connections associated with Azure Private Peering
     * for this circuit.
     */
    @JsonProperty(value = "properties.connections")
    private List<ExpressRouteCircuitConnectionInner> connections;

    /*
     * The list of peered circuit connections associated with Azure Private
     * Peering for this circuit.
     */
    @JsonProperty(value = "properties.peeredConnections", access = JsonProperty.Access.WRITE_ONLY)
    private List<PeerExpressRouteCircuitConnectionInner> peeredConnections;

    /**
     * Get the name property: Gets name of the resource that is unique within a resource group. This name can be used to
     * access the resource.
     *
     * @return the name value.
     */
    public String name() {
        return this.name;
    }

    /**
     * Set the name property: Gets name of the resource that is unique within a resource group. This name can be used to
     * access the resource.
     *
     * @param name the name value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the etag property: A unique read-only string that changes whenever the resource is updated.
     *
     * @return the etag value.
     */
    public String etag() {
        return this.etag;
    }

    /**
     * Get the type property: Type of the resource.
     *
     * @return the type value.
     */
    public String type() {
        return this.type;
    }

    /**
     * Get the peeringType property: The peering type.
     *
     * @return the peeringType value.
     */
    public ExpressRoutePeeringType peeringType() {
        return this.peeringType;
    }

    /**
     * Set the peeringType property: The peering type.
     *
     * @param peeringType the peeringType value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withPeeringType(ExpressRoutePeeringType peeringType) {
        this.peeringType = peeringType;
        return this;
    }

    /**
     * Get the state property: The peering state.
     *
     * @return the state value.
     */
    public ExpressRoutePeeringState state() {
        return this.state;
    }

    /**
     * Set the state property: The peering state.
     *
     * @param state the state value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withState(ExpressRoutePeeringState state) {
        this.state = state;
        return this;
    }

    /**
     * Get the azureASN property: The Azure ASN.
     *
     * @return the azureASN value.
     */
    public Integer azureASN() {
        return this.azureASN;
    }

    /**
     * Set the azureASN property: The Azure ASN.
     *
     * @param azureASN the azureASN value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withAzureASN(Integer azureASN) {
        this.azureASN = azureASN;
        return this;
    }

    /**
     * Get the peerASN property: The peer ASN.
     *
     * @return the peerASN value.
     */
    public Long peerASN() {
        return this.peerASN;
    }

    /**
     * Set the peerASN property: The peer ASN.
     *
     * @param peerASN the peerASN value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withPeerASN(Long peerASN) {
        this.peerASN = peerASN;
        return this;
    }

    /**
     * Get the primaryPeerAddressPrefix property: The primary address prefix.
     *
     * @return the primaryPeerAddressPrefix value.
     */
    public String primaryPeerAddressPrefix() {
        return this.primaryPeerAddressPrefix;
    }

    /**
     * Set the primaryPeerAddressPrefix property: The primary address prefix.
     *
     * @param primaryPeerAddressPrefix the primaryPeerAddressPrefix value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withPrimaryPeerAddressPrefix(String primaryPeerAddressPrefix) {
        this.primaryPeerAddressPrefix = primaryPeerAddressPrefix;
        return this;
    }

    /**
     * Get the secondaryPeerAddressPrefix property: The secondary address prefix.
     *
     * @return the secondaryPeerAddressPrefix value.
     */
    public String secondaryPeerAddressPrefix() {
        return this.secondaryPeerAddressPrefix;
    }

    /**
     * Set the secondaryPeerAddressPrefix property: The secondary address prefix.
     *
     * @param secondaryPeerAddressPrefix the secondaryPeerAddressPrefix value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withSecondaryPeerAddressPrefix(String secondaryPeerAddressPrefix) {
        this.secondaryPeerAddressPrefix = secondaryPeerAddressPrefix;
        return this;
    }

    /**
     * Get the primaryAzurePort property: The primary port.
     *
     * @return the primaryAzurePort value.
     */
    public String primaryAzurePort() {
        return this.primaryAzurePort;
    }

    /**
     * Set the primaryAzurePort property: The primary port.
     *
     * @param primaryAzurePort the primaryAzurePort value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withPrimaryAzurePort(String primaryAzurePort) {
        this.primaryAzurePort = primaryAzurePort;
        return this;
    }

    /**
     * Get the secondaryAzurePort property: The secondary port.
     *
     * @return the secondaryAzurePort value.
     */
    public String secondaryAzurePort() {
        return this.secondaryAzurePort;
    }

    /**
     * Set the secondaryAzurePort property: The secondary port.
     *
     * @param secondaryAzurePort the secondaryAzurePort value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withSecondaryAzurePort(String secondaryAzurePort) {
        this.secondaryAzurePort = secondaryAzurePort;
        return this;
    }

    /**
     * Get the sharedKey property: The shared key.
     *
     * @return the sharedKey value.
     */
    public String sharedKey() {
        return this.sharedKey;
    }

    /**
     * Set the sharedKey property: The shared key.
     *
     * @param sharedKey the sharedKey value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
        return this;
    }

    /**
     * Get the vlanId property: The VLAN ID.
     *
     * @return the vlanId value.
     */
    public Integer vlanId() {
        return this.vlanId;
    }

    /**
     * Set the vlanId property: The VLAN ID.
     *
     * @param vlanId the vlanId value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withVlanId(Integer vlanId) {
        this.vlanId = vlanId;
        return this;
    }

    /**
     * Get the microsoftPeeringConfig property: The Microsoft peering configuration.
     *
     * @return the microsoftPeeringConfig value.
     */
    public ExpressRouteCircuitPeeringConfig microsoftPeeringConfig() {
        return this.microsoftPeeringConfig;
    }

    /**
     * Set the microsoftPeeringConfig property: The Microsoft peering configuration.
     *
     * @param microsoftPeeringConfig the microsoftPeeringConfig value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withMicrosoftPeeringConfig(
        ExpressRouteCircuitPeeringConfig microsoftPeeringConfig) {
        this.microsoftPeeringConfig = microsoftPeeringConfig;
        return this;
    }

    /**
     * Get the stats property: Gets peering stats.
     *
     * @return the stats value.
     */
    public ExpressRouteCircuitStatsInner stats() {
        return this.stats;
    }

    /**
     * Set the stats property: Gets peering stats.
     *
     * @param stats the stats value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withStats(ExpressRouteCircuitStatsInner stats) {
        this.stats = stats;
        return this;
    }

    /**
     * Get the provisioningState property: Gets the provisioning state of the public IP resource. Possible values are:
     * 'Updating', 'Deleting', and 'Failed'.
     *
     * @return the provisioningState value.
     */
    public String provisioningState() {
        return this.provisioningState;
    }

    /**
     * Set the provisioningState property: Gets the provisioning state of the public IP resource. Possible values are:
     * 'Updating', 'Deleting', and 'Failed'.
     *
     * @param provisioningState the provisioningState value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withProvisioningState(String provisioningState) {
        this.provisioningState = provisioningState;
        return this;
    }

    /**
     * Get the gatewayManagerEtag property: The GatewayManager Etag.
     *
     * @return the gatewayManagerEtag value.
     */
    public String gatewayManagerEtag() {
        return this.gatewayManagerEtag;
    }

    /**
     * Set the gatewayManagerEtag property: The GatewayManager Etag.
     *
     * @param gatewayManagerEtag the gatewayManagerEtag value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withGatewayManagerEtag(String gatewayManagerEtag) {
        this.gatewayManagerEtag = gatewayManagerEtag;
        return this;
    }

    /**
     * Get the lastModifiedBy property: Gets whether the provider or the customer last modified the peering.
     *
     * @return the lastModifiedBy value.
     */
    public String lastModifiedBy() {
        return this.lastModifiedBy;
    }

    /**
     * Set the lastModifiedBy property: Gets whether the provider or the customer last modified the peering.
     *
     * @param lastModifiedBy the lastModifiedBy value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        return this;
    }

    /**
     * Get the routeFilter property: The reference of the RouteFilter resource.
     *
     * @return the routeFilter value.
     */
    public SubResource routeFilter() {
        return this.routeFilter;
    }

    /**
     * Set the routeFilter property: The reference of the RouteFilter resource.
     *
     * @param routeFilter the routeFilter value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withRouteFilter(SubResource routeFilter) {
        this.routeFilter = routeFilter;
        return this;
    }

    /**
     * Get the ipv6PeeringConfig property: The IPv6 peering configuration.
     *
     * @return the ipv6PeeringConfig value.
     */
    public Ipv6ExpressRouteCircuitPeeringConfig ipv6PeeringConfig() {
        return this.ipv6PeeringConfig;
    }

    /**
     * Set the ipv6PeeringConfig property: The IPv6 peering configuration.
     *
     * @param ipv6PeeringConfig the ipv6PeeringConfig value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withIpv6PeeringConfig(
        Ipv6ExpressRouteCircuitPeeringConfig ipv6PeeringConfig) {
        this.ipv6PeeringConfig = ipv6PeeringConfig;
        return this;
    }

    /**
     * Get the expressRouteConnection property: The ExpressRoute connection.
     *
     * @return the expressRouteConnection value.
     */
    public ExpressRouteConnectionId expressRouteConnection() {
        return this.expressRouteConnection;
    }

    /**
     * Set the expressRouteConnection property: The ExpressRoute connection.
     *
     * @param expressRouteConnection the expressRouteConnection value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withExpressRouteConnection(ExpressRouteConnectionId expressRouteConnection) {
        this.expressRouteConnection = expressRouteConnection;
        return this;
    }

    /**
     * Get the connections property: The list of circuit connections associated with Azure Private Peering for this
     * circuit.
     *
     * @return the connections value.
     */
    public List<ExpressRouteCircuitConnectionInner> connections() {
        return this.connections;
    }

    /**
     * Set the connections property: The list of circuit connections associated with Azure Private Peering for this
     * circuit.
     *
     * @param connections the connections value to set.
     * @return the ExpressRouteCircuitPeeringInner object itself.
     */
    public ExpressRouteCircuitPeeringInner withConnections(List<ExpressRouteCircuitConnectionInner> connections) {
        this.connections = connections;
        return this;
    }

    /**
     * Get the peeredConnections property: The list of peered circuit connections associated with Azure Private Peering
     * for this circuit.
     *
     * @return the peeredConnections value.
     */
    public List<PeerExpressRouteCircuitConnectionInner> peeredConnections() {
        return this.peeredConnections;
    }
}
