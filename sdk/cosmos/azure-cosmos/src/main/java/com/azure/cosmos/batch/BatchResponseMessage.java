// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.CosmosError;
import com.azure.cosmos.Resource;
import com.azure.cosmos.batch.unimplemented.CosmosDiagnosticsContext;
import com.azure.cosmos.implementation.HttpConstants.HttpHeaders;
import com.azure.cosmos.implementation.RxDocumentServiceResponse;
import com.azure.cosmos.implementation.directconnectivity.StoreResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.base.Preconditions.throwUnsupportedOperationException;

/**
 * Represents a batch response message.
 */
public final class BatchResponseMessage extends Resource implements AutoCloseable {

    private final CosmosDiagnosticsContext diagnosticsContext;
    private final RxDocumentServiceResponse documentServiceResponse;
    private final CosmosError error;
    private final String errorMessage;
    private final Headers headers;
    private final BatchRequestMessage requestMessage;
    private final HttpResponseStatus status;

    BatchResponseMessage(
        @Nonnull final HttpResponseStatus status,
        @Nullable final BatchRequestMessage requestMessage,
        @Nullable final String errorMessage,
        @Nullable final CosmosError error,
        @Nonnull final Headers headers,
        @Nonnull final InputStream content,
        @Nonnull final CosmosDiagnosticsContext diagnosticsContext) throws IOException {

        checkNotNull(status, "expected non-null status");
        checkNotNull(headers, "expected non-null headers");
        checkNotNull(content, "expected non-null content");
        checkNotNull(diagnosticsContext, "expected non-null diagnosticsContext");

        this.documentServiceResponse = new RxDocumentServiceResponse(new StoreResponse(
            status.code(),
            headers.asList(),
            BatchExecUtils.readAll(content)));

        this.diagnosticsContext = diagnosticsContext;
        this.error = error;
        this.errorMessage = errorMessage;
        this.headers = headers;
        this.requestMessage = requestMessage;
        this.status = status;
    }

    /**
     * Gets a stream for reading the body part of the current {@link BatchResponseMessage batch response message}.
     *
     * @return a stream for reading the body part of the current {@link BatchResponseMessage batch response message}.
     */
    @Nonnull
    public InputStream getContent() {
        return new ByteArrayInputStream(this.documentServiceResponse.getResponseBodyAsByteArray());
    }

    /***
     * Gets the {@link CosmosDiagnosticsContext diagnostics context} for the current {@link BatchResponseMessage batch
     * response message}.
     *
     * @return the {@link CosmosDiagnosticsContext diagnostics context} for the current {@link BatchResponseMessage
     * batch response message}.
     */
    @Nonnull
    public CosmosDiagnosticsContext getDiagnosticsContext() {
        return this.diagnosticsContext;
    }

    /***
     * Gets the {@link CosmosDiagnosticsContext diagnostics context} for the current {@link BatchResponseMessage batch
     * response message}.
     *
     * @return the {@link CosmosDiagnosticsContext diagnostics context} for the current {@link BatchResponseMessage
     * batch response message}.
     */
    @Nullable
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /***
     * Gets the {@link BatchResponseMessage.Headers headers} part of the current {@link BatchResponseMessage batch
     * response message}.
     *
     * @return the {@link BatchResponseMessage.Headers headers} part of the current {@link BatchResponseMessage batch
     * response message}.
     */
    @Nonnull
    public Headers getHeaders() {
        return this.headers;
    }

    /***
     * Gets the address of the resource represented by the current {@link BatchResponseMessage batch response message}.
     *
     * @return the address of the resource represented by the current {@link BatchResponseMessage batch response
     * message}.
     */
    public String getResourceAddress() {
        // TODO (DANOBLE) implement BatchResponseMessage.getResourceAddress
        throwUnsupportedOperationException("%s.%s", BatchResponseMessage.class, "getResourceAddress");
        return null;
    }

    /***
     * Gets the {@link HttpResponseStatus status} of the current {@link BatchResponseMessage batch response message}.
     *
     * @return the {@link HttpResponseStatus status} of the current {@link BatchResponseMessage batch response message}.
     */
    @Nonnull
    public HttpResponseStatus getStatus() {
        return this.status;
    }

    /***
     * Gets the {@link HttpResponseStatus#code() status code} of the current {@link BatchResponseMessage batch response
     * message}.
     *
     * @return the {@link HttpResponseStatus#code() status code} of the current {@link BatchResponseMessage batch
     * response message}.
     */
    public int getStatusCode() {
        return this.status.code();
    }

    /***
     * Gets the {@link CosmosDiagnosticsContext diagnostics context} for the current {@link BatchResponseMessage batch
     * response message}.
     *
     * @return the {@link CosmosDiagnosticsContext diagnostics context} for the current {@link BatchResponseMessage
     * batch response message}.
     */
    public boolean isSuccessStatus() {
        return this.status.codeClass() == HttpStatusClass.SUCCESS;
    }

    @Override
    public void close() {
        // TODO (DANOBLE) consider removing this AutoClosable implementation as it is currently unnecessary.
    }

    /**
     * Represents the headers part of a {@link BatchResponseMessage batch response message}.
     */
    public static final class Headers {

        private final Map<String, Object> headers;

        private Headers(Map<String, Object> headers) {
            this.headers = headers;
        }

        public String get(String name) {
            return this.headers.get(name).toString();
        }

        public String getActivityId() {
            return (String) this.headers.get(HttpHeaders.ACTIVITY_ID);
        }

        public double getRequestCharge() {
            return (double) this.headers.get(HttpHeaders.REQUEST_CHARGE);
        }

        public Duration getRetryAfter() {
            return Duration.ofMillis((long) this.headers.get(HttpHeaders.RETRY_AFTER_IN_MILLISECONDS));
        }

        public int getSubStatusCode() {
            return (int) this.headers.get(HttpHeaders.SUB_STATUS);
        }

        public static Builder builder() {
            return new Builder();
        }

        List<Entry<String, String>> asList() {
            Stream<Entry<String, String>> stream = this.headers.entrySet().stream().map(
                e -> new SimpleImmutableEntry<>(e.getKey(), e.getValue().toString()));
            return Collections.unmodifiableList(stream.collect(Collectors.toList()));
        }

        /**
         * A factory for constructing {@link BatchResponseMessage.Headers}.
         */
        public static final class Builder {

            final Map<String, Object> headers = new HashMap<>();

            public Headers build() {
                return new Headers(this.headers);
            }

            public Builder activityId(@Nonnull final UUID value) {
                checkNotNull(value, "expected non-null value");
                this.headers.put(HttpHeaders.ACTIVITY_ID, value.toString());
                return this;
            }

            public Builder etag(@Nonnull final String value) {
                checkNotNull(value, "expected non-null value");
                this.headers.put(HttpHeaders.E_TAG, value);
                return this;
            }

            public Builder requestCharge(final double value) {
                this.headers.put(HttpHeaders.REQUEST_CHARGE, value);
                return this;
            }

            public Builder retryAfter(@Nonnull final Duration value) {
                checkNotNull(value, "expected non-null value");
                this.headers.put(HttpHeaders.RETRY_AFTER_IN_MILLISECONDS, value.toMillis());
                return this;
            }

            public Builder subStatusCode(final int value) {
                this.headers.put(HttpHeaders.SUB_STATUS, value);
                return this;
            }
        }
    }
}
