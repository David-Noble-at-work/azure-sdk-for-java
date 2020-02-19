// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.PartitionKeyDefinition;
import com.azure.cosmos.RetryOptions;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticScope;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
import com.azure.cosmos.batch.unimplemented.RequestMessage;
import com.azure.cosmos.batch.unimplemented.ResponseMessage;
import com.azure.cosmos.implementation.DocumentClientRetryPolicy;
import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.ResourceThrottleRetryPolicy;
import com.azure.cosmos.implementation.ResourceType;
import com.azure.cosmos.implementation.directconnectivity.WFConstants.BackendHeaders;
import com.azure.cosmos.implementation.routing.CollectionRoutingMap;
import com.azure.cosmos.implementation.routing.PartitionKeyInternal;
import io.netty.util.HashedWheelTimer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import static com.azure.cosmos.BridgeInternal.getPartitionKeyInternal;
import static com.azure.cosmos.base.Preconditions.checkState;
import static com.azure.cosmos.implementation.routing.PartitionKeyInternalHelper.getEffectivePartitionKeyString;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Bulk batch executor for operations in the same container.
 * <p>
 * It maintains one {@link BatchAsyncStreamer} for each Partition Key Range, which allows independent execution of
 * requests. Semaphores are in place to rate limit the operations at the Streamer / Partition Key Range level, this
 * means that we can send parallel and independent requests to different Partition Key Ranges, but for the same Range,
 * requests will be limited. Two delegate implementations define how a particular request should be executed, and how
 * operations should be retried. When the {@link BatchAsyncStreamer} dispatches a batch, the batch will create a request
 * and call the execute delegate, if conditions are met, it might call the retry delegate.
 * <p>
 * {@link BatchAsyncStreamer}
 */
public class BatchAsyncContainerExecutor implements AutoCloseable {

    private static final int DEFAULT_DISPATCH_TIMER_IN_SECONDS = 1;
    private static final int MINIMUM_DISPATCH_TIMER_IN_SECONDS = 1;

    private final HashedWheelTimer timer = new HashedWheelTimer();
    private final ConcurrentHashMap<String, Semaphore> limiters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BatchAsyncStreamer> streamers = new ConcurrentHashMap<>();

    private final CosmosClientContext clientContext;
    private final CosmosAsyncContainer container;
    private final int dispatchTimerInSeconds;
    private final int maxOperationCount;
    private final int maxPayloadLength;
    private final RetryOptions retryOptions;

    public BatchAsyncContainerExecutor(
        @Nonnull final CosmosClientContext clientContext,
        @Nonnull final CosmosAsyncContainer container,
        final int maxOperationCount,
        final int maxPayloadLength) {

        this(
            clientContext, container,
            maxOperationCount,
            maxPayloadLength,
            BatchAsyncContainerExecutor.DEFAULT_DISPATCH_TIMER_IN_SECONDS);
    }

    public BatchAsyncContainerExecutor(
        @Nonnull final CosmosClientContext clientContext,
        @Nonnull final CosmosAsyncContainer container,
        final int maxOperationCount,
        final int maxPayloadLength,
        final int dispatchTimerInSeconds) {

        checkNotNull(clientContext, "expected non-null clientContext");
        checkNotNull(container, "expected non-null containerCore");

        checkArgument(maxOperationCount > 0,
            "expected maxServerRequestOperationCount > 0, not %s",
            maxOperationCount);

        checkArgument(maxPayloadLength > 0,
            "expected maxServerRequestBodyLength > 0, not %s",
            maxPayloadLength);

        checkArgument(dispatchTimerInSeconds > 0,
            "expected dispatchTimerInSeconds > 0, not %s",
            dispatchTimerInSeconds);

        this.clientContext = clientContext;
        this.container = container;
        this.maxOperationCount = maxOperationCount;
        this.maxPayloadLength = maxPayloadLength;
        this.dispatchTimerInSeconds = dispatchTimerInSeconds;
        this.retryOptions = clientContext.getConnectionPolicy().getRetryOptions();
    }


    public CompletableFuture<TransactionalBatchOperationResult> addAsync(ItemBatchOperation operation) {
        return this.addAsync(operation, null);
    }

    public CompletableFuture<TransactionalBatchOperationResult> addAsync(
        @Nonnull final ItemBatchOperation operation,
        @Nullable final RequestOptions options) {

        checkNotNull(operation, "expected non-null operation");

        return this.validateOperationAsync(operation, options).thenComposeAsync((Void result) ->

            this.ResolvePartitionKeyRangeIdAsync(operation)

        ).thenComposeAsync((String resolvedPartitionKeyRangeId) -> {

            final BatchAsyncStreamer streamer = this.getOrAddStreamerForPartitionKeyRange(resolvedPartitionKeyRangeId);

            final ItemBatchOperationContext context = new ItemBatchOperationContext(
                resolvedPartitionKeyRangeId,
                BatchAsyncContainerExecutor.GetRetryPolicy(this.retryOptions));

            operation.attachContext(context);
            streamer.add(operation);
            return context.getOperationTask();
        });
    }

    /**
     * Closes the current {@link BatchAsyncContainerExecutor async batch executor}.
     *
     * @throws IOException if an input/output error occurs while closing any of the underlying {@link BatchAsyncStreamer
     * streamer} instances.
     */
    public final void close() throws IOException {

        IOException aggregateException = null;

        for (BatchAsyncStreamer streamer : this.streamers.values()) {
            try {
                streamer.close();
            } catch (IOException error) {
                if (aggregateException == null) {
                    aggregateException = new IOException("close failed because one or more streamers failed to close");
                }
                aggregateException.addSuppressed(error);
            }
        }

        this.timer.stop();
        this.limiters.clear();
        this.streamers.clear();

        if (aggregateException != null) {
            throw aggregateException;
        }
    }

    public CompletableFuture<Void> validateOperationAsync(
        @Nonnull final ItemBatchOperation<?> operation,
        @Nullable final RequestOptions options) {

        if (options != null) {
            checkState(options.getConsistencyLevel() == null
                && options.getPostTriggerInclude() == null
                && options.getPreTriggerInclude() == null
                && options.getSessionToken() == null);
            checkState(BatchAsyncContainerExecutor.ValidateOperationEPK(operation, options));
        }

        return operation.materializeResource(this.clientContext.getSerializerCore());
    }

    public CompletableFuture<Void> validateOperationAsync(ItemBatchOperation operation) {
        return validateOperationAsync(operation, null);
    }

    private static void AddHeadersToRequestMessage(RequestMessage requestMessage, String partitionKeyRangeId) {
        requestMessage.getHeaders().PartitionKeyRangeId = partitionKeyRangeId;
        requestMessage.getHeaders().Add(HttpHeaders.SHOULD_BATCH_CONTINUE_ON_ERROR, Boolean.TRUE.toString());
        requestMessage.getHeaders().Add(HttpHeaders.IS_BATCH_REQUEST, Boolean.TRUE.toString());
    }

    private CompletableFuture<PartitionKeyRangeBatchExecutionResult> ExecuteAsync(
        PartitionKeyRangeServerBatchRequest serverRequest) {

        CompletableFuture<PartitionKeyRangeBatchExecutionResult> future = new CompletableFuture<>();

        CosmosDiagnosticsContext diagnosticsContext = new CosmosDiagnosticsContext();
        Semaphore limiter = this.getOrAddLimiterForPartitionKeyRange(serverRequest.getPartitionKeyRangeId());

        try (CosmosDiagnosticScope scope = diagnosticsContext.createScope("BatchAsyncContainerExecutor.Limiter")) {
            limiter.acquire();
        } catch (InterruptedException error) {
            future.completeExceptionally(error);
            return future;
        }

        try (InputStream payload = serverRequest.transferBodyStream()) {

            assert payload != null : "expected non-null serverRequestPayload";

            ResponseMessage responseMessage = /*await*/ this.clientContext.processResourceOperationStreamAsync(
                this.container.LinkUri,
                ResourceType.Document,
                OperationType.Batch,
                new RequestOptions(),
                this.container,
                null,
                payload,
                requestMessage -> BatchAsyncContainerExecutor.AddHeadersToRequestMessage(
                    requestMessage,
                    serverRequest.getPartitionKeyRangeId()),
                diagnosticsContext);

            try (CosmosDiagnosticScope scope = diagnosticsContext.createScope("BatchAsyncContainerExecutor.ToResponse")) {
                TransactionalBatchResponse serverResponse = /*await*/TransactionalBatchResponse.fromResponseMessageAsync(responseMessage, serverRequest, this.clientContext.SerializerCore).ConfigureAwait(false);
                return new PartitionKeyRangeBatchExecutionResult(serverRequest.getPartitionKeyRangeId(), serverRequest.getOperations(), serverResponse);
            }

        } catch (IOException error) {
            future.completeExceptionally(error);
        } finally {
            limiter.release();
        }

        return future;
    }

    private static DocumentClientRetryPolicy GetRetryPolicy(RetryOptions retryOptions) {
        return new BulkPartitionKeyRangeGoneRetryPolicy(
            new ResourceThrottleRetryPolicy(
                retryOptions.getMaxRetryAttemptsOnThrottledRequests(),
                retryOptions.getMaxRetryWaitTimeInSeconds()));
    }

    private CompletableFuture<Void> ReBatchAsync(ItemBatchOperation operation) {
        return this.ResolvePartitionKeyRangeIdAsync(operation).thenApplyAsync((String resolvedPartitionKeyRangeId) -> {
            BatchAsyncStreamer streamer = this.getOrAddStreamerForPartitionKeyRange(resolvedPartitionKeyRangeId);
            streamer.add(operation);
            return null;
        });
    }

    private CompletableFuture<String> ResolvePartitionKeyRangeIdAsync(@Nonnull final ItemBatchOperation operation) {

        checkNotNull(operation, "expected non-null operation");

        PartitionKeyDefinition partitionKeyDefinition = /*await*/ this.container.GetPartitionKeyDefinitionAsync();
        CollectionRoutingMap collectionRoutingMap = /*await*/ this.container.GetRoutingMapAsync();
        final RequestOptions options = operation.getRequestOptions();
        byte[] effectivePartitionKey = null;

        if (options != null) {

            final Map<String, Object> properties = options.getProperties();

            if (properties != null) {
                effectivePartitionKey = (byte[]) properties.computeIfPresent(
                    BackendHeaders.EFFECTIVE_PARTITION_KEY_STRING,
                    (k, v) -> v instanceof byte[] ? v : null);
            }
        }

        checkState(effectivePartitionKey != null, "EPK is not supported");

        return this.getPartitionKeyInternalAsync(operation).thenApplyAsync(partitionKeyInternal -> {

            operation.setPartitionKeyJson(partitionKeyInternal.toJson());

            return collectionRoutingMap.getRangeByEffectivePartitionKey(getEffectivePartitionKeyString(
                partitionKeyInternal,
                partitionKeyDefinition)
            ).getId();
        });
    }

    private static boolean ValidateOperationEPK(
        @Nonnull final ItemBatchOperation operation,
        @Nonnull final RequestOptions options) {

        checkNotNull(operation, "expected non-null operation");
        checkNotNull(options, "expected non-null options");

        final Map<String, Object> properties = options.getProperties();

        if (properties == null) {
            return true;
        }

        byte[] epk = (byte[]) properties.computeIfPresent(BackendHeaders.EFFECTIVE_PARTITION_KEY,
            (k, v) -> v instanceof byte[] ? v : null);

        String epkString = (String) properties.computeIfPresent(BackendHeaders.EFFECTIVE_PARTITION_KEY_STRING,
            (k, v) -> v.getClass()  == String.class ? v : null);

        String pk = (String) properties.computeIfPresent(HttpHeaders.PARTITION_KEY,
            (k, v) -> v.getClass() == String.class ? v : null);

        checkState(epkString != null || epk != null || pk != null,
            "expected byte array value for {0} and string value for {1} when either property is set",
            BackendHeaders.EFFECTIVE_PARTITION_KEY,
            BackendHeaders.EFFECTIVE_PARTITION_KEY_STRING);

        checkState(operation.getPartitionKey() == null,
            "partition key and effective partition key may not both be set.");

        return true;
    }

    private Semaphore getOrAddLimiterForPartitionKeyRange(String partitionKeyRangeId) {
        return this.limiters.computeIfAbsent(partitionKeyRangeId, id -> {
            Semaphore semaphore = new Semaphore(1);
            semaphore.tryAcquire();
            return semaphore;
        });
    }

    private BatchAsyncStreamer getOrAddStreamerForPartitionKeyRange(String partitionKeyRangeId) {

        return this.streamers.computeIfAbsent(partitionKeyRangeId, id -> new BatchAsyncStreamer(
            this.maxOperationCount,
            this.maxPayloadLength,
            this.dispatchTimerInSeconds,
            this.timer,
            this.clientContext.getSerializerCore(),
            this::ExecuteAsync,
            this::ReBatchAsync));
    }

    private CompletableFuture<PartitionKeyInternal> getPartitionKeyInternalAsync(
        @Nonnull final ItemBatchOperation operation) {

        checkNotNull(operation, "expected non-null operation");

        PartitionKeyInternal partitionKeyInternal = getPartitionKeyInternal(Objects.requireNonNull(
            operation.getPartitionKey(),
            "expected non-null operation partition key"));

        if (partitionKeyInternal != null) {
            return CompletableFuture.completedFuture(partitionKeyInternal);
        }

        return /*await*/ this.container.GetNonePartitionKeyValueAsync();
    }
}
