// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serializer;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * The default Cosmos JSON.NET serializer.
 */
public final class CosmosJsonDotNetSerializer extends CosmosSerializer {

    private static final Encoding DefaultEncoding = new UTF8Encoding(false, true);
    private JsonSerializerSettings SerializerSettings;

    /**
     * Create a serializer that uses the JSON.net serializer
     * <p>
     * <p>
     * This is internal to reduce exposure of JSON.net types so it is easier to convert to System.Text.Json
     */
    public CosmosJsonDotNetSerializer() {
        this.SerializerSettings = null;
    }

    /**
     * Create a serializer that uses the JSON.net serializer
     * <p>
     * <p>
     * This is internal to reduce exposure of JSON.net types so it is easier to convert to System.Text.Json
     */
    public CosmosJsonDotNetSerializer(CosmosSerializationOptions cosmosSerializerOptions) {
        if (cosmosSerializerOptions == null) {
            this.SerializerSettings = null;
            return;
        }

        JsonSerializerSettings jsonSerializerSettings = new JsonSerializerSettings();
        jsonSerializerSettings.NullValueHandling = cosmosSerializerOptions.getIgnoreNullValues() ?
            NullValueHandling.Ignore : NullValueHandling.Include;
        jsonSerializerSettings.Formatting = cosmosSerializerOptions.getIndented() ? Formatting.Indented :
            Formatting.None;
        jsonSerializerSettings.ContractResolver =
            cosmosSerializerOptions.getPropertyNamingPolicy() == CosmosPropertyNamingPolicy.CamelCase ?
                new CamelCasePropertyNamesContractResolver() : null;

        this.SerializerSettings = jsonSerializerSettings;
    }

    /**
     * Create a serializer that uses the JSON.net serializer
     * <p>
     * <p>
     * This is internal to reduce exposure of JSON.net types so it is easier to convert to System.Text.Json
     */
    public CosmosJsonDotNetSerializer(JsonSerializerSettings jsonSerializerSettings) {
        //C# TO JAVA CONVERTER TODO TASK: Throw expressions are not converted by C# to Java Converter:
        //ORIGINAL LINE: this.SerializerSettings = jsonSerializerSettings ?? throw new ArgumentNullException(nameof
        // (jsonSerializerSettings));
        this.SerializerSettings = jsonSerializerSettings != null ? jsonSerializerSettings :
        throw new NullPointerException("jsonSerializerSettings");
    }

    /**
     * Convert a Stream to the passed in type.
     *
     * <typeparam name="T">The type of object that should be deserialized</typeparam>
     *
     * @param inputStream An open stream that is readable that contains JSON
     *
     * @return The object representing the deserialized stream
     */
    @Override
    public <T> T FromStream(InputStream inputStream) {
        try (inputStream) {
            //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is
            // input or output:
            if (T.class.isAssignableFrom(Stream.class)) {
                return (T) inputStream;
            }

            try (InputStreamReader sr = new InputStreamReader(inputStream)) {
                try (JsonTextReader jsonTextReader = new JsonTextReader(sr)) {
                    JsonSerializer jsonSerializer = this.GetSerializer();
                    return jsonSerializer.<T>Deserialize(jsonTextReader);
                }
            }
        }
    }

    /**
     * Converts an object to a open readable stream
     *
     * <typeparam name="T">The type of object being serialized</typeparam>
     *
     * @param input The object to be serialized
     *
     * @return An open readable stream containing the JSON of the serialized object
     */
    //C# TO JAVA CONVERTER TODO TASK: C# to Java Converter cannot determine whether this System.IO.Stream is input or
    // output:
    @Override
    public <T> InputStream ToStream(T input) {
        ByteArrayOutputStream streamPayload = new ByteArrayOutputStream();
        try (OutputStreamWriter streamWriter = new OutputStreamWriter(streamPayload)) {
            try (JsonWriter writer = new JsonTextWriter(streamWriter)) {
                writer.Formatting = Newtonsoft.Json.Formatting.None;
                JsonSerializer jsonSerializer = this.GetSerializer();
                jsonSerializer.Serialize(writer, input);
                writer.Flush();
                streamWriter.flush();
            }
        }

        streamPayload.Position = 0;
        return streamPayload;
    }

    /**
     * JsonSerializer has hit a race conditions with custom settings that cause null reference exception. To avoid the
     * race condition a new JsonSerializer is created for each call
     */
    private JsonSerializer GetSerializer() {
        return JsonSerializer.Create(this.SerializerSettings);
    }
}
