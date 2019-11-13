// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation.query;

import com.azure.cosmos.implementation.query.metrics.ClientSideMetrics;
import com.azure.cosmos.implementation.query.metrics.FetchExecutionRangeAccumulator;
import com.azure.cosmos.implementation.query.metrics.SchedulingStopwatch;
import com.azure.cosmos.implementation.query.metrics.SchedulingTimeSpan;
import com.azure.cosmos.implementation.routing.Range;
import com.azure.cosmos.BridgeInternal;
import com.azure.cosmos.CosmosClientException;
import com.azure.cosmos.FeedOptions;
import com.azure.cosmos.FeedResponse;
import com.azure.cosmos.JsonSerializable;
import com.azure.cosmos.Resource;
import com.azure.cosmos.implementation.Exceptions;
import com.azure.cosmos.implementation.HttpConstants;
import com.azure.cosmos.implementation.IDocumentClientRetryPolicy;
import com.azure.cosmos.implementation.ObservableHelper;
import com.azure.cosmos.implementation.PartitionKeyRange;
import com.azure.cosmos.implementation.QueryMetrics;
import com.azure.cosmos.implementation.QueryMetricsConstants;
import com.azure.cosmos.implementation.RxDocumentServiceRequest;
import com.azure.cosmos.implementation.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * While this class is public, but it is not part of our published public APIs.
 * This is meant to be internally used only by our sdk.
 */
class DocumentProducer<T extends Resource> {
    private static final Logger logger = LoggerFactory.getLogger(DocumentProducer.class);
    private int retries;

    class DocumentProducerFeedResponse {
        FeedResponse<T> pageResult;
        PartitionKeyRange sourcePartitionKeyRange;

        DocumentProducerFeedResponse(FeedResponse<T> pageResult) {
            this.pageResult = pageResult;
            this.sourcePartitionKeyRange = DocumentProducer.this.targetRange;
            populatePartitionedQueryMetrics();
        }

        DocumentProducerFeedResponse(FeedResponse<T> pageResult, PartitionKeyRange pkr) {
            this.pageResult = pageResult;
            this.sourcePartitionKeyRange = pkr;
            populatePartitionedQueryMetrics();
        }

        void populatePartitionedQueryMetrics() {
            String queryMetricsDelimitedString = pageResult.getResponseHeaders().get(HttpConstants.HttpHeaders.QUERY_METRICS);
            if (!StringUtils.isEmpty(queryMetricsDelimitedString)) {
                queryMetricsDelimitedString += String.format(";%s=%.2f", QueryMetricsConstants.RequestCharge, pageResult.getRequestCharge());
                ImmutablePair<String, SchedulingTimeSpan> schedulingTimeSpanMap =
                        new ImmutablePair<>(targetRange.getId(), fetchSchedulingMetrics.getElapsedTime());

                QueryMetrics qm =BridgeInternal.createQueryMetricsFromDelimitedStringAndClientSideMetrics(queryMetricsDelimitedString,
                        new ClientSideMetrics(retries,
                                pageResult.getRequestCharge(),
                                fetchExecutionRangeAccumulator.getExecutionRanges(),
                                Arrays.asList(schedulingTimeSpanMap)
                        ), pageResult.getActivityId());
                BridgeInternal.putQueryMetricsIntoMap(pageResult, targetRange.getId(), qm);
            }
        }
    }

    protected final IDocumentQueryClient client;
    protected final String collectionRid;
    protected final FeedOptions feedOptions;
    protected final Class<T> resourceType;
    protected final PartitionKeyRange targetRange;
    protected final String collectionLink;
    protected final TriFunction<PartitionKeyRange, String, Integer, RxDocumentServiceRequest> createRequestFunc;
    protected final Function<RxDocumentServiceRequest, Flux<FeedResponse<T>>> executeRequestFuncWithRetries;
    protected final Callable<IDocumentClientRetryPolicy> createRetryPolicyFunc;
    protected final int pageSize;
    protected final UUID correlatedActivityId;
    public int top;
    private volatile String lastResponseContinuationToken;
    private final SchedulingStopwatch fetchSchedulingMetrics;
    private SchedulingStopwatch moveNextSchedulingMetrics;
    private final FetchExecutionRangeAccumulator fetchExecutionRangeAccumulator;

    public DocumentProducer(
            IDocumentQueryClient client,
            String collectionResourceId,
            FeedOptions feedOptions,
            TriFunction<PartitionKeyRange, String, Integer, RxDocumentServiceRequest> createRequestFunc,
            Function<RxDocumentServiceRequest, Flux<FeedResponse<T>>> executeRequestFunc,
            PartitionKeyRange targetRange,
            String collectionLink,
            Callable<IDocumentClientRetryPolicy> createRetryPolicyFunc,
            Class<T> resourceType ,
            UUID correlatedActivityId,
            int initialPageSize, // = -1,
            String initialContinuationToken,
            int top) {

        this.client = client;
        this.collectionRid = collectionResourceId;

        this.createRequestFunc = createRequestFunc;

        this.fetchSchedulingMetrics = new SchedulingStopwatch();
        this.fetchSchedulingMetrics.ready();
        this.fetchExecutionRangeAccumulator = new FetchExecutionRangeAccumulator(targetRange.getId());

        this.executeRequestFuncWithRetries = request -> {
            retries = -1;
            this.fetchSchedulingMetrics.start();
            this.fetchExecutionRangeAccumulator.beginFetchRange();
            IDocumentClientRetryPolicy retryPolicy = null;
            if (createRetryPolicyFunc != null) {
                try {
                    retryPolicy = createRetryPolicyFunc.call();
                } catch (Exception e) {
                    return Flux.error(e);
                }
                retryPolicy.onBeforeSendRequest(request);
            }
            return ObservableHelper.inlineIfPossibleAsObs(
                    () -> {
                        ++retries;
                        return executeRequestFunc.apply(request);
                    }, retryPolicy);
        };

        this.correlatedActivityId = correlatedActivityId;

        this.feedOptions = feedOptions != null ? feedOptions : new FeedOptions();
        this.feedOptions.requestContinuation(initialContinuationToken);
        this.lastResponseContinuationToken = initialContinuationToken;
        this.resourceType = resourceType;
        this.targetRange = targetRange;
        this.collectionLink = collectionLink;
        this.createRetryPolicyFunc = createRetryPolicyFunc;
        this.pageSize = initialPageSize;
        this.top = top;
    }

    public Flux<DocumentProducerFeedResponse> produceAsync() {
        BiFunction<String, Integer, RxDocumentServiceRequest> sourcePartitionCreateRequestFunc =
                (token, maxItemCount) -> createRequestFunc.apply(targetRange, token, maxItemCount);
        Flux<FeedResponse<T>> obs = Paginator
                .getPaginatedQueryResultAsObservable(
                        feedOptions.requestContinuation(),
                        sourcePartitionCreateRequestFunc,
                        executeRequestFuncWithRetries,
                        resourceType,
                        top,
                        pageSize)
                .map(rsp -> {
                    lastResponseContinuationToken = rsp.getContinuationToken();
                    this.fetchExecutionRangeAccumulator.endFetchRange(rsp.getActivityId(),
                            rsp.getResults().size(),
                            this.retries);
                    this.fetchSchedulingMetrics.stop();
                    return rsp;});

        return splitProof(obs.map(DocumentProducerFeedResponse::new));
    }

    private Flux<DocumentProducerFeedResponse> splitProof(Flux<DocumentProducerFeedResponse> sourceFeedResponseObservable) {
        return sourceFeedResponseObservable.onErrorResume( t -> {
            CosmosClientException dce = Utils.as(t, CosmosClientException.class);
            if (dce == null || !isSplit(dce)) {
                logger.error("Unexpected failure", t);
                return Flux.error(t);
            }

            // we are dealing with Split
            logger.info("DocumentProducer handling a partition split in [{}], detail:[{}]", targetRange, dce);
            Mono<Utils.ValueHolder<List<PartitionKeyRange>>> replacementRangesObs = getReplacementRanges(targetRange.toRange());

            // Since new DocumentProducers are instantiated for the new replacement ranges, if for the new
            // replacement partitions split happens the corresponding DocumentProducer can recursively handle splits.
            // so this is resilient to split on splits.
            Flux<DocumentProducer<T>> replacementProducers = replacementRangesObs.flux().flatMap(
                    partitionKeyRanges ->  {
                        if (logger.isDebugEnabled()) {
                            logger.info("Cross Partition Query Execution detected partition [{}] split into [{}] partitions,"
                                    + " last continuation token is [{}].",
                                    targetRange.toJson(),
                                    partitionKeyRanges.v.stream()
                                            .map(JsonSerializable::toJson).collect(Collectors.joining(", ")),
                                    lastResponseContinuationToken);
                        }
                        return Flux.fromIterable(createReplacingDocumentProducersOnSplit(partitionKeyRanges.v));
                    });

            return produceOnSplit(replacementProducers);
        });
    }

    protected Flux<DocumentProducerFeedResponse> produceOnSplit(Flux<DocumentProducer<T>> replacingDocumentProducers) {
        return replacingDocumentProducers.flatMap(DocumentProducer::produceAsync, 1);
    }

    private List<DocumentProducer<T>> createReplacingDocumentProducersOnSplit(List<PartitionKeyRange> partitionKeyRanges) {

        List<DocumentProducer<T>> replacingDocumentProducers = new ArrayList<>(partitionKeyRanges.size());
        for(PartitionKeyRange pkr: partitionKeyRanges) {
            replacingDocumentProducers.add(createChildDocumentProducerOnSplit(pkr, lastResponseContinuationToken));
        }
        return replacingDocumentProducers;
    }

    protected DocumentProducer<T> createChildDocumentProducerOnSplit(
            PartitionKeyRange targetRange,
            String initialContinuationToken) {

        return new DocumentProducer<T>(
                client,
                collectionRid,
                feedOptions,
                createRequestFunc,
                executeRequestFuncWithRetries,
                targetRange,
                collectionLink,
                null,
                resourceType ,
                correlatedActivityId,
                pageSize,
                initialContinuationToken,
                top);
    }

    private Mono<Utils.ValueHolder<List<PartitionKeyRange>>> getReplacementRanges(Range<String> range) {
        return client.getPartitionKeyRangeCache().tryGetOverlappingRangesAsync(collectionRid, range, true, feedOptions.properties());
    }

    private boolean isSplit(CosmosClientException e) {
        return Exceptions.isPartitionSplit(e);
    }
}
