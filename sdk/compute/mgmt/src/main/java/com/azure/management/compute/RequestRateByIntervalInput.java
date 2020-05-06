// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.compute;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The RequestRateByIntervalInput model. */
@Fluent
public final class RequestRateByIntervalInput extends LogAnalyticsInputBase {
    /*
     * Interval value in minutes used to create LogAnalytics call rate logs.
     */
    @JsonProperty(value = "intervalLength", required = true)
    private IntervalInMins intervalLength;

    /**
     * Get the intervalLength property: Interval value in minutes used to create LogAnalytics call rate logs.
     *
     * @return the intervalLength value.
     */
    public IntervalInMins intervalLength() {
        return this.intervalLength;
    }

    /**
     * Set the intervalLength property: Interval value in minutes used to create LogAnalytics call rate logs.
     *
     * @param intervalLength the intervalLength value to set.
     * @return the RequestRateByIntervalInput object itself.
     */
    public RequestRateByIntervalInput withIntervalLength(IntervalInMins intervalLength) {
        this.intervalLength = intervalLength;
        return this;
    }
}
