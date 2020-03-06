// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
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
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.base.Preconditions.checkState;

/**
 * Represents a result for a specific operation that was part of a {@link TransactionalBatch} request.
 *
 * @param <TResource> the type parameter
 */
public class TransactionalBatchOperationResult<TResource> {

    private static final Logger logger = LoggerFactory.getLogger(TransactionalBatchOperationResult.class);

    private CosmosDiagnosticsContext diagnosticsContext;
    private String eTag;
    private double requestCharge;
    private TResource resource;
    private InputStream resourceStream;

    /**
     * Gets the completion status of the operation.
     */
    private HttpResponseStatus responseStatus;
    /**
     * In case the operation is rate limited, indicates the time post which a retry can be attempted.
     */
    private Duration retryAfter;
    /**
     * Gets detail on the completion status of the operation.
     */
    private int subStatusCode;

    /**
     * Instantiates a new Transactional batch operation result.
     *
     * @param responseStatus the response status
     */
    public TransactionalBatchOperationResult(final HttpResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    /**
     * Instantiates a new Transactional batch operation result.
     *
     * @param other the other
     */
    public TransactionalBatchOperationResult(@Nonnull final TransactionalBatchOperationResult<TResource> other) {

        checkNotNull(other, "expected non-null other");

        this.responseStatus = other.responseStatus;
        this.subStatusCode = other.subStatusCode;
        this.eTag = other.eTag;
        this.resourceStream = other.resourceStream;
        this.requestCharge = other.requestCharge;
        this.retryAfter = other.retryAfter;

        this.resource = null;
        this.diagnosticsContext = null;
    }

    /**
     * Instantiates a new Transactional batch operation result.
     *
     * @param result the result
     * @param resource the resource
     */
    public TransactionalBatchOperationResult(TransactionalBatchOperationResult<TResource> result, TResource resource) {
        this(result);
        this.resource = resource;
    }

    /**
     * Initializes a new instance of the {@link TransactionalBatchOperationResult} class.
     */
    protected TransactionalBatchOperationResult() {
    }

    /**
     * Gets the Cosmos diagnostic information for the current request to the Azure Cosmos DB service.
     *
     * @return Cosmos diagnostic information for the current request to the Azure Cosmos DB service.
     */
    public CosmosDiagnosticsContext getDiagnosticsContext() {
        return this.diagnosticsContext;
    }

    /**
     * Sets diagnostics context.
     *
     * @param value the value
     *
     * @return the diagnostics context
     */
    public TransactionalBatchOperationResult<TResource> setDiagnosticsContext(final CosmosDiagnosticsContext value) {
        this.diagnosticsContext = value;
        return this;
    }

    /**
     * Gets the entity tag associated with the current resource.
     * <p>
     * ETags are used for concurrency checking when updating resources.
     *
     * @return Entity tag associated with the current resource.
     */
    public String getETag() {
        return this.eTag;
    }

    /**
     * Sets e tag.
     *
     * @param value the value
     *
     * @return the e tag
     */
    public TransactionalBatchOperationResult<TResource> setETag(final String value) {
        this.eTag = value;
        return this;
    }

    /**
     * Gets the request charge in request units for the current operation.
     *
     * @return Request charge in request units for the current operation.
     */
    public double getRequestCharge() {
        return requestCharge;
    }

    /**
     * Sets request charge.
     *
     * @param value the value
     *
     * @return the request charge
     */
    public TransactionalBatchOperationResult<TResource> setRequestCharge(final double value) {
        this.requestCharge = value;
        return this;
    }

    /**
     * Gets the resource associated with the current result.
     *
     * @return Resource associated with the current result.
     */
    public TResource getResource() {
        return this.resource;
    }

    /**
     * Sets resource.
     *
     * @param value the value
     *
     * @return the resource
     */
    public TransactionalBatchOperationResult<TResource> setResource(final TResource value) {
        this.resource = value;
        return this;
    }

    /**
     * Gets the content of the resource associated with the current result as an {@link InputStream}.
     *
     * @return Content of the resource associated with the current result as an {@link InputStream}.
     */
    public InputStream getResourceStream() {
        return this.resourceStream;
    }

    /**
     * Sets resource stream.
     *
     * @param value the value
     *
     * @return the resource stream
     */
    public TransactionalBatchOperationResult<TResource> setResourceStream(final InputStream value) {
        this.resourceStream = value;
        return this;
    }

    /**
     * Gets retry after.
     *
     * @return the retry after
     */
    public Duration getRetryAfter() {
        return this.retryAfter;
    }

    /**
     * Sets retry after.
     *
     * @param value the value
     *
     * @return the retry after
     */
    public TransactionalBatchOperationResult<TResource> setRetryAfter(final Duration value) {
        this.retryAfter = value;
        return this;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public HttpResponseStatus getStatus() {
        return this.responseStatus;
    }

    /**
     * Gets status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return this.responseStatus.code();
    }

    /**
     * Gets sub status code.
     *
     * @return the sub status code
     */
    public int getSubStatusCode() {
        return this.subStatusCode;
    }

    /**
     * Sets sub status code.
     *
     * @param value the value
     *
     * @return the sub status code
     */
    public TransactionalBatchOperationResult<TResource> setSubStatusCode(final int value) {
        this.subStatusCode = value;
        return this;
    }

    /**
     * Gets a value indicating whether the current operation completed successfully.
     *
     * @return {@code true} if the current operation completed successfully; {@code false} otherwise.
     */
    public boolean isSuccessStatusCode() {
        final int statusCode = this.responseStatus.code();
        return 200 <= statusCode && statusCode <= 299;
    }

    /**
     * Read batch operation result result.
     *
     * @param in the in
     * @param out the out
     *
     * @return the result
     */
    public static Result readBatchOperationResult(
        @Nonnull final ByteBuf in, @Nonnull final Out<TransactionalBatchOperationResult<?>> out) {

        checkNotNull(in, "expected non-null in");
        checkNotNull(out, "expected non-null out");

        RowBuffer rowBuffer = new RowBuffer(in.readableBytes());

        if (!rowBuffer.readFrom(in, HybridRowVersion.V1,
            BatchSchemaProvider.getBatchLayoutResolverNamespace())) {
            out.set(null);
            return Result.FAILURE;
        }

        Result result = TransactionalBatchOperationResult.readBatchOperationResult(
            new RowReader(rowBuffer),
            out);

        if (result != Result.SUCCESS) {
            return result;
        }

        // Ensure the mandatory fields were populated

        if (out.get().getResponseStatus() == null) {
            return Result.FAILURE;
        }

        return Result.SUCCESS;
    }

    /**
     * Converts the current {@link TransactionalBatchOperationResult transactional batch operation result} to a {@link
     * BatchResponseMessage batch response message}.
     *
     * @return a new {@link BatchResponseMessage batch response message}.
     *
     * @throws IOException if the {@link TransactionalBatchOperationResult result} body cannot be read from the current
     * {@link TransactionalBatchOperationResult#getResourceStream resource stream}.
     */
    public final BatchResponseMessage toResponseMessage() throws IOException {

        BatchResponseMessage.Headers headers = BatchResponseMessage.Headers.builder()
            .subStatusCode(this.getSubStatusCode())
            .etag(this.getETag())
            .retryAfter(this.getRetryAfter())
            .requestCharge(this.requestCharge)
            .build();

        return new BatchResponseMessage(
            this.getResponseStatus(),
            null,
            null,
            null,
            headers,
            this.getResourceStream(),
            this.getDiagnosticsContext() != null ? this.getDiagnosticsContext() : new CosmosDiagnosticsContext());
    }

    /**
     * Gets response status.
     *
     * @return the response status
     */
    protected HttpResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    private TransactionalBatchOperationResult<TResource> setResponseStatus(HttpResponseStatus value) {
        this.responseStatus = value;
        return this;
    }

    @SuppressWarnings("unchecked")
    private static Result readBatchOperationResult(
        @Nonnull final RowReader reader,
        @Nonnull final Out<TransactionalBatchOperationResult<?>> batchOperationResult) {

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
