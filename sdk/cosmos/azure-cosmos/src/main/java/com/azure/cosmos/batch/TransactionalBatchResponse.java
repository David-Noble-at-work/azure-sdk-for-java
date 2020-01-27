// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.HttpConstants.StatusCodes;
import com.azure.cosmos.implementation.HttpConstants.SubStatusCodes;
import com.azure.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.cosmos.serialization.hybridrow.Result;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Response of a {@link TransactionalBatch} request.
 */
public class TransactionalBatchResponse implements AutoCloseable, List<TransactionalBatchOperationResult> {

    private static String EMPTY_UUID = "00000000-0000-0000-0000-000000000000";
    /**
     * Gets the cosmos diagnostic information for the current request to Azure Cosmos DB service
     */
    private CosmosDiagnostics Diagnostics;
    private CosmosDiagnosticsContext DiagnosticsContext;
    private CosmosSerializerCore SerializerCore;
    /**
     * Gets the ActivityId that identifies the server request made to execute the batch.
     */
    private String activityId;
    /**
     * Gets the reason for failure of the batch request.
     *
     * <value>The reason for failure, if any.</value>
     */
    private String errorMessage;
    private boolean isClosed;
    private List<ItemBatchOperation> operations;

    /**
     * Gets the request charge for the batch request.
     *
     * <value>
     * The request charge measured in request units.
     * </value>
     */
    private double requestCharge;

    private List<TransactionalBatchOperationResult> results;

    /**
     * Gets the amount of time to wait before retrying this or any other request within Cosmos container or collection
     * due to throttling.
     */
    private Duration retryAfter = null;
    /**
     * Gets the completion status code of the batch request.
     *
     * <value>The request completion status code.</value>
     */
    private int statusCode;

    private int subStatusCode;

    /**
     * Initializes a new instance of the {@link TransactionalBatchResponse} class. This method is intended to be used
     * only when a response from the server is not available.
     *
     * @param statusCode Indicates why the batch was not processed.
     * @param subStatusCode Provides further details about why the batch was not processed.
     * @param errorMessage The reason for failure.
     * @param operations Operations that were to be executed.
     * @param diagnosticsContext Diagnostics for the operation
     */
    public TransactionalBatchResponse(
        int statusCode,
        int subStatusCode,
        String errorMessage,
        List<ItemBatchOperation> operations,
        CosmosDiagnosticsContext diagnosticsContext) {

        this(statusCode, subStatusCode, errorMessage, 0.0D, null, EMPTY_UUID, diagnosticsContext, operations, null);
        this.CreateAndPopulateResults(operations);
    }

    /**
     * Initializes a new instance of the {@link TransactionalBatchResponse} class.
     */
    protected TransactionalBatchResponse() {
    }

    private TransactionalBatchResponse(
        int statusCode,
        int subStatusCode,
        String errorMessage,
        double requestCharge,
        Duration retryAfter,
        String activityId,
        CosmosDiagnosticsContext diagnosticsContext,
        List<ItemBatchOperation> operations,
        CosmosSerializerCore serializer) {

        this.setStatusCode(statusCode);
        this.subStatusCode = subStatusCode;
        this.setErrorMessage(errorMessage);
        this.setOperations(operations);
        this.SerializerCore = serializer;
        this.setRequestCharge(requestCharge);
        this.retryAfter = retryAfter;
        this.activityId = activityId;
        this.Diagnostics = diagnosticsContext;
        checkNotNull(diagnosticsContext, "expected non-null diagnosticsContext");
        this.DiagnosticsContext = diagnosticsContext;
    }

    public String getActivityId() {
        return activityId;
    }

    public CosmosDiagnostics getDiagnostics() {
        return Diagnostics;
    }

    public CosmosDiagnosticsContext getDiagnosticsContext() {
        return DiagnosticsContext;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String value) {
        errorMessage = value;
    }

    /**
     * Gets a value indicating whether the batch was processed.
     */
    public boolean getIsSuccessStatusCode() {
        int statusCodeInt = (int) this.getStatusCode();
        return statusCodeInt >= 200 && statusCodeInt <= 299;
    }

    public final List<ItemBatchOperation> getOperations() {
        return this.operations;
    }

    public final void setOperations(List<ItemBatchOperation> value) {
        this.operations = value;
    }

    public double getRequestCharge() {
        return requestCharge;
    }

    public void setRequestCharge(double value) {
        requestCharge = value;
    }

    public Duration getRetryAfter() {
        return retryAfter;
    }

    public CosmosSerializerCore getSerializerCore() {
        return SerializerCore;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public TransactionalBatchResponse setStatusCode(int value) {
        this.statusCode = value;
        return this;
    }

    public int getSubStatusCode() {
        return this.subStatusCode;
    }

    @Override
    public boolean isEmpty() {
        return this.results.isEmpty();
    }

    @Override
    public boolean add(TransactionalBatchOperationResult result) {
        return this.results.add(result);
    }

    @Override
    public boolean addAll(Collection<? extends TransactionalBatchOperationResult> collection) {
        return this.results.addAll(collection);
    }

    @Override
    public boolean addAll(int index, Collection<? extends TransactionalBatchOperationResult> collection) {
        return this.results.addAll(index, collection);
    }

    @Override
    public void clear() {
        this.results.clear();
    }

    @Override
    public boolean contains(Object result) {
        return this.results.contains(result);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public Iterator<TransactionalBatchOperationResult> iterator() {
        return this.results.iterator();
    }

    @Override
    public boolean remove(Object result) {
        return this.results.remove(result);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return this.results.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return this.results.retainAll(collection);
    }

    @Override
    public Object[] toArray() {
        return this.results.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.results.toArray(a);
    }

    public static CompletableFuture<TransactionalBatchResponse> FromResponseMessageAsync(
        ResponseMessage responseMessage,
        ServerBatchRequest serverRequest,
        CosmosSerializerCore serializer) {
        return FromResponseMessageAsync(responseMessage, serverRequest, serializer, true);
    }

    //C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: internal static async Task<TransactionalBatchResponse> FromResponseMessageAsync(ResponseMessage
    // responseMessage, ServerBatchRequest serverRequest, CosmosSerializerCore serializer, bool
    // shouldPromoteOperationStatus = true)
    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    public static CompletableFuture<TransactionalBatchResponse> FromResponseMessageAsync(
        ResponseMessage responseMessage,
        ServerBatchRequest serverRequest,
        CosmosSerializerCore serializer,
        boolean shouldPromoteOperationStatus) {

        try (responseMessage) {
            TransactionalBatchResponse response = null;
            if (responseMessage.Content != null) {
                //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream
                // is input or output:
                Stream content = responseMessage.Content;

                // Shouldn't be the case practically, but handle it for safety.
                if (!responseMessage.Content.CanSeek) {
                    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO
                    // .MemoryStream is input or output:
                    content = new MemoryStream();
                    //C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
                    /*await*/ responseMessage.Content.CopyToAsync(content);
                }

                if (content.ReadByte() == (int) HybridRowVersion.V1) {
                    content.Position = 0;
                    response = /*await*/
                    TransactionalBatchResponse.PopulateFromContentAsync(content, responseMessage, serverRequest,
                        serializer, shouldPromoteOperationStatus);

                    if (response == null) {
                        // Convert any payload read failures as InternalServerError
                        response = new TransactionalBatchResponse(StatusCodes.INTERNAL_SERVER_ERROR,
                            SubStatusCodes.UNKNOWN, ClientResources.ServerResponseDeserializationFailure,
                            responseMessage.Headers.RequestCharge, responseMessage.Headers.RetryAfter,
                            responseMessage.Headers.ActivityId, responseMessage.DiagnosticsContext,
                            serverRequest.getOperations(), serializer);
                    }
                }
            }

            if (response == null) {
                response = new TransactionalBatchResponse(responseMessage.StatusCode,
                    responseMessage.Headers.SubStatusCode, responseMessage.ErrorMessage,
                    responseMessage.Headers.RequestCharge, responseMessage.Headers.RetryAfter,
                    responseMessage.Headers.ActivityId, responseMessage.DiagnosticsContext,
                    serverRequest.getOperations(), serializer);
            }

            if (response.results == null || response.results.size() != serverRequest.getOperations().Count) {
                if (responseMessage.IsSuccessStatusCode) {
                    // Server should be guaranteeing number of results equal to operations when
                    // batch request is successful - so fail as InternalServerError if this is not the case.
                    response = new TransactionalBatchResponse(HttpResponseStatus.InternalServerError,
                        SubStatusCodes.UNKNOWN, ClientResources.InvalidServerResponse,
                        responseMessage.Headers.RequestCharge, responseMessage.Headers.RetryAfter,
                        responseMessage.Headers.ActivityId, responseMessage.DiagnosticsContext,
                        serverRequest.getOperations(), serializer);
                }

                // When the overall response status code is TooManyRequests, propagate the RetryAfter into the
                // individual operations.
                int retryAfterMilliseconds = 0;

                if ((int) responseMessage.StatusCode == (int) StatusCodes.TOO_MANY_REQUESTS) {
                    tangible.OutObject<Integer> tempOut_retryAfterMilliseconds = new tangible.OutObject<Integer>();
                    String retryAfter;
                    //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword
                    // - these cannot be converted using the 'OutObject' helper class unless the method is within the
                    // code being modified:
                    if (!responseMessage.Headers.TryGetValue(HttpHeaders.RETRY_AFTER_IN_MILLISECONDS,
                        out retryAfter) || retryAfter == null || !tangible.TryParseHelper.tryParseInt(retryAfter,
                        tempOut_retryAfterMilliseconds)) {
                        retryAfterMilliseconds = tempOut_retryAfterMilliseconds.argValue;
                        retryAfterMilliseconds = 0;
                    } else {
                        retryAfterMilliseconds = tempOut_retryAfterMilliseconds.argValue;
                    }
                }

                response.CreateAndPopulateResults(serverRequest.getOperations(), retryAfterMilliseconds);
            }

            return response;
        }
    }

    /**
     * Gets all the Activity IDs associated with the response.
     *
     * @return An enumerable that contains the Activity IDs.
     */
    public Iterable<String> GetActivityIds() {
        return Stream.of(this.getActivityId())::iterator;
    }

    /**
     * Gets the result of the operation at the provided index in the batch - the returned result has a Resource of
     * provided type.
     *
     * <typeparam name="T">Type to which the Resource in the operation result needs to be deserialized to, when
     * present.</typeparam>
     *
     * @param index 0-based index of the operation in the batch whose result needs to be returned.
     *
     * @return Result of batch operation that contains a Resource deserialized to specified type.
     */
    public <T> TransactionalBatchOperationResult<T> GetOperationResultAtIndex(int index) {

        TransactionalBatchOperationResult result = this.results.get(index);
        T resource = null;

        if (result.getResourceStream() != null) {
            resource = this.getSerializerCore().<T>FromStream(result.getResourceStream());
        }

        return new TransactionalBatchOperationResult<T>(result, resource);
    }

    /**
     * Disposes the current {@link TransactionalBatchResponse}.
     */
    public final void close() throws IOException {
        this.Dispose(true);
        GC.SuppressFinalize(this);
    }

    /**
     * Gets the result of the operation at the provided index in the batch.
     *
     * @param index 0-based index of the operation in the batch whose result needs to be returned.
     *
     * @return Result of operation at the provided index in the batch.
     */
    @Override
    public TransactionalBatchOperationResult get(int index) {
        return this.results.get(index);
    }

    @Override
    public TransactionalBatchOperationResult set(int index, TransactionalBatchOperationResult result) {
        return this.results.set(index, result);
    }

    @Override
    public void add(int index, TransactionalBatchOperationResult element) {

    }

    @Override
    public TransactionalBatchOperationResult remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<TransactionalBatchOperationResult> listIterator() {
        return null;
    }

    @Override
    public ListIterator<TransactionalBatchOperationResult> listIterator(int index) {
        return null;
    }

    @Override
    public List<TransactionalBatchOperationResult> subList(int fromIndex, int toIndex) {
        return null;
    }

    /**
     * Gets the number of operation results.
     */
    public int size() {
        return this.results == null ? 0 : this.results.size();
    }

    /**
     * Disposes the disposable members held by this class.
     *
     * @param disposing Indicates whether to dispose managed resources or not.
     */
    protected void Dispose(boolean disposing) {
        if (disposing && !this.isClosed) {
            this.isClosed = true;
            if (this.getOperations() != null) {
                for (ItemBatchOperation operation : this.getOperations()) {
                    operation.close();
                }

                this.setOperations(null);
            }
        }
    }

    private void CreateAndPopulateResults(List<ItemBatchOperation> operations) {
        CreateAndPopulateResults(operations, 0);
    }

    private void CreateAndPopulateResults(List<ItemBatchOperation> operations, int retryAfterMilliseconds) {

        TransactionalBatchOperationResult[] results = new TransactionalBatchOperationResult[operations.size()];

        for (int i = 0; i < operations.size(); i++) {
            this.results.add(new TransactionalBatchOperationResult(this.getStatusCode())
                .setSubStatusCode(this.getSubStatusCode())
                .setRetryAfter(Duration.ofMillis(retryAfterMilliseconds)));
        }

        this.results = Collections.unmodifiableList(Arrays.asList(results));
    }

    //C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: private static async Task<TransactionalBatchResponse> PopulateFromContentAsync(Stream content,
    // ResponseMessage responseMessage, ServerBatchRequest serverRequest, CosmosSerializerCore serializer, bool
    // shouldPromoteOperationStatus)
    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    private static Task<TransactionalBatchResponse> PopulateFromContentAsync(
        Stream content,
        ResponseMessage responseMessage,
        ServerBatchRequest serverRequest,
        CosmosSerializerCore serializer,
        boolean shouldPromoteOperationStatus) {

        ArrayList<TransactionalBatchOperationResult> results = new ArrayList<TransactionalBatchOperationResult>();

        // content is ensured to be seekable in caller.
        int resizerInitialCapacity = (int) content.Length;

        //C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: Result res = await content.ReadRecordIOAsync(record =>
        Result res = /*await*/ content.ReadRecordIOAsync(record ->
        {
            TransactionalBatchOperationResult operationResult;
            Result r = TransactionalBatchOperationResult.ReadOperationResult(record, out operationResult);
            if (r != Result.SUCCESS) {
                return r;
            }

            results.add(operationResult);
            return r;
        }, resizer:new MemorySpanResizer<Byte>(resizerInitialCapacity))

        if (res != Result.SUCCESS) {
            return null;
        }

        HttpResponseStatus responseStatusCode = responseMessage.StatusCode;
        SubStatusCodes responseSubStatusCode = responseMessage.Headers.SubStatusCode;

        // Promote the operation error status as the Batch response error status if we have a MultiStatus response
        // to provide users with status codes they are used to.
        if ((int) responseMessage.StatusCode == (int) StatusCodes.MultiStatus && shouldPromoteOperationStatus) {
            for (TransactionalBatchOperationResult result : results) {
                if ((int) result.getStatusCode() != (int) StatusCodes.FailedDependency) {
                    responseStatusCode = result.getStatusCode();
                    responseSubStatusCode = result.getSubStatusCode();
                    break;
                }
            }
        }

        TransactionalBatchResponse response = new TransactionalBatchResponse(responseStatusCode,
            responseSubStatusCode, responseMessage.ErrorMessage, responseMessage.Headers.RequestCharge,
            responseMessage.Headers.RetryAfter, responseMessage.Headers.ActivityId,
            responseMessage.DiagnosticsContext, serverRequest.getOperations(), serializer);

        response.results = results;
        return response;
    }
}
