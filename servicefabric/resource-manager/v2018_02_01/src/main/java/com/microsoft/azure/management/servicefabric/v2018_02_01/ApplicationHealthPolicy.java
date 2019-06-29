/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.servicefabric.v2018_02_01;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Defines a health policy used to evaluate the health of an application or one
 * of its children entities.
 */
public class ApplicationHealthPolicy {
    /**
     * The health policy used by default to evaluate the health of a service
     * type.
     */
    @JsonProperty(value = "defaultServiceTypeHealthPolicy")
    private ServiceTypeHealthPolicy defaultServiceTypeHealthPolicy;

    /**
     * The map with service type health policy per service type name. The map
     * is empty by default.
     */
    @JsonProperty(value = "serviceTypeHealthPolicies")
    private Map<String, ServiceTypeHealthPolicy> serviceTypeHealthPolicies;

    /**
     * Get the health policy used by default to evaluate the health of a service type.
     *
     * @return the defaultServiceTypeHealthPolicy value
     */
    public ServiceTypeHealthPolicy defaultServiceTypeHealthPolicy() {
        return this.defaultServiceTypeHealthPolicy;
    }

    /**
     * Set the health policy used by default to evaluate the health of a service type.
     *
     * @param defaultServiceTypeHealthPolicy the defaultServiceTypeHealthPolicy value to set
     * @return the ApplicationHealthPolicy object itself.
     */
    public ApplicationHealthPolicy withDefaultServiceTypeHealthPolicy(ServiceTypeHealthPolicy defaultServiceTypeHealthPolicy) {
        this.defaultServiceTypeHealthPolicy = defaultServiceTypeHealthPolicy;
        return this;
    }

    /**
     * Get the map with service type health policy per service type name. The map is empty by default.
     *
     * @return the serviceTypeHealthPolicies value
     */
    public Map<String, ServiceTypeHealthPolicy> serviceTypeHealthPolicies() {
        return this.serviceTypeHealthPolicies;
    }

    /**
     * Set the map with service type health policy per service type name. The map is empty by default.
     *
     * @param serviceTypeHealthPolicies the serviceTypeHealthPolicies value to set
     * @return the ApplicationHealthPolicy object itself.
     */
    public ApplicationHealthPolicy withServiceTypeHealthPolicies(Map<String, ServiceTypeHealthPolicy> serviceTypeHealthPolicies) {
        this.serviceTypeHealthPolicies = serviceTypeHealthPolicies;
        return this;
    }

}
