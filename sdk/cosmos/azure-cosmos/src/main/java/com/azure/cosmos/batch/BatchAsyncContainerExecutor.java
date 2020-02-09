// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

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
    private static final int DefaultDispatchTimerInSeconds = 1;
    private static final int MinimumDispatchTimerInSeconds = 1;
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
            BatchAsyncContainerExecutor.DefaultDispatchTimerInSeconds);
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
        this.timerPool = new TimerPool(BatchAsyncContainerExecutor.MinimumDispatchTimerInSeconds);
        this.retryOptions = cosmosClientContext.ClientOptions.GetConnectionPolicy().RetryOptions;
    }


    public CompletableFuture<TransactionalBatchOperationResult> AddAsync(
        ItemBatchOperation operation,
        ItemRequestOptions itemRequestOptions) {
        return AddAsync(operation, itemRequestOptions, null);
    }

    public CompletableFuture<TransactionalBatchOperationResult> AddAsync(ItemBatchOperation operation) {
        return AddAsync(operation, null, null);
    }

    public CompletableFuture<TransactionalBatchOperationResult> AddAsync(
        @Nonnull final ItemBatchOperation operation, final ItemRequestOptions itemRequestOptions) {

        checkNotNull(operation, "expected non-null operation");

        /*await*/ this.ValidateOperationAsync(operation, itemRequestOptions);

        String resolvedPartitionKeyRangeId = /*await*/this.ResolvePartitionKeyRangeIdAsync(operation);
        BatchAsyncStreamer streamer = this.GetOrAddStreamerForPartitionKeyRange(resolvedPartitionKeyRangeId);
        ItemBatchOperationContext context = new ItemBatchOperationContext(resolvedPartitionKeyRangeId,
            BatchAsyncContainerExecutor.GetRetryPolicy(this.retryOptions));
        operation.AttachContext(context);
        streamer.Add(operation);
        return /*await*/ context.getOperationTask();
    }

    public CompletableFuture<Void> ValidateOperationAsync(ItemBatchOperation operation) {
        return ValidateOperationAsync(operation, null);
    }

    public CompletableFuture<Void> ValidateOperationAsync(ItemBatchOperation operation, ItemRequestOptions itemRequestOptions) {

        if (itemRequestOptions != null) {
            if (itemRequestOptions.BaseConsistencyLevel.HasValue || itemRequestOptions.PreTriggers != null || itemRequestOptions.PostTriggers != null || itemRequestOptions.SessionToken != null) {
                throw new IllegalStateException(ClientResources.UnsupportedBulkRequestOptions);
            }
            assert BatchAsyncContainerExecutor.ValidateOperationEPK(operation, itemRequestOptions);
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

        try (InputStream payload = serverRequest.TransferBodyStream()) {

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

    //C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
    //ORIGINAL LINE: private async Task<PartitionKeyInternal> GetPartitionKeyInternalAsync
    // (ItemBatchOperation operation, CancellationToken cancellationToken)
    private CompletableFuture<PartitionKeyInternal> GetPartitionKeyInternalAsync(ItemBatchOperation operation) {
        assert operation.getPartitionKey() != null : "expected non-null operation.partitionKey";
        if (operation.getPartitionKey().getValue().IsNone) {
            //C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
            return /*await*/ this.cosmosContainer.GetNonePartitionKeyValueAsync().ConfigureAwait(false);
        }

        return operation.getPartitionKey().getValue().InternalKey;
    }

    private static DocumentClientRetryPolicy GetRetryPolicy(RetryOptions retryOptions) {
        return new BulkPartitionKeyRangeGoneRetryPolicy(
            new ResourceThrottleRetryPolicy(
                retryOptions.getMaxRetryAttemptsOnThrottledRequests(),
                retryOptions.getMaxRetryWaitTimeInSeconds()));
    }


    private CompletableFuture<Void> ReBatchAsync(ItemBatchOperation operation) {
        String resolvedPartitionKeyRangeId = /*await*/this.ResolvePartitionKeyRangeIdAsync(operation);
        BatchAsyncStreamer streamer = this.GetOrAddStreamerForPartitionKeyRange(resolvedPartitionKeyRangeId);
        streamer.Add(operation);
    }

    private CompletableFuture<String> ResolvePartitionKeyRangeIdAsync(@Nonnull final ItemBatchOperation operation) {

        checkNotNull(operation, "expected non-null operation");

        PartitionKeyDefinition partitionKeyDefinition =/*await*/this.cosmosContainer.GetPartitionKeyDefinitionAsync();
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

    private static boolean ValidateOperationEPK(ItemBatchOperation operation, ItemRequestOptions itemRequestOptions) {
        Object epkObj;
        Object epkStrObj;
        Object pkStringObj;
        //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword - these
        // cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
        if (itemRequestOptions.Properties != null && (itemRequestOptions.Properties.TryGetValue(BackendHeaders.EFFECTIVE_PARTITION_KEY, out epkObj) | itemRequestOptions.Properties.TryGetValue(BackendHeaders.EffectivePartitionKeyString, out epkStrObj) | itemRequestOptions.Properties.TryGetValue(HttpHeaders.PartitionKey, out pkStringObj))) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: byte[] epk = epkObj instanceof byte[] ? (byte[])epkObj : null;
            byte[] epk = epkObj instanceof byte[] ? (byte[]) epkObj : null;
            String epkStr = epkStrObj instanceof String ? (String) epkStrObj : null;
            String pkString = pkStringObj instanceof String ? (String) pkStringObj : null;
            if ((epk == null && pkString == null) || epkStr == null) {
                throw new IllegalStateException(String.format(ClientResources.EpkPropertiesPairingExpected,
                    BackendHeaders.EFFECTIVE_PARTITION_KEY,
                    BackendHeaders.EFFECTIVE_PARTITION_KEY_STRING));
            }

            if (operation.getPartitionKey() != null) {
                throw new IllegalStateException(ClientResources.PKAndEpkSetTogether);
            }
        }

        return true;
    }
}
