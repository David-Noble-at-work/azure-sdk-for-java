/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.cosmosdb.v2019_12_12;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The availability of the metric.
 */
public class MetricAvailability {
    /**
     * The time grain to be used to summarize the metric values.
     */
    @JsonProperty(value = "timeGrain", access = JsonProperty.Access.WRITE_ONLY)
    private String timeGrain;

    /**
     * The retention for the metric values.
     */
    @JsonProperty(value = "retention", access = JsonProperty.Access.WRITE_ONLY)
    private String retention;

    /**
     * Get the time grain to be used to summarize the metric values.
     *
     * @return the timeGrain value
     */
    public String timeGrain() {
        return this.timeGrain;
    }

    /**
     * Get the retention for the metric values.
     *
     * @return the retention value
     */
    public String retention() {
        return this.retention;
    }

}
