// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.serializer;

import com.azure.cosmos.CosmosConflictProperties;
import com.azure.cosmos.CosmosContainerProperties;
import com.azure.cosmos.CosmosDatabaseProperties;
import com.azure.cosmos.CosmosPermissionProperties;
import com.azure.cosmos.CosmosStoredProcedureProperties;
import com.azure.cosmos.CosmosTriggerProperties;
import com.azure.cosmos.CosmosUserDefinedFunctionProperties;
import com.azure.cosmos.CosmosUserProperties;
import com.azure.cosmos.DatabaseAccount;
import com.azure.cosmos.SqlQuerySpec;
import com.azure.cosmos.implementation.Offer;
import com.azure.cosmos.implementation.ResourceType;
import com.azure.cosmos.implementation.query.PartitionedQueryExecutionInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

import static com.azure.cosmos.implementation.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.base.Preconditions.throwUnsupportedOperationException;

/**
 * This is an interface to allow a custom serializer to be used by the CosmosClient
 */
public final class CosmosSerializerCore {

    private static final CosmosSerializer DEFAULT_SERIALIZER = new CosmosSerializerWrapper(
        new CosmosJacksonSerializer((CosmosSerializationOptions) null));

    private final CosmosSerializer objectSerializer;

    /**
     * Initializes a new {@link CosmosSerializerCore} instance from a {@link CosmosSerializer} instance.
     *
     * @param serializer a {@link CosmosSerializer} instance.
     */
    private CosmosSerializerCore(@Nullable final CosmosSerializer serializer) {

        this.objectSerializer = new CosmosSerializerWrapper(serializer != null ? serializer : DEFAULT_SERIALIZER);

        // TODO (DANOBLE) just serialize using jackson
        //  Goal:
        //  Create the thinnest JSON serialization wrapper possible to avoid porting all of the C# element model.
        //  Assertion:
        //  In the Java SDK there's nothing special about serializing a query spec to JSON. This assertion is based on
        //  a conversation I had with Bhaskar on Tue 2020-03-03.
        //  Conclusion:
        //  The sqlQuerySpecSerializer field is irrelevant.

//        this.sqlQuerySpecSerializer = CosmosSqlQuerySpecJsonConverter.CreateSqlQuerySpecSerializer(
//            this.objectSerializer,
//            CosmosSerializerCore.DEFAULT_SERIALIZER);
    }

    /**
     * Constructs a new {@link CosmosSerializerCore} instance with the given {@link CosmosSerializationFormatOptions
     * options}.
     *
     * @param options {@link CosmosSerializationOptions options}.
     *
     * @return a new {@link CosmosSerializerCore} instance.
     */
    public static CosmosSerializerCore create(final CosmosSerializationOptions options) {
        return new CosmosSerializerCore(new CosmosSerializerWrapper(new CosmosJacksonSerializer(options)));
    }

    /**
     * Constructs a new {@link CosmosSerializerCore} instance from the given {@link CosmosSerializer serializer}.
     *
     * @param serializer a {@link CosmosSerializer serializer}.
     *
     * @return a new {@link CosmosSerializerCore} instance.
     */
    public static CosmosSerializerCore create(@Nonnull final CosmosSerializer serializer) {
        checkNotNull(serializer, "expected non-null serializer");
        return new CosmosSerializerCore(new CosmosSerializerWrapper(serializer));
    }

    /**
     * From feed response stream iterable.
     *
     * @param <T> the type parameter
     * @param inputStream the input stream
     * @param resourceType the resource type
     *
     * @return the iterable
     */
    public <T> Iterable<T> fromFeedResponseStream(InputStream inputStream, ResourceType resourceType) {

        // TODO: Resurrect this method when it comes time to support feed response streams

//        CosmosArray cosmosArray = CosmosElementSerializer.ToCosmosElements(inputStream, resourceType);
//        return CosmosElementSerializer.<T>GetResources(cosmosArray, this);

        throwUnsupportedOperationException("CosmosSerializerCore.fromFeedResponseStream");
        return null;
    }

    /**
     * From stream t.
     *
     * @param <T> the type parameter
     * @param inputStream the input stream
     * @param type the type
     *
     * @return the t
     *
     * @throws IOException the io exception
     */
    public <T> T fromStream(
        @Nonnull final InputStream inputStream, @Nonnull final Class<T> type) throws IOException {

        checkNotNull(inputStream, "expected non-null inputStream");
        checkNotNull(type, "expected non-null type");

        return this.getSerializer(type).fromStream(inputStream, type);
    }

    /**
     * To stream input stream.
     *
     * @param <T> the type parameter
     * @param object the object
     *
     * @return the input stream
     *
     * @throws IOException the io exception
     */
    public <T> InputStream toStream(@Nonnull final T object) throws IOException {
        checkNotNull(object, "expected non-null object");
        return this.getSerializer(object.getClass()).toStream(object);
    }

    /**
     * Creates a new {@link InputStream input stream} for reading the given {@link SqlQuerySpec SQL query spec} for the
     * given {@link ResourceType resource type}.
     *
     * @param input a {@link SqlQuerySpec SQL query spec}.
     * @param resourceType the {@link ResourceType resource type} that the {@code input} returns.
     *
     * @return a new {@link InputStream input stream}.
     *
     * @throws IOException if a new stream cannot be created from the input.
     */
    @Nonnull
    public InputStream toStream(SqlQuerySpec input, ResourceType resourceType) throws IOException {

        // Public resource types that support query use the current serializer while internal resource types such as
        // offers use the default serializer

        final CosmosSerializer serializer = resourceType == ResourceType.Database
            || resourceType == ResourceType.DocumentCollection
            || resourceType == ResourceType.Document
            || resourceType == ResourceType.Trigger
            || resourceType == ResourceType.UserDefinedFunction
            || resourceType == ResourceType.StoredProcedure
            || resourceType == ResourceType.Permission
            || resourceType == ResourceType.User
            || resourceType == ResourceType.Conflict
            ? this.objectSerializer
            : DEFAULT_SERIALIZER;

        return serializer.toStream(input);
    }

    private <T> CosmosSerializer getSerializer(@Nonnull final Class<T> type) {

        checkNotNull(type, "expected non-null type");

        checkArgument(type != SqlQuerySpec.class, "%s to stream must use the %s override",
            SqlQuerySpec.class.getSimpleName(),
            SqlQuerySpec.class.getSimpleName());

        // TODO (DANOBLE) consider porting ThroughputResponse from v3 because it may map most closely to
        //  ThroughputProperties

        return type == DatabaseAccount.class
            || type == CosmosDatabaseProperties.class
            || type == CosmosContainerProperties.class
            || type == CosmosPermissionProperties.class
            || type == CosmosStoredProcedureProperties.class
            || type == CosmosTriggerProperties.class
            || type == CosmosUserDefinedFunctionProperties.class
            || type == CosmosUserProperties.class
            || type == CosmosConflictProperties.class
            // || type == ThroughputProperties.class
            || type == Offer.class
            || type == PartitionedQueryExecutionInfo.class
            ? CosmosSerializerCore.DEFAULT_SERIALIZER
            : this.objectSerializer;
    }
}
