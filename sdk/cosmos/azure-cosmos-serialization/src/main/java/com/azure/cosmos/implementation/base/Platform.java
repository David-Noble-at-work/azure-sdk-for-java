/*
 * Copyright (C) 2009 The Guava Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.azure.cosmos.implementation.base;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Methods factored out so that they can be emulated differently in GWT.
 *
 * @author Jesse Wilson
 */
final class Platform {
    private static final Logger logger = Logger.getLogger(Platform.class.getName());
    private static final PatternCompiler patternCompiler = loadPatternCompiler();

    private Platform() {
    }

    static void checkGwtRpcEnabled() {
        String propertyName = "guava.gwt.emergency_reenable_rpc";

        if (!Boolean.parseBoolean(System.getProperty(propertyName, "false"))) {
            throw new UnsupportedOperationException(
                Strings.lenientFormat(
                    "We are removing GWT-RPC support for Guava types. You can temporarily reenable"
                        + " support by setting the system property %s to true. For more about system"
                        + " properties, see %s. For more about Guava's GWT-RPC support, see %s.",
                    propertyName,
                    "https://stackoverflow.com/q/5189914/28465",
                    "https://groups.google.com/d/msg/guava-announce/zHZTFg7YF3o/rQNnwdHeEwAJ"));
        }
        logger.log(
            java.util.logging.Level.WARNING,
            "Later in 2020, we will remove GWT-RPC support for Guava types. You are seeing this"
                + " warning because you are sending a Guava type over GWT-RPC, which will break. You"
                + " can identify which type by looking at the class name in the attached stack trace.",
            new Throwable());

    }

    static CommonPattern compilePattern(String pattern) {
        Preconditions.checkNotNull(pattern);
        return patternCompiler.compile(pattern);
    }

    static String emptyToNull(@Nullable String string) {
        return stringIsNullOrEmpty(string) ? null : string;
    }

    static String formatCompact4Digits(double value) {
        return String.format(Locale.ROOT, "%.4g", value);
    }

    static <T extends Enum<T>> Optional<T> getEnumIfPresent(Class<T> enumClass, String value) {
        WeakReference<? extends Enum<?>> ref = Enums.getEnumConstants(enumClass).get(value);
        return ref == null ? Optional.empty() : Optional.of(enumClass.cast(ref.get()));
    }

    static String nullToEmpty(@Nullable String string) {
        return (string == null) ? "" : string;
    }

    static boolean patternCompilerIsPcreLike() {
        return patternCompiler.isPcreLike();
    }

    static CharMatcher precomputeCharMatcher(CharMatcher matcher) {
        return matcher.precomputedInternal();
    }

    static boolean stringIsNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Calls {@link System#nanoTime()}.
     */
    @SuppressWarnings("GoodTime") // reading system time without TimeSource
    static long systemNanoTime() {
        return System.nanoTime();
    }

    private static PatternCompiler loadPatternCompiler() {
        return new JdkPatternCompiler();
    }

    @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    private static void logPatternCompilerError(ServiceConfigurationError e) {
        logger.log(Level.WARNING, "Error loading regex compiler, falling back to next option", e);
    }

    private static final class JdkPatternCompiler implements PatternCompiler {
        @Override
        public boolean isPcreLike() {
            return true;
        }

        @Override
        public CommonPattern compile(String pattern) {
            return new JdkPattern(Pattern.compile(pattern));
        }
    }
}
