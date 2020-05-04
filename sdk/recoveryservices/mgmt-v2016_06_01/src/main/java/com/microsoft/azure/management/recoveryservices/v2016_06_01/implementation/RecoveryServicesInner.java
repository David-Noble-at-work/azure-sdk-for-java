/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.recoveryservices.v2016_06_01.implementation;

import retrofit2.Retrofit;
import com.google.common.reflect.TypeToken;
import com.microsoft.azure.CloudException;
import com.microsoft.azure.management.recoveryservices.v2016_06_01.CheckNameAvailabilityParameters;
import com.microsoft.rest.ServiceCallback;
import com.microsoft.rest.ServiceFuture;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.rest.Validator;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.Response;
import rx.functions.Func1;
import rx.Observable;

/**
 * An instance of this class provides access to all the operations defined
 * in RecoveryServices.
 */
public class RecoveryServicesInner {
    /** The Retrofit service to perform REST calls. */
    private RecoveryServicesService service;
    /** The service client containing this operation class. */
    private RecoveryServicesClientImpl client;

    /**
     * Initializes an instance of RecoveryServicesInner.
     *
     * @param retrofit the Retrofit instance built from a Retrofit Builder.
     * @param client the instance of the service client containing this operation class.
     */
    public RecoveryServicesInner(Retrofit retrofit, RecoveryServicesClientImpl client) {
        this.service = retrofit.create(RecoveryServicesService.class);
        this.client = client;
    }

    /**
     * The interface defining all the services for RecoveryServices to be
     * used by Retrofit to perform actually REST calls.
     */
    interface RecoveryServicesService {
        @Headers({ "Content-Type: application/json; charset=utf-8", "x-ms-logging-context: com.microsoft.azure.management.recoveryservices.v2016_06_01.RecoveryServices checkNameAvailability" })
        @POST("subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/Microsoft.RecoveryServices/locations/{location}/checkNameAvailability")
        Observable<Response<ResponseBody>> checkNameAvailability(@Path("subscriptionId") String subscriptionId, @Path("resourceGroupName") String resourceGroupName, @Path("location") String location, @Query("api-version") String apiVersion, @Body CheckNameAvailabilityParameters input, @Header("accept-language") String acceptLanguage, @Header("User-Agent") String userAgent);

    }

    /**
     * API to check for resource name availability.
     A name is available if no other resource exists that has the same SubscriptionId, Resource Name and Type
     or if one or more such resources exist, each of these must be GC'd and their time of deletion be more than 24 Hours Ago.
     *
     * @param resourceGroupName The name of the resource group where the recovery services vault is present.
     * @param location Location of the resource
     * @param input Contains information about Resource type and Resource name
     * @throws IllegalArgumentException thrown if parameters fail the validation
     * @throws CloudException thrown if the request is rejected by server
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent
     * @return the CheckNameAvailabilityResultInner object if successful.
     */
    public CheckNameAvailabilityResultInner checkNameAvailability(String resourceGroupName, String location, CheckNameAvailabilityParameters input) {
        return checkNameAvailabilityWithServiceResponseAsync(resourceGroupName, location, input).toBlocking().single().body();
    }

    /**
     * API to check for resource name availability.
     A name is available if no other resource exists that has the same SubscriptionId, Resource Name and Type
     or if one or more such resources exist, each of these must be GC'd and their time of deletion be more than 24 Hours Ago.
     *
     * @param resourceGroupName The name of the resource group where the recovery services vault is present.
     * @param location Location of the resource
     * @param input Contains information about Resource type and Resource name
     * @param serviceCallback the async ServiceCallback to handle successful and failed responses.
     * @throws IllegalArgumentException thrown if parameters fail the validation
     * @return the {@link ServiceFuture} object
     */
    public ServiceFuture<CheckNameAvailabilityResultInner> checkNameAvailabilityAsync(String resourceGroupName, String location, CheckNameAvailabilityParameters input, final ServiceCallback<CheckNameAvailabilityResultInner> serviceCallback) {
        return ServiceFuture.fromResponse(checkNameAvailabilityWithServiceResponseAsync(resourceGroupName, location, input), serviceCallback);
    }

    /**
     * API to check for resource name availability.
     A name is available if no other resource exists that has the same SubscriptionId, Resource Name and Type
     or if one or more such resources exist, each of these must be GC'd and their time of deletion be more than 24 Hours Ago.
     *
     * @param resourceGroupName The name of the resource group where the recovery services vault is present.
     * @param location Location of the resource
     * @param input Contains information about Resource type and Resource name
     * @throws IllegalArgumentException thrown if parameters fail the validation
     * @return the observable to the CheckNameAvailabilityResultInner object
     */
    public Observable<CheckNameAvailabilityResultInner> checkNameAvailabilityAsync(String resourceGroupName, String location, CheckNameAvailabilityParameters input) {
        return checkNameAvailabilityWithServiceResponseAsync(resourceGroupName, location, input).map(new Func1<ServiceResponse<CheckNameAvailabilityResultInner>, CheckNameAvailabilityResultInner>() {
            @Override
            public CheckNameAvailabilityResultInner call(ServiceResponse<CheckNameAvailabilityResultInner> response) {
                return response.body();
            }
        });
    }

    /**
     * API to check for resource name availability.
     A name is available if no other resource exists that has the same SubscriptionId, Resource Name and Type
     or if one or more such resources exist, each of these must be GC'd and their time of deletion be more than 24 Hours Ago.
     *
     * @param resourceGroupName The name of the resource group where the recovery services vault is present.
     * @param location Location of the resource
     * @param input Contains information about Resource type and Resource name
     * @throws IllegalArgumentException thrown if parameters fail the validation
     * @return the observable to the CheckNameAvailabilityResultInner object
     */
    public Observable<ServiceResponse<CheckNameAvailabilityResultInner>> checkNameAvailabilityWithServiceResponseAsync(String resourceGroupName, String location, CheckNameAvailabilityParameters input) {
        if (this.client.subscriptionId() == null) {
            throw new IllegalArgumentException("Parameter this.client.subscriptionId() is required and cannot be null.");
        }
        if (resourceGroupName == null) {
            throw new IllegalArgumentException("Parameter resourceGroupName is required and cannot be null.");
        }
        if (location == null) {
            throw new IllegalArgumentException("Parameter location is required and cannot be null.");
        }
        if (this.client.apiVersion() == null) {
            throw new IllegalArgumentException("Parameter this.client.apiVersion() is required and cannot be null.");
        }
        if (input == null) {
            throw new IllegalArgumentException("Parameter input is required and cannot be null.");
        }
        Validator.validate(input);
        return service.checkNameAvailability(this.client.subscriptionId(), resourceGroupName, location, this.client.apiVersion(), input, this.client.acceptLanguage(), this.client.userAgent())
            .flatMap(new Func1<Response<ResponseBody>, Observable<ServiceResponse<CheckNameAvailabilityResultInner>>>() {
                @Override
                public Observable<ServiceResponse<CheckNameAvailabilityResultInner>> call(Response<ResponseBody> response) {
                    try {
                        ServiceResponse<CheckNameAvailabilityResultInner> clientResponse = checkNameAvailabilityDelegate(response);
                        return Observable.just(clientResponse);
                    } catch (Throwable t) {
                        return Observable.error(t);
                    }
                }
            });
    }

    private ServiceResponse<CheckNameAvailabilityResultInner> checkNameAvailabilityDelegate(Response<ResponseBody> response) throws CloudException, IOException, IllegalArgumentException {
        return this.client.restClient().responseBuilderFactory().<CheckNameAvailabilityResultInner, CloudException>newInstance(this.client.serializerAdapter())
                .register(200, new TypeToken<CheckNameAvailabilityResultInner>() { }.getType())
                .registerError(CloudException.class)
                .build(response);
    }

}
