/*
 * Copyright (C) 2016 The Guava Authors
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

package com.azure.cosmos.base;

/**
 * The subset of the {@link java.util.regex.Pattern} API which is used by this package, and also
 * shared with the {@code re2j} library. For internal use only. Please refer to the {@code Pattern}
 * javadoc for details.
 */
abstract class CommonPattern {
    public static boolean isPcreLike() {
        return Platform.patternCompilerIsPcreLike();
    }

    public static CommonPattern compile(String pattern) {
        return Platform.compilePattern(pattern);
    }

    public abstract int flags();

    public abstract CommonMatcher matcher(CharSequence t);

    public abstract String pattern();

    // Re-declare this as abstract to force subclasses to override.
    @Override
    public abstract String toString();
}
