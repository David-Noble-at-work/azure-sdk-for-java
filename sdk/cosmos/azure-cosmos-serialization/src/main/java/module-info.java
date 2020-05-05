// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

module com.azure.cosmos.serialization {

    requires annotations;
    requires io.netty.common;
    requires io.netty.buffer;
    requires java.management;
    requires jdk.management;

    //  This is only required by guava shaded libraries
    requires java.logging;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires it.unimi.dsi.fastutil;
    requires com.google.common;
    requires org.slf4j;
    requires findbugs;

    // Public surface area
    exports com.azure.cosmos.core;
    exports com.azure.cosmos.serialization.hybridrow;

    // exporting some packages specifically for azure-cosmos
    opens com.azure.cosmos.implementation.base to com.azure.cosmos;
    exports com.azure.cosmos.implementation.base;
    exports com.azure.cosmos.serialization.hybridrow.recordio;
    exports com.azure.cosmos.serialization.hybridrow.io;
    exports com.azure.cosmos.serialization.hybridrow.layouts;
    exports com.azure.cosmos.serialization.hybridrow.schemas;
}
