/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.batch.protocol.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Options when disabling a Job.
 */
public class JobDisableParameter {
    /**
     * What to do with active Tasks associated with the Job.
     * Possible values include: 'requeue', 'terminate', 'wait'.
     */
    @JsonProperty(value = "disableTasks", required = true)
    private DisableJobOption disableTasks;

    /**
     * Get possible values include: 'requeue', 'terminate', 'wait'.
     *
     * @return the disableTasks value
     */
    public DisableJobOption disableTasks() {
        return this.disableTasks;
    }

    /**
     * Set possible values include: 'requeue', 'terminate', 'wait'.
     *
     * @param disableTasks the disableTasks value to set
     * @return the JobDisableParameter object itself.
     */
    public JobDisableParameter withDisableTasks(DisableJobOption disableTasks) {
        this.disableTasks = disableTasks;
        return this;
    }

}
