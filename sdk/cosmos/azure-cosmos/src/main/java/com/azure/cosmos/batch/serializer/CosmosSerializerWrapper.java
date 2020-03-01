// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.serializer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class CosmosSerializerWrapper implements CosmosSerializer {

    private final CosmosSerializer serializer;

    public CosmosSerializerWrapper(CosmosSerializer serializer) {
        checkNotNull(serializer, "expected non-null serializer");
        this.serializer = serializer;
    }

    public final CosmosSerializer getSerializer() {
        return serializer;
    }

    @Override
    public <T> T fromStream(@Nonnull final InputStream inputStream, @Nonnull final Class<T> type) throws IOException {

        checkNotNull(inputStream, "expected non-null inputStream");
        checkNotNull(type, "expected non-null type");

        final T object;

        try {
            object = this.getSerializer().fromStream(inputStream, type);
            checkState(inputStream.available() == 0, "expected closed inputStream");
        } finally {
            inputStream.close();
        }

        return object;
    }

    @Override
    public <T> InputStream toStream(T object) throws IOException {

        checkNotNull(object, "expected non-null object");

        InputStream inputStream = this.getSerializer().toStream(object);
        checkState(inputStream != null, "expected non-null inputStream");
        checkState(inputStream.available() > 0, "expected readable inputStream");

        return inputStream;
    }
}
