// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.PartitionKeyDefinition;
import com.azure.cosmos.implementation.HttpConstants;
import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.directconnectivity.WFConstants.BackendHeaders;
import com.azure.cosmos.implementation.routing.CollectionRoutingMap;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Util methods for batch requests.
 */
public final class BatchExecUtils {

    // Using the same buffer size as the Stream.DefaultCopyBufferSize
    private static final int BufferSize = 81920;

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

        final String errorMessage = null;

        if (operations.size() == 0) {
            errorMessage = ClientResources.BatchNoOperations;
        }

        if (errorMessage == null && options != null) {
            if (options.IfMatchEtag != null || options.IfNoneMatchEtag != null) {
                errorMessage = ClientResources.BatchRequestOptionNotSupported;
            }
        }

        if (errorMessage == null) {

            for (ItemBatchOperation operation : operations) {

                final RequestOptions operationOptions = operation.getRequestOptions();

                final Map<String, Object> properties = operationOptions != null
                    ? operationOptions.getProperties()
                    : null;

                if (properties != null) {

                    final Object epkObj = properties.get(BackendHeaders.EFFECTIVE_PARTITION_KEY);
                    final Object epkStrObj = properties.get(BackendHeaders.EFFECTIVE_PARTITION_KEY_STRING);
                    final Object pkStrObj = properties.get(HttpHeaders.PARTITION_KEY);
                    && (
                        operation.getRequestOptions().getProperties().TryGetValue(BackendHeaders.EFFECTIVE_PARTITION_KEY, out epkObj)
                        | operation.getRequestOptions().getProperties().TryGetValue(BackendHeaders.EFFECTIVE_PARTITION_KEY_STRING, out epkStrObj)
                        | operation.getRequestOptions().getProperties().TryGetValue(HttpHeaders.PARTITION_KEY, out pkStrObj)))
                    {
                        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                        //ORIGINAL LINE: byte[] epk = epkObj instanceof byte[] ? (byte[])epkObj : null;
                        byte[] epk = epkObj instanceof byte[] ? (byte[]) epkObj : null;
                        String epkStr = epkStrObj instanceof String ? (String) epkStrObj : null;
                        String partitionKeyJsonString = pkStrObj instanceof String ? (String) pkStrObj : null;
                        if ((epk == null && partitionKeyJsonString == null) || epkStr == null) {
                            errorMessage = String.format(ClientResources.EpkPropertiesPairingExpected, BackendHeaders.EFFECTIVE_PARTITION_KEY, BackendHeaders.EFFECTIVE_PARTITION_KEY_STRING);
                        }

                        if (operation.getPartitionKey() != null && !operation.getRequestOptions().IsEffectivePartitionKeyRouting) {
                            errorMessage = ClientResources.PKAndEpkSetTogether;
                        }
                    }
                }
            }
        }

        return errorMessage;
    }

    /**
     * Converts a Stream to a Memory{byte} wrapping a byte array.
     *
     * @param stream Stream to be converted to bytes.
     *
     * @return A Memory{byte}.
     */
    public static CompletableFuture<Memory<Byte>> StreamToMemoryAsync(InputStream stream) {

        if (stream.CanSeek) {
            // Some derived implementations of MemoryStream (such as versions of RecyclableMemoryStream prior to 1.2
            // .2 that we may be using)
            // return an incorrect response from TryGetBuffer. Use TryGetBuffer only on the MemoryStream type and not
            // derived types.
            //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO
            // .MemoryStream is input or output:
            MemoryStream memStream = stream instanceof MemoryStream ? (MemoryStream) stream : null;
            ArraySegment<Byte> memBuffer;
            tangible.OutObject<System.ArraySegment<Byte>> tempOut_memBuffer =
                new tangible.OutObject<System.ArraySegment<Byte>>();
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: if (memStream != null && memStream.GetType() == typeof(MemoryStream) && memStream
            // .TryGetBuffer(out ArraySegment<byte> memBuffer))
            //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO
            // .MemoryStream is input or output:
            if (memStream != null && memStream.getClass() == MemoryStream.class && memStream.TryGetBuffer(tempOut_memBuffer)) {
                memBuffer = tempOut_memBuffer.argValue;
                return memBuffer;
            } else {
                memBuffer = tempOut_memBuffer.argValue;
            }

            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: byte[] bytes = new byte[stream.Length];
            byte[] bytes = new byte[stream.Length];
            int sum = 0;
            int count;
            while ((count =/*await*/ stream.ReadAsync(bytes, sum, bytes.length - sum)) > 0) {
                sum += count;
            }

            return bytes;
        } else {
            int bufferSize = BatchExecUtils.BufferSize;
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: byte[] buffer = new byte[bufferSize];
            byte[] buffer = new byte[bufferSize];

            try (MemoryStream memoryStream = new MemoryStream(bufferSize)) {
                int sum = 0;
                int count;
                while ((count = /*await*/ stream.ReadAsync(buffer, 0, bufferSize)) >0) {
                    sum += count;
                    memoryStream.Write(buffer, 0, count);
                }

                //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                //ORIGINAL LINE: return new Memory<byte>(memoryStream.GetBuffer(), 0, (int)memoryStream.Length);
                return new Memory<Byte>(memoryStream.GetBuffer(), 0, (int) memoryStream.Length);
            }
        }
    }
}
