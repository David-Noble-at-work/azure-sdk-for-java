// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.examples;

import com.azure.data.cosmos.CosmosClient;
import com.azure.data.cosmos.CosmosContainer;
import com.azure.data.cosmos.CosmosContainerProperties;
import com.azure.data.cosmos.CosmosItemResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import java.util.Objects;
import java.util.UUID;

/**
 * Contains the account configurations for Sample.
 * <p>
 * For running tests, you can pass a customized endpoint configuration in one of the following ways:
 * <ul>
 * <li>-DACCOUNT_KEY="[your-key]" -DACCOUNT_HOST="[your-endpoint]" as JVM
 * command-line option.</li>
 * <li>You can set COSMOS_ACCOUNT_KEY and COSMOS_ACCOUNT_HOST as environment variables.</li>
 * </ul>
 * <p>
 * If none of the above is set, emulator endpoint will be used.
 * Emulator http cert is self signed. If you are using emulator,
 * make sure emulator https certificate is imported
 * to java trusted cert store:
 * https://docs.microsoft.com/en-us/azure/cosmos-db/local-emulator-export-ssl-certificates
 */
public class LongPartitionKeys {

    private static final String DATABASE_NAME = "test-database";
    private static final String CONTAINER_NAME = "test-container";

    public static void main(String[] args) {

        try (final CosmosClient client = CosmosClient.builder()
            .endpoint(AccountSettings.HOST)
            .key(AccountSettings.MASTER_KEY)
            .build()) {

            new LongPartitionKeys().runDemo(client);
        }
    }

    void runDemo(CosmosClient client) {

        final CosmosContainerProperties containerProperties = new CosmosContainerProperties(CONTAINER_NAME, "/pk");

        final CosmosContainer container = Objects.requireNonNull(
            Objects.requireNonNull(client.createDatabaseIfNotExists(DATABASE_NAME).block())
                .database()
                .createContainerIfNotExists(containerProperties).block()
        ).container();

        final Thread[] threadGroup = new Thread[3];

        for (int i = 0; i < 3; i++) { //ipk is the partition key property
            threadGroup[i] = new Thread(() -> {
                final Document doc = new Document("A", Strings.repeat("a", 95)
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                );
                try {
                    final CosmosItemResponse itemResponse = container.createItem(doc).single().block();
                } catch (Throwable error) {
                    error.printStackTrace();
                    System.exit(1);
                }
                //noinspection UnnecessaryReturnStatement
                return;
            });
        }

        for (Thread t : threadGroup) {
            t.start();
        }

        for (Thread t : threadGroup) {
            try {
                t.join();
            } catch (InterruptedException interrupted) {
                interrupted.printStackTrace();
                System.exit(2);
            }
        }
    }

    private static class Document {

        @JsonProperty
        private final String id;

        @JsonProperty
        private final String pk;

        Document(String id, String pk) {
            this.id = id;
            this.pk = pk;
        }
    }
}
