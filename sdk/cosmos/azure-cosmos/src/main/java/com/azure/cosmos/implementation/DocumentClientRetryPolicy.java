// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation;

import com.azure.cosmos.batch.unimplemented.ResponseMessage;
import reactor.core.publisher.Mono;

/**
 * While this class is public, but it is not part of our published public APIs.
 * This is meant to be internally used only by our sdk.
 */
public abstract class DocumentClientRetryPolicy extends RetryPolicyWithDiagnostics {

    // TODO: this is just a place holder for now. As .Net has this method.
    // I have to spend more time figure out what's the right pattern for this (if method needed)

    /**
     * Method that is called before a request is sent to allow the retry policy implementation
     * to modify the state of the request.
     *
     * @param request The request being sent to the service.</param>
     * <p>
     * Currently only read operations will invoke this method. There is no scenario for write
     * operations to modify requests before retrying.
     */
    // TODO: need to investigate what's the right contract here and/or if/how this is useful
    public abstract void onBeforeSendRequest(RxDocumentServiceRequest request);

    /**
     * Method that is called to determine from the policy that needs to retry on the a particular status code.
     *
     * @param message {@link ResponseMessage} in return of the request.
     *
     * @return If the retry needs to be attempted or not.
     */
    public abstract Mono<ShouldRetryResult> shouldRetry(ResponseMessage message);
}
