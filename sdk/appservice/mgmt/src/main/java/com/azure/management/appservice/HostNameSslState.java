// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.appservice;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The HostNameSslState model. */
@Fluent
public final class HostNameSslState {
    /*
     * Hostname.
     */
    @JsonProperty(value = "name")
    private String name;

    /*
     * SSL type.
     */
    @JsonProperty(value = "sslState")
    private SslState sslState;

    /*
     * Virtual IP address assigned to the hostname if IP based SSL is enabled.
     */
    @JsonProperty(value = "virtualIP")
    private String virtualIP;

    /*
     * SSL certificate thumbprint.
     */
    @JsonProperty(value = "thumbprint")
    private String thumbprint;

    /*
     * Set to <code>true</code> to update existing hostname.
     */
    @JsonProperty(value = "toUpdate")
    private Boolean toUpdate;

    /*
     * Indicates whether the hostname is a standard or repository hostname.
     */
    @JsonProperty(value = "hostType")
    private HostType hostType;

    /**
     * Get the name property: Hostname.
     *
     * @return the name value.
     */
    public String name() {
        return this.name;
    }

    /**
     * Set the name property: Hostname.
     *
     * @param name the name value to set.
     * @return the HostNameSslState object itself.
     */
    public HostNameSslState withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the sslState property: SSL type.
     *
     * @return the sslState value.
     */
    public SslState sslState() {
        return this.sslState;
    }

    /**
     * Set the sslState property: SSL type.
     *
     * @param sslState the sslState value to set.
     * @return the HostNameSslState object itself.
     */
    public HostNameSslState withSslState(SslState sslState) {
        this.sslState = sslState;
        return this;
    }

    /**
     * Get the virtualIP property: Virtual IP address assigned to the hostname if IP based SSL is enabled.
     *
     * @return the virtualIP value.
     */
    public String virtualIP() {
        return this.virtualIP;
    }

    /**
     * Set the virtualIP property: Virtual IP address assigned to the hostname if IP based SSL is enabled.
     *
     * @param virtualIP the virtualIP value to set.
     * @return the HostNameSslState object itself.
     */
    public HostNameSslState withVirtualIP(String virtualIP) {
        this.virtualIP = virtualIP;
        return this;
    }

    /**
     * Get the thumbprint property: SSL certificate thumbprint.
     *
     * @return the thumbprint value.
     */
    public String thumbprint() {
        return this.thumbprint;
    }

    /**
     * Set the thumbprint property: SSL certificate thumbprint.
     *
     * @param thumbprint the thumbprint value to set.
     * @return the HostNameSslState object itself.
     */
    public HostNameSslState withThumbprint(String thumbprint) {
        this.thumbprint = thumbprint;
        return this;
    }

    /**
     * Get the toUpdate property: Set to &lt;code&gt;true&lt;/code&gt; to update existing hostname.
     *
     * @return the toUpdate value.
     */
    public Boolean toUpdate() {
        return this.toUpdate;
    }

    /**
     * Set the toUpdate property: Set to &lt;code&gt;true&lt;/code&gt; to update existing hostname.
     *
     * @param toUpdate the toUpdate value to set.
     * @return the HostNameSslState object itself.
     */
    public HostNameSslState withToUpdate(Boolean toUpdate) {
        this.toUpdate = toUpdate;
        return this;
    }

    /**
     * Get the hostType property: Indicates whether the hostname is a standard or repository hostname.
     *
     * @return the hostType value.
     */
    public HostType hostType() {
        return this.hostType;
    }

    /**
     * Set the hostType property: Indicates whether the hostname is a standard or repository hostname.
     *
     * @param hostType the hostType value to set.
     * @return the HostNameSslState object itself.
     */
    public HostNameSslState withHostType(HostType hostType) {
        this.hostType = hostType;
        return this;
    }
}
