// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.network.models;

import com.azure.core.annotation.Immutable;
import com.azure.core.util.logging.ClientLogger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The TunnelConnectionHealth model. */
@Immutable
public final class TunnelConnectionHealth {
    @JsonIgnore private final ClientLogger logger = new ClientLogger(TunnelConnectionHealth.class);

    /*
     * Tunnel name.
     */
    @JsonProperty(value = "tunnel", access = JsonProperty.Access.WRITE_ONLY)
    private String tunnel;

    /*
     * Virtual Network Gateway connection status.
     */
    @JsonProperty(value = "connectionStatus", access = JsonProperty.Access.WRITE_ONLY)
    private VirtualNetworkGatewayConnectionStatus connectionStatus;

    /*
     * The Ingress Bytes Transferred in this connection.
     */
    @JsonProperty(value = "ingressBytesTransferred", access = JsonProperty.Access.WRITE_ONLY)
    private Long ingressBytesTransferred;

    /*
     * The Egress Bytes Transferred in this connection.
     */
    @JsonProperty(value = "egressBytesTransferred", access = JsonProperty.Access.WRITE_ONLY)
    private Long egressBytesTransferred;

    /*
     * The time at which connection was established in Utc format.
     */
    @JsonProperty(value = "lastConnectionEstablishedUtcTime", access = JsonProperty.Access.WRITE_ONLY)
    private String lastConnectionEstablishedUtcTime;

    /**
     * Get the tunnel property: Tunnel name.
     *
     * @return the tunnel value.
     */
    public String tunnel() {
        return this.tunnel;
    }

    /**
     * Get the connectionStatus property: Virtual Network Gateway connection status.
     *
     * @return the connectionStatus value.
     */
    public VirtualNetworkGatewayConnectionStatus connectionStatus() {
        return this.connectionStatus;
    }

    /**
     * Get the ingressBytesTransferred property: The Ingress Bytes Transferred in this connection.
     *
     * @return the ingressBytesTransferred value.
     */
    public Long ingressBytesTransferred() {
        return this.ingressBytesTransferred;
    }

    /**
     * Get the egressBytesTransferred property: The Egress Bytes Transferred in this connection.
     *
     * @return the egressBytesTransferred value.
     */
    public Long egressBytesTransferred() {
        return this.egressBytesTransferred;
    }

    /**
     * Get the lastConnectionEstablishedUtcTime property: The time at which connection was established in Utc format.
     *
     * @return the lastConnectionEstablishedUtcTime value.
     */
    public String lastConnectionEstablishedUtcTime() {
        return this.lastConnectionEstablishedUtcTime;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }
}
