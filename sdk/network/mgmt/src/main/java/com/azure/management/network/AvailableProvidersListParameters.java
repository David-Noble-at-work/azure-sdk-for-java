// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.network;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** The AvailableProvidersListParameters model. */
@Fluent
public final class AvailableProvidersListParameters {
    /*
     * A list of Azure regions.
     */
    @JsonProperty(value = "azureLocations")
    private List<String> azureLocations;

    /*
     * The country for available providers list.
     */
    @JsonProperty(value = "country")
    private String country;

    /*
     * The state for available providers list.
     */
    @JsonProperty(value = "state")
    private String state;

    /*
     * The city or town for available providers list.
     */
    @JsonProperty(value = "city")
    private String city;

    /**
     * Get the azureLocations property: A list of Azure regions.
     *
     * @return the azureLocations value.
     */
    public List<String> azureLocations() {
        return this.azureLocations;
    }

    /**
     * Set the azureLocations property: A list of Azure regions.
     *
     * @param azureLocations the azureLocations value to set.
     * @return the AvailableProvidersListParameters object itself.
     */
    public AvailableProvidersListParameters withAzureLocations(List<String> azureLocations) {
        this.azureLocations = azureLocations;
        return this;
    }

    /**
     * Get the country property: The country for available providers list.
     *
     * @return the country value.
     */
    public String country() {
        return this.country;
    }

    /**
     * Set the country property: The country for available providers list.
     *
     * @param country the country value to set.
     * @return the AvailableProvidersListParameters object itself.
     */
    public AvailableProvidersListParameters withCountry(String country) {
        this.country = country;
        return this;
    }

    /**
     * Get the state property: The state for available providers list.
     *
     * @return the state value.
     */
    public String state() {
        return this.state;
    }

    /**
     * Set the state property: The state for available providers list.
     *
     * @param state the state value to set.
     * @return the AvailableProvidersListParameters object itself.
     */
    public AvailableProvidersListParameters withState(String state) {
        this.state = state;
        return this;
    }

    /**
     * Get the city property: The city or town for available providers list.
     *
     * @return the city value.
     */
    public String city() {
        return this.city;
    }

    /**
     * Set the city property: The city or town for available providers list.
     *
     * @param city the city value to set.
     * @return the AvailableProvidersListParameters object itself.
     */
    public AvailableProvidersListParameters withCity(String city) {
        this.city = city;
        return this;
    }
}
