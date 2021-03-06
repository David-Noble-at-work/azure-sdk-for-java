/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.network.v2020_06_01;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Application gateway BackendHealth pool.
 */
public class ApplicationGatewayBackendHealthPool {
    /**
     * Reference to an ApplicationGatewayBackendAddressPool resource.
     */
    @JsonProperty(value = "backendAddressPool")
    private ApplicationGatewayBackendAddressPool backendAddressPool;

    /**
     * List of ApplicationGatewayBackendHealthHttpSettings resources.
     */
    @JsonProperty(value = "backendHttpSettingsCollection")
    private List<ApplicationGatewayBackendHealthHttpSettings> backendHttpSettingsCollection;

    /**
     * Get reference to an ApplicationGatewayBackendAddressPool resource.
     *
     * @return the backendAddressPool value
     */
    public ApplicationGatewayBackendAddressPool backendAddressPool() {
        return this.backendAddressPool;
    }

    /**
     * Set reference to an ApplicationGatewayBackendAddressPool resource.
     *
     * @param backendAddressPool the backendAddressPool value to set
     * @return the ApplicationGatewayBackendHealthPool object itself.
     */
    public ApplicationGatewayBackendHealthPool withBackendAddressPool(ApplicationGatewayBackendAddressPool backendAddressPool) {
        this.backendAddressPool = backendAddressPool;
        return this;
    }

    /**
     * Get list of ApplicationGatewayBackendHealthHttpSettings resources.
     *
     * @return the backendHttpSettingsCollection value
     */
    public List<ApplicationGatewayBackendHealthHttpSettings> backendHttpSettingsCollection() {
        return this.backendHttpSettingsCollection;
    }

    /**
     * Set list of ApplicationGatewayBackendHealthHttpSettings resources.
     *
     * @param backendHttpSettingsCollection the backendHttpSettingsCollection value to set
     * @return the ApplicationGatewayBackendHealthPool object itself.
     */
    public ApplicationGatewayBackendHealthPool withBackendHttpSettingsCollection(List<ApplicationGatewayBackendHealthHttpSettings> backendHttpSettingsCollection) {
        this.backendHttpSettingsCollection = backendHttpSettingsCollection;
        return this;
    }

}
