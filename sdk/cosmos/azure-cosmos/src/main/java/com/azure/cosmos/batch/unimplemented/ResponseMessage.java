// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.unimplemented;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.InputStream;
import java.time.Duration;

public final class ResponseMessage implements AutoCloseable {

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
    }
}
