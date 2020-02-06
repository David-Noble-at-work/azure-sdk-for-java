// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serializer;

public final class CosmosSerializationFormatOptions {
    /**
     *          What serialization format to request the response in from the backend
     */
    private String ContentSerializationFormat;
    /**
     *          Creates a navigator that can navigate a JSON in the specified ContentSerializationFormat
     */
    private CreateCustomNavigator CreateCustomNavigatorCallback;
    
    /**
     *          Creates a writer to use to write out the stream.
     */
    private CreateCustomWriter CreateCustomWriterCallback;

    public CosmosSerializationFormatOptions(String contentSerializationFormat,
                                            CreateCustomNavigator createCustomNavigator,
                                            CreateCustomWriter createCustomWriter) {
        if (contentSerializationFormat == null) {
            throw new NullPointerException("contentSerializationFormat");
        }

        if (createCustomNavigator == null) {
            throw new NullPointerException("createCustomNavigator");
        }

        if (createCustomWriter == null) {
            throw new NullPointerException("createCustomWriter");
        }

        this.ContentSerializationFormat = contentSerializationFormat;
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: this.CreateCustomNavigatorCallback = (ReadOnlyMemory<byte> content) -> createCustomNavigator
        // .invoke(content);
        this.CreateCustomNavigatorCallback = (ReadOnlyMemory<Byte> content) -> createCustomNavigator.invoke(content);
        this.CreateCustomWriterCallback = () -> createCustomWriter.invoke();
    }

        

    public String getContentSerializationFormat() {
        return ContentSerializationFormat;
    }

    public CreateCustomNavigator getCreateCustomNavigatorCallback() {
        return CreateCustomNavigatorCallback;
    }

        

    public CreateCustomWriter getCreateCustomWriterCallback() {
        return CreateCustomWriterCallback;
    }

    @FunctionalInterface
    public interface CreateCustomNavigator {
        IJsonNavigator invoke(ReadOnlyMemory<Byte> content);
    }

    @FunctionalInterface
    public interface CreateCustomWriter {
        IJsonWriter invoke();
    }
}
