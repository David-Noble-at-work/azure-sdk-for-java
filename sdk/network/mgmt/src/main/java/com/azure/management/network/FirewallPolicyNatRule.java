// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.network;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/** The FirewallPolicyNatRule model. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "ruleType")
@JsonTypeName("FirewallPolicyNatRule")
@Fluent
public final class FirewallPolicyNatRule extends FirewallPolicyRule {
    /*
     * The action type of a Nat rule, SNAT or DNAT
     */
    @JsonProperty(value = "action")
    private FirewallPolicyNatRuleAction action;

    /*
     * The translated address for this NAT rule.
     */
    @JsonProperty(value = "translatedAddress")
    private String translatedAddress;

    /*
     * The translated port for this NAT rule.
     */
    @JsonProperty(value = "translatedPort")
    private String translatedPort;

    /*
     * The match conditions for incoming traffic
     */
    @JsonProperty(value = "ruleCondition")
    private FirewallPolicyRuleCondition ruleCondition;

    /**
     * Get the action property: The action type of a Nat rule, SNAT or DNAT.
     *
     * @return the action value.
     */
    public FirewallPolicyNatRuleAction action() {
        return this.action;
    }

    /**
     * Set the action property: The action type of a Nat rule, SNAT or DNAT.
     *
     * @param action the action value to set.
     * @return the FirewallPolicyNatRule object itself.
     */
    public FirewallPolicyNatRule withAction(FirewallPolicyNatRuleAction action) {
        this.action = action;
        return this;
    }

    /**
     * Get the translatedAddress property: The translated address for this NAT rule.
     *
     * @return the translatedAddress value.
     */
    public String translatedAddress() {
        return this.translatedAddress;
    }

    /**
     * Set the translatedAddress property: The translated address for this NAT rule.
     *
     * @param translatedAddress the translatedAddress value to set.
     * @return the FirewallPolicyNatRule object itself.
     */
    public FirewallPolicyNatRule withTranslatedAddress(String translatedAddress) {
        this.translatedAddress = translatedAddress;
        return this;
    }

    /**
     * Get the translatedPort property: The translated port for this NAT rule.
     *
     * @return the translatedPort value.
     */
    public String translatedPort() {
        return this.translatedPort;
    }

    /**
     * Set the translatedPort property: The translated port for this NAT rule.
     *
     * @param translatedPort the translatedPort value to set.
     * @return the FirewallPolicyNatRule object itself.
     */
    public FirewallPolicyNatRule withTranslatedPort(String translatedPort) {
        this.translatedPort = translatedPort;
        return this;
    }

    /**
     * Get the ruleCondition property: The match conditions for incoming traffic.
     *
     * @return the ruleCondition value.
     */
    public FirewallPolicyRuleCondition ruleCondition() {
        return this.ruleCondition;
    }

    /**
     * Set the ruleCondition property: The match conditions for incoming traffic.
     *
     * @param ruleCondition the ruleCondition value to set.
     * @return the FirewallPolicyNatRule object itself.
     */
    public FirewallPolicyNatRule withRuleCondition(FirewallPolicyRuleCondition ruleCondition) {
        this.ruleCondition = ruleCondition;
        return this;
    }
}
