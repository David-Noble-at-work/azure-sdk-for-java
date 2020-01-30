// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serializer;

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
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * This is an interface to allow a custom serializer to be used by the CosmosClient
 */
public class CosmosSerializerCore {

    private static final CosmosSerializer DEFAULT_SERIALIZER = new CosmosJsonSerializerWrapper(
        new CosmosJsonDotNetSerializer());

    private final CosmosSerializer serializer;
    private final CosmosSerializer sqlQuerySpecSerializer;

    public CosmosSerializerCore() {
        this.serializer = null;
        this.sqlQuerySpecSerializer = null;
    }

    public CosmosSerializerCore(final CosmosSerializer serializer) {

        this.serializer = new CosmosJsonSerializerWrapper(serializer);
        this.sqlQuerySpecSerializer = CosmosSqlQuerySpecJsonConverter.CreateSqlQuerySpecSerializer(
            this.serializer,
            CosmosSerializerCore.DEFAULT_SERIALIZER);
    }

    public static CosmosSerializerCore Create(final CosmosSerializationOptions options) {
        return new CosmosSerializerCore(new CosmosJsonSerializerWrapper(new CosmosJsonDotNetSerializer(options)));
    }

    public static CosmosSerializerCore Create(final CosmosSerializer serializer) {
        return new CosmosSerializerCore(new CosmosJsonSerializerWrapper(serializer));
    }

    public final <T> Iterable<T> FromFeedResponseStream(InputStream inputStream, ResourceType resourceType) {
        CosmosArray cosmosArray = CosmosElementSerializer.ToCosmosElements(inputStream, resourceType);
        return CosmosElementSerializer.<T>GetResources(cosmosArray, this);
    }

    public final <T> T FromStream(InputStream inputStream) {
        final CosmosSerializer serializer = this.<T>getSerializer();
        return serializer.<T>FromStream(inputStream);
    }

    public final <T> InputStream ToStream(T input) {
        final CosmosSerializer serializer = this.<T>getSerializer();
        return serializer.ToStream(input);
    }

    public final InputStream ToStreamSqlQuerySpec(SqlQuerySpec input, ResourceType resourceType) {

        // Public resource types that support query use the current serializer while internal resource types such as
        // offers use the default serializer

        final CosmosSerializer serializer = this.serializer != null && (resourceType == ResourceType.Database
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

        return serializer.<SqlQuerySpec>ToStream(input);
    }

    private <T> CosmosSerializer getSerializer(@Nonnull final Class<T> type) {

        checkNotNull(type, "expected non-null type");

        Type genericSuperclass = type.getGenericSuperclass();
        checkState(!(genericSuperclass instanceof Class), "expected type information for the class modeled by %s",
            type);

        Type type = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];

        checkArgument(type != SqlQuerySpec.class, "%s to stream must use the %s override",
            type.getSimpleName(),
            type.getSimpleName());

        if (this.serializer == null) {
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

        return this.serializer;
    }
}
