// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.io.RowReader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.time.Duration;

import static com.google.common.base.Preconditions.checkState;

/**
 * Represents a result for a specific operation that was part of a {@link TransactionalBatch} request.
 */
public class TransactionalBatchOperationResult {

    private static Logger logger = LoggerFactory.getLogger(TransactionalBatchOperationResult.class);

    /**
     * Gets the cosmos diagnostic information for the current request to Azure Cosmos DB service
     */
    private CosmosDiagnosticsContext DiagnosticsContext;
    /**
     * Gets the entity tag associated with the resource.
     *
     * <value>
     * The entity tag associated with the resource.
     * </value>
     * <p>
     * ETags are used for concurrency checking when updating resources.
     */
    private String ETag;
    /**
     * Request charge in request units for the operation.
     */
    private double RequestCharge;
    /**
     * Gets the content of the resource.
     *
     * <value>
     * The content of the resource as a Stream.
     * </value>
     */
    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    private InputStream resourceStream;
    /**
     * In case the operation is rate limited, indicates the time post which a retry can be attempted.
     */
    private Duration RetryAfter;
    /**
     * Gets the completion status of the operation.
     */
    private HttpResponseStatus responseStatus;
    /**
     * Gets detail on the completion status of the operation.
     */
    private int SubStatusCode;

    public TransactionalBatchOperationResult(HttpResponseStatus responseStatus) {
        this.setResponseStatus(responseStatus);
    }

    public TransactionalBatchOperationResult(TransactionalBatchOperationResult other) {
        this.setResponseStatus(other.getResponseStatus());
        this.setSubStatusCode(other.getSubStatusCode());
        this.setETag(other.getETag());
        this.setResourceStream(other.getResourceStream());
        this.setRequestCharge(other.getRequestCharge());
        this.setRetryAfter(other.getRetryAfter());
    }

    /**
     * Initializes a new instance of the {@link TransactionalBatchOperationResult} class.
     */
    protected TransactionalBatchOperationResult() {
    }

    public CosmosDiagnosticsContext getDiagnosticsContext() {
        return DiagnosticsContext;
    }

    public void setDiagnosticsContext(CosmosDiagnosticsContext value) {
        DiagnosticsContext = value;
    }

    public String getETag() {
        return ETag;
    }

    public void setETag(String value) {
        ETag = value;
    }

    /**
     * Gets a value indicating whether the current operation completed successfully.
     */
    public boolean isSuccessStatusCode() {
        int statusCode = (int) this.getResponseStatus().code();
        return statusCode >= 200 && statusCode <= 299;
    }

    public double getRequestCharge() {
        return RequestCharge;
    }

    public void setRequestCharge(double value) {
        RequestCharge = value;
    }

    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    public InputStream getResourceStream() {
        return this.resourceStream;
    }

    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    public void setResourceStream(InputStream value) {
        this.resourceStream = value;
    }

    public Duration getRetryAfter() {
        return RetryAfter;
    }

    public void setRetryAfter(Duration value) {
        RetryAfter = value;
    }

    public HttpResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    private void setResponseStatus(HttpResponseStatus value) {
        this.responseStatus = value;
    }

    public int getSubStatusCode() {
        return SubStatusCode;
    }

    public void setSubStatusCode(int value) {
        SubStatusCode = value;
    }

    public static Result ReadOperationResult(
        @Nonnull Memory<Byte> input, @Nonnull Out<TransactionalBatchOperationResult> batchOperationResult) {

        RowBuffer rowBuffer = new RowBuffer(input.Length);

        if (!rowBuffer.readFrom(input.Span, HybridRowVersion.V1, BatchSchemaProvider.getBatchLayoutResolverNamespace())) {
            batchOperationResult.set(null);
            return Result.FAILURE;
        }

        Result result = TransactionalBatchOperationResult.ReadOperationResult(new RowReader(rowBuffer), batchOperationResult);

        if (result != Result.SUCCESS) {
            return result;
        }

        // Ensure the mandatory fields were populated

        if (batchOperationResult.get().getResponseStatus() == null) {
            return Result.FAILURE;
        }

        return Result.SUCCESS;
    }

    public final ResponseMessage ToResponseMessage() {

        Headers headers = new Headers();
        headers.SubStatusCode = this.getSubStatusCode();
        headers.ETag = this.getETag();
        headers.RetryAfter = this.getRetryAfter();
        headers.RequestCharge = this.getRequestCharge();

        return new ResponseMessage(
            this.getResponseStatus(), null, null, null, headers, this.getDiagnosticsContext() != null
            ? this.getDiagnosticsContext()
            : new CosmosDiagnosticsContext()).setContent(this.getResourceStream());
    }

    @SuppressWarnings("unchecked")
    private static Result ReadOperationResult(
        @Nonnull final RowReader reader,
        @Nonnull final Out<TransactionalBatchOperationResult> batchOperationResult) {

        batchOperationResult.set(new TransactionalBatchOperationResult());
        @SuppressWarnings("rawtypes") Out out = new Out();

        while (reader.read()) {

            final String path = reader.path().toUtf16();
            final Result result;

            checkState(path != null, "expected non-null path");

            switch (path) {

                case "statusCode":

                    result = reader.readInt32((Out<Integer>) out);

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    batchOperationResult.get().setResponseStatus(HttpResponseStatus.valueOf((Integer) out.get()));
                    break;

                case "subStatusCode":

                    result = reader.readInt32((Out<Integer>) out);

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    batchOperationResult.get().setSubStatusCode((Integer) out.get());
                    break;

                case "eTag":

                    result = reader.readString((Out<String>) out);

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    batchOperationResult.get().setETag((String) out.get());
                    break;

                case "resourceBody":

                    result = reader.readBinary((Out<ByteBuf>) out);

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    batchOperationResult.get().setResourceStream(new ByteBufInputStream((ByteBuf) out.get(), true));
                    break;

                case "requestCharge":

                    result = reader.readFloat64((Out<Double>) out);

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    // Round charge to 2 decimals on the results similar to how we round them for a full response

                    Double requestCharge = (Double) out.get();

                    batchOperationResult.get().setRequestCharge(Double.isNaN(requestCharge)
                        ? Double.NaN
                        : Math.round(requestCharge * Math.pow(10, 2)) / Math.pow(10, 2));

                    break;

                case "retryAfterMilliseconds":

                    result = reader.readUInt32((Out<Long>) out);

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    batchOperationResult.get().setRetryAfter(Duration.ofMillis((Long) out.get()));
                    break;

                default:

                    logger.debug("unrecognized field skipped: {}", path);
                    break;
            }
        }

        return Result.SUCCESS;
    }
}
