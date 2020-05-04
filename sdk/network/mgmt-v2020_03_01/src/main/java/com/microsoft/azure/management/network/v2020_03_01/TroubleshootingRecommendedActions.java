/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.network.v2020_03_01;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Recommended actions based on discovered issues.
 */
public class TroubleshootingRecommendedActions {
    /**
     * ID of the recommended action.
     */
    @JsonProperty(value = "actionId")
    private String actionId;

    /**
     * Description of recommended actions.
     */
    @JsonProperty(value = "actionText")
    private String actionText;

    /**
     * The uri linking to a documentation for the recommended troubleshooting
     * actions.
     */
    @JsonProperty(value = "actionUri")
    private String actionUri;

    /**
     * The information from the URI for the recommended troubleshooting
     * actions.
     */
    @JsonProperty(value = "actionUriText")
    private String actionUriText;

    /**
     * Get iD of the recommended action.
     *
     * @return the actionId value
     */
    public String actionId() {
        return this.actionId;
    }

    /**
     * Set iD of the recommended action.
     *
     * @param actionId the actionId value to set
     * @return the TroubleshootingRecommendedActions object itself.
     */
    public TroubleshootingRecommendedActions withActionId(String actionId) {
        this.actionId = actionId;
        return this;
    }

    /**
     * Get description of recommended actions.
     *
     * @return the actionText value
     */
    public String actionText() {
        return this.actionText;
    }

    /**
     * Set description of recommended actions.
     *
     * @param actionText the actionText value to set
     * @return the TroubleshootingRecommendedActions object itself.
     */
    public TroubleshootingRecommendedActions withActionText(String actionText) {
        this.actionText = actionText;
        return this;
    }

    /**
     * Get the uri linking to a documentation for the recommended troubleshooting actions.
     *
     * @return the actionUri value
     */
    public String actionUri() {
        return this.actionUri;
    }

    /**
     * Set the uri linking to a documentation for the recommended troubleshooting actions.
     *
     * @param actionUri the actionUri value to set
     * @return the TroubleshootingRecommendedActions object itself.
     */
    public TroubleshootingRecommendedActions withActionUri(String actionUri) {
        this.actionUri = actionUri;
        return this;
    }

    /**
     * Get the information from the URI for the recommended troubleshooting actions.
     *
     * @return the actionUriText value
     */
    public String actionUriText() {
        return this.actionUriText;
    }

    /**
     * Set the information from the URI for the recommended troubleshooting actions.
     *
     * @param actionUriText the actionUriText value to set
     * @return the TroubleshootingRecommendedActions object itself.
     */
    public TroubleshootingRecommendedActions withActionUriText(String actionUriText) {
        this.actionUriText = actionUriText;
        return this;
    }

}
