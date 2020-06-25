/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.containerinstance.v2019_12_01;

import com.microsoft.azure.arm.model.HasInner;
import com.microsoft.azure.arm.resources.models.HasManager;
import com.microsoft.azure.management.containerinstance.v2019_12_01.implementation.ContainerInstanceManager;
import com.microsoft.azure.management.containerinstance.v2019_12_01.implementation.ContainerExecResponseInner;

/**
 * Type representing ContainerExecResponse.
 */
public interface ContainerExecResponse extends HasInner<ContainerExecResponseInner>, HasManager<ContainerInstanceManager> {
    /**
     * @return the password value.
     */
    String password();

    /**
     * @return the webSocketUri value.
     */
    String webSocketUri();

}
