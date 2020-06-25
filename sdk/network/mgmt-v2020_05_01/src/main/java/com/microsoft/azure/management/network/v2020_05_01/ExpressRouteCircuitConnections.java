/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.network.v2020_05_01;

import com.microsoft.azure.arm.collection.SupportsCreating;
import rx.Completable;
import rx.Observable;
import com.microsoft.azure.management.network.v2020_05_01.implementation.ExpressRouteCircuitConnectionsInner;
import com.microsoft.azure.arm.model.HasInner;

/**
 * Type representing ExpressRouteCircuitConnections.
 */
public interface ExpressRouteCircuitConnections extends SupportsCreating<ExpressRouteCircuitConnection.DefinitionStages.Blank>, HasInner<ExpressRouteCircuitConnectionsInner> {
    /**
     * Gets the specified Express Route Circuit Connection from the specified express route circuit.
     *
     * @param resourceGroupName The name of the resource group.
     * @param circuitName The name of the express route circuit.
     * @param peeringName The name of the peering.
     * @param connectionName The name of the express route circuit connection.
     * @throws IllegalArgumentException thrown if parameters fail the validation
     * @return the observable for the request
     */
    Observable<ExpressRouteCircuitConnection> getAsync(String resourceGroupName, String circuitName, String peeringName, String connectionName);

    /**
     * Gets all global reach connections associated with a private peering in an express route circuit.
     *
     * @param resourceGroupName The name of the resource group.
     * @param circuitName The name of the circuit.
     * @param peeringName The name of the peering.
     * @throws IllegalArgumentException thrown if parameters fail the validation
     * @return the observable for the request
     */
    Observable<ExpressRouteCircuitConnection> listAsync(final String resourceGroupName, final String circuitName, final String peeringName);

    /**
     * Deletes the specified Express Route Circuit Connection from the specified express route circuit.
     *
     * @param resourceGroupName The name of the resource group.
     * @param circuitName The name of the express route circuit.
     * @param peeringName The name of the peering.
     * @param connectionName The name of the express route circuit connection.
     * @throws IllegalArgumentException thrown if parameters fail the validation
     * @return the observable for the request
     */
    Completable deleteAsync(String resourceGroupName, String circuitName, String peeringName, String connectionName);

}
