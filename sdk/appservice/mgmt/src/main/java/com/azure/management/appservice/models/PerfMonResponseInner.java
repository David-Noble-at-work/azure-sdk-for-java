// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.appservice.models;

import com.azure.core.annotation.Fluent;
import com.azure.management.appservice.PerfMonSet;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The PerfMonResponse model. */
@Fluent
public final class PerfMonResponseInner {
    /*
     * The response code.
     */
    @JsonProperty(value = "code")
    private String code;

    /*
     * The message.
     */
    @JsonProperty(value = "message")
    private String message;

    /*
     * The performance monitor counters.
     */
    @JsonProperty(value = "data")
    private PerfMonSet data;

    /**
     * Get the code property: The response code.
     *
     * @return the code value.
     */
    public String code() {
        return this.code;
    }

    /**
     * Set the code property: The response code.
     *
     * @param code the code value to set.
     * @return the PerfMonResponseInner object itself.
     */
    public PerfMonResponseInner withCode(String code) {
        this.code = code;
        return this;
    }

    /**
     * Get the message property: The message.
     *
     * @return the message value.
     */
    public String message() {
        return this.message;
    }

    /**
     * Set the message property: The message.
     *
     * @param message the message value to set.
     * @return the PerfMonResponseInner object itself.
     */
    public PerfMonResponseInner withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Get the data property: The performance monitor counters.
     *
     * @return the data value.
     */
    public PerfMonSet data() {
        return this.data;
    }

    /**
     * Set the data property: The performance monitor counters.
     *
     * @param data the data value to set.
     * @return the PerfMonResponseInner object itself.
     */
    public PerfMonResponseInner withData(PerfMonSet data) {
        this.data = data;
        return this;
    }
}
