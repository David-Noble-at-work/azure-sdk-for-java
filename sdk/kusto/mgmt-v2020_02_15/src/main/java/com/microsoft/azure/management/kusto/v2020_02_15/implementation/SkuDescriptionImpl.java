/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.kusto.v2020_02_15.implementation;

import com.microsoft.azure.management.kusto.v2020_02_15.SkuDescription;
import com.microsoft.azure.arm.model.implementation.WrapperImpl;
import java.util.List;
import com.microsoft.azure.management.kusto.v2020_02_15.SkuLocationInfoItem;

class SkuDescriptionImpl extends WrapperImpl<SkuDescriptionInner> implements SkuDescription {
    private final KustoManager manager;
    SkuDescriptionImpl(SkuDescriptionInner inner, KustoManager manager) {
        super(inner);
        this.manager = manager;
    }

    @Override
    public KustoManager manager() {
        return this.manager;
    }

    @Override
    public List<SkuLocationInfoItem> locationInfo() {
        return this.inner().locationInfo();
    }

    @Override
    public List<String> locations() {
        return this.inner().locations();
    }

    @Override
    public String name() {
        return this.inner().name();
    }

    @Override
    public String resourceType() {
        return this.inner().resourceType();
    }

    @Override
    public List<Object> restrictions() {
        return this.inner().restrictions();
    }

    @Override
    public String tier() {
        return this.inner().tier();
    }

}
