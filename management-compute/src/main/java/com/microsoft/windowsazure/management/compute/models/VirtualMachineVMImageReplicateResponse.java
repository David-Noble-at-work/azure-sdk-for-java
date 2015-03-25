/**
 * 
 * Copyright (c) Microsoft and contributors.  All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

// Warning: This code was generated by a tool.
// 
// Changes to this file may cause incorrect behavior and will be lost if the
// code is regenerated.

package com.microsoft.windowsazure.management.compute.models;

import com.microsoft.windowsazure.core.AzureOperationResponse;

/**
* The response body contains the published name of the image.
*/
public class VirtualMachineVMImageReplicateResponse extends AzureOperationResponse {
    private String publishedName;
    
    /**
    * Optional. The published name of the image.
    * @return The PublishedName value.
    */
    public String getPublishedName() {
        return this.publishedName;
    }
    
    /**
    * Optional. The published name of the image.
    * @param publishedNameValue The PublishedName value.
    */
    public void setPublishedName(final String publishedNameValue) {
        this.publishedName = publishedNameValue;
    }
}
