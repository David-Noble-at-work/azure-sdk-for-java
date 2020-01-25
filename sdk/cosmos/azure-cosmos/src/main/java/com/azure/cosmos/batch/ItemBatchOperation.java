// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.implementation.OperationType;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents an operation on an item which will be executed as part of a batch request on a container.
 */
public class ItemBatchOperation implements AutoCloseable {
    /**
     * Operational context used in stream operations.
     * <p>
     * {@link BatchAsyncBatcher} {@link BatchAsyncStreamer} {@link BatchAsyncContainerExecutor}
     */
    private ItemBatchOperationContext Context;
    private CosmosDiagnosticsContext DiagnosticsContext;
    private String Id;
    private int OperationIndex;
    private OperationType OperationType;
    private Documents.PartitionKey ParsedPartitionKey;
    private PartitionKey PartitionKey = null;
    private String PartitionKeyJson;
    private TransactionalBatchItemRequestOptions RequestOptions;
    private Stream resourceStream;
    private Memory<Byte> body;
    private boolean isDisposed;

    public ItemBatchOperation(
        final OperationType operationType,
        final int operationIndex,
        PartitionKey partitionKey,
        String id,
        Stream resourceStream,
        TransactionalBatchItemRequestOptions requestOptions) {

        this(operationType, operationIndex, partitionKey, id, resourceStream, requestOptions, null);
    }

    public ItemBatchOperation(
        OperationType operationType,
        int operationIndex,
        PartitionKey partitionKey,
        String id,
        Stream resourceStream) {

        this(operationType, operationIndex, partitionKey, id, resourceStream, null, null);
    }

    public ItemBatchOperation(OperationType operationType, int operationIndex, PartitionKey partitionKey, String id) {
        this(operationType, operationIndex, partitionKey, id, null, null, null);
    }

    public ItemBatchOperation(OperationType operationType, int operationIndex, PartitionKey partitionKey) {
        this(operationType, operationIndex, partitionKey, null, null, null, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public ItemBatchOperation(OperationType operationType, int operationIndex, PartitionKey
    // partitionKey, string id = null, Stream resourceStream = null, TransactionalBatchItemRequestOptions
    // requestOptions = null, CosmosDiagnosticsContext diagnosticsContext = null)
    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    public ItemBatchOperation(
        OperationType operationType,
        int operationIndex,
        PartitionKey partitionKey,
        String id,
        Stream resourceStream,
        TransactionalBatchItemRequestOptions requestOptions,
        CosmosDiagnosticsContext diagnosticsContext) {

        this.OperationType = operationType;
        this.setOperationIndex(operationIndex);
        this.setPartitionKey(partitionKey);
        this.Id = id;
        this.setResourceStream(resourceStream);
        this.RequestOptions = requestOptions;
        this.DiagnosticsContext = diagnosticsContext;
    }

    public ItemBatchOperation(OperationType operationType, int operationIndex, String id, Stream resourceStream) {
        this(operationType, operationIndex, id, resourceStream, null);
    }

    public ItemBatchOperation(OperationType operationType, int operationIndex, String id) {
        this(operationType, operationIndex, id, null, null);
    }

    public ItemBatchOperation(OperationType operationType, int operationIndex) {
        this(operationType, operationIndex, null, null, null);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
    //ORIGINAL LINE: public ItemBatchOperation(OperationType operationType, int operationIndex, string id = null,
    // Stream resourceStream = null, TransactionalBatchItemRequestOptions requestOptions = null)
    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    public ItemBatchOperation(
        OperationType operationType,
        int operationIndex,
        String id,
        Stream resourceStream,
        TransactionalBatchItemRequestOptions requestOptions) {

        this.OperationType = operationType;
        this.setOperationIndex(operationIndex);
        this.Id = id;
        this.setResourceStream(resourceStream);
        this.RequestOptions = requestOptions;
        this.DiagnosticsContext = null;
    }

    public Memory<Byte> getBody() {
        return body;
    }

    public ItemBatchOperation setBody(Memory<Byte> body) {
        this.body = body;
        return this;
    }

    public final ItemBatchOperationContext getContext() {
        return Context;
    }

    private void setContext(ItemBatchOperationContext value) {
        Context = value;
    }

    public final CosmosDiagnosticsContext getDiagnosticsContext() {
        return DiagnosticsContext;
    }

    public final String getId() {
        return Id;
    }

    public final int getOperationIndex() {
        return OperationIndex;
    }

    public final void setOperationIndex(int value) {
        OperationIndex = value;
    }

    public final OperationType getOperationType() {
        return OperationType;
    }

    public final Documents.PartitionKey getParsedPartitionKey() {
        return ParsedPartitionKey;
    }

    public final void setParsedPartitionKey(Documents.PartitionKey value) {
        ParsedPartitionKey = value;
    }

    public final PartitionKey getPartitionKey() {
        return PartitionKey;
    }

    public final void setPartitionKey(PartitionKey value) {
        PartitionKey = value;
    }

    public final String getPartitionKeyJson() {
        return PartitionKeyJson;
    }

    public final void setPartitionKeyJson(String value) {
        PartitionKeyJson = value;
    }

    public final TransactionalBatchItemRequestOptions getRequestOptions() {
        return RequestOptions;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal Memory<byte> getResourceBody()
    public final Memory<Byte> getResourceBody() {
        Debug.Assert(this.getResourceStream() == null || !this.body.IsEmpty, "ResourceBody read without " +
            "materialization of ResourceStream");

        return this.body;
    }

    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: internal void setResourceBody(Memory<byte> value)
    public final void setResourceBody(Memory<Byte> value) {
        this.body = value;
    }

    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    public final Stream getResourceStream() {
        return resourceStream;
    }

    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    protected final void setResourceStream(Stream value) {
        resourceStream = value;
    }

    /**
     * Attaches a context to the current operation to track resolution.
     */
    public final void AttachContext(@Nonnull final ItemBatchOperationContext context) {
        checkNotNull(context, "expected non-null context");
        this.setContext(context);
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
                if (this.getRequestOptions().Properties.TryGetValue(WFConstants.BackendHeaders.BinaryId,
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
                if (this.getRequestOptions().Properties.TryGetValue(WFConstants.BackendHeaders.EffectivePartitionKey,
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
    public CompletableFuture<Void> MaterializeResourceAsync(CosmosSerializerCore serializerCore) {
        if (this.body.IsEmpty && this.getResourceStream() != null) {
            this.body = /*await*/ BatchExecUtils.StreamToMemoryAsync(this.getResourceStream());
        }
    }

    public static Result WriteOperation(tangible.RefObject<RowWriter> writer, TypeArgument typeArg,
                                        ItemBatchOperation operation) {
        boolean pkWritten = false;
        Result r = writer.argValue.WriteInt32("operationType", (int) operation.getOperationType());
        if (r != Result.Success) {
            return r;
        }

        r = writer.argValue.WriteInt32("resourceType", (int) ResourceType.Document);
        if (r != Result.Success) {
            return r;
        }

        if (operation.getPartitionKeyJson() != null) {
            r = writer.argValue.WriteString("partitionKey", operation.getPartitionKeyJson());
            if (r != Result.Success) {
                return r;
            }

            pkWritten = true;
        }

        if (operation.getId() != null) {
            r = writer.argValue.WriteString("id", operation.getId());
            if (r != Result.Success) {
                return r;
            }
        }

        if (!operation.getResourceBody().IsEmpty) {
            r = writer.argValue.WriteBinary("resourceBody", operation.getResourceBody().Span);
            if (r != Result.Success) {
                return r;
            }
        }

        if (operation.getRequestOptions() != null) {
            TransactionalBatchItemRequestOptions options = operation.getRequestOptions();
            if (options.getIndexingDirective() != null) {
                String indexingDirectiveString =
                    IndexingDirectiveStrings.FromIndexingDirective(options.getIndexingDirective().getValue());
                r = writer.argValue.WriteString("indexingDirective", indexingDirectiveString);
                if (r != Result.Success) {
                    return r;
                }
            }

            if (options.IfMatchEtag != null) {
                r = writer.argValue.WriteString("ifMatch", options.IfMatchEtag);
                if (r != Result.Success) {
                    return r;
                }
            } else if (options.IfNoneMatchEtag != null) {
                r = writer.argValue.WriteString("ifNoneMatch", options.IfNoneMatchEtag);
                if (r != Result.Success) {
                    return r;
                }
            }

            if (options.Properties != null) {
                Object binaryIdObj;
                //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                if (options.Properties.TryGetValue(WFConstants.BackendHeaders.BinaryId, out binaryIdObj)) {
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: byte[] binaryId = binaryIdObj instanceof byte[] ? (byte[])binaryIdObj : null;
                    byte[] binaryId = binaryIdObj instanceof byte[] ? (byte[]) binaryIdObj : null;
                    if (binaryId != null) {
                        r = writer.argValue.WriteBinary("binaryId", binaryId);
                        if (r != Result.Success) {
                            return r;
                        }
                    }
                }

                Object epkObj;
                //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                if (options.Properties.TryGetValue(WFConstants.BackendHeaders.EffectivePartitionKey, out epkObj)) {
                    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
                    //ORIGINAL LINE: byte[] epk = epkObj instanceof byte[] ? (byte[])epkObj : null;
                    byte[] epk = epkObj instanceof byte[] ? (byte[]) epkObj : null;
                    if (epk != null) {
                        r = writer.argValue.WriteBinary("effectivePartitionKey", epk);
                        if (r != Result.Success) {
                            return r;
                        }
                    }
                }

                Object pkStrObj;
                //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                if (!pkWritten && options.Properties.TryGetValue(HttpConstants.HttpHeaders.PartitionKey,
                    out pkStrObj)) {
                    String pkString = pkStrObj instanceof String ? (String) pkStrObj : null;
                    if (pkString != null) {
                        r = writer.argValue.WriteString("partitionKey", pkString);
                        if (r != Result.Success) {
                            return r;
                        }
                    }
                }

                Object ttlObj;
                //C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword -
                // these cannot be converted using the 'OutObject' helper class unless the method is within the code
                // being modified:
                if (options.Properties.TryGetValue(WFConstants.BackendHeaders.TimeToLiveInSeconds, out ttlObj)) {
                    String ttlStr = ttlObj instanceof String ? (String) ttlObj : null;
                    int ttl;
                    tangible.OutObject<Integer> tempOut_ttl = new tangible.OutObject<Integer>();
                    if (ttlStr != null && tangible.TryParseHelper.tryParseInt(ttlStr, tempOut_ttl)) {
                        ttl = tempOut_ttl.argValue;
                        r = writer.argValue.WriteInt32("timeToLiveInSeconds", ttl);
                        if (r != Result.Success) {
                            return r;
                        }
                    } else {
                        ttl = tempOut_ttl.argValue;
                    }
                }
            }
        }

        return Result.Success;
    }

    /**
     * Disposes the current {@link ItemBatchOperation}.
     */
    public final void close() throws IOException {
        this.Dispose(true);
    }

    /**
     * Disposes the disposable members held by this class.
     *
     * @param disposing Indicates whether to dispose managed resources or not.
     */
    protected void Dispose(boolean disposing) {
        if (disposing && !this.isDisposed) {
            this.isDisposed = true;
            if (this.getResourceStream() != null) {
                this.getResourceStream().Dispose();
                this.setResourceStream(null);
            }
        }
    }
}
