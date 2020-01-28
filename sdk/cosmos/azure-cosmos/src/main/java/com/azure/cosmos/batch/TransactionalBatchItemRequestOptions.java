// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.CosmosItemRequestOptions;
import com.azure.cosmos.implementation.RequestOptions;

/**
 * {@link RequestOptions} that applies to an operation within a {@link TransactionalBatch}.
 */
public class TransactionalBatchItemRequestOptions extends RequestOptions {

    public static TransactionalBatchItemRequestOptions fromItemRequestOptions(final CosmosItemRequestOptions options) {

        if (options == null) {
            return null;
        }

        TransactionalBatchItemRequestOptions batchItemRequestOptions = new TransactionalBatchItemRequestOptions()
            .setIndexingDirective(options.getIndexingDirective())
            .setAccessCondition(options.getAccessCondition())
            .setProperties(options.Properties);
        batchItemRequestOptions.IsEffectivePartitionKeyRouting = options.IsEffectivePartitionKeyRouting;

        return batchItemRequestOptions;
    }
}
