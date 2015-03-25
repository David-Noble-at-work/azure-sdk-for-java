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

package com.microsoft.azure.management.resources.models;

import com.microsoft.windowsazure.core.LazyArrayList;
import com.microsoft.windowsazure.core.LazyHashMap;
import java.util.ArrayList;
import java.util.HashMap;

/**
* Resource type managed by the resource provider.
*/
public class ProviderResourceType {
    private ArrayList<String> apiVersions;
    
    /**
    * Optional. Gets or sets the api version.
    * @return The ApiVersions value.
    */
    public ArrayList<String> getApiVersions() {
        return this.apiVersions;
    }
    
    /**
    * Optional. Gets or sets the api version.
    * @param apiVersionsValue The ApiVersions value.
    */
    public void setApiVersions(final ArrayList<String> apiVersionsValue) {
        this.apiVersions = apiVersionsValue;
    }
    
    private ArrayList<String> locations;
    
    /**
    * Optional. Gets or sets the collection of locations where this resource
    * type can be created in.
    * @return The Locations value.
    */
    public ArrayList<String> getLocations() {
        return this.locations;
    }
    
    /**
    * Optional. Gets or sets the collection of locations where this resource
    * type can be created in.
    * @param locationsValue The Locations value.
    */
    public void setLocations(final ArrayList<String> locationsValue) {
        this.locations = locationsValue;
    }
    
    private String name;
    
    /**
    * Optional. Gets or sets the resource type.
    * @return The Name value.
    */
    public String getName() {
        return this.name;
    }
    
    /**
    * Optional. Gets or sets the resource type.
    * @param nameValue The Name value.
    */
    public void setName(final String nameValue) {
        this.name = nameValue;
    }
    
    private HashMap<String, String> properties;
    
    /**
    * Optional. Gets or sets the properties.
    * @return The Properties value.
    */
    public HashMap<String, String> getProperties() {
        return this.properties;
    }
    
    /**
    * Optional. Gets or sets the properties.
    * @param propertiesValue The Properties value.
    */
    public void setProperties(final HashMap<String, String> propertiesValue) {
        this.properties = propertiesValue;
    }
    
    /**
    * Initializes a new instance of the ProviderResourceType class.
    *
    */
    public ProviderResourceType() {
        this.setApiVersions(new LazyArrayList<String>());
        this.setLocations(new LazyArrayList<String>());
        this.setProperties(new LazyHashMap<String, String>());
    }
}
