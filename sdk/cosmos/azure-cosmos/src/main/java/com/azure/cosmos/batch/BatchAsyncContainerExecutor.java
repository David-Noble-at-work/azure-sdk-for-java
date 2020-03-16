// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.BridgeInternal;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosBridgeInternal;
import com.azure.cosmos.ThrottlingRetryOptions;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticScope;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
import com.azure.cosmos.core.Out;
import com.azure.cosmos.implementation.AsyncDocumentClient;
import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.ResourceThrottleRetryPolicy;
import com.azure.cosmos.implementation.ResourceType;
import com.azure.cosmos.implementation.directconnectivity.WFConstants.BackendHeaders;
import com.azure.cosmos.implementation.routing.PartitionKeyInternal;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.PartitionKeyDefinition;
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
import static com.azure.cosmos.batch.TransactionalBatchResponse.fromResponseMessageAsync;
import static com.azure.cosmos.implementation.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.base.Preconditions.checkState;
import static com.azure.cosmos.implementation.routing.PartitionKeyInternalHelper.getEffectivePartitionKeyString;

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
@SuppressWarnings("try")  // the resources this class closes may throw an InterruptedException and we cannot prevent it
public class BatchAsyncContainerExecutor implements AutoCloseable {

    private static final int DEFAULT_DISPATCH_TIMER_IN_SECONDS = 1;

    private final HashedWheelTimer timer = new HashedWheelTimer();
    private final ConcurrentHashMap<String, Semaphore> limiters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BatchAsyncStreamer> streamers = new ConcurrentHashMap<>();

    private final CosmosAsyncClientContext clientContext;
    private final CosmosAsyncContainer container;
    private final int dispatchTimerInSeconds;
    private final int maxOperationCount;
    private final int maxPayloadLength;
    private final ThrottlingRetryOptions throttlingRetryOptions;

    public BatchAsyncContainerExecutor(
        @Nonnull final CosmosAsyncClientContext clientContext,
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
        @Nonnull final CosmosAsyncClientContext clientContext,
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
        this.throttlingRetryOptions = clientContext.getConnectionPolicy().getThrottlingRetryOptions();
    }


    public CompletableFuture<TransactionalBatchOperationResult<?>> addAsync(ItemBatchOperation<?> operation) {
        return this.addAsync(operation, null);
    }

    public CompletableFuture<TransactionalBatchOperationResult<?>> addAsync(
        @Nonnull final ItemBatchOperation<?> operation,
        @Nullable final RequestOptions options) {

        checkNotNull(operation, "expected non-null operation");

        return this.validateOperationAsync(operation, options).thenComposeAsync(
            (Void result) -> this.resolvePartitionKeyRangeIdAsync(operation)

        ).thenComposeAsync((String resolvedPartitionKeyRangeId) -> {

            final BatchAsyncStreamer streamer = this.getOrAddStreamerForPartitionKeyRange(resolvedPartitionKeyRangeId);

            // TODO (DANOBLE) create a future to put into the ItemBatchOperationContext; one that is completed when this
            //  batch operation completes

            final ItemBatchOperationContext context = new ItemBatchOperationContext(
                resolvedPartitionKeyRangeId,
                BatchAsyncContainerExecutor.getRetryPolicy(this.throttlingRetryOptions));

            operation.attachContext(context);
            streamer.add(operation);

            return context.getOperationResultFuture();
        });
    }

    /**
     * Closes the current {@link BatchAsyncContainerExecutor async batch executor}.
     */
    public final void close() {

        for (BatchAsyncStreamer streamer : this.streamers.values()) {
            streamer.close();
        }

        this.timer.stop();
        this.limiters.clear();
        this.streamers.clear();
    }

    public CompletableFuture<Void> validateOperationAsync(
        @Nonnull final ItemBatchOperation<?> operation,
        @Nullable final RequestOptions options) {

        if (options != null) {
            checkState(options.getConsistencyLevel() == null
                && options.getPostTriggerInclude() == null
                && options.getPreTriggerInclude() == null
                && options.getSessionToken() == null);
            checkState(BatchAsyncContainerExecutor.validateOperationEpk(operation, options));
        }

        return operation.materializeResource(this.clientContext.getSerializerCore());
    }

    public CompletableFuture<Void> validateOperationAsync(ItemBatchOperation<?> operation) {
        return validateOperationAsync(operation, null);
    }

    // region Privates

    private static void addHeadersToRequestMessage(BatchRequestMessage requestMessage, String partitionKeyRangeId) {
        requestMessage.getHeaders().setPartitionKeyRangeId(partitionKeyRangeId);
        requestMessage.getHeaders().set(HttpHeaders.IS_BATCH_REQUEST, Boolean.TRUE);
        requestMessage.getHeaders().set(HttpHeaders.SHOULD_BATCH_CONTINUE_ON_ERROR, Boolean.TRUE);
    }

    private CompletableFuture<PartitionKeyRangeBatchExecutionResult> ExecuteAsync(
        PartitionKeyRangeServerBatchRequest request) {

        final Semaphore limiter = this.getOrAddLimiterForPartitionKeyRange(request.getPartitionKeyRangeId());
        final CosmosDiagnosticsContext diagnosticsContext = new CosmosDiagnosticsContext();

        try (CosmosDiagnosticScope ignored = diagnosticsContext.createScope("BatchAsyncContainerExecutor.Limiter")) {
            limiter.acquire();
        } catch (InterruptedException error) {
            CompletableFuture<PartitionKeyRangeBatchExecutionResult> future = new CompletableFuture<>();
            future.completeExceptionally(error);
            return future;
        }

        final InputStream payload = Objects.requireNonNull(request.transferBodyStream(), "expected non-null payload");
        final Out<CosmosDiagnosticScope> scope = new Out<>();

        return this.clientContext.processResourceOperationStreamAsync(
            this.container.getLink(),
            ResourceType.Document,
            OperationType.Batch,
            new RequestOptions(),
            this.container,
            null,
            payload,
            requestMessage -> BatchAsyncContainerExecutor.addHeadersToRequestMessage(
                requestMessage,
                request.getPartitionKeyRangeId()),
            diagnosticsContext

        ).thenApplyAsync(responseMessage -> {
            scope.set(diagnosticsContext.createScope("BatchAsyncContainerExecutor.ToResponse"));
            return responseMessage;

        }).thenComposeAsync(responseMessage ->
            fromResponseMessageAsync(responseMessage, request, this.clientContext.getSerializerCore())

        ).thenApplyAsync(response -> new PartitionKeyRangeBatchExecutionResult(
            request.getPartitionKeyRangeId(),
            request.getOperations(),
            response)

        ).whenCompleteAsync((result, error) -> {
            scope.ifPresent(CosmosDiagnosticScope::close);
            try {
                payload.close();
            } catch (IOException cause) {
                error.addSuppressed(cause);
            }
            limiter.release();
        });
    }

    private static BatchPartitionKeyRangeGoneRetryPolicy getRetryPolicy(ThrottlingRetryOptions throttlingRetryOptions) {
        return new BatchPartitionKeyRangeGoneRetryPolicy(
            new ResourceThrottleRetryPolicy(
                throttlingRetryOptions.getMaxRetryAttemptsOnThrottledRequests(),
                throttlingRetryOptions.getMaxRetryWaitTime()));
    }

    private CompletableFuture<Void> ReBatchAsync(ItemBatchOperation<?> operation) {
        return this.resolvePartitionKeyRangeIdAsync(operation).thenApplyAsync((String resolvedPartitionKeyRangeId) -> {
            BatchAsyncStreamer streamer = this.getOrAddStreamerForPartitionKeyRange(resolvedPartitionKeyRangeId);
            streamer.add(operation);
            return null;
        });
    }

    private CompletableFuture<String> resolvePartitionKeyRangeIdAsync(@Nonnull final ItemBatchOperation<?> operation) {

        checkNotNull(operation, "expected non-null operation");

        final AsyncDocumentClient client = CosmosBridgeInternal.getAsyncDocumentClient(this.clientContext.getClient());
        final PartitionKeyDefinition definition = this.container.getProperties().getPartitionKeyDefinition();

        final CompletableFuture<String> future = new CompletableFuture<>();
        final RequestOptions options = operation.getRequestOptions();
        final byte[] effectivePartitionKey;

        if (options == null) {
            effectivePartitionKey = null;
        } else {
            final Map<String, Object> properties = options.getProperties();
            effectivePartitionKey = properties != null
                ? (byte[]) properties.computeIfPresent(
                    BackendHeaders.EFFECTIVE_PARTITION_KEY_STRING,
                    (k, v) -> v instanceof byte[] ? v : null)
                : null;
        }

        if (effectivePartitionKey == null) {
            future.completeExceptionally(new IllegalStateException("expected non-null effectivePartitionKey"));
            return future;
        }

        final String resourceId = this.container.getProperties().getResourceId();

        client.getPartitionKeyRangeCache().getRoutingMapForCollectionAsync(resourceId, null, null)
            .subscribe(routingMap ->
                this.getPartitionKeyAsync(operation)
                    .thenApplyAsync(partitionKey -> {
                        operation.setPartitionKeyJson(partitionKey.toJsonString());
                        return routingMap.getRangeByEffectivePartitionKey(getEffectivePartitionKeyString(
                            BridgeInternal.getPartitionKeyInternal(partitionKey),
                            definition)
                        ).getId();
                    })
                    .whenCompleteAsync((String id, Throwable error) -> {
                        if (error != null) {
                            future.completeExceptionally(error);
                        }
                        future.complete(id);
                    }));

        return future;
    }

    private static boolean validateOperationEpk(
        @Nonnull final ItemBatchOperation<?> operation,
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

    private CompletableFuture<PartitionKey> getPartitionKeyAsync(
        @Nonnull final ItemBatchOperation<?> operation) {

        checkNotNull(operation, "expected non-null operation");

        PartitionKeyInternal partitionKeyInternal = getPartitionKeyInternal(Objects.requireNonNull(
            operation.getPartitionKey(),
            "expected non-null operation partition key"));

        if (partitionKeyInternal != null) {
            return CompletableFuture.completedFuture(new PartitionKey(partitionKeyInternal));
        }

        // TODO (DANOBLE) Gain access to the None partition key value.
        //  My reading of the code indicates that we might:
        //  * replace BatchAsyncContainerExecutor.container with BatchAsyncContainerExecutor.containerResponse or
        //  * update CosmosAsyncContainerResponse such that it creates its CosmosAsyncContainer with the
        //    CosmosContainerProperties that it holds.
        //  I'm assuming other options might exist and might be better. For now I have updated
        //  CosmosAsyncContainerResponse such that it creates its CosmosAsyncContainer with the
        //  CosmosContainerProperties that it holds.

        PartitionKeyDefinition definition = this.container.getProperties().getPartitionKeyDefinition();
        return CompletableFuture.completedFuture(definition.getNoneValue());
    }

    // endregion
}
