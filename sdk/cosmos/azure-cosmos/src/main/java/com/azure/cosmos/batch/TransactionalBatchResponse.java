// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.batch.serializer.CosmosSerializerCore;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnostics;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
import com.azure.cosmos.core.Out;
import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.HttpConstants.SubStatusCodes;
import com.azure.cosmos.models.Resource;
import com.azure.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.recordio.RecordIOStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.commons.collections4.list.UnmodifiableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.stream.Stream;

import static com.azure.cosmos.implementation.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * Response of a {@link TransactionalBatch} request.
 */
@SuppressWarnings("try")
public class TransactionalBatchResponse implements AutoCloseable, List<TransactionalBatchOperationResult<?>> {

    // region Fields

    private static final String EMPTY_UUID = "00000000-0000-0000-0000-000000000000";

    @SuppressWarnings("rawtypes")
    private static final AtomicReferenceFieldUpdater<TransactionalBatchResponse, UnmodifiableList> operationsUpdater =
        AtomicReferenceFieldUpdater.newUpdater(
            TransactionalBatchResponse.class,
            UnmodifiableList.class,
            "operations");

    private final String activityId;
    private final CosmosDiagnosticsContext diagnosticsContext;
    private final double requestCharge;
    private final HttpResponseStatus responseStatus;
    private final Duration retryAfter;
    private final CosmosSerializerCore serializer;
    private String errorMessage;
    private List<TransactionalBatchOperationResult<?>> results;
    private int subStatusCode;

    private volatile UnmodifiableList<ItemBatchOperation<?>> operations;

    // endregion

    // region Constructors

    /**
     * Initializes a new instance of the {@link TransactionalBatchResponse} class. This method is intended to be used
     * only when a response from the server is not available.
     *
     * @param responseStatus Indicates why the batch was not processed.
     * @param subStatusCode Provides further details about why the batch was not processed.
     * @param errorMessage The reason for failure.
     * @param operations Operations that were to be executed.
     * @param diagnosticsContext Diagnostics for the operation
     */
    public TransactionalBatchResponse(
        HttpResponseStatus responseStatus,
        int subStatusCode,
        String errorMessage,
        List<ItemBatchOperation<?>> operations,
        CosmosDiagnosticsContext diagnosticsContext) {

        this(responseStatus, subStatusCode, errorMessage, 0.0D, null, EMPTY_UUID, diagnosticsContext, operations, null);
        this.createAndPopulateResults(operations);
    }

    /**
     * Initializes a new instance of the {@link TransactionalBatchResponse} class.
     *
     * @param responseStatus the {@link HttpResponseStatus response status}.
     * @param subStatusCode the response sub-status code.
     * @param errorMessage an error message or {@code null}.
     * @param requestCharge the request charge or {@link Double#NaN}
     * @param retryAfter the retry {@link Duration duration} or {@code null}.
     * @param activityId the activity ID.
     * @param diagnosticsContext a {@link CosmosDiagnosticsContext diagnostics context}.
     * @param operations a {@link List list} of {@link ItemBatchOperation batch operations}.
     * @param serializer a {@link CosmosSerializerCore serializer}.
     */
    protected TransactionalBatchResponse(
        @Nonnull final HttpResponseStatus responseStatus,
        final int subStatusCode,
        @Nullable final String errorMessage,
        final double requestCharge,
        @Nullable final Duration retryAfter,
        @Nonnull final String activityId,
        @Nonnull final CosmosDiagnosticsContext diagnosticsContext,
        @Nonnull List<ItemBatchOperation<?>> operations,
        @Nonnull final CosmosSerializerCore serializer) {

        checkNotNull(responseStatus, "expected non-null responseStatus");
        checkNotNull(activityId, "expected non-null activityId");
        checkNotNull(diagnosticsContext, "expected non-null diagnosticsContext");
        checkNotNull(operations, "expected non-null operations");
        checkNotNull(serializer, "expected non-null serializer");

        this.responseStatus = responseStatus;
        this.subStatusCode = subStatusCode;
        this.errorMessage = errorMessage;
        this.requestCharge = requestCharge;
        this.retryAfter = retryAfter;
        this.activityId = activityId;
        this.diagnosticsContext = diagnosticsContext;

        this.operations = operations instanceof UnmodifiableList<?>
            ? (UnmodifiableList<ItemBatchOperation<?>>) operations
            : (UnmodifiableList<ItemBatchOperation<?>>) Collections.unmodifiableList(operations);

        this.serializer = serializer;
    }

    // endregion

    // region Accessors

    /**
     * Gets the activity ID that identifies the server request made to execute the batch.
     *
     * @return the activity ID that identifies the server request made to execute the batch.
     */
    public String getActivityId() {
        return this.activityId;
    }

    /**
     * Gets all the activity IDs associated with the response.
     *
     * @return an enumerable that contains the activity IDs.
     */
    public Iterable<String> getActivityIds() {
        return Stream.of(this.getActivityId())::iterator;
    }

    public final List<ItemBatchOperation<?>> getBatchOperations() {
        return this.operations;
    }

    public CosmosDiagnostics getDiagnostics() {
        return this.diagnosticsContext;
    }

    /**
     * Gets {@link CosmosDiagnosticsContext diagnostic information} for the current request to Azure Cosmos DB.
     *
     * @return {@link CosmosDiagnosticsContext diagnostic information} for the current request to Azure Cosmos DB.
     */
    public CosmosDiagnosticsContext getDiagnosticsContext() {
        return this.diagnosticsContext;
    }

    /**
     * Gets the reason for the failure of the batch request, if any, or {@code null}.
     *
     * @return the reason for the failure of the batch request, if any, or {@code null}.
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

    /**
     * Gets the request charge for the batch request.
     *
     * @return the request charge measured in request units.
     */
    public double getRequestCharge() {
        return this.requestCharge;
    }

    /**
     * Gets the response status code of the batch request.
     *
     * @return the response status code of the batch request.
     */
    public HttpResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    /**
     * Gets the amount of time to wait before retrying this or any other request due to throttling.
     *
     * @return the amount of time to wait before retrying this or any other request due to throttling.
     */
    public Duration getRetryAfter() {
        return this.retryAfter;
    }

    public CosmosSerializerCore getSerializer() {
        return this.serializer;
    }

    public int getSubStatusCode() {
        return this.subStatusCode;
    }

    @Override
    public boolean isEmpty() {
        return this.results.isEmpty();
    }

    // endregion

    // region Methods

    /**
     * Returns a value indicating whether the batch was successfully processed.
     *
     * @return a value indicating whether the batch was successfully processed.
     */
    public boolean isSuccessStatusCode() {
        int statusCode = this.responseStatus.code();
        return statusCode >= 200 && statusCode <= 299;
    }

    @Override
    public boolean add(TransactionalBatchOperationResult<?> result) {
        return this.results.add(result);
    }

    @Override
    public void add(int index, TransactionalBatchOperationResult<?> element) {

    }

    @Override
    public boolean addAll(Collection<? extends TransactionalBatchOperationResult<?>> collection) {
        return this.results.addAll(collection);
    }

    @Override
    public boolean addAll(int index, Collection<? extends TransactionalBatchOperationResult<?>> collection) {
        return this.results.addAll(index, collection);
    }

    @Override
    public void clear() {
        this.results.clear();
    }

    /**
     * Closes the current {@link TransactionalBatchResponse}.
     *
     * @throws Exception if the close fails.
     */
    public void close() throws Exception {

        @SuppressWarnings("unchecked")
        UnmodifiableList<ItemBatchOperation<?>> operations = operationsUpdater.getAndSet(this, null);

        // TODO (DANOBLE) Aggregate exceptions to ensure that all operations that be closed are closed.

        if (operations != null) {
            for (ItemBatchOperation<?> operation : operations) {
                operation.close();
            }
        }
    }

    @Override
    public boolean contains(Object result) {
        return this.results.contains(result);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    public static CompletableFuture<TransactionalBatchResponse> fromResponseMessageAsync(
        BatchResponseMessage responseMessage,
        ServerBatchRequest serverRequest,
        CosmosSerializerCore serializer) {
        return fromResponseMessageAsync(responseMessage, serverRequest, serializer, true);
    }

    /** Creates a transactional batch response} from a response message
     *
     * @param message the {@link BatchResponseMessage response message}.
     * @param request the {@link ServerBatchRequest batch request} that produced {@code message}.
     * @param serializer a serializer that will be used to deserialize {@code message} content.
     * @param shouldPromoteOperationStatus indicates whether the operation status should be promoted.
     *
     * @return a future that provides the {@link TransactionalBatchResponse transactional batch response} created
     * from {@link BatchResponseMessage message} when the asynchronous operation completes.
     */
    public static CompletableFuture<TransactionalBatchResponse> fromResponseMessageAsync(
        @Nonnull final BatchResponseMessage message,
        @Nonnull final ServerBatchRequest request,
        @Nonnull final CosmosSerializerCore serializer,
        final boolean shouldPromoteOperationStatus) {

        try {

            InputStream inputStream = message.getContent();

            if (inputStream != null) {

                if (!inputStream.markSupported()) {

                    int length = inputStream.available();

                    if (length <= 0) {
                        inputStream = null;
                    } else {

                        ByteBuf buffer = Unpooled.buffer(length);

                        do {
                            buffer.writeBytes(inputStream, length);
                            length = inputStream.available();
                        } while (length > 0);

                        inputStream = new ByteBufInputStream(buffer);
                    }
                }
            }

            CompletableFuture<TransactionalBatchResponse> future = null;

            if (inputStream != null) {

                inputStream.mark(1);
                byte b = (byte) inputStream.read();

                if (b == HybridRowVersion.V1.value()) {

                    inputStream.reset();

                    future = populateFromContentAsync(
                        inputStream,
                        message,
                        request,
                        serializer,
                        shouldPromoteOperationStatus

                    ).thenApplyAsync(response -> response != null
                        ? response
                        : new TransactionalBatchResponse(
                            HttpResponseStatus.INTERNAL_SERVER_ERROR,
                            SubStatusCodes.UNKNOWN,
                            "failed to deserialize response returned by server",
                            message.getHeaders().getRequestCharge(),
                            message.getHeaders().getRetryAfter(),
                            message.getHeaders().getActivityId(),
                            message.getDiagnosticsContext(),
                            request.getOperations(),
                            serializer)
                    );
                }
            }

            if (future == null) {
                future = CompletableFuture.completedFuture(new TransactionalBatchResponse(
                    message.getStatus(),
                    message.getHeaders().getSubStatusCode(),
                    message.getErrorMessage(),
                    message.getHeaders().getRequestCharge(),
                    message.getHeaders().getRetryAfter(),
                    message.getHeaders().getActivityId(),
                    message.getDiagnosticsContext(),
                    request.getOperations(),
                    serializer));
            }

            future = future.thenApplyAsync(response -> {

                if (response.results == null || response.results.size() != request.getOperations().size()) {
                    if (message.isSuccessStatus()) {
                        // Server should be guaranteeing number of results equal to operations when
                        // batch request is successful - so fail as InternalServerError if this is not the case.
                        response = new TransactionalBatchResponse(
                            HttpResponseStatus.INTERNAL_SERVER_ERROR,
                            SubStatusCodes.UNKNOWN,
                            "received an invalid response from the server",
                            message.getHeaders().getRequestCharge(),
                            message.getHeaders().getRetryAfter(),
                            message.getHeaders().getActivityId(),
                            message.getDiagnosticsContext(),
                            request.getOperations(),
                            serializer);
                    }

                    int retryAfterMilliseconds = 0;

                    if (message.getStatus() == HttpResponseStatus.TOO_MANY_REQUESTS) {

                        // Propagate the RetryAfter into the individual operations

                        String value = message.getHeaders().get(HttpHeaders.RETRY_AFTER_IN_MILLISECONDS);

                        if (value == null) {
                            retryAfterMilliseconds = 0;
                        } else {
                            try {
                                retryAfterMilliseconds = Integer.parseInt(value);
                            } catch (NumberFormatException error) {
                                retryAfterMilliseconds = 0;
                            }
                        }
                    }

                    response.createAndPopulateResults(request.getOperations(), retryAfterMilliseconds);
                }

                return response;

            }).whenCompleteAsync((response, error) -> message.close());

            return future;

        } catch (IOException error) {
            CompletableFuture<TransactionalBatchResponse> future = new CompletableFuture<>();
            future.completeExceptionally(error);
            message.close();
            return future;
        }
    }

    /**
     * Gets the result of the operation at the provided index in the batch.
     *
     * @param index 0-based index of the operation in the batch whose result needs to be returned.
     *
     * @return Result of operation at the provided index in the batch.
     */
    @Override
    public TransactionalBatchOperationResult<?> get(int index) {
        return this.results.get(index);
    }

    /**
     * Gets the result of the operation at the provided index in the current {@link TransactionalBatchResponse batch}.
     * <p>
     * The returned result has a {@link Resource} of the provided type.
     *
     * @param <T> type to which the {@link Resource} in the operation result needs to be de-serialized, when present.
     * @param index 0-based index of the operation in the batch whose result needs to be returned.
     * @param type the class representing the type to which the {@link Resource} in the operation result needs to be
     * de-serialized, when present.
     *
     * @return the result of the batch operation that contains a  {@link Resource}  de-serialized to the specified type.
     *
     * @throws IOException if the body of the resource stream cannot be read.
     */
    @SuppressWarnings("unchecked")
    public <T> TransactionalBatchOperationResult<T> getOperationResultAtIndex(
        final int index,
        @Nonnull final Class<T> type) throws IOException {

        checkArgument(index >= 0, "expected non-negative index");
        checkNotNull(type, "expected non-null type");

        TransactionalBatchOperationResult<T> result = (TransactionalBatchOperationResult<T>) this.results.get(index);
        T resource = null;

        if (result.getResourceStream() != null) {
            resource = this.getSerializer().fromStream(result.getResourceStream(), type);
        }

        return new TransactionalBatchOperationResult<>(result, resource);
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public Iterator<TransactionalBatchOperationResult<?>> iterator() {
        return this.results.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<TransactionalBatchOperationResult<?>> listIterator() {
        return null;
    }

    @Override
    public ListIterator<TransactionalBatchOperationResult<?>> listIterator(int index) {
        return null;
    }

    @Override
    public boolean remove(Object result) {
        return this.results.remove(result);
    }

    @Override
    public TransactionalBatchOperationResult<?> remove(int index) {
        return null;
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
    public TransactionalBatchOperationResult<?> set(int index, TransactionalBatchOperationResult<?> result) {
        return this.results.set(index, result);
    }

    /**
     * Gets the number of operation results.
     */
    public int size() {
        return this.results == null ? 0 : this.results.size();
    }

    @Override
    public List<TransactionalBatchOperationResult<?>> subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public Object[] toArray() {
        return this.results.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.results.toArray(a);
    }

    private void createAndPopulateResults(List<ItemBatchOperation<?>> operations) {
        createAndPopulateResults(operations, 0);
    }

    private void createAndPopulateResults(List<ItemBatchOperation<?>> operations, int retryAfterMilliseconds) {

        TransactionalBatchOperationResult<?>[] results = new TransactionalBatchOperationResult<?>[operations.size()];

        for (int i = 0; i < operations.size(); i++) {
             results[i] = new TransactionalBatchOperationResult<>(this.getResponseStatus())
                .setSubStatusCode(this.getSubStatusCode())
                .setRetryAfter(Duration.ofMillis(retryAfterMilliseconds));
        }

        this.results = Collections.unmodifiableList(Arrays.asList(results));
    }

    @Nonnull
    private static CompletableFuture<TransactionalBatchResponse> populateFromContentAsync(
        @Nonnull final InputStream inputStream,
        @Nonnull final BatchResponseMessage message,
        @Nonnull final ServerBatchRequest request,
        @Nonnull final CosmosSerializerCore serializer,
        final boolean shouldPromoteOperationStatus) {

        ArrayList<TransactionalBatchOperationResult<?>> results = new ArrayList<>();

        // inputStream is ensured to be seekable in caller

        return RecordIOStream.readRecordIOAsync(inputStream, record -> {

            final Out<TransactionalBatchOperationResult<?>> batchOperationResult = new Out<>();
            final Result result = TransactionalBatchOperationResult.readBatchOperationResult(record, batchOperationResult);

            if (result != Result.SUCCESS) {
                return result;
            }

            results.add(batchOperationResult.get());
            return result;

        }).thenApplyAsync(result -> {

            if (result != Result.SUCCESS) {
                return null;
            }

            HttpResponseStatus status = message.getStatus();
            int subStatusCode = message.getHeaders().getSubStatusCode();

            // Promote the operation error status as the Batch response error status if we have a MultiStatus response
            // to provide users with status codes they are used to.

            if (status == HttpResponseStatus.MULTI_STATUS && shouldPromoteOperationStatus) {

                for (TransactionalBatchOperationResult<?> batchOperationResult : results) {

                    if (batchOperationResult.getStatus() != HttpResponseStatus.FAILED_DEPENDENCY) {
                        status = batchOperationResult.getStatus();
                        subStatusCode = batchOperationResult.getSubStatusCode();
                        break;
                    }
                }
            }

            TransactionalBatchResponse response = new TransactionalBatchResponse(
                status,
                subStatusCode,
                message.getErrorMessage(),
                message.getHeaders().getRequestCharge(),
                message.getHeaders().getRetryAfter(),
                message.getHeaders().getActivityId(),
                message.getDiagnosticsContext(),
                request.getOperations(),
                serializer);

            response.results = results;
            return response;
        });
    }

    // endregion
}
