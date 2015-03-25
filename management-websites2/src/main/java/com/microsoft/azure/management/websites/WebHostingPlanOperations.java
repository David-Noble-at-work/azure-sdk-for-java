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

package com.microsoft.azure.management.websites;

import com.microsoft.azure.management.websites.models.WebHostingPlanCreateOrUpdateParameters;
import com.microsoft.azure.management.websites.models.WebHostingPlanCreateOrUpdateResponse;
import com.microsoft.azure.management.websites.models.WebHostingPlanGetHistoricalUsageMetricsParameters;
import com.microsoft.azure.management.websites.models.WebHostingPlanGetHistoricalUsageMetricsResponse;
import com.microsoft.azure.management.websites.models.WebHostingPlanGetResponse;
import com.microsoft.azure.management.websites.models.WebHostingPlanListResponse;
import com.microsoft.windowsazure.core.AzureOperationResponse;
import com.microsoft.windowsazure.exception.ServiceException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Future;

/**
* Operations for managing the Web Hosting Plans in a resource group. Web
* hosting plans (WHPs) represent a set of features and capacity that you can
* share across your web sites. Web hosting plans support the 4 Azure Web Sites
* pricing tiers (Free, Shared, Basic, and Standard) where each tier has its
* own capabilities and capacity. Sites in the same subscription, resource
* group, and geographic location can share a web hosting plan. All the sites
* sharing a web hosting plan can leverage all the capabilities and features
* defined by the web hosting plan tier. All web sites associated with a given
* web hosting plan run on the resources defined by the web hosting plan.  (see
* http://azure.microsoft.com/en-us/documentation/articles/azure-web-sites-web-hosting-plans-in-depth-overview/
* for more information)
*/
public interface WebHostingPlanOperations {
    /**
    * Creates a new Web Hosting Plan or updates an existing one.  (see
    * http://azure.microsoft.com/en-us/documentation/articles/azure-web-sites-web-hosting-plans-in-depth-overview/
    * for more information)
    *
    * @param resourceGroupName Required. The name of the resource group.
    * @param parameters Required. Parameters supplied to the Create Server Farm
    * operation.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @throws URISyntaxException Thrown if there was an error parsing a URI in
    * the response.
    * @return The Create Web Hosting Plan operation response.
    */
    WebHostingPlanCreateOrUpdateResponse createOrUpdate(String resourceGroupName, WebHostingPlanCreateOrUpdateParameters parameters) throws IOException, ServiceException, URISyntaxException;
    
    /**
    * Creates a new Web Hosting Plan or updates an existing one.  (see
    * http://azure.microsoft.com/en-us/documentation/articles/azure-web-sites-web-hosting-plans-in-depth-overview/
    * for more information)
    *
    * @param resourceGroupName Required. The name of the resource group.
    * @param parameters Required. Parameters supplied to the Create Server Farm
    * operation.
    * @return The Create Web Hosting Plan operation response.
    */
    Future<WebHostingPlanCreateOrUpdateResponse> createOrUpdateAsync(String resourceGroupName, WebHostingPlanCreateOrUpdateParameters parameters);
    
    /**
    * Deletes a Web Hosting Plan  (see
    * http://azure.microsoft.com/en-us/documentation/articles/azure-web-sites-web-hosting-plans-in-depth-overview/
    * for more information)
    *
    * @param resourceGroupName Required. The name of the resource group.
    * @param webHostingPlanName Required. The name of the Web Hosting Plan to
    * delete.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @return A standard service response including an HTTP status code and
    * request ID.
    */
    AzureOperationResponse delete(String resourceGroupName, String webHostingPlanName) throws IOException, ServiceException;
    
    /**
    * Deletes a Web Hosting Plan  (see
    * http://azure.microsoft.com/en-us/documentation/articles/azure-web-sites-web-hosting-plans-in-depth-overview/
    * for more information)
    *
    * @param resourceGroupName Required. The name of the resource group.
    * @param webHostingPlanName Required. The name of the Web Hosting Plan to
    * delete.
    * @return A standard service response including an HTTP status code and
    * request ID.
    */
    Future<AzureOperationResponse> deleteAsync(String resourceGroupName, String webHostingPlanName);
    
    /**
    * Gets details of an existing Web Hosting Plan  (see
    * http://azure.microsoft.com/en-us/documentation/articles/azure-web-sites-web-hosting-plans-in-depth-overview/
    * for more information)
    *
    * @param resourceGroupName Required. The name of the resource group.
    * @param webHostingPlanName Required. The name of the Web Hosting Plan.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @throws URISyntaxException Thrown if there was an error parsing a URI in
    * the response.
    * @return The Get Web Hosting Plan operation response.
    */
    WebHostingPlanGetResponse get(String resourceGroupName, String webHostingPlanName) throws IOException, ServiceException, URISyntaxException;
    
    /**
    * Gets details of an existing Web Hosting Plan  (see
    * http://azure.microsoft.com/en-us/documentation/articles/azure-web-sites-web-hosting-plans-in-depth-overview/
    * for more information)
    *
    * @param resourceGroupName Required. The name of the resource group.
    * @param webHostingPlanName Required. The name of the Web Hosting Plan.
    * @return The Get Web Hosting Plan operation response.
    */
    Future<WebHostingPlanGetResponse> getAsync(String resourceGroupName, String webHostingPlanName);
    
    /**
    * You can retrieve historical usage metrics for a site by issuing an HTTP
    * GET request.  (see
    * http://msdn.microsoft.com/en-us/library/windowsazure/dn166964.aspx for
    * more information)
    *
    * @param resourceGroupName Required. The name of the resource group.
    * @param webHostingPlanName Required. The name of the web hosting plan.
    * @param parameters Required. Parameters supplied to the Get Historical
    * Usage Metrics Web hosting plan operation.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @return The Get Historical Usage Metrics Web hosting plan operation
    * response.
    */
    WebHostingPlanGetHistoricalUsageMetricsResponse getHistoricalUsageMetrics(String resourceGroupName, String webHostingPlanName, WebHostingPlanGetHistoricalUsageMetricsParameters parameters) throws IOException, ServiceException;
    
    /**
    * You can retrieve historical usage metrics for a site by issuing an HTTP
    * GET request.  (see
    * http://msdn.microsoft.com/en-us/library/windowsazure/dn166964.aspx for
    * more information)
    *
    * @param resourceGroupName Required. The name of the resource group.
    * @param webHostingPlanName Required. The name of the web hosting plan.
    * @param parameters Required. Parameters supplied to the Get Historical
    * Usage Metrics Web hosting plan operation.
    * @return The Get Historical Usage Metrics Web hosting plan operation
    * response.
    */
    Future<WebHostingPlanGetHistoricalUsageMetricsResponse> getHistoricalUsageMetricsAsync(String resourceGroupName, String webHostingPlanName, WebHostingPlanGetHistoricalUsageMetricsParameters parameters);
    
    /**
    * Gets all Web Hosting Plans in a current subscription and Resource Group.
    * (see http://msdn.microsoft.com/en-us/library/windowsazure/dn194277.aspx
    * for more information)
    *
    * @param resourceGroupName Required. The name of the resource group.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @throws URISyntaxException Thrown if there was an error parsing a URI in
    * the response.
    * @return The List Web Hosting Plans operation response.
    */
    WebHostingPlanListResponse list(String resourceGroupName) throws IOException, ServiceException, URISyntaxException;
    
    /**
    * Gets all Web Hosting Plans in a current subscription and Resource Group.
    * (see http://msdn.microsoft.com/en-us/library/windowsazure/dn194277.aspx
    * for more information)
    *
    * @param resourceGroupName Required. The name of the resource group.
    * @return The List Web Hosting Plans operation response.
    */
    Future<WebHostingPlanListResponse> listAsync(String resourceGroupName);
}
