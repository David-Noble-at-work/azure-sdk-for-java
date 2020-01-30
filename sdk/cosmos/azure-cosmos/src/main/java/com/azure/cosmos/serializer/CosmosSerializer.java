// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serializer;

import com.azure.cosmos.CosmosContainer;

import java.io.InputStream;

/**
 * This is an interface to allow a custom serializer to be used by the CosmosClient
 */
public interface CosmosSerializer {
    /**
     * Deserialize an object from a JSON input stream.
     * <p>
     * The implementation is responsible for closing the {@link InputStream input stream}, whether or not the operation
     * is successful.
     *
     * @param <T> type of object to deserialize from the {@link InputStream input stream}.
     * @param inputStream response containing JSON from Cosmos DB.
     *
     * @return an object deserialized from the {@code inputStream}.
     */
    <T> T FromStream(InputStream inputStream);

    /**
     * Serialize a JSON representation of an input object to a stream.
     * <p>
     * The caller must take ownership of the stream and ensure that it is closed.
     *
     * @param <T> type of object to instantiate from {@code stream}.
     * @param input any type passed to a {@link CosmosContainer}.
     *
     * @return an {@link InputStream input stream} containing a JSON representation of the {@code input} object.
     */
    <T> InputStream ToStream(T input);
}
