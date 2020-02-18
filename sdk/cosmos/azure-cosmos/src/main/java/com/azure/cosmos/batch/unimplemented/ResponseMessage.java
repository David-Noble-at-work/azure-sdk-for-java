// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.unimplemented;

import com.azure.cosmos.CosmosError;
import io.netty.handler.codec.http.HttpResponseStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.time.Duration;

public final class ResponseMessage implements AutoCloseable {

    public ResponseMessage(
        @Nonnull final HttpResponseStatus status,
        @Nullable final RequestMessage requestMessage,
        @Nullable final String errorMessage,
        @Nullable final CosmosError error,
        @Nonnull final Headers headers,
        @Nonnull final InputStream content,
        @Nonnull final CosmosDiagnosticsContext diagnosticsContext) {
    }

    public InputStream getContent() {
        throw new UnsupportedOperationException();
    }

    public CosmosDiagnosticsContext getDiagnosticsContext() {
        throw new UnsupportedOperationException();
    }

    public String getErrorMessage() {
        throw new UnsupportedOperationException();
    }

    public Headers getHeaders() {
        throw new UnsupportedOperationException();
    }

    public HttpResponseStatus getStatus() {
        throw new UnsupportedOperationException();
    }

    public boolean isSuccessStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    public static final class Headers {

        public static Builder builder() {
            return new Builder();
        }

        public String get(String name) {
            throw new UnsupportedOperationException();
        }

        public String getActivityId() {
            throw new UnsupportedOperationException();
        }

        public double getRequestCharge() {
            throw new UnsupportedOperationException();
        }

        public Duration getRetryAfter() {
            throw new UnsupportedOperationException();
        }

        public int getSubStatusCode() {
            throw new UnsupportedOperationException();
        }

        public static final class Builder {

            String etag;
            double requestCharge;
            Duration retryAfter;
            int subStatusCode;

            public Headers build() {
                return new Headers();
            }

            public Builder etag(@Nullable final String value) {
                this.etag = value;
                return this;
            }

            public Builder requestCharge(final double value) {
                this.requestCharge = value;
                return this;
            }

            public Builder retryAfter(@Nullable final Duration value) {
                this.retryAfter = value;
                return this;
            }

            public Builder subStatusCode(final int value) {
                this.subStatusCode = value;
                return this;
            }
        }
    }
}
