/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.eventgrid.v2020_04_01_preview.implementation;

import com.microsoft.azure.arm.resources.models.implementation.GroupableResourceCoreImpl;
import com.microsoft.azure.management.eventgrid.v2020_04_01_preview.PartnerTopic;
import rx.Observable;
import java.util.Map;
import com.microsoft.azure.management.eventgrid.v2020_04_01_preview.PartnerTopicActivationState;
import com.microsoft.azure.management.eventgrid.v2020_04_01_preview.PartnerTopicProvisioningState;

class PartnerTopicImpl extends GroupableResourceCoreImpl<PartnerTopic, PartnerTopicInner, PartnerTopicImpl, EventGridManager> implements PartnerTopic, PartnerTopic.Update {
    PartnerTopicImpl(String name, PartnerTopicInner inner, EventGridManager manager) {
        super(name, inner, manager);
    }

    @Override
    public Observable<PartnerTopic> createResourceAsync() {
        PartnerTopicsInner client = this.manager().inner().partnerTopics();
        return null; // NOP createResourceAsync implementation as create is not supported
    }

    @Override
    public Observable<PartnerTopic> updateResourceAsync() {
        PartnerTopicsInner client = this.manager().inner().partnerTopics();
        return client.updateAsync(this.resourceGroupName(), this.name(), this.inner().getTags())
            .map(innerToFluentMap(this));
    }

    @Override
    protected Observable<PartnerTopicInner> getInnerAsync() {
        PartnerTopicsInner client = this.manager().inner().partnerTopics();
        return client.getByResourceGroupAsync(this.resourceGroupName(), this.name());
    }

    @Override
    public boolean isInCreateMode() {
        return this.inner().id() == null;
    }


    @Override
    public PartnerTopicActivationState activationState() {
        return this.inner().activationState();
    }

    @Override
    public PartnerTopicProvisioningState provisioningState() {
        return this.inner().provisioningState();
    }

    @Override
    public String source() {
        return this.inner().source();
    }
}
