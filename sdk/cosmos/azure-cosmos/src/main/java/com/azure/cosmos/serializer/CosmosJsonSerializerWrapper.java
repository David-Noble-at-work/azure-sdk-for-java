// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serializer;

import java.io.InputStream;

public class CosmosJsonSerializerWrapper extends CosmosSerializer {
    private CosmosSerializer InternalJsonSerializer;

    public CosmosJsonSerializerWrapper(CosmosSerializer cosmosJsonSerializer) {
        //C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
        //ORIGINAL LINE: this.InternalJsonSerializer = cosmosJsonSerializer ?? throw new ArgumentNullException(nameof
        // (cosmosJsonSerializer));
        this.InternalJsonSerializer = cosmosJsonSerializer != null ? cosmosJsonSerializer :
        throw new NullPointerException("cosmosJsonSerializer");
    }

    public final CosmosSerializer getInternalJsonSerializer() {
        return InternalJsonSerializer;
    }

    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    @Override
    public <T> T FromStream(InputStream inputStream) {
        T item = this.getInternalJsonSerializer().FromStream(inputStream);
        if (inputStream.CanRead) {
            throw new IllegalStateException("Json Serializer left an open stream.");
        }

        return item;
    }

    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    @Override
    public <T> InputStream ToStream(T input) {
        //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is
        // input or output:
        Stream stream = this.getInternalJsonSerializer().ToStream(input);
        if (stream == null) {
            throw new IllegalStateException("Json Serializer returned a null stream.");
        }

        if (!stream.CanRead) {
            throw new IllegalStateException("Json Serializer returned a closed stream.");
        }

        return stream;
    }
}
