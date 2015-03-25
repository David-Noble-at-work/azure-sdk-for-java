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

package com.microsoft.windowsazure.management.network;

import com.microsoft.windowsazure.core.OperationStatusResponse;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.management.network.models.NetworkSecurityGroupAddToSubnetParameters;
import com.microsoft.windowsazure.management.network.models.NetworkSecurityGroupCreateParameters;
import com.microsoft.windowsazure.management.network.models.NetworkSecurityGroupGetForSubnetResponse;
import com.microsoft.windowsazure.management.network.models.NetworkSecurityGroupGetResponse;
import com.microsoft.windowsazure.management.network.models.NetworkSecurityGroupListResponse;
import com.microsoft.windowsazure.management.network.models.NetworkSecuritySetRuleParameters;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
* The Network Management API includes operations for managing the Network
* Security Groups for your subscription.
*/
public interface NetworkSecurityGroupOperations {
    /**
    * Adds a Network Security Group to a subnet.
    *
    * @param virtualNetworkName Required.
    * @param subnetName Required.
    * @param parameters Required. Parameters supplied to the Add Network
    * Security Group to subnet operation.
    * @throws InterruptedException Thrown when a thread is waiting, sleeping,
    * or otherwise occupied, and the thread is interrupted, either before or
    * during the activity. Occasionally a method may wish to test whether the
    * current thread has been interrupted, and if so, to immediately throw
    * this exception. The following code can be used to achieve this effect:
    * @throws ExecutionException Thrown when attempting to retrieve the result
    * of a task that aborted by throwing an exception. This exception can be
    * inspected using the Throwable.getCause() method.
    * @throws ServiceException Thrown if the server returned an error for the
    * request.
    * @throws IOException Thrown if there was an error setting up tracing for
    * the request.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse addToSubnet(String virtualNetworkName, String subnetName, NetworkSecurityGroupAddToSubnetParameters parameters) throws InterruptedException, ExecutionException, ServiceException, IOException;
    
    /**
    * Adds a Network Security Group to a subnet.
    *
    * @param virtualNetworkName Required.
    * @param subnetName Required.
    * @param parameters Required. Parameters supplied to the Add Network
    * Security Group to subnet operation.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> addToSubnetAsync(String virtualNetworkName, String subnetName, NetworkSecurityGroupAddToSubnetParameters parameters);
    
    /**
    * Adds a Network Security Group to a subnet.
    *
    * @param virtualNetworkName Required.
    * @param subnetName Required.
    * @param parameters Required. Parameters supplied to the Add Network
    * Security Group to subnet operation.
    * @throws ParserConfigurationException Thrown if there was an error
    * configuring the parser for the response body.
    * @throws SAXException Thrown if there was an error parsing the response
    * body.
    * @throws TransformerException Thrown if there was an error creating the
    * DOM transformer.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse beginAddingToSubnet(String virtualNetworkName, String subnetName, NetworkSecurityGroupAddToSubnetParameters parameters) throws ParserConfigurationException, SAXException, TransformerException, IOException, ServiceException;
    
    /**
    * Adds a Network Security Group to a subnet.
    *
    * @param virtualNetworkName Required.
    * @param subnetName Required.
    * @param parameters Required. Parameters supplied to the Add Network
    * Security Group to subnet operation.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> beginAddingToSubnetAsync(String virtualNetworkName, String subnetName, NetworkSecurityGroupAddToSubnetParameters parameters);
    
    /**
    * Creates a new Network Security Group.
    *
    * @param parameters Required. Parameters supplied to the Create Network
    * Security Group operation.
    * @throws ParserConfigurationException Thrown if there was an error
    * configuring the parser for the response body.
    * @throws SAXException Thrown if there was an error parsing the response
    * body.
    * @throws TransformerException Thrown if there was an error creating the
    * DOM transformer.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse beginCreating(NetworkSecurityGroupCreateParameters parameters) throws ParserConfigurationException, SAXException, TransformerException, IOException, ServiceException;
    
    /**
    * Creates a new Network Security Group.
    *
    * @param parameters Required. Parameters supplied to the Create Network
    * Security Group operation.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> beginCreatingAsync(NetworkSecurityGroupCreateParameters parameters);
    
    /**
    * Deletes the pecified Network Security Group from your subscription.If the
    * Network Security group is still associated with some VM/Role/Subnet, the
    * deletion will fail. In order to successfully delete the Network
    * Security, it needs to be not used.
    *
    * @param networkSecurityGroupName Required. The name of the Network
    * Security Group to delete.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @throws ParserConfigurationException Thrown if there was a serious
    * configuration error with the document parser.
    * @throws SAXException Thrown if there was an error parsing the XML
    * response.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse beginDeleting(String networkSecurityGroupName) throws IOException, ServiceException, ParserConfigurationException, SAXException;
    
    /**
    * Deletes the pecified Network Security Group from your subscription.If the
    * Network Security group is still associated with some VM/Role/Subnet, the
    * deletion will fail. In order to successfully delete the Network
    * Security, it needs to be not used.
    *
    * @param networkSecurityGroupName Required. The name of the Network
    * Security Group to delete.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> beginDeletingAsync(String networkSecurityGroupName);
    
    /**
    * Deletes a rule from the specified Network Security Group.
    *
    * @param networkSecurityGroupName Required. The name of the Network
    * Security Group.
    * @param ruleName Required. The name of the rule to delete.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse beginDeletingRule(String networkSecurityGroupName, String ruleName) throws IOException, ServiceException;
    
    /**
    * Deletes a rule from the specified Network Security Group.
    *
    * @param networkSecurityGroupName Required. The name of the Network
    * Security Group.
    * @param ruleName Required. The name of the rule to delete.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> beginDeletingRuleAsync(String networkSecurityGroupName, String ruleName);
    
    /**
    * Removes a Network Security Group from a subnet.
    *
    * @param virtualNetworkName Required.
    * @param subnetName Required.
    * @param networkSecurityGroupName Required.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse beginRemovingFromSubnet(String virtualNetworkName, String subnetName, String networkSecurityGroupName) throws IOException, ServiceException;
    
    /**
    * Removes a Network Security Group from a subnet.
    *
    * @param virtualNetworkName Required.
    * @param subnetName Required.
    * @param networkSecurityGroupName Required.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> beginRemovingFromSubnetAsync(String virtualNetworkName, String subnetName, String networkSecurityGroupName);
    
    /**
    * Sets a new Network Security Rule to existing Network Security Group.
    *
    * @param networkSecurityGroupName Optional.
    * @param ruleName Optional.
    * @param parameters Required. Parameters supplied to the Set Network
    * Security Rule operation.
    * @throws ParserConfigurationException Thrown if there was an error
    * configuring the parser for the response body.
    * @throws SAXException Thrown if there was an error parsing the response
    * body.
    * @throws TransformerException Thrown if there was an error creating the
    * DOM transformer.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse beginSettingRule(String networkSecurityGroupName, String ruleName, NetworkSecuritySetRuleParameters parameters) throws ParserConfigurationException, SAXException, TransformerException, IOException, ServiceException;
    
    /**
    * Sets a new Network Security Rule to existing Network Security Group.
    *
    * @param networkSecurityGroupName Optional.
    * @param ruleName Optional.
    * @param parameters Required. Parameters supplied to the Set Network
    * Security Rule operation.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> beginSettingRuleAsync(String networkSecurityGroupName, String ruleName, NetworkSecuritySetRuleParameters parameters);
    
    /**
    * Creates a new Network Security Group.
    *
    * @param parameters Required. Parameters supplied to the Create Network
    * Security Group operation.
    * @throws InterruptedException Thrown when a thread is waiting, sleeping,
    * or otherwise occupied, and the thread is interrupted, either before or
    * during the activity. Occasionally a method may wish to test whether the
    * current thread has been interrupted, and if so, to immediately throw
    * this exception. The following code can be used to achieve this effect:
    * @throws ExecutionException Thrown when attempting to retrieve the result
    * of a task that aborted by throwing an exception. This exception can be
    * inspected using the Throwable.getCause() method.
    * @throws ServiceException Thrown if the server returned an error for the
    * request.
    * @throws IOException Thrown if there was an error setting up tracing for
    * the request.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @throws ParserConfigurationException Thrown if there was a serious
    * configuration error with the document parser.
    * @throws SAXException Thrown if there was an error parsing the XML
    * response.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse create(NetworkSecurityGroupCreateParameters parameters) throws InterruptedException, ExecutionException, ServiceException, IOException, ParserConfigurationException, SAXException;
    
    /**
    * Creates a new Network Security Group.
    *
    * @param parameters Required. Parameters supplied to the Create Network
    * Security Group operation.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> createAsync(NetworkSecurityGroupCreateParameters parameters);
    
    /**
    * The Delete Network Security Group operation removes thespecified Network
    * Security Group from your subscription.If the Network Security group is
    * still associated with some VM/Role/Subnet, the deletion will fail. In
    * order to successfully delete the Network Security, it needs to be not
    * used.
    *
    * @param networkSecurityGroupName Required. The name of the Network
    * Security Group to delete.
    * @throws InterruptedException Thrown when a thread is waiting, sleeping,
    * or otherwise occupied, and the thread is interrupted, either before or
    * during the activity. Occasionally a method may wish to test whether the
    * current thread has been interrupted, and if so, to immediately throw
    * this exception. The following code can be used to achieve this effect:
    * @throws ExecutionException Thrown when attempting to retrieve the result
    * of a task that aborted by throwing an exception. This exception can be
    * inspected using the Throwable.getCause() method.
    * @throws ServiceException Thrown if the server returned an error for the
    * request.
    * @throws IOException Thrown if there was an error setting up tracing for
    * the request.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @throws ParserConfigurationException Thrown if there was a serious
    * configuration error with the document parser.
    * @throws SAXException Thrown if there was an error parsing the XML
    * response.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse delete(String networkSecurityGroupName) throws InterruptedException, ExecutionException, ServiceException, IOException, ParserConfigurationException, SAXException;
    
    /**
    * The Delete Network Security Group operation removes thespecified Network
    * Security Group from your subscription.If the Network Security group is
    * still associated with some VM/Role/Subnet, the deletion will fail. In
    * order to successfully delete the Network Security, it needs to be not
    * used.
    *
    * @param networkSecurityGroupName Required. The name of the Network
    * Security Group to delete.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> deleteAsync(String networkSecurityGroupName);
    
    /**
    * The Delete Network Security Rule operation removes a rule from the
    * specified Network Security Group.
    *
    * @param networkSecurityGroupName Required. The name of the Network
    * Security Group.
    * @param ruleName Required. The name of the rule to delete.
    * @throws InterruptedException Thrown when a thread is waiting, sleeping,
    * or otherwise occupied, and the thread is interrupted, either before or
    * during the activity. Occasionally a method may wish to test whether the
    * current thread has been interrupted, and if so, to immediately throw
    * this exception. The following code can be used to achieve this effect:
    * @throws ExecutionException Thrown when attempting to retrieve the result
    * of a task that aborted by throwing an exception. This exception can be
    * inspected using the Throwable.getCause() method.
    * @throws ServiceException Thrown if the server returned an error for the
    * request.
    * @throws IOException Thrown if there was an error setting up tracing for
    * the request.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse deleteRule(String networkSecurityGroupName, String ruleName) throws InterruptedException, ExecutionException, ServiceException, IOException;
    
    /**
    * The Delete Network Security Rule operation removes a rule from the
    * specified Network Security Group.
    *
    * @param networkSecurityGroupName Required. The name of the Network
    * Security Group.
    * @param ruleName Required. The name of the rule to delete.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> deleteRuleAsync(String networkSecurityGroupName, String ruleName);
    
    /**
    * Gets the details for the specified Network Security Group in the
    * subscription.
    *
    * @param networkSecurityGroupName Required. The name of the Network
    * Security Group to retrieve.
    * @param detailLevel Optional. Use 'Full' to list rules.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @throws ParserConfigurationException Thrown if there was a serious
    * configuration error with the document parser.
    * @throws SAXException Thrown if there was an error parsing the XML
    * response.
    * @return A Network Security Group associated with your subscription.
    */
    NetworkSecurityGroupGetResponse get(String networkSecurityGroupName, String detailLevel) throws IOException, ServiceException, ParserConfigurationException, SAXException;
    
    /**
    * Gets the details for the specified Network Security Group in the
    * subscription.
    *
    * @param networkSecurityGroupName Required. The name of the Network
    * Security Group to retrieve.
    * @param detailLevel Optional. Use 'Full' to list rules.
    * @return A Network Security Group associated with your subscription.
    */
    Future<NetworkSecurityGroupGetResponse> getAsync(String networkSecurityGroupName, String detailLevel);
    
    /**
    * Gets the Network Security Group applied to a specific subnet.
    *
    * @param virtualNetworkName Required.
    * @param subnetName Required.
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @throws ParserConfigurationException Thrown if there was a serious
    * configuration error with the document parser.
    * @throws SAXException Thrown if there was an error parsing the XML
    * response.
    * @return The Network Security Group associated with a subnet.
    */
    NetworkSecurityGroupGetForSubnetResponse getForSubnet(String virtualNetworkName, String subnetName) throws IOException, ServiceException, ParserConfigurationException, SAXException;
    
    /**
    * Gets the Network Security Group applied to a specific subnet.
    *
    * @param virtualNetworkName Required.
    * @param subnetName Required.
    * @return The Network Security Group associated with a subnet.
    */
    Future<NetworkSecurityGroupGetForSubnetResponse> getForSubnetAsync(String virtualNetworkName, String subnetName);
    
    /**
    * Lists all of the Network Security Groups for the subscription.
    *
    * @throws IOException Signals that an I/O exception of some sort has
    * occurred. This class is the general class of exceptions produced by
    * failed or interrupted I/O operations.
    * @throws ServiceException Thrown if an unexpected response is found.
    * @throws ParserConfigurationException Thrown if there was a serious
    * configuration error with the document parser.
    * @throws SAXException Thrown if there was an error parsing the XML
    * response.
    * @return The List Definitions operation response.
    */
    NetworkSecurityGroupListResponse list() throws IOException, ServiceException, ParserConfigurationException, SAXException;
    
    /**
    * Lists all of the Network Security Groups for the subscription.
    *
    * @return The List Definitions operation response.
    */
    Future<NetworkSecurityGroupListResponse> listAsync();
    
    /**
    * Removes a Network Security Group from a subnet.
    *
    * @param virtualNetworkName Required.
    * @param subnetName Required.
    * @param networkSecurityGroupName Required.
    * @throws InterruptedException Thrown when a thread is waiting, sleeping,
    * or otherwise occupied, and the thread is interrupted, either before or
    * during the activity. Occasionally a method may wish to test whether the
    * current thread has been interrupted, and if so, to immediately throw
    * this exception. The following code can be used to achieve this effect:
    * @throws ExecutionException Thrown when attempting to retrieve the result
    * of a task that aborted by throwing an exception. This exception can be
    * inspected using the Throwable.getCause() method.
    * @throws ServiceException Thrown if the server returned an error for the
    * request.
    * @throws IOException Thrown if there was an error setting up tracing for
    * the request.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse removeFromSubnet(String virtualNetworkName, String subnetName, String networkSecurityGroupName) throws InterruptedException, ExecutionException, ServiceException, IOException;
    
    /**
    * Removes a Network Security Group from a subnet.
    *
    * @param virtualNetworkName Required.
    * @param subnetName Required.
    * @param networkSecurityGroupName Required.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> removeFromSubnetAsync(String virtualNetworkName, String subnetName, String networkSecurityGroupName);
    
    /**
    * Add new Network Security Rule to existing Network Security Group.
    *
    * @param networkSecurityGroupName Optional.
    * @param ruleName Optional.
    * @param parameters Required. Parameters supplied to the Set Network
    * Security Rule operation.
    * @throws InterruptedException Thrown when a thread is waiting, sleeping,
    * or otherwise occupied, and the thread is interrupted, either before or
    * during the activity. Occasionally a method may wish to test whether the
    * current thread has been interrupted, and if so, to immediately throw
    * this exception. The following code can be used to achieve this effect:
    * @throws ExecutionException Thrown when attempting to retrieve the result
    * of a task that aborted by throwing an exception. This exception can be
    * inspected using the Throwable.getCause() method.
    * @throws ServiceException Thrown if the server returned an error for the
    * request.
    * @throws IOException Thrown if there was an error setting up tracing for
    * the request.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    OperationStatusResponse setRule(String networkSecurityGroupName, String ruleName, NetworkSecuritySetRuleParameters parameters) throws InterruptedException, ExecutionException, ServiceException, IOException;
    
    /**
    * Add new Network Security Rule to existing Network Security Group.
    *
    * @param networkSecurityGroupName Optional.
    * @param ruleName Optional.
    * @param parameters Required. Parameters supplied to the Set Network
    * Security Rule operation.
    * @return The response body contains the status of the specified
    * asynchronous operation, indicating whether it has succeeded, is
    * inprogress, or has failed. Note that this status is distinct from the
    * HTTP status code returned for the Get Operation Status operation itself.
    * If the asynchronous operation succeeded, the response body includes the
    * HTTP status code for the successful request. If the asynchronous
    * operation failed, the response body includes the HTTP status code for
    * the failed request, and also includes error information regarding the
    * failure.
    */
    Future<OperationStatusResponse> setRuleAsync(String networkSecurityGroupName, String ruleName, NetworkSecuritySetRuleParameters parameters);
}
