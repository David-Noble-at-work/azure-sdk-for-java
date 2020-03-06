// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.implementation.RxDocumentServiceRequest;
import com.azure.cosmos.implementation.routing.PartitionKeyRangeIdentity;

import javax.annotation.Nonnull;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * Represents a batch request message.
 */
public class BatchRequestMessage {

    final Headers headers;
    final RxDocumentServiceRequest request;

    BatchRequestMessage(@Nonnull final RxDocumentServiceRequest request) {
        checkNotNull(request, "expected non-null request");
        this.request = request;
        this.headers = new Headers();
    }

    /**
     * Returns the headers part of the current {@link BatchRequestMessage batch request message}.
     *
     * @return headers part of the current {@link BatchRequestMessage batch request message}.
     */
    public Headers getHeaders() {
        return this.headers;
    }

    /**
     * Represents the headers part of a {@link BatchRequestMessage batch request message}.
     */
    public final class Headers {

        /**
         * Gets the partition key range ID from the current {@link BatchRequestMessage.Headers batch request message
         * headers}.
         *
         * @return the partition key range ID.
         */
        public String getPartitionKeyRangeId() {
            return request.getPartitionKeyRangeIdentity().getPartitionKeyRangeId();
        }

        /**
         * Sets the partition key range ID into the current {@link BatchRequestMessage.Headers batch request message
         * headers}.
         *
         * @param value a partition key range ID.
         *
         * @return a reference to the current {@link BatchRequestMessage.Headers batch request message headers}.
         */
        public Headers setPartitionKeyRangeId(String value) {
            request.setPartitionKeyRangeIdentity(new PartitionKeyRangeIdentity(value));
            return this;
        }

        /**
         * Gets the value of the named header from the current {@link BatchRequestMessage.Headers batch request message
         * headers}.
         *
         * @param name a header name.
         *
         * @return value of the header or {@code null}.
         */
        public String get(String name) {
            return request.getHeaders().get(name);
        }

        /**
         * Sets the value of the named header in the current {@link BatchRequestMessage.Headers batch request message
         * headers}.
         *
         * @param <T> the type of value.
         * @param name a header name.
         * @param value a value for the header which is converted to a string using the {@link Object#toString} method.
         *
         * @return a reference to the current {@link BatchRequestMessage.Headers batch request message headers}.
         */
        public <T> Headers set(String name, T value) {
            request.getHeaders().put(name, value.toString());
            return this;
        }
    }
}
