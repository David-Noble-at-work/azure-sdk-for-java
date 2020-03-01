// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

//
///**
// * Represents a batch of operations against items with the same {@link PartitionKey} in a container that will be
// * performed in a transactional manner at the Azure Cosmos DB service. Use <see cref="Container
// * .CreateTransactionalBatch(PartitionKey)"/>
// * to create an instance of TransactionalBatch.
// *
// * <example>
// * This example atomically modifies a set of documents as a batch.
// * <code language="c#">
// * <![CDATA[ public class ToDoActivity { public string type { get; set; } public string id { get; set; } public string
// * status { get; set;
// *
// }
