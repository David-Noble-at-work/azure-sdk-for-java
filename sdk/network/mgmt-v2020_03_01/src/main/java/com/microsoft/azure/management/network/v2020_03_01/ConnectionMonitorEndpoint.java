/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.network.v2020_03_01;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes the connection monitor endpoint.
 */
public class ConnectionMonitorEndpoint {
    /**
     * The name of the connection monitor endpoint.
     */
    @JsonProperty(value = "name", required = true)
    private String name;

    /**
     * Resource ID of the connection monitor endpoint.
     */
    @JsonProperty(value = "resourceId")
    private String resourceId;

    /**
     * Address of the connection monitor endpoint (IP or domain name).
     */
    @JsonProperty(value = "address")
    private String address;

    /**
     * Filter for sub-items within the endpoint.
     */
    @JsonProperty(value = "filter")
    private ConnectionMonitorEndpointFilter filter;

    /**
     * Get the name of the connection monitor endpoint.
     *
     * @return the name value
     */
    public String name() {
        return this.name;
    }

    /**
     * Set the name of the connection monitor endpoint.
     *
     * @param name the name value to set
     * @return the ConnectionMonitorEndpoint object itself.
     */
    public ConnectionMonitorEndpoint withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get resource ID of the connection monitor endpoint.
     *
     * @return the resourceId value
     */
    public String resourceId() {
        return this.resourceId;
    }

    /**
     * Set resource ID of the connection monitor endpoint.
     *
     * @param resourceId the resourceId value to set
     * @return the ConnectionMonitorEndpoint object itself.
     */
    public ConnectionMonitorEndpoint withResourceId(String resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    /**
     * Get address of the connection monitor endpoint (IP or domain name).
     *
     * @return the address value
     */
    public String address() {
        return this.address;
    }

    /**
     * Set address of the connection monitor endpoint (IP or domain name).
     *
     * @param address the address value to set
     * @return the ConnectionMonitorEndpoint object itself.
     */
    public ConnectionMonitorEndpoint withAddress(String address) {
        this.address = address;
        return this;
    }

    /**
     * Get filter for sub-items within the endpoint.
     *
     * @return the filter value
     */
    public ConnectionMonitorEndpointFilter filter() {
        return this.filter;
    }

    /**
     * Set filter for sub-items within the endpoint.
     *
     * @param filter the filter value to set
     * @return the ConnectionMonitorEndpoint object itself.
     */
    public ConnectionMonitorEndpoint withFilter(ConnectionMonitorEndpointFilter filter) {
        this.filter = filter;
        return this;
    }

}
