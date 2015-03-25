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

package com.microsoft.azure.management.sql;

import com.microsoft.windowsazure.core.ServiceClient;
import com.microsoft.windowsazure.credentials.SubscriptionCloudCredentials;
import com.microsoft.windowsazure.management.configuration.ManagementConfiguration;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.http.impl.client.HttpClientBuilder;

/**
* The Windows Azure SQL Database management API provides a RESTful set of web
* services that interact with Windows Azure SQL Database services to manage
* your databases. The API enables users to create, retrieve, update, and
* delete databases and servers.
*/
public class SqlManagementClientImpl extends ServiceClient<SqlManagementClient> implements SqlManagementClient {
    private String apiVersion;
    
    /**
    * Gets the API version.
    * @return The ApiVersion value.
    */
    public String getApiVersion() {
        return this.apiVersion;
    }
    
    private URI baseUri;
    
    /**
    * Gets the URI used as the base for all cloud service requests.
    * @return The BaseUri value.
    */
    public URI getBaseUri() {
        return this.baseUri;
    }
    
    private SubscriptionCloudCredentials credentials;
    
    /**
    * Gets subscription credentials which uniquely identify Microsoft Azure
    * subscription. The subscription ID forms part of the URI for every
    * service call.
    * @return The Credentials value.
    */
    public SubscriptionCloudCredentials getCredentials() {
        return this.credentials;
    }
    
    private int longRunningOperationInitialTimeout;
    
    /**
    * Gets or sets the initial timeout for Long Running Operations.
    * @return The LongRunningOperationInitialTimeout value.
    */
    public int getLongRunningOperationInitialTimeout() {
        return this.longRunningOperationInitialTimeout;
    }
    
    /**
    * Gets or sets the initial timeout for Long Running Operations.
    * @param longRunningOperationInitialTimeoutValue The
    * LongRunningOperationInitialTimeout value.
    */
    public void setLongRunningOperationInitialTimeout(final int longRunningOperationInitialTimeoutValue) {
        this.longRunningOperationInitialTimeout = longRunningOperationInitialTimeoutValue;
    }
    
    private int longRunningOperationRetryTimeout;
    
    /**
    * Gets or sets the retry timeout for Long Running Operations.
    * @return The LongRunningOperationRetryTimeout value.
    */
    public int getLongRunningOperationRetryTimeout() {
        return this.longRunningOperationRetryTimeout;
    }
    
    /**
    * Gets or sets the retry timeout for Long Running Operations.
    * @param longRunningOperationRetryTimeoutValue The
    * LongRunningOperationRetryTimeout value.
    */
    public void setLongRunningOperationRetryTimeout(final int longRunningOperationRetryTimeoutValue) {
        this.longRunningOperationRetryTimeout = longRunningOperationRetryTimeoutValue;
    }
    
    private AuditingPolicyOperations auditingPolicy;
    
    /**
    * Represents all the operations to manage Azure SQL Database and Database
    * Server Audit policy.  Contains operations to: Create, Retrieve and
    * Update audit policy.
    * @return The AuditingPolicyOperations value.
    */
    public AuditingPolicyOperations getAuditingPolicyOperations() {
        return this.auditingPolicy;
    }
    
    private DatabaseOperations databases;
    
    /**
    * Represents all the operations for operating on Azure SQL Databases.
    * Contains operations to: Create, Retrieve, Update, and Delete databases,
    * and also includes the ability to get the event logs for a database.
    * @return The DatabasesOperations value.
    */
    public DatabaseOperations getDatabasesOperations() {
        return this.databases;
    }
    
    private DataMaskingOperations dataMasking;
    
    /**
    * Represents all the operations for operating on Azure SQL Database data
    * masking. Contains operations to: Create, Retrieve, Update, and Delete
    * data masking rules, as well as Create, Retreive and Update data masking
    * policy.
    * @return The DataMaskingOperations value.
    */
    public DataMaskingOperations getDataMaskingOperations() {
        return this.dataMasking;
    }
    
    private FirewallRuleOperations firewallRules;
    
    /**
    * Represents all the operations for operating on Azure SQL Database Server
    * Firewall Rules.  Contains operations to: Create, Retrieve, Update, and
    * Delete firewall rules.
    * @return The FirewallRulesOperations value.
    */
    public FirewallRuleOperations getFirewallRulesOperations() {
        return this.firewallRules;
    }
    
    private SecureConnectionPolicyOperations secureConnection;
    
    /**
    * Represents all the operations for managing Azure SQL Database secure
    * connection.  Contains operations to: Create, Retrieve and Update secure
    * connection policy .
    * @return The SecureConnectionOperations value.
    */
    public SecureConnectionPolicyOperations getSecureConnectionOperations() {
        return this.secureConnection;
    }
    
    private ServerOperations servers;
    
    /**
    * Represents all the operations for operating on Azure SQL Database
    * Servers.  Contains operations to: Create, Retrieve, Update, and Delete
    * servers.
    * @return The ServersOperations value.
    */
    public ServerOperations getServersOperations() {
        return this.servers;
    }
    
    private ServiceObjectiveOperations serviceObjectives;
    
    /**
    * Represents all the operations for operating on Azure SQL Database Service
    * Objectives.   Contains operations to: Retrieve service objectives.
    * @return The ServiceObjectivesOperations value.
    */
    public ServiceObjectiveOperations getServiceObjectivesOperations() {
        return this.serviceObjectives;
    }
    
    /**
    * Initializes a new instance of the SqlManagementClientImpl class.
    *
    * @param httpBuilder The HTTP client builder.
    * @param executorService The executor service.
    */
    public SqlManagementClientImpl(HttpClientBuilder httpBuilder, ExecutorService executorService) {
        super(httpBuilder, executorService);
        this.auditingPolicy = new AuditingPolicyOperationsImpl(this);
        this.databases = new DatabaseOperationsImpl(this);
        this.dataMasking = new DataMaskingOperationsImpl(this);
        this.firewallRules = new FirewallRuleOperationsImpl(this);
        this.secureConnection = new SecureConnectionPolicyOperationsImpl(this);
        this.servers = new ServerOperationsImpl(this);
        this.serviceObjectives = new ServiceObjectiveOperationsImpl(this);
        this.apiVersion = "2014-04-01";
        this.longRunningOperationInitialTimeout = -1;
        this.longRunningOperationRetryTimeout = -1;
    }
    
    /**
    * Initializes a new instance of the SqlManagementClientImpl class.
    *
    * @param httpBuilder The HTTP client builder.
    * @param executorService The executor service.
    * @param credentials Required. Gets subscription credentials which uniquely
    * identify Microsoft Azure subscription. The subscription ID forms part of
    * the URI for every service call.
    * @param baseUri Optional. Gets the URI used as the base for all cloud
    * service requests.
    */
    @Inject
    public SqlManagementClientImpl(HttpClientBuilder httpBuilder, ExecutorService executorService, @Named(ManagementConfiguration.SUBSCRIPTION_CLOUD_CREDENTIALS) SubscriptionCloudCredentials credentials, @Named(ManagementConfiguration.URI) URI baseUri) {
        this(httpBuilder, executorService);
        if (credentials == null) {
            throw new NullPointerException("credentials");
        } else {
            this.credentials = credentials;
        }
        if (baseUri == null) {
            try {
                this.baseUri = new URI("https://management.azure.com");
            }
            catch (URISyntaxException ex) {
            }
        } else {
            this.baseUri = baseUri;
        }
    }
    
    /**
    * Initializes a new instance of the SqlManagementClientImpl class.
    *
    * @param httpBuilder The HTTP client builder.
    * @param executorService The executor service.
    * @param credentials Required. Gets subscription credentials which uniquely
    * identify Microsoft Azure subscription. The subscription ID forms part of
    * the URI for every service call.
    * @throws URISyntaxException Thrown if there was an error parsing a URI in
    * the response.
    */
    public SqlManagementClientImpl(HttpClientBuilder httpBuilder, ExecutorService executorService, SubscriptionCloudCredentials credentials) throws URISyntaxException {
        this(httpBuilder, executorService);
        if (credentials == null) {
            throw new NullPointerException("credentials");
        }
        this.credentials = credentials;
        this.baseUri = new URI("https://management.azure.com");
    }
    
    /**
    * Initializes a new instance of the SqlManagementClientImpl class.
    *
    * @param httpBuilder The HTTP client builder.
    * @param executorService The executor service.
    * @param credentials Required. Gets subscription credentials which uniquely
    * identify Microsoft Azure subscription. The subscription ID forms part of
    * the URI for every service call.
    * @param baseUri Optional. Gets the URI used as the base for all cloud
    * service requests.
    * @param apiVersion Optional. Gets the API version.
    * @param longRunningOperationInitialTimeout Required. Gets or sets the
    * initial timeout for Long Running Operations.
    * @param longRunningOperationRetryTimeout Required. Gets or sets the retry
    * timeout for Long Running Operations.
    */
    public SqlManagementClientImpl(HttpClientBuilder httpBuilder, ExecutorService executorService, SubscriptionCloudCredentials credentials, URI baseUri, String apiVersion, int longRunningOperationInitialTimeout, int longRunningOperationRetryTimeout) {
        this(httpBuilder, executorService);
        this.credentials = credentials;
        this.baseUri = baseUri;
        this.apiVersion = apiVersion;
        this.longRunningOperationInitialTimeout = longRunningOperationInitialTimeout;
        this.longRunningOperationRetryTimeout = longRunningOperationRetryTimeout;
    }
    
    /**
    * Initializes a new instance of the SqlManagementClientImpl class.
    *
    * @param httpBuilder The HTTP client builder.
    * @param executorService The executor service.
    */
    protected SqlManagementClientImpl newInstance(HttpClientBuilder httpBuilder, ExecutorService executorService) {
        return new SqlManagementClientImpl(httpBuilder, executorService, this.getCredentials(), this.getBaseUri(), this.getApiVersion(), this.getLongRunningOperationInitialTimeout(), this.getLongRunningOperationRetryTimeout());
    }
}
