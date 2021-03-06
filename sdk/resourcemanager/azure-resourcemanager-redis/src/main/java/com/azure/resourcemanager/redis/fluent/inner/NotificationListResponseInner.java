// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.redis.fluent.inner;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.redis.models.UpgradeNotification;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** The NotificationListResponse model. */
@Fluent
public final class NotificationListResponseInner {
    @JsonIgnore private final ClientLogger logger = new ClientLogger(NotificationListResponseInner.class);

    /*
     * List of all notifications.
     */
    @JsonProperty(value = "value")
    private List<UpgradeNotification> value;

    /*
     * Link for next set of notifications.
     */
    @JsonProperty(value = "nextLink", access = JsonProperty.Access.WRITE_ONLY)
    private String nextLink;

    /**
     * Get the value property: List of all notifications.
     *
     * @return the value value.
     */
    public List<UpgradeNotification> value() {
        return this.value;
    }

    /**
     * Set the value property: List of all notifications.
     *
     * @param value the value value to set.
     * @return the NotificationListResponseInner object itself.
     */
    public NotificationListResponseInner withValue(List<UpgradeNotification> value) {
        this.value = value;
        return this;
    }

    /**
     * Get the nextLink property: Link for next set of notifications.
     *
     * @return the nextLink value.
     */
    public String nextLink() {
        return this.nextLink;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (value() != null) {
            value().forEach(e -> e.validate());
        }
    }
}
