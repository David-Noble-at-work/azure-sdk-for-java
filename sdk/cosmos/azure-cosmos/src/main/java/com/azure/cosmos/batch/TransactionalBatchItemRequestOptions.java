// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.IndexingDirective;
import com.azure.cosmos.implementation.RequestOptions;

/**
 * {@link RequestOptions} that applies to an operation within a {@link TransactionalBatch}.
 */
public class TransactionalBatchItemRequestOptions extends RequestOptions {
    /**
     * Gets or sets the indexing directive (Include or Exclude) for the request in the Azure Cosmos DB service.
     *
     * <value>
     * The indexing directive to use with a request.
     * </value>
     * {@link IndexingPolicy} {@link IndexingDirective}
     */
    private IndexingDirective IndexingDirective;

    public final IndexingDirective getIndexingDirective() {
        return IndexingDirective;
    }

    public final void setIndexingDirective(IndexingDirective value) {
        IndexingDirective = value;
    }

    public static TransactionalBatchItemRequestOptions FromItemRequestOptions(ItemRequestOptions itemRequestOptions) {

        if (itemRequestOptions == null) {
            return null;
        }

        RequestOptions requestOptions = itemRequestOptions instanceof RequestOptions
            ? (RequestOptions) itemRequestOptions
            : null;

        TransactionalBatchItemRequestOptions batchItemRequestOptions = new TransactionalBatchItemRequestOptions();

        batchItemRequestOptions.setIndexingDirective(itemRequestOptions.IndexingDirective);
        batchItemRequestOptions.IfMatchEtag = requestOptions.IfMatchEtag;
        batchItemRequestOptions.IfNoneMatchEtag = requestOptions.IfNoneMatchEtag;
        batchItemRequestOptions.Properties = requestOptions.Properties;
        batchItemRequestOptions.IsEffectivePartitionKeyRouting = requestOptions.IsEffectivePartitionKeyRouting;
        return batchItemRequestOptions;
    }
}
