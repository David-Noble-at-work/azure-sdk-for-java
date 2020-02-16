// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

import com.azure.cosmos.implementation.RequestOptions;

/**
 * {@link RequestOptions} that applies to an operation within a {@link TransactionalBatch}.
 */
public class TransactionalBatchItemRequestOptions extends RequestOptions {

    public static TransactionalBatchItemRequestOptions fromItemRequestOptions(final RequestOptions options) {

        if (options == null) {
            return null;
        }

        TransactionalBatchItemRequestOptions itemRequestOptions = new TransactionalBatchItemRequestOptions();

        itemRequestOptions
            .setIndexingDirective(options.getIndexingDirective())
            .setAccessCondition(options.getAccessCondition())
            .setProperties(options.getProperties());

        itemRequestOptions.IsEffectivePartitionKeyRouting = options.IsEffectivePartitionKeyRouting;
        return itemRequestOptions;
    }
}
