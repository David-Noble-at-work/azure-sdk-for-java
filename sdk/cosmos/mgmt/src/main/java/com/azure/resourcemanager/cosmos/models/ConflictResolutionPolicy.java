// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.cosmos.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The ConflictResolutionPolicy model. */
@Fluent
public final class ConflictResolutionPolicy {
    @JsonIgnore private final ClientLogger logger = new ClientLogger(ConflictResolutionPolicy.class);

    /*
     * Indicates the conflict resolution mode.
     */
    @JsonProperty(value = "mode")
    private ConflictResolutionMode mode;

    /*
     * The conflict resolution path in the case of LastWriterWins mode.
     */
    @JsonProperty(value = "conflictResolutionPath")
    private String conflictResolutionPath;

    /*
     * The procedure to resolve conflicts in the case of custom mode.
     */
    @JsonProperty(value = "conflictResolutionProcedure")
    private String conflictResolutionProcedure;

    /**
     * Get the mode property: Indicates the conflict resolution mode.
     *
     * @return the mode value.
     */
    public ConflictResolutionMode mode() {
        return this.mode;
    }

    /**
     * Set the mode property: Indicates the conflict resolution mode.
     *
     * @param mode the mode value to set.
     * @return the ConflictResolutionPolicy object itself.
     */
    public ConflictResolutionPolicy withMode(ConflictResolutionMode mode) {
        this.mode = mode;
        return this;
    }

    /**
     * Get the conflictResolutionPath property: The conflict resolution path in the case of LastWriterWins mode.
     *
     * @return the conflictResolutionPath value.
     */
    public String conflictResolutionPath() {
        return this.conflictResolutionPath;
    }

    /**
     * Set the conflictResolutionPath property: The conflict resolution path in the case of LastWriterWins mode.
     *
     * @param conflictResolutionPath the conflictResolutionPath value to set.
     * @return the ConflictResolutionPolicy object itself.
     */
    public ConflictResolutionPolicy withConflictResolutionPath(String conflictResolutionPath) {
        this.conflictResolutionPath = conflictResolutionPath;
        return this;
    }

    /**
     * Get the conflictResolutionProcedure property: The procedure to resolve conflicts in the case of custom mode.
     *
     * @return the conflictResolutionProcedure value.
     */
    public String conflictResolutionProcedure() {
        return this.conflictResolutionProcedure;
    }

    /**
     * Set the conflictResolutionProcedure property: The procedure to resolve conflicts in the case of custom mode.
     *
     * @param conflictResolutionProcedure the conflictResolutionProcedure value to set.
     * @return the ConflictResolutionPolicy object itself.
     */
    public ConflictResolutionPolicy withConflictResolutionProcedure(String conflictResolutionProcedure) {
        this.conflictResolutionProcedure = conflictResolutionProcedure;
        return this;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }
}
