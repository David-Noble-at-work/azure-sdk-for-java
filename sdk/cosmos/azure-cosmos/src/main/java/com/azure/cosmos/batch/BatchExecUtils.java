// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.PartitionKeyDefinition;
import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.directconnectivity.WFConstants.BackendHeaders;
import com.azure.cosmos.implementation.routing.CollectionRoutingMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.lenientFormat;
import static java.lang.Math.max;

/**
 * Util methods for batch requests.
 */
public final class BatchExecUtils {

    private static final int MINIMUM_BUFFER_SIZE = 81920;

    public static void ensureValid(final List<ItemBatchOperation> operations, final RequestOptions options) {

        final String errorMessage = BatchExecUtils.isValid(operations, options);

        if (errorMessage != null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static String getPartitionKeyRangeId(
        @Nonnull final PartitionKey key,
        @Nonnull final PartitionKeyDefinition keyDefinition,
        @Nonnull final CollectionRoutingMap collectionRoutingMap) {

        checkNotNull(key, "expected non-null key");
        checkNotNull(keyDefinition, "expected non-null keyDefinition");
        checkNotNull(collectionRoutingMap, "expected non-null collectionRoutingMap");

        final String effectiveKey = key.getEffectivePartitionKeyString(keyDefinition);
        return collectionRoutingMap.getRangeByEffectivePartitionKey(effectiveKey).getId();
    }

    public static String isValid(final List<ItemBatchOperation> operations, RequestOptions options) {

        if (operations.size() == 0) {
            return "batch request did not have any operations to be executed";
        }

        if (options != null && options.getAccessCondition() != null) {
            assert options.getAccessCondition().getCondition() != null;
            assert options.getAccessCondition().getType() != null;
            return "one or more request options provided on the batch request are not supported";
        }

        for (ItemBatchOperation operation : operations) {

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
     * Converts an input stream to a byte buffer.
     *
     * @param inputStream Stream to be converted to bytes.
     *
     * @return A Memory{byte}.
     *
     * @throws IOException if a {@link ReadableByteChannel} over {@code inputStream} cannot be created.
     */
    public static byte[] inputStreamToBytes(@Nonnull final InputStream inputStream) throws IOException {

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

        return buffer.array();
    }
}
