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
import com.azure.cosmos.SqlQuerySpec;
import com.azure.cosmos.implementation.ResourceType;
import com.azure.cosmos.implementation.query.PartitionedQueryExecutionInfo;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is an interface to allow a custom serializer to be used by the CosmosClient
 */
public class CosmosSerializerCore {

    private static final CosmosSerializer DEFAULT_SERIALIZER = new CosmosSerializerWrapper(
        new CosmosJacksonSerializer((CosmosSerializationOptions) null));

    private final CosmosSerializer objectSerializer;
    private final CosmosSerializer sqlQuerySpecSerializer;

    public CosmosSerializerCore() {
        this.objectSerializer = null;
        this.sqlQuerySpecSerializer = null;
    }

    public CosmosSerializerCore(final CosmosSerializer serializer) {

        this.objectSerializer = new CosmosSerializerWrapper(serializer);

        this.sqlQuerySpecSerializer = CosmosSqlQuerySpecJsonConverter.CreateSqlQuerySpecSerializer(
            this.objectSerializer,
            CosmosSerializerCore.DEFAULT_SERIALIZER);
    }

    public static CosmosSerializerCore Create(final CosmosSerializationOptions options) {
        return new CosmosSerializerCore(new CosmosSerializerWrapper(new CosmosJacksonSerializer(options)));
    }

    public static CosmosSerializerCore Create(final CosmosSerializer serializer) {
        return new CosmosSerializerCore(new CosmosSerializerWrapper(serializer));
    }

    public final <T> Iterable<T> FromFeedResponseStream(InputStream inputStream, ResourceType resourceType) {
        CosmosArray cosmosArray = CosmosElementSerializer.ToCosmosElements(inputStream, resourceType);
        return CosmosElementSerializer.<T>GetResources(cosmosArray, this);
    }

    public final <T> T fromStream(
        @Nonnull final InputStream inputStream, @Nonnull final Class<T> type) throws IOException {

        checkNotNull(inputStream, "expected non-null inputStream");
        checkNotNull(type, "expected non-null type");

        return this.getSerializer(type).fromStream(inputStream, type);
    }

    public final <T> InputStream toStream(@Nonnull final T object) throws IOException {
        checkNotNull(object, "expected non-null object");
        return this.getSerializer(object.getClass()).toStream(object);
    }

    public final InputStream ToStreamSqlQuerySpec(SqlQuerySpec input, ResourceType resourceType) {

        // Public resource types that support query use the current serializer while internal resource types such as
        // offers use the default serializer

        final CosmosSerializer serializer = this.objectSerializer != null && (resourceType == ResourceType.Database
            || resourceType == ResourceType.DocumentCollection
            || resourceType == ResourceType.Document
            || resourceType == ResourceType.Trigger
            || resourceType == ResourceType.UserDefinedFunction
            || resourceType == ResourceType.StoredProcedure
            || resourceType == ResourceType.Permission
            || resourceType == ResourceType.User
            || resourceType == ResourceType.Conflict)
            ? this.sqlQuerySpecSerializer
            : DEFAULT_SERIALIZER;

        return serializer.toStream(input);
    }

    private <T> CosmosSerializer getSerializer(@Nonnull final Class<T> type) {

        checkNotNull(type, "expected non-null type");

        checkArgument(type != SqlQuerySpec.class, "%s to stream must use the %s override",
            SqlQuerySpec.class.getSimpleName(),
            SqlQuerySpec.class.getSimpleName());

        if (this.objectSerializer == null) {
            return CosmosSerializerCore.DEFAULT_SERIALIZER;
        }

        if (type == AccountProperties.class
            || type == CosmosDatabaseProperties.class
            || type == CosmosContainerProperties.class
            || type == CosmosPermissionProperties.class
            || type == CosmosStoredProcedureProperties.class
            || type == CosmosTriggerProperties.class
            || type == CosmosUserDefinedFunctionProperties.class
            || type == CosmosUserProperties.class
            || type == CosmosConflictProperties.class
            || type == ThroughputProperties.class
            || type == OfferV2.class
            || type == PartitionedQueryExecutionInfo.class) {
            return CosmosSerializerCore.DEFAULT_SERIALIZER;
        }

        return this.objectSerializer;
    }
}
