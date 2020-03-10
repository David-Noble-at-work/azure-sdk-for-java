package com.azure.cosmos.batch;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainerProperties;
import com.azure.cosmos.PartitionKey;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.guava27.Strings.lenientFormat;
import static org.testng.Assert.assertTrue;
import static org.testng.FileAssert.fail;

public class TransactionalBatchTest {

    private static CosmosAsyncClient client;
    private static CosmosAsyncDatabase database;
    private static CosmosAsyncContainer container;

    @BeforeClass(groups = { "simple" })
    void before_TransactionalBatchTest() {

        CosmosClientBuilder builder = new CosmosClientBuilder()
            .setEndpoint(checkNotNull(
                System.getProperty("ACCOUNT_HOST", System.getenv("ACCOUNT_HOST"))))
            .setKey(checkNotNull(
                System.getProperty("ACCOUNT_KEY", System.getenv("ACCOUNT_KEY"))))
            .setConsistencyLevel(ConsistencyLevel.valueOf(checkNotNull(
                System.getProperty("ACCOUNT_CONSISTENCY", System.getenv("ACCOUNT_CONSISTENCY")))));

        client = builder.buildAsyncClient();

        database = client.getDatabase(lenientFormat("%s.%s",
            TransactionalBatchTest.class.getSimpleName(),
            System.nanoTime()));

        container = checkNotNull(
            database.createContainer(new CosmosContainerProperties("container", "/type")).block()
        ).getContainer();
    }

    @Test(groups = { "simple" })
    void update() {

        String type = "personal";

        Activity[] activities = {
            new Activity(type, "learning", "to-do"),
            new Activity(type, "shopping", "done"),
            new Activity(type, "swimming", "to-do"),
        };

        try (TransactionalBatchResponse response = container.createTransactionalBatch(new PartitionKey(type))
            .createItem(activities[0])
            .createItem(activities[1])
            .createItem(activities[2])
            .executeAsync().get()) {

            assertTrue(response.isSuccessStatusCode());
            TransactionalBatchOperationResult<Activity> result = response.getOperationResultAtIndex(0, Activity.class);
            Activity activity = result.getResource();

        } catch (Exception error) {
            fail(lenientFormat("unexpected exception: ", error), error);
        }
    }

    private static class Activity {

        public final String type;
        public final String name;
        public final String status;

        public Activity(String type, String name, String status) {
            this.type = type;
            this.name = name;
            this.status = status;
        }
    }
}
