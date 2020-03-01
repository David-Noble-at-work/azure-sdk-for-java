// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.unimplemented;

public class RequestMessage {

    public Headers getHeaders() {
        throw new UnsupportedOperationException();
    }

    public static final class Headers {

        public String getPartitionKeyRangeId() {
            throw new UnsupportedOperationException();
        }

        public Headers setPartitionKeyRangeId(String value) {
            throw new UnsupportedOperationException();
        }

        public static ResponseMessage.Headers.Builder builder() {
            return new ResponseMessage.Headers.Builder();
        }

        public String get(String name) {
            throw new UnsupportedOperationException();
        }

        public <T> T put(String name, T value) {
            throw new UnsupportedOperationException();
        }

        public static final class Builder {
            public ResponseMessage.Headers build() {
                return new ResponseMessage.Headers();
            }
        }
    }
}
