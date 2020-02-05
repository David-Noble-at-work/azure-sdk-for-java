// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation.directconnectivity.rntbd;

import com.azure.cosmos.implementation.directconnectivity.RntbdTransportClient;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.net.PercentEscaper;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.dropwizard.DropwizardConfig;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.core.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * Creates and manages metrics for the Cosmos Direct TCP stack.
 * <p>
 * Metrics are created and maintained for each {@link RntbdTransportClient} instance and each of its service {@link
 * RntbdEndpoint endpoints}. Metrics are tagged with values that identify the {@link RntbdTransportClient transport
 * client} and service {@link RntbdEndpoint endpoint} to which they apply. See {@link RntbdTransportClient#tag()} and
 * {@link RntbdEndpoint#tag}.
 * <p>
 * This table describes the metrics that are created and managed by this class.
 * <table>
 *   <tr>
 *      <th>Metric</th>
 *      <th>Description</th>
 *   </tr>
 *   <tr><td colspan="2">Timers</td></tr>
 *   <tr>
 *     <td>requests</td>
 *     <td>request rate</td>
 *   </tr>
 *   <tr>
 *     <td>responseErrors</td>
 *     <td>response error rate</td>
 *   </tr>
 *   <tr>
 *     <td>responseSuccesses</td>
 *     <td>response success rate</td>
 *   </tr>
 *   <tr><td colspan="2">Distribution summaries</td></tr>
 *   <tr>
 *     <td>requestSize</td>
 *     <td>request size (bytes)</td>
 *   </tr>
 *   <tr>
 *     <td>responseSize</td>
 *     <td>response size (bytes)</td>
 *   </tr>
 *   <tr><td colspan="2">Gauges</td></tr>
 *   <tr>
 *     <td>channelsAcquired</td>
 *     <td>acquired channel count</td>
 *   </tr>
 *   <tr>
 *     <td>channelsAvailable</td>
 *     <td>available channel count</td>
 *   </tr>
 *   <tr>
 *     <td>concurrentRequests</td>
 *     <td>executing or queued request count</td>
 *   </tr>
 *   <tr>
 *     <td>requestQueueLength</td>
 *     <td>queued request count</td>
 *   </tr>
 *   <tr>
 *     <td>usedDirectMemory</td>
 *     <td>Java direct memory usage (MiB)</td>
 *   </tr>
 *   <tr>
 *     <td>usedHeapMemory</td>
 *     <td>Java heap memory usage (MiB)</td>
 *   </tr>
 * </table>
 *
 * <h3>Console logging</h3>
 * <p>
 * Enable the built-in {@link RntbdMetrics} console logging facility by specifying a positive integer polling interval
 * in seconds for system property {@code azure.cosmos.monitoring.consoleLogging.step}. Negate this specificatio and
 * disable console logging by assigning a value of {@coce false} to boolean system property {@code azure.cosmos
 * .monitoring.consoleLogging.disabled}. This can be useful in debug scenarios when it desirable to enable/disable
 * console logging without losing the value of {@code azure.cosmos.monitoring.consoleLogging.step}
 */
@SuppressWarnings("UnstableApiUsage")
@JsonPropertyOrder({
    "tags", "requests", "responseErrors", "responseSuccesses", "requestSize", "responseSize", "channelsAcquired",
    "channelsAvailable", "concurrentRequests", "endpoints", "endpointsEvicted", "requestQueueLength",
    "usedDirectMemory", "usedHeapMemory"
})
public final class RntbdMetrics {

    // region Fields

    private static final PercentEscaper PERCENT_ESCAPER = new PercentEscaper("_-", false);

    private static final Logger logger = LoggerFactory.getLogger(RntbdMetrics.class);
    private static final CompositeMeterRegistry registry = new CompositeMeterRegistry();

    static {
        try {
            boolean disabled = Boolean.getBoolean("azure.cosmos.monitoring.consoleLogging.disabled");
            if (!disabled) {
                int step = Integer.getInteger("azure.cosmos.monitoring.consoleLogging.step", 0);
                if (step > 0) {
                    RntbdMetrics.add(RntbdMetrics.consoleLoggingRegistry(step));
                }
            }
        } catch (Throwable error) {
            logger.error("failed to initialize console logging registry due to ", error);
        }
    }

    private final RntbdEndpoint endpoint;

    private final DistributionSummary requestSize;
    private final Timer requests;
    private final Timer responseErrors;
    private final DistributionSummary responseSize;
    private final Timer responseSuccesses;
    private final Tags tags;
    private final RntbdTransportClient transportClient;

    // endregion

    // region Constructors

    public RntbdMetrics(RntbdTransportClient client, RntbdEndpoint endpoint) {

        this.transportClient = client;
        this.endpoint = endpoint;

        this.tags = Tags.of(client.tag(), endpoint.tag());

        // Timers

        this.requests = Timer.builder(nameOf("requests"))
            .description("request rate")
            .tags(tags)
            .register(registry);

        this.responseErrors = Timer.builder(nameOf("responseErrors"))
            .description("response error rate")
            .tags(tags)
            .register(registry);

        this.responseSuccesses = Timer.builder(nameOf("responseSuccesses"))
            .description("response success rate")
            .tags(tags)
            .register(registry);

        // Distribution summaries

        this.requestSize = DistributionSummary.builder(nameOf("requestSize"))
            .description("request size (bytes)")
            .baseUnit("bytes")
            .tags(this.tags)
            .register(registry);

        this.responseSize = DistributionSummary.builder(nameOf("responseSize"))
            .description("response size (bytes)")
            .baseUnit("bytes")
            .tags(this.tags)
            .register(registry);

        // Gauges

        Gauge.builder(nameOf("endpoints"), client, RntbdTransportClient::endpointCount)
             .description("endpoint count")
             .tag(client.tag().getKey(), client.tag().getValue())
             .register(registry);

        Gauge.builder(nameOf("endpointsEvicted"), client, RntbdTransportClient::endpointEvictionCount)
             .description("endpoint eviction count")
             .tag(client.tag().getKey(), client.tag().getValue())
             .register(registry);

        Gauge.builder(nameOf("channelsAcquired"), endpoint, RntbdEndpoint::channelsAcquired)
             .description("acquired channel count")
             .tags(this.tags)
             .register(registry);

        Gauge.builder(nameOf("channelsAvailable"), endpoint, RntbdEndpoint::channelsAvailable)
             .description("available channel count")
            .tags(this.tags)
            .register(registry);

        Gauge.builder(nameOf("concurrentRequests"), endpoint, RntbdEndpoint::concurrentRequests)
            .description("executing or queued request count")
             .tags(this.tags)
             .register(registry);

        Gauge.builder(nameOf("requestQueueLength"), endpoint, RntbdEndpoint::requestQueueLength)
            .description("queued request count")
             .tags(this.tags)
             .register(registry);

        Gauge.builder(nameOf("usedDirectMemory"), endpoint, x -> x.usedDirectMemory())
             .description("Java direct memory usage (MiB)")
             .baseUnit("bytes")
             .tags(this.tags)
             .register(registry);

        Gauge.builder(nameOf("usedHeapMemory"), endpoint, x -> x.usedHeapMemory())
             .description("Java heap memory usage (MiB)")
             .baseUnit("MiB")
             .tags(this.tags)
             .register(registry);
    }

    // endregion

    // region Accessors

    public static void add(MeterRegistry registry) {
        RntbdMetrics.registry.add(registry);
    }

    @JsonProperty
    public int channelsAcquired() {
        return this.endpoint.channelsAcquired();
    }

    @JsonProperty
    public int channelsAvailable() {
        return this.endpoint.channelsAvailable();
    }

    /***
     * Computes the number of successful (non-error) responses received divided by the number of completed requests.
     *
     * @return number of successful (non-error) responses received divided by the number of completed requests.
     */
    @JsonProperty
    public double completionRate() {
        return this.responseSuccesses.count() / (double) this.requests.count();
    }

    @JsonProperty
    public long concurrentRequests() {
        return this.endpoint.concurrentRequests();
    }

    @JsonProperty
    public int endpoints() {
        return this.transportClient.endpointCount();
    }

    @JsonProperty
    public int requestQueueLength() {
        return this.endpoint.requestQueueLength();
    }

    @JsonProperty
    public HistogramSnapshot requestSize() {
        return this.requestSize.takeSnapshot();
    }

    @JsonProperty
    public HistogramSnapshot requests() {
        return this.requests.takeSnapshot();
    }

    @JsonProperty
    public HistogramSnapshot responseErrors() {
        return this.responseErrors.takeSnapshot();
    }

    /***
     * Computes the number of successful (non-error) responses received divided by the number of requests sent
     *
     * @return The number of successful (non-error) responses received divided by the number of requests sent
     */
    @JsonProperty
    public double responseRate() {
        return this.responseSuccesses.count() / (double) (this.requests.count() + this.endpoint.concurrentRequests());
    }

    @JsonProperty
    public HistogramSnapshot responseSize() {
        return this.responseSize.takeSnapshot();
    }

    @JsonProperty
    public HistogramSnapshot responseSuccesses() {
        return this.responseSuccesses.takeSnapshot();
    }

    @JsonProperty
    public Iterable<Tag> tags() {
        return this.tags;
    }

    @JsonProperty
    public long usedDirectMemory() {
        return this.endpoint.usedDirectMemory();
    }

    @JsonProperty
    public long usedHeapMemory() {
        return this.endpoint.usedHeapMemory();
    }

    // endregion

    // region Methods

    public void markComplete(RntbdRequestRecord requestRecord) {
        requestRecord.stop(this.requests, requestRecord.isCompletedExceptionally()
            ? this.responseErrors
            : this.responseSuccesses);
        this.requestSize.record(requestRecord.requestLength());
        this.responseSize.record(requestRecord.responseLength());
    }

    @Override
    public String toString() {
        return RntbdObjectMapper.toString(this);
    }

    // endregion

    // region Private

    static String escape(String value) {
        return PERCENT_ESCAPER.escape(value);
    }

    private static MeterRegistry consoleLoggingRegistry(final int step) {

        final MetricRegistry dropwizardRegistry = new MetricRegistry();

        ConsoleReporter consoleReporter = ConsoleReporter
            .forRegistry(dropwizardRegistry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();

        consoleReporter.start(step, TimeUnit.SECONDS);

        DropwizardConfig dropwizardConfig = new DropwizardConfig() {

            @Override
            public String get(@Nullable String key) {
                return null;
    }

    @Override
            public String prefix() {
                return "console";
    }

        };

        final MeterRegistry consoleLoggingRegistry = new DropwizardMeterRegistry(
            dropwizardConfig, dropwizardRegistry, HierarchicalNameMapper.DEFAULT, Clock.SYSTEM) {
            @Override
            @Nonnull
            protected Double nullGaugeValue() {
                return Double.NaN;
            }
        };

        consoleLoggingRegistry.config().namingConvention(NamingConvention.dot);
        return consoleLoggingRegistry;
    }

    private static String nameOf(final String member) {
        return "azure.cosmos.directTcp." + member;
    }

    // endregion
}
