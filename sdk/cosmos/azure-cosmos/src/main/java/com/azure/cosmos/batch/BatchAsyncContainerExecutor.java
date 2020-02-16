// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.BridgeInternal;
import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.PartitionKeyDefinition;
import com.azure.cosmos.RetryOptions;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticScope;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
import com.azure.cosmos.implementation.DocumentClientRetryPolicy;
import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.ResourceThrottleRetryPolicy;
import com.azure.cosmos.implementation.ResourceType;
import com.azure.cosmos.implementation.directconnectivity.WFConstants.BackendHeaders;
import com.azure.cosmos.implementation.routing.CollectionRoutingMap;
import com.azure.cosmos.implementation.routing.PartitionKeyInternal;
import com.azure.cosmos.implementation.routing.PartitionKeyInternalHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import static com.azure.cosmos.base.Preconditions.checkState;
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

    private final ConcurrentHashMap<String, Semaphore> limitersByPartitionkeyRange = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BatchAsyncStreamer> streamersByPartitionKeyRange = new ConcurrentHashMap<>();

    private CosmosClientContext cosmosClientContext;
    private ContainerCore cosmosContainer;
    private int dispatchTimerInSeconds;
    private int maxServerRequestBodyLength;
    private int maxServerRequestOperationCount;
    private RetryOptions retryOptions;
    private TimerPool timerPool;

    /**
     * For unit testing.
     */
    public BatchAsyncContainerExecutor() {
    }


    public BatchAsyncContainerExecutor(
        ContainerCore cosmosContainer,
        CosmosClientContext cosmosClientContext,
        int maxServerRequestOperationCount,
        int maxServerRequestBodyLength) {

        this(
            cosmosContainer,
            cosmosClientContext,
            maxServerRequestOperationCount,
            maxServerRequestBodyLength,
            BatchAsyncContainerExecutor.DEFAULT_DISPATCH_TIMER_IN_SECONDS);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public BatchAsyncContainerExecutor(ContainerCore cosmosContainer, CosmosClientContext
    // cosmosClientContext, int maxServerRequestOperationCount, int maxServerRequestBodyLength, int
    // dispatchTimerInSeconds = BatchAsyncContainerExecutor.DefaultDispatchTimerInSeconds)
    public BatchAsyncContainerExecutor(
        ContainerCore cosmosContainer,
        @Nonnull final CosmosClientContext cosmosClientContext,
        int maxServerRequestOperationCount,
        int maxServerRequestBodyLength,
        int dispatchTimerInSeconds) {

        checkNotNull(cosmosClientContext, "expected non-null cosmosClientContext");

        checkArgument(maxServerRequestOperationCount > 0,
            "expected maxServerRequestOperationCount > 0, not %s",
            maxServerRequestOperationCount);

        checkArgument(maxServerRequestBodyLength > 0,
            "expected maxServerRequestBodyLength > 0, not %s",
            maxServerRequestBodyLength);

        checkArgument(dispatchTimerInSeconds > 0,
            "expected dispatchTimerInSeconds > 0, not %s",
            dispatchTimerInSeconds);

        this.cosmosContainer = cosmosContainer;
        this.cosmosClientContext = cosmosClientContext;
        this.maxServerRequestBodyLength = maxServerRequestBodyLength;
        this.maxServerRequestOperationCount = maxServerRequestOperationCount;
        this.dispatchTimerInSeconds = dispatchTimerInSeconds;
        this.timerPool = new TimerPool(BatchAsyncContainerExecutor.MINIMUM_DISPATCH_TIMER_IN_SECONDS);
        this.retryOptions = cosmosClientContext.ClientOptions.GetConnectionPolicy().RetryOptions;
    }


    public CompletableFuture<TransactionalBatchOperationResult> AddAsync(ItemBatchOperation operation) {
        return AddAsync(operation, null, null);
    }

    public CompletableFuture<TransactionalBatchOperationResult> AddAsync(
        @Nonnull final ItemBatchOperation operation, @Nullable final RequestOptions options) {

        checkNotNull(operation, "expected non-null operation");

        /*await*/ this.ValidateOperationAsync(operation, options);

        final String resolvedPartitionKeyRangeId = /*await*/ this.ResolvePartitionKeyRangeIdAsync(operation);
        final BatchAsyncStreamer streamer = this.GetOrAddStreamerForPartitionKeyRange(resolvedPartitionKeyRangeId);

        final ItemBatchOperationContext context = new ItemBatchOperationContext(
            resolvedPartitionKeyRangeId,
            BatchAsyncContainerExecutor.GetRetryPolicy(this.retryOptions));

        operation.AttachContext(context);
        streamer.Add(operation);
        return /*await*/ context.getOperationTask();
    }

    public CompletableFuture<Void> ValidateOperationAsync(ItemBatchOperation operation) {
        return ValidateOperationAsync(operation, null);
    }

    public CompletableFuture<Void> ValidateOperationAsync(
        @Nonnull final ItemBatchOperation operation,
        @Nullable final RequestOptions options) {

        if (options != null) {
            checkState(options.getConsistencyLevel() == null
                && options.getPostTriggerInclude() == null
                && options.getPreTriggerInclude() == null
                && options.getSessionToken() == null);
            checkState(BatchAsyncContainerExecutor.ValidateOperationEPK(operation, options));
        }

        /*await*/ operation.materializeResource(this.cosmosClientContext.SerializerCore);
    }

    public final void close() throws IOException {

        IOException aggregateException = null;

        for (BatchAsyncStreamer streamer : this.streamersByPartitionKeyRange.values()) {
            try {
                streamer.close();
            } catch (IOException error) {
                if (aggregateException == null) {
                    aggregateException = new IOException("close failed because one or more streamers failed to close");
                }
                aggregateException.addSuppressed(error);
            }
        }

        this.timerPool.Dispose();
        this.limitersByPartitionkeyRange.clear();
        this.streamersByPartitionKeyRange.clear();

        if (aggregateException != null) {
            throw aggregateException;
        }
    }

    private static void AddHeadersToRequestMessage(RequestMessage requestMessage, String partitionKeyRangeId) {
        requestMessage.Headers.PartitionKeyRangeId = partitionKeyRangeId;
        requestMessage.Headers.Add(HttpHeaders.SHOULD_BATCH_CONTINUE_ON_ERROR, Boolean.TRUE.toString());
        requestMessage.Headers.Add(HttpHeaders.IS_BATCH_REQUEST, Boolean.TRUE.toString());
    }

    private CompletableFuture<PartitionKeyRangeBatchExecutionResult> ExecuteAsync(
        PartitionKeyRangeServerBatchRequest serverRequest) {

        CompletableFuture<PartitionKeyRangeBatchExecutionResult> future = new CompletableFuture<>();

        CosmosDiagnosticsContext diagnosticsContext = new CosmosDiagnosticsContext();
        Semaphore limiter = this.getOrAddLimiterForPartitionKeyRange(serverRequest.getPartitionKeyRangeId());

        try (CosmosDiagnosticScope scope = diagnosticsContext.CreateScope("BatchAsyncContainerExecutor.Limiter")) {
            limiter.acquire();
        } catch (InterruptedException error) {
            future.completeExceptionally(error);
            return future;
        }

        try (InputStream payload = serverRequest.transferBodyStream()) {

            assert payload != null : "expected non-null serverRequestPayload";

            ResponseMessage responseMessage = /*await*/this.cosmosClientContext.ProcessResourceOperationStreamAsync(
                this.cosmosContainer.LinkUri,
                ResourceType.Document,
                OperationType.Batch,
                new RequestOptions(),
                this.cosmosContainer,
                null,
                payload,
                requestMessage -> BatchAsyncContainerExecutor.AddHeadersToRequestMessage(
                    requestMessage,
                    serverRequest.getPartitionKeyRangeId()),
                diagnosticsContext);

            try (CosmosDiagnosticScope scope = diagnosticsContext.CreateScope("BatchAsyncContainerExecutor.ToResponse")) {
                TransactionalBatchResponse serverResponse = /*await*/TransactionalBatchResponse.FromResponseMessageAsync(responseMessage, serverRequest, this.cosmosClientContext.SerializerCore).ConfigureAwait(false);
                return new PartitionKeyRangeBatchExecutionResult(serverRequest.getPartitionKeyRangeId(), serverRequest.getOperations(), serverResponse);
            }

        } catch (IOException error) {
            future.completeExceptionally(error);
        } finally {
            limiter.release();
        }

        return future;
    }

    private Semaphore getOrAddLimiterForPartitionKeyRange(String partitionKeyRangeId) {
        return this.limitersByPartitionkeyRange.computeIfAbsent(partitionKeyRangeId, id -> {
            Semaphore semaphore = new Semaphore(1);
            semaphore.tryAcquire();
            return semaphore;
        });
    }

    private BatchAsyncStreamer GetOrAddStreamerForPartitionKeyRange(String partitionKeyRangeId) {

        return this.streamersByPartitionKeyRange.computeIfAbsent(partitionKeyRangeId, id -> new BatchAsyncStreamer(
            this.maxServerRequestOperationCount,
            this.maxServerRequestBodyLength,
            this.dispatchTimerInSeconds,
            this.timerPool,
            this.cosmosClientContext.SerializerCore,
            this::ExecuteAsync,
            this::ReBatchAsync));
    }

    private CompletableFuture<PartitionKeyInternal> GetPartitionKeyInternalAsync(
        @Nonnull final ItemBatchOperation operation) {

        checkNotNull(operation, "expected non-null operation");
        PartitionKey partitionKey = operation.getPartitionKey();
        checkNotNull(partitionKey, "expected non-null operation partition key");
        PartitionKeyInternal partitionKeyInternal = BridgeInternal.getPartitionKeyInternal(partitionKey);

        if (partitionKeyInternal == null) {
            return /*await*/ this.cosmosContainer.GetNonePartitionKeyValueAsync();
        }

        return CompletableFuture.completedFuture(partitionKeyInternal);
    }

    private static DocumentClientRetryPolicy GetRetryPolicy(RetryOptions retryOptions) {
        return new BulkPartitionKeyRangeGoneRetryPolicy(
            new ResourceThrottleRetryPolicy(
                retryOptions.getMaxRetryAttemptsOnThrottledRequests(),
                retryOptions.getMaxRetryWaitTimeInSeconds()));
    }


    private CompletableFuture<Void> ReBatchAsync(ItemBatchOperation operation) {
        String resolvedPartitionKeyRangeId = /*await*/ this.ResolvePartitionKeyRangeIdAsync(operation);
        BatchAsyncStreamer streamer = this.GetOrAddStreamerForPartitionKeyRange(resolvedPartitionKeyRangeId);
        streamer.Add(operation);
    }

    private CompletableFuture<String> ResolvePartitionKeyRangeIdAsync(@Nonnull final ItemBatchOperation operation) {

        checkNotNull(operation, "expected non-null operation");

        PartitionKeyDefinition partitionKeyDefinition = /*await*/ this.cosmosContainer.GetPartitionKeyDefinitionAsync();
        CollectionRoutingMap collectionRoutingMap = /*await*/ this.cosmosContainer.GetRoutingMapAsync();
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

        PartitionKeyInternal partitionKeyInternal = /*await*/ this.GetPartitionKeyInternalAsync(operation);
        operation.setPartitionKeyJson(partitionKeyInternal.toJson());

        String effectivePartitionKeyString = PartitionKeyInternalHelper.getEffectivePartitionKeyString(
            partitionKeyInternal,
            partitionKeyDefinition);

        return collectionRoutingMap.getRangeByEffectivePartitionKey(effectivePartitionKeyString).getId();
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
}
