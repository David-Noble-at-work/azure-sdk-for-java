// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.storage.file.share.implementation;

import com.azure.core.http.HttpPipeline;
import com.azure.core.http.swagger.RestProxy;

/**
 * Initializes a new instance of the AzureFileStorage type.
 */
public final class AzureFileStorageImpl {
    /**
     * Specifies the version of the operation to use for this request.
     */
    private String version;

    /**
     * Gets Specifies the version of the operation to use for this request.
     *
     * @return the version value.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Sets Specifies the version of the operation to use for this request.
     *
     * @param version the version value.
     */
    AzureFileStorageImpl setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * The URL of the service account, share, directory or file that is the target of the desired operation.
     */
    private String url;

    /**
     * Gets The URL of the service account, share, directory or file that is the target of the desired operation.
     *
     * @return the url value.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Sets The URL of the service account, share, directory or file that is the target of the desired operation.
     *
     * @param url the url value.
     */
    AzureFileStorageImpl setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * The HTTP pipeline to send requests through.
     */
    private HttpPipeline httpPipeline;

    /**
     * Gets The HTTP pipeline to send requests through.
     *
     * @return the httpPipeline value.
     */
    public HttpPipeline getHttpPipeline() {
        return this.httpPipeline;
    }

    /**
     * The ServicesImpl object to access its operations.
     */
    private ServicesImpl services;

    /**
     * Gets the ServicesImpl object to access its operations.
     *
     * @return the ServicesImpl object.
     */
    public ServicesImpl services() {
        return this.services;
    }

    /**
     * The SharesImpl object to access its operations.
     */
    private SharesImpl shares;

    /**
     * Gets the SharesImpl object to access its operations.
     *
     * @return the SharesImpl object.
     */
    public SharesImpl shares() {
        return this.shares;
    }

    /**
     * The DirectorysImpl object to access its operations.
     */
    private DirectorysImpl directorys;

    /**
     * Gets the DirectorysImpl object to access its operations.
     *
     * @return the DirectorysImpl object.
     */
    public DirectorysImpl directorys() {
        return this.directorys;
    }

    /**
     * The FilesImpl object to access its operations.
     */
    private FilesImpl files;

    /**
     * Gets the FilesImpl object to access its operations.
     *
     * @return the FilesImpl object.
     */
    public FilesImpl files() {
        return this.files;
    }

    /**
     * Initializes an instance of AzureFileStorage client.
     */
    public AzureFileStorageImpl() {
        this(RestProxy.createDefaultPipeline());
    }

    /**
     * Initializes an instance of AzureFileStorage client.
     *
     * @param httpPipeline The HTTP pipeline to send requests through.
     */
    public AzureFileStorageImpl(HttpPipeline httpPipeline) {
        this.httpPipeline = httpPipeline;
        this.services = new ServicesImpl(this);
        this.shares = new SharesImpl(this);
        this.directorys = new DirectorysImpl(this);
        this.files = new FilesImpl(this);
    }
}
