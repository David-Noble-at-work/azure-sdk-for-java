// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.PartitionKey;
import com.azure.cosmos.implementation.HttpConstants;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a batch of operations against items with the same {@link PartitionKey} in a container.
 * <p>
 * The batch operations will be performed in a transactional manner at the Azure Cosmos DB service. Use {@link
 * CosmosContainer#CreateTransactionalBatch} to create an instance of this class.
 * <h3>Example</h3>
 * <p>
 * This example atomically modifies a set of documents as a batch.<pre>{@code
 * public class ToDoActivity {
 *     public final String type;
 *     public final String id;
 *     public final String status;
 *     public ToDoActivity(String type, String id, String status) {
 *         this.type = type;
 *         this.id = id;
 *         this.status = status;
 *     }
 * }
 *
 * String activityType = "personal";
 *
 * ToDoActivity test1 = new ToDoActivity(activityType, "learning", "ToBeDone");
 * ToDoActivity test2 = new ToDoActivity(activityType, "shopping", "Done");
 * ToDoActivity test3 = new ToDoActivity(activityType, "swimming", "ToBeDone");
 *
 * try (TransactionalBatchResponse response = container.CreateTransactionalBatch(new Cosmos.PartitionKey(activityType))
 *     .CreateItem<ToDoActivity>(test1)
 *     .ReplaceItem<ToDoActivity>(test2.id, test2)
 *     .UpsertItem<ToDoActivity>(test3)
 *     .DeleteItem("reading")
 *     .CreateItemStream(streamPayload1)
 *     .ReplaceItemStream("eating", streamPayload2)
 *     .UpsertItemStream(streamPayload3)
 *     .ExecuteAsync()) {
 *
 *     if (!response.IsSuccessStatusCode) {
 *        // Handle and log exception
 *        return;
 *     }
 *
 *     // Look up interested results - e.g., via typed access on operation results
 *
 *     TransactionalBatchOperationResult<ToDoActivity> result = response.GetOperationResultAtIndex<ToDoActivity>(0);
 *     ToDoActivity readActivity = result.Resource;
 * }
 * }</pre>
 * <h3>Example</h3>
 * <p>This example atomically reads a set of documents as a batch.<pre>{@code
 * String activityType = "personal";
 *
 * try (TransactionalBatchResponse response = container.CreateTransactionalBatch(new Cosmos.PartitionKey(activityType))
 *    .ReadItem("playing")
 *    .ReadItem("walking")
 *    .ReadItem("jogging")
 *    .ReadItem("running")
 *    .ExecuteAsync()) {
 *
 *     // Look up interested results - eg. via direct access to operation result stream
 *
 *     List<String> items = new ArrayList<String>();
 *
 *     for (TransactionalBatchOperationResult result : response) {
 *         try (InputStreamReader reader = new InputStreamReader(result.ResourceStream)) {
 *             resultItems.Add(await reader.ReadToEndAsync());
 *         }
 *     }
 * }
 * }</pre>
 *
 * @see <a href="https://docs.microsoft.com/azure/cosmos-db/concepts-limits">Limits on TransactionalBatch requests</a>.
 */
public interface TransactionalBatch {
    /**
     * Adds an operation to create an item into the batch.
     *
     * @param item A JSON serializable object that must contain an id property. See {@link CosmosSerializer} to
     * implement a custom serializer.
     * @param <T> The type of item to be created.
     *
     * @return The transactional batch instance with the operation added.
     */
    default <T> TransactionalBatch createItem(@Nonnull T item) {
        checkNotNull(item, "expected non-null item");
        return this.CreateItem(item, null);
    }

    /**
     * Adds an operation to create an item into the batch.
     *
     * @param item A JSON serializable object that must contain an id property. See {@link CosmosSerializer} to
     * implement a custom serializer.
     * @param requestOptions The options for the item request.
     * @param <T> The type of item to be created.
     *
     * @return The transactional batch instance with the operation added.
     */
    <T> TransactionalBatch CreateItem(@Nonnull T item, TransactionalBatchItemRequestOptions requestOptions);

    /**
     * Adds an operation to create an item into the batch.
     *
     * @param streamPayload A Stream containing the payload of the item. The stream must have a UTF-8 encoded JSON
     * object which contains an id property.
     *
     * @return The transactional batch instance with the operation added.
     */
    default TransactionalBatch CreateItemStream(@Nonnull Stream streamPayload) {
        return this.CreateItemStream(streamPayload, null);
    }

    /**
     * Adds an operation to create an item into the batch.
     *
     * @param streamPayload A Stream containing the payload of the item. The stream must have a UTF-8 encoded JSON
     * object which contains an id property.
     * @param requestOptions The options for the item request.
     *
     * @return The transactional batch instance with the operation added.
     */
    TransactionalBatch CreateItemStream(
        @Nonnull Stream streamPayload, TransactionalBatchItemRequestOptions requestOptions);

    /**
     * Adds an operation to delete an item into the batch.
     *
     * @param id The unique id of the item.
     *
     * @return The transactional batch instance with the operation added.
     */
    default TransactionalBatch DeleteItem(@Nonnull String id) {
        checkNotNull(id, "expected non-null id");
        return this.DeleteItem(id, null);
    }

    /**
     * Adds an operation to delete an item into the batch.
     *
     * @param id The unique id of the item.
     * @param requestOptions The options for the item request.
     *
     * @return The transactional batch instance with the operation added.
     */
    TransactionalBatch DeleteItem(@Nonnull String id, TransactionalBatchItemRequestOptions requestOptions);

    /**
     * Executes the transactional batch at the Azure Cosmos service as an asynchronous operation.
     *
     * @return An awaitable response which contains details of execution of the transactional batch.
     * <p>
     * If the transactional batch executes successfully, the {@link TransactionalBatchResponse#StatusCode} on the
     * response returned will be set to {@link HttpConstants.StatusCodes#OK}.
     * <p>
     * If an operation within the transactional batch fails during execution, no changes from the batch will be
     * committed and the status of the failing operation is made available in the <see
     * cref="TransactionalBatchResponse.StatusCode"/>. To get more details about the operation that failed, the response
     * can be enumerated - this returns {@link TransactionalBatchOperationResult} instances corresponding to each
     * operation in the transactional batch in the order they were added into the transactional batch. For a result
     * corresponding to an operation within the transactional batch, the <see cref="TransactionalBatchOperationResult
     * .StatusCode"/> indicates the status of the operation - if the operation was not executed or it was aborted due to
     * the failure of another operation within the transactional batch, the value of this field will be HTTP 424 (Failed
     * Dependency); for the operation that caused the batch to abort, the value of this field will indicate the cause of
     * failure as a HTTP status code.
     * <p>
     * The {@link TransactionalBatchResponse#StatusCode} on the response returned may also have values such as
     * HTTP 5xx in case of server errors and HTTP 429 (Too Many Requests).
     * <p>
     * This API only throws on client side exceptions. This is to increase performance and prevent the overhead of
     * throwing exceptions. Use {@link TransactionalBatchResponse#IsSuccessStatusCode} on the response returned to
     * ensure that the transactional batch succeeded.
     */
    CompletableFuture<TransactionalBatchResponse> ExecuteAsync();

    /**
     * Adds an operation to read an item into the batch.
     *
     * @param id The unique id of the item.
     *
     * @return The transactional batch instance with the operation added.
     */
    default TransactionalBatch ReadItem(@Nonnull String id) {
        checkNotNull(id, "expected non-null id");
        return this.ReadItem(id, null);
    }

    /**
     * Adds an operation to read an item into the batch.
     *
     * @param id The unique id of the item.
     * @param requestOptions The options for the item request.
     *
     * @return The transactional batch instance with the operation added.
     */
    TransactionalBatch ReadItem(@Nonnull String id, TransactionalBatchItemRequestOptions requestOptions);

    /**
     * Adds an operation to replace an item into the batch.
     *
     * @param id The unique id of the item.
     * @param item A JSON serializable object that must contain an id property. See {@link CosmosSerializer} to
     * implement a custom serializer.
     * @param <T> The type of item to be created.
     *
     * @return The transactional batch instance with the operation added.
     */
    default <T> TransactionalBatch ReplaceItem(@Nonnull String id, @Nonnull T item) {
        checkNotNull(id, "expected non-null id");
        checkNotNull(item, "expected non-null item");
        return this.ReplaceItem(id, item, null);
    }

    /**
     * Adds an operation to replace an item into the batch.
     *
     * @param id The unique id of the item.
     * @param item A JSON serializable object that must contain an id property. See {@link CosmosSerializer} to
     * implement a custom serializer.
     * @param requestOptions The options for the item request.
     * @param <T> The type of item to be created.
     *
     * @return The transactional batch instance with the operation added.
     */
    <T> TransactionalBatch ReplaceItem(
        @Nonnull String id, @Nonnull T item, TransactionalBatchItemRequestOptions requestOptions);

    /**
     * Adds an operation to replace an item into the batch.
     *
     * @param id The unique id of the item.
     * @param streamPayload A Stream containing the payload of the item. The stream must have a UTF-8 encoded JSON
     * object which contains an id property.
     *
     * @return The transactional batch instance with the operation added.
     */
    default TransactionalBatch ReplaceItemStream(@Nonnull String id, @Nonnull Stream streamPayload) {
        checkNotNull(id, "expected non-null id");
        checkNotNull(streamPayload, "expected non-null streamPayload");
        return this.ReplaceItemStream(id, streamPayload, null);
    }

    /**
     * Adds an operation to replace an item into the batch.
     *
     * @param id The unique id of the item.
     * @param streamPayload A Stream containing the payload of the item. The stream must have a UTF-8 encoded JSON
     * object which contains an id property.
     * @param requestOptions The options for the item request.
     *
     * @return The transactional batch instance with the operation added.
     */
    TransactionalBatch ReplaceItemStream(
        @Nonnull String id, @Nonnull Stream streamPayload, TransactionalBatchItemRequestOptions requestOptions);

    /**
     * Adds an operation to upsert an item into the batch.
     *
     * @param item A JSON serializable object that must contain an id property. See {@link CosmosSerializer} to
     * implement a custom serializer.
     * @param <T> The type of item to be created.
     *
     * @return The transactional batch instance with the operation added.
     */
    default <T> TransactionalBatch UpsertItem(@Nonnull T item) {
        checkNotNull(item, "expected non-null item");
        return this.UpsertItem(item, null);
    }

    /**
     * Adds an operation to upsert an item into the batch.
     *
     * @param item A JSON serializable object that must contain an id property. See {@link CosmosSerializer} to
     * implement a custom serializer.
     * @param requestOptions The options for the item request.
     * @param <T> The type of item to be created.
     *
     * @return The transactional batch instance with the operation added.
     */
    <T> TransactionalBatch UpsertItem(@Nonnull T item, TransactionalBatchItemRequestOptions requestOptions);

    /**
     * Adds an operation to upsert an item into the batch.
     *
     * @param streamPayload A Stream containing the payload of the item. The stream must have a UTF-8 encoded JSON
     * object which contains an id property.
     *
     * @return The transactional batch instance with the operation added.
     */
    default TransactionalBatch UpsertItemStream(@Nonnull Stream streamPayload) {
        checkNotNull(streamPayload, "expected non-null streamPayload");
        return this.UpsertItemStream(streamPayload, null);
    }

    /**
     * Adds an operation to upsert an item into the batch.
     *
     * @param streamPayload A Stream containing the payload of the item. The stream must have a UTF-8 encoded JSON
     * object which contains an id property.
     * @param requestOptions The options for the item request.
     *
     * @return The transactional batch instance with the operation added.
     */
    TransactionalBatch UpsertItemStream(
        @Nonnull Stream streamPayload, TransactionalBatchItemRequestOptions requestOptions);
}
