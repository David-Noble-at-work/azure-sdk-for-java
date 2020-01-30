// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serializer;

public final class CosmosSerializationUtil {
    private static CamelCaseNamingStrategy camelCaseNamingStrategy = new CamelCaseNamingStrategy();

    public static String GetStringWithPropertyNamingPolicy(CosmosSerializationOptions options, String name) {
        if (options != null && options.getPropertyNamingPolicy() == CosmosPropertyNamingPolicy.CamelCase) {
            return CosmosSerializationUtil.ToCamelCase(name);
        }

        return name;
    }

    public static String ToCamelCase(String name) {
        return CosmosSerializationUtil.camelCaseNamingStrategy.GetPropertyName(name, false);
    }
}
