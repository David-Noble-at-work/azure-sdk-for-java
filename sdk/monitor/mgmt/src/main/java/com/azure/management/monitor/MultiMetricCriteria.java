// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.monitor;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The MultiMetricCriteria model. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "criterionType",
    defaultImpl = MultiMetricCriteria.class)
@JsonTypeName("MultiMetricCriteria")
@JsonSubTypes({
    @JsonSubTypes.Type(name = "StaticThresholdCriterion", value = MetricCriteria.class),
    @JsonSubTypes.Type(name = "DynamicThresholdCriterion", value = DynamicMetricCriteria.class)
})
@Fluent
public class MultiMetricCriteria {
    /*
     * Name of the criteria.
     */
    @JsonProperty(value = "name", required = true)
    private String name;

    /*
     * Name of the metric.
     */
    @JsonProperty(value = "metricName", required = true)
    private String metricName;

    /*
     * Namespace of the metric.
     */
    @JsonProperty(value = "metricNamespace")
    private String metricNamespace;

    /*
     * the criteria time aggregation types.
     */
    @JsonProperty(value = "timeAggregation", required = true)
    private AggregationType timeAggregation;

    /*
     * List of dimension conditions.
     */
    @JsonProperty(value = "dimensions")
    private List<MetricDimension> dimensions;

    /*
     * The types of conditions for a multi resource alert.
     */
    @JsonIgnore private Map<String, Object> additionalProperties;

    /**
     * Get the name property: Name of the criteria.
     *
     * @return the name value.
     */
    public String name() {
        return this.name;
    }

    /**
     * Set the name property: Name of the criteria.
     *
     * @param name the name value to set.
     * @return the MultiMetricCriteria object itself.
     */
    public MultiMetricCriteria withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the metricName property: Name of the metric.
     *
     * @return the metricName value.
     */
    public String metricName() {
        return this.metricName;
    }

    /**
     * Set the metricName property: Name of the metric.
     *
     * @param metricName the metricName value to set.
     * @return the MultiMetricCriteria object itself.
     */
    public MultiMetricCriteria withMetricName(String metricName) {
        this.metricName = metricName;
        return this;
    }

    /**
     * Get the metricNamespace property: Namespace of the metric.
     *
     * @return the metricNamespace value.
     */
    public String metricNamespace() {
        return this.metricNamespace;
    }

    /**
     * Set the metricNamespace property: Namespace of the metric.
     *
     * @param metricNamespace the metricNamespace value to set.
     * @return the MultiMetricCriteria object itself.
     */
    public MultiMetricCriteria withMetricNamespace(String metricNamespace) {
        this.metricNamespace = metricNamespace;
        return this;
    }

    /**
     * Get the timeAggregation property: the criteria time aggregation types.
     *
     * @return the timeAggregation value.
     */
    public AggregationType timeAggregation() {
        return this.timeAggregation;
    }

    /**
     * Set the timeAggregation property: the criteria time aggregation types.
     *
     * @param timeAggregation the timeAggregation value to set.
     * @return the MultiMetricCriteria object itself.
     */
    public MultiMetricCriteria withTimeAggregation(AggregationType timeAggregation) {
        this.timeAggregation = timeAggregation;
        return this;
    }

    /**
     * Get the dimensions property: List of dimension conditions.
     *
     * @return the dimensions value.
     */
    public List<MetricDimension> dimensions() {
        return this.dimensions;
    }

    /**
     * Set the dimensions property: List of dimension conditions.
     *
     * @param dimensions the dimensions value to set.
     * @return the MultiMetricCriteria object itself.
     */
    public MultiMetricCriteria withDimensions(List<MetricDimension> dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    /**
     * Get the additionalProperties property: The types of conditions for a multi resource alert.
     *
     * @return the additionalProperties value.
     */
    @JsonAnyGetter
    public Map<String, Object> additionalProperties() {
        return this.additionalProperties;
    }

    /**
     * Set the additionalProperties property: The types of conditions for a multi resource alert.
     *
     * @param additionalProperties the additionalProperties value to set.
     * @return the MultiMetricCriteria object itself.
     */
    public MultiMetricCriteria withAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
        return this;
    }

    @JsonAnySetter
    void withAdditionalProperties(String key, Object value) {
        if (additionalProperties == null) {
            additionalProperties = new HashMap<>();
        }
        additionalProperties.put(key, value);
    }
}
