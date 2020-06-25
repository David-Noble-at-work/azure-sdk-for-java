// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.network.fluent.inner;

import com.azure.core.annotation.Fluent;
import com.azure.core.annotation.JsonFlatten;
import com.azure.core.management.SubResource;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.network.models.ApplicationGatewayBackendAddressPool;
import com.azure.resourcemanager.network.models.IpAllocationMethod;
import com.azure.resourcemanager.network.models.IpVersion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** The NetworkInterfaceIpConfiguration model. */
@JsonFlatten
@Fluent
public class NetworkInterfaceIpConfigurationInner extends SubResource {
    @JsonIgnore private final ClientLogger logger = new ClientLogger(NetworkInterfaceIpConfigurationInner.class);

    /*
     * The name of the resource that is unique within a resource group. This
     * name can be used to access the resource.
     */
    @JsonProperty(value = "name")
    private String name;

    /*
     * A unique read-only string that changes whenever the resource is updated.
     */
    @JsonProperty(value = "etag")
    private String etag;

    /*
     * The reference to Virtual Network Taps.
     */
    @JsonProperty(value = "properties.virtualNetworkTaps")
    private List<VirtualNetworkTapInner> virtualNetworkTaps;

    /*
     * The reference of ApplicationGatewayBackendAddressPool resource.
     */
    @JsonProperty(value = "properties.applicationGatewayBackendAddressPools")
    private List<ApplicationGatewayBackendAddressPool> applicationGatewayBackendAddressPools;

    /*
     * The reference of LoadBalancerBackendAddressPool resource.
     */
    @JsonProperty(value = "properties.loadBalancerBackendAddressPools")
    private List<BackendAddressPoolInner> loadBalancerBackendAddressPools;

    /*
     * A list of references of LoadBalancerInboundNatRules.
     */
    @JsonProperty(value = "properties.loadBalancerInboundNatRules")
    private List<InboundNatRuleInner> loadBalancerInboundNatRules;

    /*
     * Private IP address of the IP configuration.
     */
    @JsonProperty(value = "properties.privateIPAddress")
    private String privateIpAddress;

    /*
     * The private IP address allocation method.
     */
    @JsonProperty(value = "properties.privateIPAllocationMethod")
    private IpAllocationMethod privateIpAllocationMethod;

    /*
     * Available from Api-Version 2016-03-30 onwards, it represents whether the
     * specific ipconfiguration is IPv4 or IPv6. Default is taken as IPv4.
     */
    @JsonProperty(value = "properties.privateIPAddressVersion")
    private IpVersion privateIpAddressVersion;

    /*
     * Subnet bound to the IP configuration.
     */
    @JsonProperty(value = "properties.subnet")
    private SubnetInner subnet;

    /*
     * Gets whether this is a primary customer address on the network
     * interface.
     */
    @JsonProperty(value = "properties.primary")
    private Boolean primary;

    /*
     * Public IP address bound to the IP configuration.
     */
    @JsonProperty(value = "properties.publicIPAddress")
    private PublicIpAddressInner publicIpAddress;

    /*
     * Application security groups in which the IP configuration is included.
     */
    @JsonProperty(value = "properties.applicationSecurityGroups")
    private List<ApplicationSecurityGroupInner> applicationSecurityGroups;

    /*
     * The provisioning state of the network interface IP configuration.
     * Possible values are: 'Updating', 'Deleting', and 'Failed'.
     */
    @JsonProperty(value = "properties.provisioningState")
    private String provisioningState;

    /**
     * Get the name property: The name of the resource that is unique within a resource group. This name can be used to
     * access the resource.
     *
     * @return the name value.
     */
    public String name() {
        return this.name;
    }

    /**
     * Set the name property: The name of the resource that is unique within a resource group. This name can be used to
     * access the resource.
     *
     * @param name the name value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withName(String name) {
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
     * Set the etag property: A unique read-only string that changes whenever the resource is updated.
     *
     * @param etag the etag value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withEtag(String etag) {
        this.etag = etag;
        return this;
    }

    /**
     * Get the virtualNetworkTaps property: The reference to Virtual Network Taps.
     *
     * @return the virtualNetworkTaps value.
     */
    public List<VirtualNetworkTapInner> virtualNetworkTaps() {
        return this.virtualNetworkTaps;
    }

    /**
     * Set the virtualNetworkTaps property: The reference to Virtual Network Taps.
     *
     * @param virtualNetworkTaps the virtualNetworkTaps value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withVirtualNetworkTaps(
        List<VirtualNetworkTapInner> virtualNetworkTaps) {
        this.virtualNetworkTaps = virtualNetworkTaps;
        return this;
    }

    /**
     * Get the applicationGatewayBackendAddressPools property: The reference of ApplicationGatewayBackendAddressPool
     * resource.
     *
     * @return the applicationGatewayBackendAddressPools value.
     */
    public List<ApplicationGatewayBackendAddressPool> applicationGatewayBackendAddressPools() {
        return this.applicationGatewayBackendAddressPools;
    }

    /**
     * Set the applicationGatewayBackendAddressPools property: The reference of ApplicationGatewayBackendAddressPool
     * resource.
     *
     * @param applicationGatewayBackendAddressPools the applicationGatewayBackendAddressPools value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withApplicationGatewayBackendAddressPools(
        List<ApplicationGatewayBackendAddressPool> applicationGatewayBackendAddressPools) {
        this.applicationGatewayBackendAddressPools = applicationGatewayBackendAddressPools;
        return this;
    }

    /**
     * Get the loadBalancerBackendAddressPools property: The reference of LoadBalancerBackendAddressPool resource.
     *
     * @return the loadBalancerBackendAddressPools value.
     */
    public List<BackendAddressPoolInner> loadBalancerBackendAddressPools() {
        return this.loadBalancerBackendAddressPools;
    }

    /**
     * Set the loadBalancerBackendAddressPools property: The reference of LoadBalancerBackendAddressPool resource.
     *
     * @param loadBalancerBackendAddressPools the loadBalancerBackendAddressPools value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withLoadBalancerBackendAddressPools(
        List<BackendAddressPoolInner> loadBalancerBackendAddressPools) {
        this.loadBalancerBackendAddressPools = loadBalancerBackendAddressPools;
        return this;
    }

    /**
     * Get the loadBalancerInboundNatRules property: A list of references of LoadBalancerInboundNatRules.
     *
     * @return the loadBalancerInboundNatRules value.
     */
    public List<InboundNatRuleInner> loadBalancerInboundNatRules() {
        return this.loadBalancerInboundNatRules;
    }

    /**
     * Set the loadBalancerInboundNatRules property: A list of references of LoadBalancerInboundNatRules.
     *
     * @param loadBalancerInboundNatRules the loadBalancerInboundNatRules value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withLoadBalancerInboundNatRules(
        List<InboundNatRuleInner> loadBalancerInboundNatRules) {
        this.loadBalancerInboundNatRules = loadBalancerInboundNatRules;
        return this;
    }

    /**
     * Get the privateIpAddress property: Private IP address of the IP configuration.
     *
     * @return the privateIpAddress value.
     */
    public String privateIpAddress() {
        return this.privateIpAddress;
    }

    /**
     * Set the privateIpAddress property: Private IP address of the IP configuration.
     *
     * @param privateIpAddress the privateIpAddress value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
        return this;
    }

    /**
     * Get the privateIpAllocationMethod property: The private IP address allocation method.
     *
     * @return the privateIpAllocationMethod value.
     */
    public IpAllocationMethod privateIpAllocationMethod() {
        return this.privateIpAllocationMethod;
    }

    /**
     * Set the privateIpAllocationMethod property: The private IP address allocation method.
     *
     * @param privateIpAllocationMethod the privateIpAllocationMethod value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withPrivateIpAllocationMethod(
        IpAllocationMethod privateIpAllocationMethod) {
        this.privateIpAllocationMethod = privateIpAllocationMethod;
        return this;
    }

    /**
     * Get the privateIpAddressVersion property: Available from Api-Version 2016-03-30 onwards, it represents whether
     * the specific ipconfiguration is IPv4 or IPv6. Default is taken as IPv4.
     *
     * @return the privateIpAddressVersion value.
     */
    public IpVersion privateIpAddressVersion() {
        return this.privateIpAddressVersion;
    }

    /**
     * Set the privateIpAddressVersion property: Available from Api-Version 2016-03-30 onwards, it represents whether
     * the specific ipconfiguration is IPv4 or IPv6. Default is taken as IPv4.
     *
     * @param privateIpAddressVersion the privateIpAddressVersion value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withPrivateIpAddressVersion(IpVersion privateIpAddressVersion) {
        this.privateIpAddressVersion = privateIpAddressVersion;
        return this;
    }

    /**
     * Get the subnet property: Subnet bound to the IP configuration.
     *
     * @return the subnet value.
     */
    public SubnetInner subnet() {
        return this.subnet;
    }

    /**
     * Set the subnet property: Subnet bound to the IP configuration.
     *
     * @param subnet the subnet value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withSubnet(SubnetInner subnet) {
        this.subnet = subnet;
        return this;
    }

    /**
     * Get the primary property: Gets whether this is a primary customer address on the network interface.
     *
     * @return the primary value.
     */
    public Boolean primary() {
        return this.primary;
    }

    /**
     * Set the primary property: Gets whether this is a primary customer address on the network interface.
     *
     * @param primary the primary value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withPrimary(Boolean primary) {
        this.primary = primary;
        return this;
    }

    /**
     * Get the publicIpAddress property: Public IP address bound to the IP configuration.
     *
     * @return the publicIpAddress value.
     */
    public PublicIpAddressInner publicIpAddress() {
        return this.publicIpAddress;
    }

    /**
     * Set the publicIpAddress property: Public IP address bound to the IP configuration.
     *
     * @param publicIpAddress the publicIpAddress value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withPublicIpAddress(PublicIpAddressInner publicIpAddress) {
        this.publicIpAddress = publicIpAddress;
        return this;
    }

    /**
     * Get the applicationSecurityGroups property: Application security groups in which the IP configuration is
     * included.
     *
     * @return the applicationSecurityGroups value.
     */
    public List<ApplicationSecurityGroupInner> applicationSecurityGroups() {
        return this.applicationSecurityGroups;
    }

    /**
     * Set the applicationSecurityGroups property: Application security groups in which the IP configuration is
     * included.
     *
     * @param applicationSecurityGroups the applicationSecurityGroups value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withApplicationSecurityGroups(
        List<ApplicationSecurityGroupInner> applicationSecurityGroups) {
        this.applicationSecurityGroups = applicationSecurityGroups;
        return this;
    }

    /**
     * Get the provisioningState property: The provisioning state of the network interface IP configuration. Possible
     * values are: 'Updating', 'Deleting', and 'Failed'.
     *
     * @return the provisioningState value.
     */
    public String provisioningState() {
        return this.provisioningState;
    }

    /**
     * Set the provisioningState property: The provisioning state of the network interface IP configuration. Possible
     * values are: 'Updating', 'Deleting', and 'Failed'.
     *
     * @param provisioningState the provisioningState value to set.
     * @return the NetworkInterfaceIpConfigurationInner object itself.
     */
    public NetworkInterfaceIpConfigurationInner withProvisioningState(String provisioningState) {
        this.provisioningState = provisioningState;
        return this;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (virtualNetworkTaps() != null) {
            virtualNetworkTaps().forEach(e -> e.validate());
        }
        if (applicationGatewayBackendAddressPools() != null) {
            applicationGatewayBackendAddressPools().forEach(e -> e.validate());
        }
        if (loadBalancerBackendAddressPools() != null) {
            loadBalancerBackendAddressPools().forEach(e -> e.validate());
        }
        if (loadBalancerInboundNatRules() != null) {
            loadBalancerInboundNatRules().forEach(e -> e.validate());
        }
        if (subnet() != null) {
            subnet().validate();
        }
        if (publicIpAddress() != null) {
            publicIpAddress().validate();
        }
        if (applicationSecurityGroups() != null) {
            applicationSecurityGroups().forEach(e -> e.validate());
        }
    }
}
