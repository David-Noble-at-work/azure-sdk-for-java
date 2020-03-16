// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation.http;

import reactor.core.publisher.Mono;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * A generic interface for sending HTTP requests and getting responses.
 */
public interface HttpClient {

    /**
     * Send the provided request asynchronously.
     *
     * @param request The HTTP request to send
     * @return A {@link Mono} that emits response asynchronously
     */
    Mono<HttpResponse> send(HttpRequest request);

    /**
     * Create HttpClient with FixedChannelPool {@link HttpClientConfig}
     *
     * @return the HttpClient
     */
    static HttpClient createFixed(HttpClientConfig httpClientConfig) {
        if (httpClientConfig.getConfigs() == null) {
            throw new IllegalArgumentException("HttpClientConfig is null");
        }

        Duration maxIdleConnectionTimeoutInMillis = httpClientConfig.getConfigs().getMaxIdleConnectionTimeout();
        if (httpClientConfig.getMaxIdleConnectionTimeout() != null) {
            maxIdleConnectionTimeoutInMillis = httpClientConfig.getMaxIdleConnectionTimeout();
        }

        //  Default pool size
        Integer maxPoolSize = httpClientConfig.getConfigs().getReactorNettyMaxConnectionPoolSize();
        if (httpClientConfig.getMaxPoolSize() != null) {
            maxPoolSize = httpClientConfig.getMaxPoolSize();
        }

        Duration connectionAcquireTimeout = httpClientConfig.getConfigs().getConnectionAcquireTimeout();

        ConnectionProvider fixedConnectionProvider =
            ConnectionProvider.fixed(httpClientConfig.getConfigs().getReactorNettyConnectionPoolName(),
                maxPoolSize, connectionAcquireTimeout.toMillis(), maxIdleConnectionTimeoutInMillis);

        return ReactorNettyClient.createWithConnectionProvider(fixedConnectionProvider, httpClientConfig);
    }

    /**
     * Create HttpClient with un-pooled connection {@link HttpClientConfig}
     *
     * @return the HttpClient
     */
    static HttpClient create(HttpClientConfig httpClientConfig) {
        if (httpClientConfig.getConfigs() == null) {
            throw new IllegalArgumentException("HttpClientConfig is null");
        }

        return ReactorNettyClient.create(httpClientConfig);
    }

    /**
     * Shutdown the Http Client and clean up resources
     */
    void shutdown();
}
