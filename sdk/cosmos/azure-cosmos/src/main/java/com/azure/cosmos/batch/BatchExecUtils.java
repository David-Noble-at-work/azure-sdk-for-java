// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.BridgeInternal;
import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.directconnectivity.WFConstants.BackendHeaders;
import com.azure.cosmos.implementation.routing.CollectionRoutingMap;
import com.azure.cosmos.implementation.routing.PartitionKeyInternalHelper;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.PartitionKeyDefinition;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.azure.cosmos.implementation.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.base.Strings.lenientFormat;
import static java.lang.Math.max;

/**
 * Util methods for batch requests.
 */
public final class BatchExecUtils {

    private static final int MINIMUM_BUFFER_SIZE = 81920;

    public static void ensureValid(
        @Nonnull final List<ItemBatchOperation<?>> operations,
        @Nullable final RequestOptions options) {

        final String errorMessage = BatchExecUtils.isValid(operations, options);
        checkArgument(errorMessage == null, errorMessage);
    }

    public static String getPartitionKeyRangeId(
        @Nonnull final PartitionKey key,
        @Nonnull final PartitionKeyDefinition keyDefinition,
        @Nonnull final CollectionRoutingMap collectionRoutingMap) {

        checkNotNull(key, "expected non-null key");
        checkNotNull(keyDefinition, "expected non-null keyDefinition");
        checkNotNull(collectionRoutingMap, "expected non-null collectionRoutingMap");

        String epkString = PartitionKeyInternalHelper.getEffectivePartitionKeyString(
            BridgeInternal.getPartitionKeyInternal(key),
            keyDefinition);

        return collectionRoutingMap.getRangeByEffectivePartitionKey(epkString).getId();
    }

    public static String isValid(final List<ItemBatchOperation<?>> operations, final RequestOptions options) {

        if (operations == null) {
            return "expected non-null operations";
        }

        if (operations.size() <= 0) {
            return "expected operations.size > 0";
        }

        if (options != null && options.getAccessCondition() != null) {
            assert options.getAccessCondition().getCondition() != null;
            assert options.getAccessCondition().getType() != null;
            return "one or more request options provided on the batch request are not supported";
        }

        for (ItemBatchOperation<?> operation : operations) {

            final RequestOptions operationOptions = operation.getRequestOptions();

            final Map<String, Object> properties = operationOptions != null
                ? operationOptions.getProperties()
                : null;

            if (properties != null) {

                final String epkString = (String) properties.computeIfPresent(
                    BackendHeaders.EFFECTIVE_PARTITION_KEY_STRING,
                    (k, v) -> v instanceof String ? v : null);

                final byte[] epk = (byte[]) properties.computeIfPresent(
                    BackendHeaders.EFFECTIVE_PARTITION_KEY,
                    (k, v) -> v instanceof byte[] ? v : null);

                final String pk = (String) properties.computeIfPresent(
                    HttpHeaders.PARTITION_KEY,
                    (k, v) -> v instanceof String ? v : null);

                if ((epk == null && pk == null) || epkString == null) {
                    return lenientFormat(
                        "expected byte[] value for %s and string value for %s, not (%s, %s)",
                        BackendHeaders.EFFECTIVE_PARTITION_KEY,
                        BackendHeaders.EFFECTIVE_PARTITION_KEY_STRING,
                        epk == null
                            ? (pk == null ? "null" : pk)
                            : ByteBufUtil.hexDump(epk), epkString == null ? "null" : epkString);
                }
            }
        }

        return null;
    }

    /**
     * Reads a sequence of bytes from an {@link AsynchronousByteChannel asynchronous byte channel} into a byte array.
     *
     * @param channel a readable asynchronous byte channel.
     *
     * @return a {@link CompletableFuture completable future} representing the result of the operation.
     */
    public static CompletableFuture<byte[]> readAll(@Nonnull final AsynchronousByteChannel channel) {

        final CompletableFuture<byte[]> future = new CompletableFuture<>();
        final ByteBuffer buffer = ByteBuffer.allocate(MINIMUM_BUFFER_SIZE);
        final ByteBuf sequence = Unpooled.buffer(MINIMUM_BUFFER_SIZE);

        CompletionHandler<Integer, Void> completionHandler = new CompletionHandler<Integer, Void>() {

            @Override
            public void completed(final Integer result, final Void attachment) {
                if (result == -1) {
                    final byte[] array = sequence.array();
                    sequence.release();
                    future.complete(array);
                } else {
                    if (result > 0) {
                        sequence.writeBytes(buffer);
                    }
                    buffer.clear();
                    channel.read(buffer, null, this);
                }
            }

            @Override
            public void failed(final Throwable error, final Void attachment) {
                sequence.release();
                future.completeExceptionally(error);
            }
        };

        channel.read(buffer, null, completionHandler);
        return future;
    }

    /**
     * Reads a sequence of bytes from an {@link InputStream input stream} into a byte array.
     *
     * @param inputStream an input stream.
     *
     * @return a byte array.
     *
     * @throws IOException if the specified stream throws an exception.
     */
    public static byte[] readAll(@Nonnull final InputStream inputStream) throws IOException {

        checkNotNull(inputStream, "expected non-null inputStream");

        int length = max(inputStream.available(), MINIMUM_BUFFER_SIZE);
        final ByteBuf buffer = Unpooled.buffer(length);

        try {
            while (buffer.writeBytes(inputStream, length) >= 0) {
                length = max(inputStream.available(), MINIMUM_BUFFER_SIZE);
            }
        } catch (IOException error) {
            buffer.release();
            throw error;
        }

        final byte[] result = buffer.array();
        buffer.release();

        return result;
    }
}
