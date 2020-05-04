// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.ai.formrecognizer.implementation.models;

import com.azure.ai.formrecognizer.models.ErrorInformation;
import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * The TrainResult model.
 */
@Fluent
public final class TrainResult {
    /*
     * List of the documents used to train the model and any errors reported in
     * each document.
     */
    @JsonProperty(value = "trainingDocuments", required = true)
    private List<TrainingDocumentInfo> trainingDocuments;

    /*
     * List of fields used to train the model and the train operation error
     * reported by each.
     */
    @JsonProperty(value = "fields")
    private List<FormFieldsReport> fields;

    /*
     * Average accuracy.
     */
    @JsonProperty(value = "averageModelAccuracy")
    private Float averageModelAccuracy;

    /*
     * Errors returned during the training operation.
     */
    @JsonProperty(value = "errors")
    private List<ErrorInformation> errors;

    /**
     * Get the trainingDocuments property: List of the documents used to train
     * the model and any errors reported in each document.
     * 
     * @return the trainingDocuments value.
     */
    public List<TrainingDocumentInfo> getTrainingDocuments() {
        return this.trainingDocuments;
    }

    /**
     * Set the trainingDocuments property: List of the documents used to train
     * the model and any errors reported in each document.
     * 
     * @param trainingDocuments the trainingDocuments value to set.
     * @return the TrainResult object itself.
     */
    public TrainResult setTrainingDocuments(List<TrainingDocumentInfo> trainingDocuments) {
        this.trainingDocuments = trainingDocuments;
        return this;
    }

    /**
     * Get the fields property: List of fields used to train the model and the
     * train operation error reported by each.
     * 
     * @return the fields value.
     */
    public List<FormFieldsReport> getFields() {
        return this.fields;
    }

    /**
     * Set the fields property: List of fields used to train the model and the
     * train operation error reported by each.
     * 
     * @param fields the fields value to set.
     * @return the TrainResult object itself.
     */
    public TrainResult setFields(List<FormFieldsReport> fields) {
        this.fields = fields;
        return this;
    }

    /**
     * Get the averageModelAccuracy property: Average accuracy.
     * 
     * @return the averageModelAccuracy value.
     */
    public Float getAverageModelAccuracy() {
        return this.averageModelAccuracy;
    }

    /**
     * Set the averageModelAccuracy property: Average accuracy.
     * 
     * @param averageModelAccuracy the averageModelAccuracy value to set.
     * @return the TrainResult object itself.
     */
    public TrainResult setAverageModelAccuracy(Float averageModelAccuracy) {
        this.averageModelAccuracy = averageModelAccuracy;
        return this;
    }

    /**
     * Get the errors property: Errors returned during the training operation.
     * 
     * @return the errors value.
     */
    public List<ErrorInformation> getErrors() {
        return this.errors;
    }

    /**
     * Set the errors property: Errors returned during the training operation.
     * 
     * @param errors the errors value to set.
     * @return the TrainResult object itself.
     */
    public TrainResult setErrors(List<ErrorInformation> errors) {
        this.errors = errors;
        return this;
    }
}
