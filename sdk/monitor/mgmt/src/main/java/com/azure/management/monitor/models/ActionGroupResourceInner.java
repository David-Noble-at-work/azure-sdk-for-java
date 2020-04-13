// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.monitor.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.annotation.JsonFlatten;
import com.azure.core.management.Resource;
import com.azure.management.monitor.ArmRoleReceiver;
import com.azure.management.monitor.AutomationRunbookReceiver;
import com.azure.management.monitor.AzureAppPushReceiver;
import com.azure.management.monitor.AzureFunctionReceiver;
import com.azure.management.monitor.EmailReceiver;
import com.azure.management.monitor.ItsmReceiver;
import com.azure.management.monitor.LogicAppReceiver;
import com.azure.management.monitor.SmsReceiver;
import com.azure.management.monitor.VoiceReceiver;
import com.azure.management.monitor.WebhookReceiver;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** The ActionGroupResource model. */
@JsonFlatten
@Fluent
public class ActionGroupResourceInner extends Resource {
    /*
     * The short name of the action group. This will be used in SMS messages.
     */
    @JsonProperty(value = "properties.groupShortName")
    private String groupShortName;

    /*
     * Indicates whether this action group is enabled. If an action group is
     * not enabled, then none of its receivers will receive communications.
     */
    @JsonProperty(value = "properties.enabled")
    private Boolean enabled;

    /*
     * The list of email receivers that are part of this action group.
     */
    @JsonProperty(value = "properties.emailReceivers")
    private List<EmailReceiver> emailReceivers;

    /*
     * The list of SMS receivers that are part of this action group.
     */
    @JsonProperty(value = "properties.smsReceivers")
    private List<SmsReceiver> smsReceivers;

    /*
     * The list of webhook receivers that are part of this action group.
     */
    @JsonProperty(value = "properties.webhookReceivers")
    private List<WebhookReceiver> webhookReceivers;

    /*
     * The list of ITSM receivers that are part of this action group.
     */
    @JsonProperty(value = "properties.itsmReceivers")
    private List<ItsmReceiver> itsmReceivers;

    /*
     * The list of AzureAppPush receivers that are part of this action group.
     */
    @JsonProperty(value = "properties.azureAppPushReceivers")
    private List<AzureAppPushReceiver> azureAppPushReceivers;

    /*
     * The list of AutomationRunbook receivers that are part of this action
     * group.
     */
    @JsonProperty(value = "properties.automationRunbookReceivers")
    private List<AutomationRunbookReceiver> automationRunbookReceivers;

    /*
     * The list of voice receivers that are part of this action group.
     */
    @JsonProperty(value = "properties.voiceReceivers")
    private List<VoiceReceiver> voiceReceivers;

    /*
     * The list of logic app receivers that are part of this action group.
     */
    @JsonProperty(value = "properties.logicAppReceivers")
    private List<LogicAppReceiver> logicAppReceivers;

    /*
     * The list of azure function receivers that are part of this action group.
     */
    @JsonProperty(value = "properties.azureFunctionReceivers")
    private List<AzureFunctionReceiver> azureFunctionReceivers;

    /*
     * The list of ARM role receivers that are part of this action group. Roles
     * are Azure RBAC roles and only built-in roles are supported.
     */
    @JsonProperty(value = "properties.armRoleReceivers")
    private List<ArmRoleReceiver> armRoleReceivers;

    /**
     * Get the groupShortName property: The short name of the action group. This will be used in SMS messages.
     *
     * @return the groupShortName value.
     */
    public String groupShortName() {
        return this.groupShortName;
    }

    /**
     * Set the groupShortName property: The short name of the action group. This will be used in SMS messages.
     *
     * @param groupShortName the groupShortName value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withGroupShortName(String groupShortName) {
        this.groupShortName = groupShortName;
        return this;
    }

    /**
     * Get the enabled property: Indicates whether this action group is enabled. If an action group is not enabled, then
     * none of its receivers will receive communications.
     *
     * @return the enabled value.
     */
    public Boolean enabled() {
        return this.enabled;
    }

    /**
     * Set the enabled property: Indicates whether this action group is enabled. If an action group is not enabled, then
     * none of its receivers will receive communications.
     *
     * @param enabled the enabled value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Get the emailReceivers property: The list of email receivers that are part of this action group.
     *
     * @return the emailReceivers value.
     */
    public List<EmailReceiver> emailReceivers() {
        return this.emailReceivers;
    }

    /**
     * Set the emailReceivers property: The list of email receivers that are part of this action group.
     *
     * @param emailReceivers the emailReceivers value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withEmailReceivers(List<EmailReceiver> emailReceivers) {
        this.emailReceivers = emailReceivers;
        return this;
    }

    /**
     * Get the smsReceivers property: The list of SMS receivers that are part of this action group.
     *
     * @return the smsReceivers value.
     */
    public List<SmsReceiver> smsReceivers() {
        return this.smsReceivers;
    }

    /**
     * Set the smsReceivers property: The list of SMS receivers that are part of this action group.
     *
     * @param smsReceivers the smsReceivers value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withSmsReceivers(List<SmsReceiver> smsReceivers) {
        this.smsReceivers = smsReceivers;
        return this;
    }

    /**
     * Get the webhookReceivers property: The list of webhook receivers that are part of this action group.
     *
     * @return the webhookReceivers value.
     */
    public List<WebhookReceiver> webhookReceivers() {
        return this.webhookReceivers;
    }

    /**
     * Set the webhookReceivers property: The list of webhook receivers that are part of this action group.
     *
     * @param webhookReceivers the webhookReceivers value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withWebhookReceivers(List<WebhookReceiver> webhookReceivers) {
        this.webhookReceivers = webhookReceivers;
        return this;
    }

    /**
     * Get the itsmReceivers property: The list of ITSM receivers that are part of this action group.
     *
     * @return the itsmReceivers value.
     */
    public List<ItsmReceiver> itsmReceivers() {
        return this.itsmReceivers;
    }

    /**
     * Set the itsmReceivers property: The list of ITSM receivers that are part of this action group.
     *
     * @param itsmReceivers the itsmReceivers value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withItsmReceivers(List<ItsmReceiver> itsmReceivers) {
        this.itsmReceivers = itsmReceivers;
        return this;
    }

    /**
     * Get the azureAppPushReceivers property: The list of AzureAppPush receivers that are part of this action group.
     *
     * @return the azureAppPushReceivers value.
     */
    public List<AzureAppPushReceiver> azureAppPushReceivers() {
        return this.azureAppPushReceivers;
    }

    /**
     * Set the azureAppPushReceivers property: The list of AzureAppPush receivers that are part of this action group.
     *
     * @param azureAppPushReceivers the azureAppPushReceivers value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withAzureAppPushReceivers(List<AzureAppPushReceiver> azureAppPushReceivers) {
        this.azureAppPushReceivers = azureAppPushReceivers;
        return this;
    }

    /**
     * Get the automationRunbookReceivers property: The list of AutomationRunbook receivers that are part of this action
     * group.
     *
     * @return the automationRunbookReceivers value.
     */
    public List<AutomationRunbookReceiver> automationRunbookReceivers() {
        return this.automationRunbookReceivers;
    }

    /**
     * Set the automationRunbookReceivers property: The list of AutomationRunbook receivers that are part of this action
     * group.
     *
     * @param automationRunbookReceivers the automationRunbookReceivers value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withAutomationRunbookReceivers(
        List<AutomationRunbookReceiver> automationRunbookReceivers) {
        this.automationRunbookReceivers = automationRunbookReceivers;
        return this;
    }

    /**
     * Get the voiceReceivers property: The list of voice receivers that are part of this action group.
     *
     * @return the voiceReceivers value.
     */
    public List<VoiceReceiver> voiceReceivers() {
        return this.voiceReceivers;
    }

    /**
     * Set the voiceReceivers property: The list of voice receivers that are part of this action group.
     *
     * @param voiceReceivers the voiceReceivers value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withVoiceReceivers(List<VoiceReceiver> voiceReceivers) {
        this.voiceReceivers = voiceReceivers;
        return this;
    }

    /**
     * Get the logicAppReceivers property: The list of logic app receivers that are part of this action group.
     *
     * @return the logicAppReceivers value.
     */
    public List<LogicAppReceiver> logicAppReceivers() {
        return this.logicAppReceivers;
    }

    /**
     * Set the logicAppReceivers property: The list of logic app receivers that are part of this action group.
     *
     * @param logicAppReceivers the logicAppReceivers value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withLogicAppReceivers(List<LogicAppReceiver> logicAppReceivers) {
        this.logicAppReceivers = logicAppReceivers;
        return this;
    }

    /**
     * Get the azureFunctionReceivers property: The list of azure function receivers that are part of this action group.
     *
     * @return the azureFunctionReceivers value.
     */
    public List<AzureFunctionReceiver> azureFunctionReceivers() {
        return this.azureFunctionReceivers;
    }

    /**
     * Set the azureFunctionReceivers property: The list of azure function receivers that are part of this action group.
     *
     * @param azureFunctionReceivers the azureFunctionReceivers value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withAzureFunctionReceivers(List<AzureFunctionReceiver> azureFunctionReceivers) {
        this.azureFunctionReceivers = azureFunctionReceivers;
        return this;
    }

    /**
     * Get the armRoleReceivers property: The list of ARM role receivers that are part of this action group. Roles are
     * Azure RBAC roles and only built-in roles are supported.
     *
     * @return the armRoleReceivers value.
     */
    public List<ArmRoleReceiver> armRoleReceivers() {
        return this.armRoleReceivers;
    }

    /**
     * Set the armRoleReceivers property: The list of ARM role receivers that are part of this action group. Roles are
     * Azure RBAC roles and only built-in roles are supported.
     *
     * @param armRoleReceivers the armRoleReceivers value to set.
     * @return the ActionGroupResourceInner object itself.
     */
    public ActionGroupResourceInner withArmRoleReceivers(List<ArmRoleReceiver> armRoleReceivers) {
        this.armRoleReceivers = armRoleReceivers;
        return this;
    }
}
