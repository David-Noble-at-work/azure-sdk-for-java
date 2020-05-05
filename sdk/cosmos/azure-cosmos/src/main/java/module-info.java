// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

module com.azure.cosmos {

    requires transitive com.azure.core;

    requires annotations;
    requires com.azure.cosmos.serialization;
    requires com.codahale.metrics;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.module.afterburner;
    requires io.netty.buffer;
    requires io.netty.codec.http2;
    requires io.netty.codec.http;
    requires io.netty.codec;
    requires io.netty.common;
    requires io.netty.handler.proxy;
    requires io.netty.handler;
    requires io.netty.resolver;
    requires io.netty.transport.epoll;
    requires io.netty.transport;
    requires java.management;
    requires jdk.management;
    requires micrometer.core;
    requires reactor.netty;
    //  This is only required by guava shaded libraries
    requires java.logging;

    // public API surface area
    exports com.azure.cosmos;
    exports com.azure.cosmos.models;
    exports com.azure.cosmos.util;

    // exporting some packages specifically for Jackson
    opens com.azure.cosmos to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.implementation to com.fasterxml.jackson.databind, java.logging;
    opens com.azure.cosmos.implementation.caches to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.implementation.changefeed to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.implementation.changefeed.implementation to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.implementation.changefeed.exceptions to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.implementation.directconnectivity to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.implementation.http to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.implementation.query to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.implementation.query.aggregation to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.implementation.query.metrics to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.implementation.query.orderbyquery to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.implementation.routing to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.models to com.fasterxml.jackson.databind;
    opens com.azure.cosmos.util to com.fasterxml.jackson.databind;

    uses com.azure.cosmos.implementation.guava25.base.PatternCompiler;
}
