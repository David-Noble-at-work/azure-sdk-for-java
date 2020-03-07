/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.compute.v2019_11_01.implementation;

import com.microsoft.azure.arm.resources.models.implementation.GroupableResourceCoreImpl;
import com.microsoft.azure.management.compute.v2019_11_01.Image;
import rx.Observable;
import com.microsoft.azure.management.compute.v2019_11_01.ImageUpdate;
import com.microsoft.azure.SubResource;
import com.microsoft.azure.management.compute.v2019_11_01.ImageStorageProfile;
import com.microsoft.azure.management.compute.v2019_11_01.HyperVGenerationTypes;
import rx.functions.Func1;

class ImageImpl extends GroupableResourceCoreImpl<Image, ImageInner, ImageImpl, ComputeManager> implements Image, Image.Definition, Image.Update {
    private ImageUpdate updateParameter;
    ImageImpl(String name, ImageInner inner, ComputeManager manager) {
        super(name, inner, manager);
        this.updateParameter = new ImageUpdate();
    }

    @Override
    public Observable<Image> createResourceAsync() {
        ImagesInner client = this.manager().inner().images();
        return client.createOrUpdateAsync(this.resourceGroupName(), this.name(), this.inner())
            .map(new Func1<ImageInner, ImageInner>() {
               @Override
               public ImageInner call(ImageInner resource) {
                   resetCreateUpdateParameters();
                   return resource;
               }
            })
            .map(innerToFluentMap(this));
    }

    @Override
    public Observable<Image> updateResourceAsync() {
        ImagesInner client = this.manager().inner().images();
        return client.updateAsync(this.resourceGroupName(), this.name(), this.updateParameter)
            .map(new Func1<ImageInner, ImageInner>() {
               @Override
               public ImageInner call(ImageInner resource) {
                   resetCreateUpdateParameters();
                   return resource;
               }
            })
            .map(innerToFluentMap(this));
    }

    @Override
    protected Observable<ImageInner> getInnerAsync() {
        ImagesInner client = this.manager().inner().images();
        return client.getByResourceGroupAsync(this.resourceGroupName(), this.name());
    }

    @Override
    public boolean isInCreateMode() {
        return this.inner().id() == null;
    }

    private void resetCreateUpdateParameters() {
        this.updateParameter = new ImageUpdate();
    }

    @Override
    public HyperVGenerationTypes hyperVGeneration() {
        return this.inner().hyperVGeneration();
    }

    @Override
    public String provisioningState() {
        return this.inner().provisioningState();
    }

    @Override
    public SubResource sourceVirtualMachine() {
        return this.inner().sourceVirtualMachine();
    }

    @Override
    public ImageStorageProfile storageProfile() {
        return this.inner().storageProfile();
    }

    @Override
    public ImageImpl withHyperVGeneration(HyperVGenerationTypes hyperVGeneration) {
        if (isInCreateMode()) {
            this.inner().withHyperVGeneration(hyperVGeneration);
        } else {
            this.updateParameter.withHyperVGeneration(hyperVGeneration);
        }
        return this;
    }

    @Override
    public ImageImpl withSourceVirtualMachine(SubResource sourceVirtualMachine) {
        if (isInCreateMode()) {
            this.inner().withSourceVirtualMachine(sourceVirtualMachine);
        } else {
            this.updateParameter.withSourceVirtualMachine(sourceVirtualMachine);
        }
        return this;
    }

    @Override
    public ImageImpl withStorageProfile(ImageStorageProfile storageProfile) {
        if (isInCreateMode()) {
            this.inner().withStorageProfile(storageProfile);
        } else {
            this.updateParameter.withStorageProfile(storageProfile);
        }
        return this;
    }

}
