// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.AccessCondition;
import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.core.UtfAnyString;
import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.implementation.RequestOptions;
import com.azure.cosmos.implementation.ResourceType;
import com.azure.cosmos.implementation.directconnectivity.WFConstants.BackendHeaders;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.cosmos.serialization.hybridrow.layouts.TypeArgument;
import com.azure.cosmos.serializer.CosmosSerializerCore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.nio.channels.AsynchronousByteChannel;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents an operation on an item which will be executed as part of a batch request on a container.
 */
public final class ItemBatchOperation<TResource> implements AutoCloseable {

    // region Fields

    // Constants

    private static final UtfAnyString BINARY_ID = new UtfAnyString("binaryId");
    private static final UtfAnyString ID = new UtfAnyString("id");
    private static final UtfAnyString IF_MATCH = new UtfAnyString("ifMatch");
    private static final UtfAnyString IF_NONE_MATCH = new UtfAnyString("ifNoneMatch");
    private static final UtfAnyString INDEXING_DIRECTIVE = new UtfAnyString("indexingDirective");
    private static final UtfAnyString OPERATION_TYPE = new UtfAnyString("operationType");
    private static final UtfAnyString PARTITION_KEY = new UtfAnyString("partitionKey");
    private static final UtfAnyString RESOURCE_BODY = new UtfAnyString("resourceBody");
    private static final UtfAnyString RESOURCE_TYPE = new UtfAnyString("resourceType");
    private static final UtfAnyString TIME_TO_LIVE_IN_SECONDS = new UtfAnyString("timeToLiveInSeconds");

    // Instance variables

    private final OperationType operationType;
    private byte[] body;

    private ItemBatchOperationContext context;
    private CosmosDiagnosticsContext diagnosticsContext;
    private String id;
    private int operationIndex;
    private Documents.PartitionKey parsedPartitionKey;
    private PartitionKey partitionKey;
    private String partitionKeyJson;
    private RequestOptions requestOptions;
    private TResource resource;

    // endregion

    // region Constructors

    private ItemBatchOperation(
        @Nonnull final OperationType operationType,
        final int operationIndex,
        final PartitionKey partitionKey,
        final String id,
        final TResource resource,
        final RequestOptions requestOptions,
        final CosmosDiagnosticsContext diagnosticsContext) {

        checkArgument(operationIndex >= 0, "expected operationIndex >= 0, not %s", operationIndex);
        checkNotNull(operationType, "expected non-null operationType");

        this.operationType = operationType;
        this.operationIndex = operationIndex;
        this.partitionKey = partitionKey;
        this.id = id;
        this.resource = resource;
        this.requestOptions = requestOptions;
        this.diagnosticsContext = diagnosticsContext;
    }

    // endregion

    // region Accessors

    public byte[] getBody() {
        return body;
    }

    public ItemBatchOperation setBody(byte[] body) {
        this.body = body;
        return this;
    }

    /**
     * Returns the {@link ItemBatchOperationContext operational context} used in stream operations.
     *
     * @return the {@link ItemBatchOperationContext operational context} used in stream operations.
     *
     * @see BatchAsyncBatcher
     * @see BatchAsyncContainerExecutor
     * @see BatchAsyncStreamer
     */
    public ItemBatchOperationContext getContext() {
        return context;
    }

    public CosmosDiagnosticsContext getDiagnosticsContext() {
        return this.diagnosticsContext;
    }

    public String getId() {
        return this.id;
    }

    public int getOperationIndex() {
        return this.operationIndex;
    }

    public ItemBatchOperation<TResource> setOperationIndex(final int value) {
        this.operationIndex = value;
        return this;
    }

    public OperationType getOperationType() {
        return this.operationType;
    }

    public Documents.PartitionKey getParsedPartitionKey() {
        return this.parsedPartitionKey;
    }

    public ItemBatchOperation<TResource> setParsedPartitionKey(Documents.PartitionKey value) {
        parsedPartitionKey = value;
        return this;
    }

    public PartitionKey getPartitionKey() {
        return partitionKey;
    }

    public ItemBatchOperation<TResource> setPartitionKey(final PartitionKey value) {
        partitionKey = value;
        return this;
    }

    public String getPartitionKeyJson() {
        return partitionKeyJson;
    }

    public ItemBatchOperation<TResource> setPartitionKeyJson(final String value) {
        partitionKeyJson = value;
        return this;
    }

    public RequestOptions getRequestOptions() {
        return requestOptions;
    }

    public TResource getResource() {
        return resource;
    }

    private ItemBatchOperation<TResource> setResource(final TResource value) {
        resource = value;
        return this;
    }

    public byte[] getResourceBody() {
        return this.body;
    }

    // endregion

    // region Methods

    /**
     * Attaches a {@link ItemBatchOperationContext context} to the {@link ItemBatchOperation current operation}.
     * <p>
     * The attached {@link ItemBatchOperationContext context} is used to track resolution.
     *
     * @param context the {@link ItemBatchOperationContext context} to attach.
     *
     * @return a reference to the {@link ItemBatchOperation current operation}.
     */
    public ItemBatchOperation<TResource> AttachContext(@Nonnull final ItemBatchOperationContext context) {
        checkNotNull(context, "expected non-null context");
        this.context = context;
        return this;
    }

    /**
     * Computes an underestimate of the serialized length of this {@link ItemBatchOperation}.
     *
     * @return an underestimate of the serialized length of this {@link ItemBatchOperation}.
     */
    public int GetApproximateSerializedLength() {

        int length = 0;

        if (this.getPartitionKeyJson() != null) {
            length += this.getPartitionKeyJson().length();
        }

        if (this.getId() != null) {
            length += this.getId().length();
        }

        length += this.body.length;

        RequestOptions requestOptions = this.getRequestOptions();

        if (requestOptions != null) {

            AccessCondition accessCondition = requestOptions.getAccessCondition();

            switch (accessCondition.getType()) {
                case IF_MATCH:
                    length += accessCondition.getCondition().length();
                    break;
                case IF_NONE_MATCH:
                    length += 7;
                    break;
                default:
                    assert false : "Unexpected value: " + accessCondition.getType();
            }

            if (requestOptions.getIndexingDirective() != null) {
                length += 7; // "Default", "Include", "Exclude" are possible values
            }

            Map<String, Object> properties = requestOptions.getProperties();

            if (properties != null) {

                byte[] binaryId = (byte[]) properties.computeIfPresent(BackendHeaders.BINARY_ID, (k, v) ->
                    v instanceof byte[] ? (byte[]) v : null);

                if (binaryId != null) {
                    length += binaryId.length;
                }

                byte[] epk = (byte[]) properties.computeIfPresent(BackendHeaders.EFFECTIVE_PARTITION_KEY, (k, v) ->
                    v instanceof byte[] ? (byte[]) v : null);

                if (epk != null) {
                    length += epk.length;
                }
            }
        }

        return length;
    }

    /**
     * Materializes the operation's resource into a byte array synchronously.
     *
     * @param serializerCore Serializer to serialize user provided objects to JSON.
     *
     * @return a {@link CompletableFuture future} that will complete when the resource is materialized or an error
     * occurs.
     */
    public CompletableFuture<Void> materializeResource(@Nonnull final CosmosSerializerCore serializerCore) {

        final CompletableFuture<Void> future = new CompletableFuture<>();

        if (this.body == null && this.resource != null) {

            if (this.resource instanceof AsynchronousByteChannel) {

                BatchExecUtils.readAll((AsynchronousByteChannel) this.resource).whenComplete((body, error) -> {
                    if (body == null) {
                        future.completeExceptionally(error);
                    } else {
                        this.body = body;
                        future.complete(null);
                    }
                });

            } else {

                try (InputStream inputStream = this.resource instanceof InputStream
                    ? (InputStream) resource
                    : serializerCore.toStream(this.resource)) {
                    this.body = BatchExecUtils.readAll(inputStream);
                } catch (Throwable error) {
                    future.completeExceptionally(error);
                    return future;
                }

                future.complete(null);
            }
        }

        return future;
    }

    public static Result writeOperation(
        @Nonnull RowWriter writer,
        @Nonnull ItemBatchOperation operation,
        @Nullable TypeArgument typeArgument) {

        checkNotNull(writer, "expected non-null writer");
        checkNotNull(operation, "expected non-null operation");
        boolean pkWritten = false;

        operation.getOperationType();
        Result result = writer.writeInt32(OPERATION_TYPE, (int) operation.getOperationType());

        if (result != Result.SUCCESS) {
            return result;
        }

        result = writer.writeInt32(RESOURCE_TYPE, (int) ResourceType.Document);
        if (result != Result.SUCCESS) {
            return result;
        }

        if (operation.getPartitionKeyJson() != null) {
            result = writer.writeString(PARTITION_KEY, operation.getPartitionKeyJson());
            if (result != Result.SUCCESS) {
                return result;
            }

            pkWritten = true;
        }

        if (operation.getId() != null) {
            result = writer.writeString(ID, operation.getId());
            if (result != Result.SUCCESS) {
                return result;
            }
        }

        if (operation.getResourceBody().length > 0) {
            result = writer.writeBinary(RESOURCE_BODY, operation.getResourceBody());
            if (result != Result.SUCCESS) {
                return result;
            }
        }

        if (operation.getRequestOptions() != null) {

            /*TransactionalBatchItem*/RequestOptions options = operation.getRequestOptions();

            if (options.getIndexingDirective() != null) {
                String indexingDirectiveString = options.getIndexingDirective().toString();
                result = writer.writeString(INDEXING_DIRECTIVE, indexingDirectiveString);
                if (result != Result.SUCCESS) {
                    return result;
                }
            }

            final AccessCondition accessCondition = options.getAccessCondition();

            switch (accessCondition.getType()) {
                case IF_MATCH:
                    result = writer.writeString(IF_MATCH, accessCondition.getCondition());
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    break;
                case IF_NONE_MATCH:
                    result = writer.writeString(IF_NONE_MATCH, accessCondition.getCondition());
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                    break;
                default:
                    assert false : "Unexpected value: " + accessCondition.getType();
            }

            final Map<String, Object> properties = options.getProperties();

            if (properties != null) {

                byte[] binaryId = (byte[]) properties.computeIfPresent(BackendHeaders.BINARY_ID, (k, v) ->
                    v instanceof byte[] ? v : null);

                if (binaryId != null) {
                    result = writer.writeBinary(BINARY_ID, binaryId);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                }

                byte[] epk = (byte[]) properties.computeIfPresent(BackendHeaders.EFFECTIVE_PARTITION_KEY, (k, v) ->
                    v instanceof byte[] ? v : null);

                if (epk != null) {
                    result = writer.writeBinary(BINARY_ID, epk);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                }

                final String pk = (String) properties.computeIfPresent(HttpHeaders.PARTITION_KEY, (k, v) ->
                    v instanceof String ? v : null);

                if (pk != null) {
                    result = writer.writeString(PARTITION_KEY, pk);
                    if (result != Result.SUCCESS) {
                        return result;
                    }
                }

                final String ttl = (String) properties.computeIfPresent(BackendHeaders.TIME_TO_LIVE_IN_SECONDS,
                    (k, v) -> v instanceof String ? v : null);

                if (ttl != null) {
                    Integer value;
                    try {
                        value = Integer.parseInt(ttl);
                    } catch (NumberFormatException error) {
                        value = null;
                    }
                    if (value != null) {
                        result = writer.writeInt32(TIME_TO_LIVE_IN_SECONDS, value);
                    }
                }
            }
        }

        return Result.SUCCESS;
    }

    /**
     * Closes this {@link ItemBatchOperation}.
     *
     * @throws Exception if the close fails.
     */
    public void close() throws Exception {
        if (this.resource instanceof AutoCloseable) {
            ((AutoCloseable) this.resource).close();  // assumes an idempotent close implementation
        }
    }

    // endregion

    public static final class Builder<TResource> {

        private final OperationType operationType;
        private final int operationIndex;

        private ItemBatchOperationContext context;
        private CosmosDiagnosticsContext diagnosticsContext;
        private String id;
        private PartitionKey partitionKey;
        private RequestOptions requestOptions;
        private TResource resource;


        public Builder(@Nonnull final OperationType type, final int index) {

            checkNotNull(type, "expected non-null type");
            checkArgument(index >= 0, "expected index >= 0, not %s", index);

            this.operationType = type;
            this.operationIndex = index;
        }

        public Builder<TResource> diagnosticsContext(CosmosDiagnosticsContext value) {
            this.diagnosticsContext = value;
            return this;
        }

        public Builder<TResource> id(String value) {
            this.id = value;
            return this;
        }

        public Builder<TResource> partitionKey(PartitionKey value) {
            this.partitionKey = value;
            return this;
        }

        public Builder<TResource> requestOptions(RequestOptions value) {
            this.requestOptions = value;
            return this;
        }

        public Builder<TResource> resource(TResource value) {
            this.resource = value;
            return this;
        }

        public ItemBatchOperation<TResource> build() {
            return new ItemBatchOperation<>(
                this.operationType,
                this.operationIndex,
                this.partitionKey,
                this.id,
                this.resource,
                this.requestOptions,
                this.diagnosticsContext);
        }
    }
}
