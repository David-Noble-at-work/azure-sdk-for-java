// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.serializer.CosmosSerializerCore;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Handles operation queueing and dispatching.
 * <p>
 * Fills batches efficiently and maintains a timer for early dispatching in case of partially-filled batches and to
 * optimize for throughput. There is always one batch at a time being filled. Locking is in place to avoid concurrent
 * threads trying to Add operations while the timer might be Dispatching the current batch. The current batch is
 * dispatched and a new one is readied to be filled by new operations, the dispatched batch runs independently through a
 * fire and forget pattern.
 * <p>
 * {@link BatchAsyncBatcher}
 */
public class BatchAsyncStreamer implements AutoCloseable {

    private final Object dispatchLimiter = new Object();
    private final int dispatchTimerInSeconds;
    private final BatchAsyncBatcherExecuteDelegate executor;
    private final int maxBatchByteSize;
    private final int maxBatchOperationCount;
    private final BatchAsyncBatcherRetryDelegate retrier;
    private final CosmosSerializerCore serializerCore;
    private final TimerPool timerPool;
    private volatile BatchAsyncBatcher currentBatcher;
    private PooledTimer currentTimer;
    private Task timerTask;

    public BatchAsyncStreamer(
        int maxBatchOperationCount,
        int maxBatchByteSize,
        int dispatchTimerInSeconds,
        TimerPool timerPool,
        CosmosSerializerCore serializerCore,
        BatchAsyncBatcherExecuteDelegate executor,
        BatchAsyncBatcherRetryDelegate retrier) {

        checkArgument(maxBatchOperationCount > 0,
            "expected maxBatchOperationCount > 0, not %s",
            maxBatchOperationCount);

        checkArgument(maxBatchByteSize > 0,
            "expected maxBatchByteSize > 0, not %s",
            maxBatchByteSize);

        checkArgument(dispatchTimerInSeconds > 0,
            "expected dispatchTimerInSeconds > 0, not %s",
            dispatchTimerInSeconds);

        checkNotNull(executor, "expected non-null executor");
        checkNotNull(retrier, "expected non-null retrier");
        checkNotNull(serializerCore, "expected non-null serializerCore");

        this.maxBatchOperationCount = maxBatchOperationCount;
        this.maxBatchByteSize = maxBatchByteSize;
        this.executor = (PartitionKeyRangeServerBatchRequest request) -> executor.invoke(request);
        this.retrier = (ItemBatchOperation operation) -> retrier.invoke(operation);
        this.dispatchTimerInSeconds = dispatchTimerInSeconds;
        this.timerPool = timerPool;
        this.serializerCore = serializerCore;
        this.currentBatcher = this.CreateBatchAsyncBatcher();

        this.ResetTimer();
    }

    public final void Add(ItemBatchOperation operation) {
        BatchAsyncBatcher toDispatch = null;
        synchronized (this.dispatchLimiter) {
            while (!this.currentBatcher.TryAdd(operation)) {
                // Batcher is full
                toDispatch = this.GetBatchToDispatchAndCreate();
            }
        }

        if (toDispatch != null) {
            toDispatch.DispatchAsync();  // result discarded for fire and forget
        }
    }

    public final void close() throws IOException {
        this.currentTimer.CancelTimer();
        this.currentTimer = null;
        this.timerTask = null;
    }

    private BatchAsyncBatcher CreateBatchAsyncBatcher() {
        return new BatchAsyncBatcher(this.maxBatchOperationCount, this.maxBatchByteSize, this.serializerCore,
            this.executor, this.retrier);
    }

    private void DispatchTimer() {

        BatchAsyncBatcher toDispatch;

        synchronized (this.dispatchLimiter) {
            toDispatch = this.GetBatchToDispatchAndCreate();
        }

        if (toDispatch != null) {
            toDispatch.DispatchAsync();  // discarded for fire and forget
        }

        this.ResetTimer();
    }

    private BatchAsyncBatcher GetBatchToDispatchAndCreate() {

        if (this.currentBatcher.isEmpty()) {
            return null;
        }

        BatchAsyncBatcher previousBatcher = this.currentBatcher;
        this.currentBatcher = this.CreateBatchAsyncBatcher();
        return previousBatcher;
    }

    private void ResetTimer() {
        this.currentTimer = this.timerPool.GetPooledTimer(this.dispatchTimerInSeconds);
        this.timerTask = this.currentTimer.StartTimerAsync().ContinueWith((task) ->
        {
            this.DispatchTimer();
        });
    }
}
