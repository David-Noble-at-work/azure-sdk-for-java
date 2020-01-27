// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.implementation.ResourceType;
import com.azure.cosmos.implementation.directconnectivity.WFConstants.BackendHeaders;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.cosmos.serialization.hybridrow.layouts.TypeArgument;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents an operation on an item which will be executed as part of a batch request on a container.
 */
public class ItemBatchOperation<TResource> implements AutoCloseable {

    // region Fields

    private ItemBatchOperationContext context;
    private CosmosDiagnosticsContext diagnosticsContext;
    private String Id;
    private int operationIndex;
    private OperationType operationType;
    private Documents.PartitionKey parsedPartitionKey;
    private PartitionKey partitionKey;
    private String partitionKeyJson;
    private TransactionalBatchItemRequestOptions requestOptions;
    private Memory<Byte> body;
    private TResource resource;

    // endregion

    // region Constructors

    public ItemBatchOperation(
        @Nonnull final OperationType operationType,
        final int operationIndex) {

        this(operationType, operationIndex, (PartitionKey) null, null, null, null, null);
    }

    public ItemBatchOperation(
        @Nonnull final OperationType operationType,
        final int operationIndex,
        final PartitionKey partitionKey) {

        this(operationType, operationIndex, partitionKey, null, null, null, null);
    }

    public ItemBatchOperation(
        @Nonnull final OperationType operationType,
        final int operationIndex,
        final PartitionKey partitionKey,
        final String id) {

        this(operationType, operationIndex, partitionKey, id, null, null, null);
    }

    public ItemBatchOperation(
        @Nonnull final OperationType operationType,
        final int operationIndex,
        final PartitionKey partitionKey,
        final String id,
        final TResource resource) {

        this(operationType, operationIndex, partitionKey, id, resource, null, null);
    }

    public ItemBatchOperation(
        @Nonnull final OperationType operationType,
        final int operationIndex,
        final PartitionKey partitionKey,
        final String id,
        final TResource resource,
        final TransactionalBatchItemRequestOptions requestOptions) {

        this(operationType, operationIndex, partitionKey, id, resource, requestOptions, null);
    }

    public ItemBatchOperation(
        @Nonnull final OperationType operationType,
        final int operationIndex,
        final String id) {

        this(operationType, operationIndex, null, id, null, null, null);
    }

    public ItemBatchOperation(
        @Nonnull final OperationType operationType,
        final int operationIndex,
        final String id,
        final TResource resource) {

        this(operationType, operationIndex, null, id, resource, null, null);
    }

    public ItemBatchOperation(
        OperationType operationType,
        int operationIndex,
        String id,
        TResource resource,
        TransactionalBatchItemRequestOptions requestOptions) {

        this(operationType, operationIndex, null, id, resource, requestOptions, null);
    }

    public ItemBatchOperation(
        @Nonnull final OperationType operationType,
        final int operationIndex,
        final PartitionKey partitionKey,
        final String id,
        final TResource resource,
        final TransactionalBatchItemRequestOptions requestOptions,
        final CosmosDiagnosticsContext diagnosticsContext) {

        checkArgument(operationIndex >= 0, "expected operationIndex >= 0, not %s", operationIndex);
        checkNotNull(operationType, "expected non-null operationType");

        this.operationType = operationType;
        this.operationIndex = operationIndex;
        this.partitionKey = partitionKey;
        this.Id = id;
        this.resource = resource;
        this.requestOptions = requestOptions;
        this.diagnosticsContext = diagnosticsContext;
    }

    // endregion

    // region Accessors

    public Memory<Byte> getBody() {
        return body;
    }

    public ItemBatchOperation setBody(Memory<Byte> body) {
        this.body = body;
        return this;
    }

    /**
     * Operational context used in stream operations.
     * <p>
     * {@link BatchAsyncBatcher} {@link BatchAsyncStreamer} {@link BatchAsyncContainerExecutor}
     */
    public final ItemBatchOperationContext getContext() {
        return context;
    }

    public final CosmosDiagnosticsContext getDiagnosticsContext() {
        return diagnosticsContext;
    }

    public final String getId() {
        return Id;
    }

    public final int getOperationIndex() {
        return operationIndex;
    }

    public final ItemBatchOperation<TResource> setOperationIndex(int value) {
        operationIndex = value;
        return this;
    }

    public final OperationType getOperationType() {
        return operationType;
    }

    public final Documents.PartitionKey getParsedPartitionKey() {
        return parsedPartitionKey;
    }

    public final void setParsedPartitionKey(Documents.PartitionKey value) {
        parsedPartitionKey = value;
    }

    public final PartitionKey getPartitionKey() {
        return partitionKey;
    }

    public final void setPartitionKey(PartitionKey value) {
        partitionKey = value;
    }

    public final String getPartitionKeyJson() {
        return partitionKeyJson;
    }

    public final void setPartitionKeyJson(String value) {
        partitionKeyJson = value;
    }

    public final TransactionalBatchItemRequestOptions getRequestOptions() {
        return requestOptions;
    }

    public final TResource getResource() {
        return resource;
    }

    private void setResource(TResource value) {
        resource = value;
    }

    public final Memory<Byte> getResourceBody() {
        return this.body;
    }

    public final void setResourceBody(Memory<Byte> value) {
        this.body = value;
    }

    // endregion

    // region Methods

    /**
     * Attaches a context to the current operation to track resolution.
     */
    public final ItemBatchOperation<TResource> AttachContext(@Nonnull final ItemBatchOperationContext context) {
        checkNotNull(context, "expected non-null context");
        this.context = context;
        return this;
    }

    /**
     * Computes and returns an approximation for the length of this {@link ItemBatchOperation}. when serialized.
     *
     * @return An under-estimate of the length.
     */
    public final int GetApproximateSerializedLength() {
        int length = 0;

        if (this.getPartitionKeyJson() != null) {
            length += this.getPartitionKeyJson().length();
        }

        if (this.getId() != null) {
            length += this.getId().length();
        }

        length += this.body.Length;

        if (this.getRequestOptions() != null) {
            if (this.getRequestOptions().IfMatchEtag != null) {
                length += this.getRequestOptions().IfMatchEtag.Length;
            }

            if (this.getRequestOptions().IfNoneMatchEtag != null) {
                length += this.getRequestOptions().IfNoneMatchEtag.Length;
            }

            if (this.getRequestOptions().getIndexingDirective() != null) {
                length += 7; // "Default", "Include", "Exclude" are possible values
            }

            if (this.getRequestOptions().Properties != null) {
                Object binaryIdObj;
                //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                if (this.getRequestOptions().Properties.TryGetValue(BackendHeaders.BINARY_ID,
                    out binaryIdObj)) {
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: byte[] binaryId = binaryIdObj instanceof byte[] ? (byte[])binaryIdObj : null;
                    byte[] binaryId = binaryIdObj instanceof byte[] ? (byte[]) binaryIdObj : null;
                    if (binaryId != null) {
                        length += binaryId.length;
                    }
                }

                Object epkObj;
                //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                if (this.getRequestOptions().Properties.TryGetValue(BackendHeaders.EFFECTIVE_PARTITION_KEY,
                    out epkObj)) {
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: byte[] epk = epkObj instanceof byte[] ? (byte[])epkObj : null;
                    byte[] epk = epkObj instanceof byte[] ? (byte[]) epkObj : null;
                    if (epk != null) {
                        length += epk.length;
                    }
                }
            }
        }

        return length;
    }

    /**
     * Materializes the operation's resource into a Memory{byte} wrapping a byte array.
     *
     * @param serializerCore Serializer to serialize user provided objects to JSON.
     */
    public CompletableFuture<Void> MaterializeResourceAsync(@Nonnull final CosmosSerializerCore serializerCore) {

        if (this.body.IsEmpty && this.resource != null) {
            try (InputStream inputStream = this.resource instanceof InputStream
                ? (InputStream) resource
                : serializerCore.ToStream(this.getResource())) {
                this.body = /*await*/ BatchExecUtils.StreamToMemoryAsync(inputStream);
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    public static Result WriteOperation(
        @Nonnull RowWriter writer,
        @Nonnull TypeArgument typeArg,
        @Nonnull ItemBatchOperation operation) {

        boolean pkWritten = false;
        Result result = writer.writeInt32("operationType", (int) operation.getOperationType());

        if (result != Result.SUCCESS) {
            return result;
        }

        result = writer.writeInt32("resourceType", (int) ResourceType.Document);
        if (result != Result.SUCCESS) {
            return result;
        }

        if (operation.getPartitionKeyJson() != null) {
            result = writer.writeString("partitionKey", operation.getPartitionKeyJson());
            if (result != Result.SUCCESS) {
                return result;
            }

            pkWritten = true;
        }

        if (operation.getId() != null) {
            result = writer.writeString("id", operation.getId());
            if (result != Result.SUCCESS) {
                return result;
            }
        }

        if (!operation.getResourceBody().IsEmpty) {
            result = writer.writeBinary("resourceBody", operation.getResourceBody().Span);
            if (result != Result.SUCCESS) {
                return result;
            }
        }

        if (operation.getRequestOptions() != null) {
            TransactionalBatchItemRequestOptions options = operation.getRequestOptions();
            if (options.getIndexingDirective() != null) {
                String indexingDirectiveString =
                    IndexingDirectiveStrings.FromIndexingDirective(options.getIndexingDirective().getValue());
                result = writer.writeString("indexingDirective", indexingDirectiveString);
                if (result != Result.SUCCESS) {
                    return result;
                }
            }

            if (options.IfMatchEtag != null) {
                result = writer.writeString("ifMatch", options.IfMatchEtag);
                if (result != Result.SUCCESS) {
                    return result;
                }
            } else if (options.IfNoneMatchEtag != null) {
                result = writer.writeString("ifNoneMatch", options.IfNoneMatchEtag);
                if (result != Result.SUCCESS) {
                    return result;
                }
            }

            if (options.Properties != null) {
                Object binaryIdObj;
                //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                if (options.Properties.TryGetValue(BackendHeaders.BINARY_ID, out binaryIdObj)) {
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: byte[] binaryId = binaryIdObj instanceof byte[] ? (byte[])binaryIdObj : null;
                    byte[] binaryId = binaryIdObj instanceof byte[] ? (byte[]) binaryIdObj : null;
                    if (binaryId != null) {
                        result = writer.writeBinary("binaryId", binaryId);
                        if (result != Result.SUCCESS) {
                            return result;
                        }
                    }
                }

                Object epkObj;
                //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                if (options.Properties.TryGetValue(BackendHeaders.EFFECTIVE_PARTITION_KEY, out epkObj)) {
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: byte[] epk = epkObj instanceof byte[] ? (byte[])epkObj : null;
                    byte[] epk = epkObj instanceof byte[] ? (byte[]) epkObj : null;
                    if (epk != null) {
                        result = writer.writeBinary("effectivePartitionKey", epk);
                        if (result != Result.SUCCESS) {
                            return result;
                        }
                    }
                }

                Object pkStrObj;
                //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                if (!pkWritten && options.Properties.TryGetValue(HttpHeaders.PARTITION_KEY,
                    out pkStrObj)) {
                    String pkString = pkStrObj instanceof String ? (String) pkStrObj : null;
                    if (pkString != null) {
                        result = writer.writeString("partitionKey", pkString);
                        if (result != Result.SUCCESS) {
                            return result;
                        }
                    }
                }

                Object ttlObj;
                //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                if (options.Properties.TryGetValue(BackendHeaders.TIME_TO_LIVE_IN_SECONDS, out ttlObj)) {
                    String ttlStr = ttlObj instanceof String ? (String) ttlObj : null;
                    int ttl;
                    tangible.OutObject<Integer> tempOut_ttl = new tangible.OutObject<Integer>();
                    if (ttlStr != null && tangible.TryParseHelper.tryParseInt(ttlStr, tempOut_ttl)) {
                        ttl = tempOut_ttl.argValue;
                        result = writer.writeInt32("timeToLiveInSeconds", ttl);
                        if (result != Result.SUCCESS) {
                            return result;
                        }
                    } else {
                        ttl = tempOut_ttl.argValue;
                    }
                }
            }
        }

        return Result.SUCCESS;
    }

    /**
     * Closes this {@link ItemBatchOperation}.
     */
    public final void close() throws Exception {
        if (this.resource instanceof AutoCloseable) {
            ((AutoCloseable) this.resource).close();  // assumes an idempotent close implementation
        }
    }

    // endregion
}
