/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 *
 */

package com.microsoft.azure.management.loganalytics.v2015_03_20.implementation;

import com.microsoft.azure.arm.model.implementation.WrapperImpl;
import com.microsoft.azure.management.loganalytics.v2015_03_20.StorageInsights;
import rx.Completable;
import rx.Observable;
import rx.functions.Func1;
import com.microsoft.azure.Page;
import com.microsoft.azure.management.loganalytics.v2015_03_20.StorageInsight;

class StorageInsightsImpl extends WrapperImpl<StorageInsightsInner> implements StorageInsights {
    private final LogAnalyticsManager manager;

    StorageInsightsImpl(LogAnalyticsManager manager) {
        super(manager.inner().storageInsights());
        this.manager = manager;
    }

    public LogAnalyticsManager manager() {
        return this.manager;
    }

    @Override
    public StorageInsightImpl define(String name) {
        return wrapModel(name);
    }

    private StorageInsightImpl wrapModel(StorageInsightInner inner) {
        return  new StorageInsightImpl(inner, manager());
    }

    private StorageInsightImpl wrapModel(String name) {
        return new StorageInsightImpl(name, this.manager());
    }

    @Override
    public Observable<StorageInsight> listByWorkspaceAsync(final String resourceGroupName, final String workspaceName) {
        StorageInsightsInner client = this.inner();
        return client.listByWorkspaceAsync(resourceGroupName, workspaceName)
        .flatMapIterable(new Func1<Page<StorageInsightInner>, Iterable<StorageInsightInner>>() {
            @Override
            public Iterable<StorageInsightInner> call(Page<StorageInsightInner> page) {
                return page.items();
            }
        })
        .map(new Func1<StorageInsightInner, StorageInsight>() {
            @Override
            public StorageInsight call(StorageInsightInner inner) {
                return wrapModel(inner);
            }
        });
    }

    @Override
    public Observable<StorageInsight> getAsync(String resourceGroupName, String workspaceName, String storageInsightName) {
        StorageInsightsInner client = this.inner();
        return client.getAsync(resourceGroupName, workspaceName, storageInsightName)
        .map(new Func1<StorageInsightInner, StorageInsight>() {
            @Override
            public StorageInsight call(StorageInsightInner inner) {
                return wrapModel(inner);
            }
       });
    }

    @Override
    public Completable deleteAsync(String resourceGroupName, String workspaceName, String storageInsightName) {
        StorageInsightsInner client = this.inner();
        return client.deleteAsync(resourceGroupName, workspaceName, storageInsightName).toCompletable();
    }

}
