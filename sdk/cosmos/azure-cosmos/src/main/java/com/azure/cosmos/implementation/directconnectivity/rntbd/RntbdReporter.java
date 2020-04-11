// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation.directconnectivity.rntbd;

import com.azure.cosmos.implementation.apachecommons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.File;
import java.net.URL;

public final class RntbdReporter {

    private static final String codeSource;

    static {
        String value;
        try {
            URL url = RntbdReporter.class.getProtectionDomain().getCodeSource().getLocation();
            File file = new File(url.toURI());
            value = file.getName();
        } catch (Throwable error) {
            value = "azure-cosmosdb-direct";
        }
        codeSource = value;
    }

    private RntbdReporter() {
    }

    public static void reportIssue(Logger logger, Object subject, String format, Object... arguments) {
        if (logger.isErrorEnabled()) {
            doReportIssue(logger, subject, format, arguments);
        }
    }

    public static void reportIssueUnless(
        final Logger logger,
        final boolean predicate,
        final Object subject,
        final String format, final Object... arguments) {
        if (!predicate && logger.isErrorEnabled()) {
            doReportIssue(logger, subject, format, arguments);
        }
    }

    private static void doReportIssue(Logger logger, Object subject, String format, Object[] arguments) {

        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(format, arguments);
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        Throwable throwable = formattingTuple.getThrowable();

        if (throwable == null) {
            logger.error("Report this {} issue to ensure it is addressed:\n[{}]\n[{}]\n[{}]",
                codeSource, subject, stackTrace[2], formattingTuple.getMessage()
            );
        } else {
            logger.error("Report this {} issue to ensure it is addressed:\n[{}]\n[{}]\n[{}{}]",
                codeSource, subject, stackTrace[2], formattingTuple.getMessage(),
                ExceptionUtils.getStackTrace(throwable)
            );
        }
    }
}
