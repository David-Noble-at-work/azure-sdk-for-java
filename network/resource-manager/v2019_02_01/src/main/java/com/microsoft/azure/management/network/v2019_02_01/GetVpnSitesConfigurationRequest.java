/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.network.v2019_02_01;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * List of Vpn-Sites.
 */
public class GetVpnSitesConfigurationRequest {
    /**
     * List of resource-ids of the vpn-sites for which config is to be
     * downloaded.
     */
    @JsonProperty(value = "vpnSites")
    private List<String> vpnSites;

    /**
     * The sas-url to download the configurations for vpn-sites.
     */
    @JsonProperty(value = "outputBlobSasUrl", required = true)
    private String outputBlobSasUrl;

    /**
     * Get list of resource-ids of the vpn-sites for which config is to be downloaded.
     *
     * @return the vpnSites value
     */
    public List<String> vpnSites() {
        return this.vpnSites;
    }

    /**
     * Set list of resource-ids of the vpn-sites for which config is to be downloaded.
     *
     * @param vpnSites the vpnSites value to set
     * @return the GetVpnSitesConfigurationRequest object itself.
     */
    public GetVpnSitesConfigurationRequest withVpnSites(List<String> vpnSites) {
        this.vpnSites = vpnSites;
        return this;
    }

    /**
     * Get the sas-url to download the configurations for vpn-sites.
     *
     * @return the outputBlobSasUrl value
     */
    public String outputBlobSasUrl() {
        return this.outputBlobSasUrl;
    }

    /**
     * Set the sas-url to download the configurations for vpn-sites.
     *
     * @param outputBlobSasUrl the outputBlobSasUrl value to set
     * @return the GetVpnSitesConfigurationRequest object itself.
     */
    public GetVpnSitesConfigurationRequest withOutputBlobSasUrl(String outputBlobSasUrl) {
        this.outputBlobSasUrl = outputBlobSasUrl;
        return this;
    }

}
