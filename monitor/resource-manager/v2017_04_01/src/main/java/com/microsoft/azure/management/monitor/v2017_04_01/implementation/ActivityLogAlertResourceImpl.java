/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.monitor.v2017_04_01.implementation;

import com.microsoft.azure.arm.resources.models.implementation.GroupableResourceCoreImpl;
import com.microsoft.azure.management.monitor.v2017_04_01.ActivityLogAlertResource;
import rx.Observable;
import com.microsoft.azure.management.monitor.v2017_04_01.ActivityLogAlertPatchBody;
import java.util.List;
import com.microsoft.azure.management.monitor.v2017_04_01.ActivityLogAlertAllOfCondition;
import com.microsoft.azure.management.monitor.v2017_04_01.ActivityLogAlertActionList;
import rx.functions.Func1;

class ActivityLogAlertResourceImpl extends GroupableResourceCoreImpl<ActivityLogAlertResource, ActivityLogAlertResourceInner, ActivityLogAlertResourceImpl, MonitorManager> implements ActivityLogAlertResource, ActivityLogAlertResource.Definition, ActivityLogAlertResource.Update {
    private ActivityLogAlertPatchBody updateParameter;
    ActivityLogAlertResourceImpl(String name, ActivityLogAlertResourceInner inner, MonitorManager manager) {
        super(name, inner, manager);
        this.updateParameter = new ActivityLogAlertPatchBody();
    }

    @Override
    public Observable<ActivityLogAlertResource> createResourceAsync() {
        ActivityLogAlertsInner client = this.manager().inner().activityLogAlerts();
        return client.createOrUpdateAsync(this.resourceGroupName(), this.name(), this.inner())
            .map(new Func1<ActivityLogAlertResourceInner, ActivityLogAlertResourceInner>() {
               @Override
               public ActivityLogAlertResourceInner call(ActivityLogAlertResourceInner resource) {
                   resetCreateUpdateParameters();
                   return resource;
               }
            })
            .map(innerToFluentMap(this));
    }

    @Override
    public Observable<ActivityLogAlertResource> updateResourceAsync() {
        ActivityLogAlertsInner client = this.manager().inner().activityLogAlerts();
        return client.updateAsync(this.resourceGroupName(), this.name(), this.updateParameter)
            .map(new Func1<ActivityLogAlertResourceInner, ActivityLogAlertResourceInner>() {
               @Override
               public ActivityLogAlertResourceInner call(ActivityLogAlertResourceInner resource) {
                   resetCreateUpdateParameters();
                   return resource;
               }
            })
            .map(innerToFluentMap(this));
    }

    @Override
    protected Observable<ActivityLogAlertResourceInner> getInnerAsync() {
        ActivityLogAlertsInner client = this.manager().inner().activityLogAlerts();
        return client.getByResourceGroupAsync(this.resourceGroupName(), this.name());
    }

    @Override
    public boolean isInCreateMode() {
        return this.inner().id() == null;
    }

    private void resetCreateUpdateParameters() {
        this.updateParameter = new ActivityLogAlertPatchBody();
    }

    @Override
    public ActivityLogAlertActionList actions() {
        return this.inner().actions();
    }

    @Override
    public ActivityLogAlertAllOfCondition condition() {
        return this.inner().condition();
    }

    @Override
    public String description() {
        return this.inner().description();
    }

    @Override
    public Boolean enabled() {
        return this.inner().enabled();
    }

    @Override
    public List<String> scopes() {
        return this.inner().scopes();
    }

    @Override
    public ActivityLogAlertResourceImpl withActions(ActivityLogAlertActionList actions) {
        this.inner().withActions(actions);
        return this;
    }

    @Override
    public ActivityLogAlertResourceImpl withCondition(ActivityLogAlertAllOfCondition condition) {
        this.inner().withCondition(condition);
        return this;
    }

    @Override
    public ActivityLogAlertResourceImpl withScopes(List<String> scopes) {
        this.inner().withScopes(scopes);
        return this;
    }

    @Override
    public ActivityLogAlertResourceImpl withDescription(String description) {
        this.inner().withDescription(description);
        return this;
    }

    @Override
    public ActivityLogAlertResourceImpl withEnabled(Boolean enabled) {
        if (isInCreateMode()) {
            this.inner().withEnabled(enabled);
        } else {
            this.updateParameter.withEnabled(enabled);
        }
        return this;
    }

}
