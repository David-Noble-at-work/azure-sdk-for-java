// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.batch.serializer.CosmosSerializerCore;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

import static com.azure.cosmos.implementation.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

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
    private final int dispatchTimerInNanos;
    private final BatchAsyncBatcherExecutor executor;
    private final int maxBatchByteSize;
    private final int maxBatchOperationCount;
    private final BatchAsyncBatcherRetrier retrier;
    private final CosmosSerializerCore serializerCore;
    private final HashedWheelTimer timer;

    private volatile BatchAsyncBatcher currentBatcher;
    private volatile Timeout currentTimeout;

    public BatchAsyncStreamer(
        final int maxBatchOperationCount,
        final int maxBatchByteSize,
        final int dispatchTimerInSeconds,
        @Nonnull final HashedWheelTimer timer,
        @Nonnull final CosmosSerializerCore serializerCore,
        @Nonnull final BatchAsyncBatcherExecutor executor,
        @Nonnull final BatchAsyncBatcherRetrier retrier) {

        checkArgument(maxBatchOperationCount > 0,
            "expected maxBatchOperationCount > 0, not %s",
            maxBatchOperationCount);

        checkArgument(maxBatchByteSize > 0,
            "expected maxBatchByteSize > 0, not %s",
            maxBatchByteSize);

        checkArgument(dispatchTimerInSeconds > 0,
            "expected dispatchTimerInSeconds > 0, not %s",
            dispatchTimerInSeconds);

        checkNotNull(timer, "expected non-null timer");
        checkNotNull(serializerCore, "expected non-null serializerCore");
        checkNotNull(executor, "expected non-null executor");
        checkNotNull(retrier, "expected non-null retrier");

        this.dispatchTimerInNanos = 1_000_000_000 * dispatchTimerInSeconds;
        this.executor = executor;
        this.maxBatchByteSize = maxBatchByteSize;
        this.maxBatchOperationCount = maxBatchOperationCount;
        this.retrier = retrier;
        this.serializerCore = serializerCore;
        this.timer = timer;

        this.currentBatcher = this.createBatchAsyncBatcher();
        this.resetTimer();
    }

    public final void add(ItemBatchOperation<?> operation) {

        BatchAsyncBatcher toDispatch = null;

        synchronized (this.dispatchLimiter) {
            while (!this.currentBatcher.tryAdd(operation)) {
                // Batcher is full
                toDispatch = this.getBatchToDispatchAndCreate();
            }
        }

        if (toDispatch != null) {
            toDispatch.dispatchAsync();  // result discarded for fire and forget
        }
    }

    public final void close() {
        this.currentTimeout.cancel();
        this.currentTimeout = null;
    }

    // region Privates

    private BatchAsyncBatcher createBatchAsyncBatcher() {
        return new BatchAsyncBatcher(
            this.maxBatchOperationCount,
            this.maxBatchByteSize,
            this.serializerCore,
            this.executor,
            this.retrier);
    }

    private void dispatchTimer() {

        final BatchAsyncBatcher toDispatch;

        synchronized (this.dispatchLimiter) {
            toDispatch = this.getBatchToDispatchAndCreate();
        }

        if (toDispatch != null) {
            toDispatch.dispatchAsync();  // discarded for fire and forget
        }
    }

    private BatchAsyncBatcher getBatchToDispatchAndCreate() {

        if (this.currentBatcher.isEmpty()) {
            return null;
        }

        final BatchAsyncBatcher previousBatcher = this.currentBatcher;
        this.currentBatcher = this.createBatchAsyncBatcher();
        return previousBatcher;
    }

    private void resetTimer() {
        this.currentTimeout = this.timer.newTimeout(
            timeout -> this.dispatchTimer(),
            this.dispatchTimerInNanos,
            TimeUnit.NANOSECONDS);
    }

    // endregion
}
