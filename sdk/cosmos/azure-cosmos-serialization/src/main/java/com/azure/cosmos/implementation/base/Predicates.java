/*
 * Copyright (C) 2007 The Guava Authors
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

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * Static utility methods pertaining to {@code Predicate} instances.
 *
 * <p>All methods return serializable predicates as long as they're given serializable parameters.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/FunctionalExplained">the use of {@code Predicate}</a>.
 *
 * @author Kevin Bourrillion
 * @since 2.0
 */
public final class Predicates {
    private Predicates() {
    }

    // TODO(kevinb): considering having these implement a VisitablePredicate
    // interface which specifies an accept(PredicateVisitor) method.

    /**
     * Returns a predicate that always evaluates to {@code false}.
     *
     * @param <T> the predicate type.
     *
     * @return a predicate that always evaluates to {@code false}.
     */
    public static <T> Predicate<T> alwaysFalse() {
        return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
    }

    /**
     * Returns a predicate that always evaluates to {@code true}.
     *
     * @param <T> the predicate type.
     *
     * @return a predicate that always evaluates to {@code false}.
     */
    public static <T> Predicate<T> alwaysTrue() {
        return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
    }

    /**
     * Returns a predicate that evaluates to {@code true} if each of its components evaluates to {@code true}.
     * <p>
     * The components are evaluated in order, and evaluation will be "short-circuited" as soon as a false predicate is
     * found. It defensively copies the iterable passed in, so future changes to it won't alter the behavior of this
     * predicate. If {@code components} is empty, the returned predicate will always evaluate to {@code true}.
     *
     * @param <T> the predicate type.
     * @param components an {@link Iterable iterable} over the components to evaluate.
     *
     * @return a predicate that evaluates to {@code true} if each of the {@code components} evaluates to {@code true}.
     */
    public static <T> Predicate<T> and(Iterable<? extends Predicate<?
        super T>> components) {
        return new AndPredicate<T>(defensiveCopy(components));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if each of its components evaluates to {@code true}.
     * <p>
     * The components are evaluated in order, and evaluation will be "short-circuited" as soon as a false predicate is
     * found. It defensively copies the array passed in, so future changes to it won't alter the behavior of this
     * predicate. If {@code components} is empty, the returned predicate will always evaluate to {@code true}.
     *
     * @param <T> the predicate type.
     * @param components a variable argument list of the components to evaluate.
     *
     * @return a predicate that evaluates to {@code true} if each of the {@code components} evaluates to {@code true}.
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Predicate<T> and(Predicate<? super T>... components) {
        return new AndPredicate<T>(defensiveCopy(components));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if both of its components evaluate to {@code true}.
     * <p>
     * The components are evaluated in order, and evaluation will be "short-circuited" as soon as a false predicate is
     * found.
     *
     * @param <T> the predicate type.
     * @param first the first component predicate to evaluate.
     * @param second the second component predicate to evaluate.
     *
     * @return a predicate that evaluates to {@code true} if the {@code first} and {@code second} component predicates
     * evaluate to {@code true}.
     */
    public static <T> Predicate<T> and(Predicate<? super T> first, Predicate<? super T> second) {
        return new AndPredicate<T>(Predicates.asList(checkNotNull(first), checkNotNull(second)));
    }

    /**
     * Returns the composition of a function and a predicate.
     * <p>
     * For every {@code x}, the generated predicate returns {@code predicate(function(x))}.
     *
     * @param <A> the type of the input to the function and the predicate.
     * @param <B> the type of the result of the function.
     * @param predicate the predicate.
     * @param function the function.
     *
     * @return the composition of the provided function and predicate.
     */
    public static <A, B> Predicate<A> compose(Predicate<B> predicate, Function<A, ? extends B> function) {
        return new CompositionPredicate<>(predicate, function);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the {@link CharSequence} being tested contains any match
     * for the given regular expression pattern.
     * <p>
     * The test used is equivalent to {@code pattern.matcher(arg).find()}.
     *
     * @param pattern the regular expression pattern.
     *
     * @return a predicate that evaluates to {@code true} if the {@link CharSequence} being tested contains any match
     * for {@code pattern}.
     *
     * @since 3.0
     */
    public static Predicate<CharSequence> contains(Pattern pattern) {
        return new ContainsPatternPredicate(new com.azure.cosmos.implementation.base.JdkPattern(pattern));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the {@code CharSequence} being tested contains any match
     * for the given regular expression pattern.
     * <p>
     * The test used is equivalent to {@code Pattern.compile(pattern).matcher(arg).find()}.
     *
     * @param pattern the regular expression pattern.
     *
     * @return a predicate that evaluates to {@code true} if the {@code CharSequence} being tested contains any match
     * for {@code pattern}.
     *
     * @throws IllegalArgumentException if the pattern is invalid
     * @since 3.0
     */
    public static Predicate<CharSequence> containsPattern(String pattern) {
        return new ContainsPatternFromStringPredicate(pattern);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object being tested {@code equals()} the given target
     * or both are null.
     *
     * @param <T> the type of the input to the predicate.
     * @param target the target.
     *
     * @return a predicate that evaluates to {@code true} if the object being tested {@code equals()} the given target
     * or both are null.
     */
    public static <T> Predicate<T> equalTo(@Nullable T target) {
        return (target == null) ? Predicates.isNull() : new IsEqualToPredicate<T>(target);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object reference being tested is a member of the given
     * collection. It does not defensively copy the collection passed in, so future changes to it will alter the
     * behavior of the predicate.
     *
     * <p>This method can technically accept any {@code Collection<?>}, but using a typed collection
     * helps prevent bugs. This approach doesn't block any potential users since it is always possible to use {@code
     * Predicates.<Object>in()}.
     *
     * @param <T> the type of the input to the predicate.
     * @param target the collection that may contain the function input
     *
     * @return the predicate
     */
    public static <T> Predicate<T> in(Collection<? extends T> target) {
        return new InPredicate<T>(target);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object being tested is an instance of the given class.
     * If the object being tested is {@code null} this predicate evaluates to {@code false}*.
     *
     * <p>If you want to filter an {@code Iterable} to narrow its type, consider using {@link
     * com.google.common.collect.Iterables#filter(Iterable, Class)}* in preference.
     *
     * <p><b>Warning:</b> contrary to the typical assumptions about predicates (as documented at
     * {@link Predicate#test}), the returned predicate may not be <i>consistent with equals</i>. For example, {@code
     * instanceOf(ArrayList.class)} will yield different results for the two equal instances {@code
     * Lists.newArrayList(1)} and {@code Arrays.asList(1)}.
     *
     * @param clazz the clazz
     *
     * @return the predicate
     */
    public static Predicate<Object> instanceOf(Class<?> clazz) {
        return new InstanceOfPredicate(clazz);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object reference being tested is null.
     *
     * @param <T> the type of the input to the predicate.
     *
     * @return the predicate
     */
    public static <T> Predicate<T> isNull() {
        return ObjectPredicate.IS_NULL.withNarrowedType();
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the given predicate evaluates to {@code false}*.
     *
     * @param <T> the type of the input to the predicate.
     * @param predicate the predicate
     *
     * @return the predicate
     */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return new NotPredicate<T>(predicate);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object reference being tested is not null.
     *
     * @param <T> the type of the input to the predicate.
     *
     * @return the predicate
     */
    public static <T> Predicate<T> notNull() {
        return ObjectPredicate.NOT_NULL.withNarrowedType();
    }

    /**
     * Returns a predicate that evaluates to {@code true} if any one of its components evaluates to {@code true}. The
     * components are evaluated in order, and evaluation will be "short-circuited" as soon as a true predicate is found.
     * It defensively copies the iterable passed in, so future changes to it won't alter the behavior of this predicate.
     * If {@code components} is empty, the returned predicate will always evaluate to {@code false}.
     *
     * @param <T> the type of the input to the predicate.
     * @param components the components
     *
     * @return the predicate
     */
    public static <T> Predicate<T> or(Iterable<? extends Predicate<?
        super T>> components) {
        return new OrPredicate<T>(defensiveCopy(components));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if any one of its components evaluates to {@code true}.
     * <p>
     * The components are evaluated in order, and evaluation will be "short-circuited" as soon as a true predicate is
     * found. It defensively copies the array passed in, so future changes to it won't alter the behavior of this
     * predicate. If {@code components} is empty, the returned predicate will always evaluate to {@code false}.
     *
     * @param <T> the type of the predicate.
     * @param components the components to be evaluated.
     *
     * @return a predicate that evaluates to {@code true} if any one of its components evaluates to {@code true}.
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Predicate<T> or(Predicate<? super T>... components) {
        return new OrPredicate<T>(defensiveCopy(components));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if either of its components evaluates to {@code true}.
     * <p>
     * The components are evaluated in order, and evaluation will be "short-circuited" as soon as a true predicate is
     * found.
     *
     * @param <T> the type of the predicate.
     * @param first the first component to be evaluated.
     * @param second the second component to be evaluated.
     *
     * @return a predicate that evaluates to {@code true} if any one of its components evaluates to {@code true}.
     */
    public static <T> Predicate<T> or(Predicate<? super T> first,
                                      Predicate<? super T> second) {
        return new OrPredicate<T>(Predicates.asList(checkNotNull(first), checkNotNull(second)));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the class being tested is assignable to (is a subtype of)
     * {@code clazz}. Example:
     *
     * <pre>{@code
     * List<Class<?>> classes = Arrays.asList(
     *     Object.class, String.class, Number.class, Long.class);
     * return Iterables.filter(classes, subtypeOf(Number.class));
     * }*</pre>
     * <p>
     * The code above returns an iterable containing {@code Number.class} and {@code Long.class}.
     *
     * @param clazz the clazz
     *
     * @return the predicate
     *
     * @since 20.0 (since 10.0 under the incorrect name {@code assignableFrom})
     */
    public static Predicate<Class<?>> subtypeOf(Class<?> clazz) {
        return new SubtypeOfPredicate(clazz);
    }

    // End public API, begin private implementation classes.

    /**
     * Defensive copy list.
     *
     * @param <T> the type of the input to the predicate.
     * @param iterable the iterable
     *
     * @return the list
     */
    static <T> List<T> defensiveCopy(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<T>();
        for (T element : iterable) {
            list.add(checkNotNull(element));
        }
        return list;
    }

    private static <T> List<Predicate<? super T>> asList(
        Predicate<? super T> first, Predicate<? super T> second) {
        // TODO(kevinb): understand why we still get a warning despite @SafeVarargs!
        return Arrays.asList(first, second);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    private static <T> List<T> defensiveCopy(T... array) {
        return defensiveCopy(Arrays.asList(array));
    }

    private static String toStringHelper(String methodName, Iterable<?> components) {
        StringBuilder builder = new StringBuilder("Predicates.").append(methodName).append('(');
        boolean first = true;
        for (Object o : components) {
            if (!first) {
                builder.append(',');
            }
            builder.append(o);
            first = false;
        }
        return builder.append(')').toString();
    }

    /**
     * The enum Object predicate.
     */
    // Package private for GWT serialization.
    enum ObjectPredicate implements Predicate<Object> {
        /**
         * The Always true.
         *
         * @see Predicates#alwaysTrue() Predicates#alwaysTrue()
         */
        ALWAYS_TRUE {
            @Override
            public boolean test(@Nullable Object o) {
                return true;
            }

            @Override
            public String toString() {
                return "Predicates.alwaysTrue()";
            }
        },
        /**
         * The Always false.
         *
         * @see Predicates#alwaysFalse() Predicates#alwaysFalse()
         */
        ALWAYS_FALSE {
            @Override
            public boolean test(@Nullable Object o) {
                return false;
            }

            @Override
            public String toString() {
                return "Predicates.alwaysFalse()";
            }
        },
        /**
         * The Is null.
         *
         * @see Predicates#isNull() Predicates#isNull()
         */
        IS_NULL {
            @Override
            public boolean test(@Nullable Object o) {
                return o == null;
            }

            @Override
            public String toString() {
                return "Predicates.isNull()";
            }
        },
        /**
         * The Not null.
         *
         * @see Predicates#notNull() Predicates#notNull()
         */
        NOT_NULL {
            @Override
            public boolean test(@Nullable Object o) {
                return o != null;
            }

            @Override
            public String toString() {
                return "Predicates.notNull()";
            }
        };

        /**
         * With narrowed type predicate.
         *
         * @param <T> the type of the input to the predicate.
         *
         * @return the predicate
         */
        @SuppressWarnings("unchecked")
        // safe contravariant cast
        <T> Predicate<T> withNarrowedType() {
            return (Predicate<T>) this;
        }
    }

    /**
     * @see Predicates#and(Iterable)
     */
    private static final class AndPredicate<T> implements Predicate<T>, Serializable {
        private static final long serialVersionUID = 0;
        private final List<? extends Predicate<? super T>> components;

        private AndPredicate(List<? extends Predicate<? super T>> components) {
            this.components = components;
        }

        @Override
        public boolean test(@Nullable T t) {
            // Avoid using the Iterator to avoid generating garbage (issue 820).
            for (int i = 0; i < components.size(); i++) {
                if (!components.get(i).test(t)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof AndPredicate) {
                AndPredicate<?> that = (AndPredicate<?>) obj;
                return components.equals(that.components);
            }
            return false;
        }

        @Override
        public int hashCode() {
            // add a random number to avoid collisions with OrPredicate
            return components.hashCode() + 0x12472c2c;
        }

        @Override
        public String toString() {
            return toStringHelper("and", components);
        }
    }

    /**
     * @see Predicates#compose(Predicate, Function)
     */
    private static final class CompositionPredicate<A, B> implements Predicate<A>, Serializable {
        private static final long serialVersionUID = 0;
        /**
         * The F.
         */
        final Function<A, ? extends B> f;
        /**
         * The P.
         */
        final Predicate<B> p;

        private CompositionPredicate(Predicate<B> p, Function<A, ? extends B> f) {
            this.p = checkNotNull(p);
            this.f = checkNotNull(f);
        }

        @Override
        public boolean test(@Nullable A a) {
            return p.test(f.apply(a));
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof CompositionPredicate) {
                CompositionPredicate<?, ?> that = (CompositionPredicate<?, ?>) obj;
                return f.equals(that.f) && p.equals(that.p);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return f.hashCode() ^ p.hashCode();
        }

        @Override
        public String toString() {
            // TODO(cpovirk): maybe make this look like the method call does ("Predicates.compose(...)")
            return p + "(" + f + ")";
        }
    }

    /**
     * @see Predicates#containsPattern(String)
     */
    private static class ContainsPatternFromStringPredicate extends ContainsPatternPredicate {

        private static final long serialVersionUID = 0;

        /**
         * Instantiates a new Contains pattern from string predicate.
         *
         * @param string the string
         */
        ContainsPatternFromStringPredicate(String string) {
            super(com.azure.cosmos.implementation.base.Platform.compilePattern(string));
        }

        @Override
        public String toString() {
            return "Predicates.containsPattern(" + pattern.pattern() + ")";
        }
    }

    /**
     * @see Predicates#contains(Pattern)
     */
    private static class ContainsPatternPredicate implements Predicate<CharSequence>,
        Serializable {
        private static final long serialVersionUID = 0;
        /**
         * The Pattern.
         */
        final com.azure.cosmos.implementation.base.CommonPattern pattern;

        /**
         * Instantiates a new Contains pattern predicate.
         *
         * @param pattern the pattern
         */
        ContainsPatternPredicate(com.azure.cosmos.implementation.base.CommonPattern pattern) {
            this.pattern = checkNotNull(pattern);
        }

        @Override
        public boolean test(CharSequence t) {
            return pattern.matcher(t).find();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof ContainsPatternPredicate) {
                ContainsPatternPredicate that = (ContainsPatternPredicate) obj;

                // Pattern uses Object (identity) equality, so we have to reach
                // inside to compare individual fields.
                return Objects.equals(pattern.pattern(), that.pattern.pattern())
                    && pattern.flags() == that.pattern.flags();
            }
            return false;
        }

        @Override
        public int hashCode() {
            // Pattern uses Object.hashCode, so we have to reach
            // inside to build a hashCode consistent with equals.

            return Objects.hash(pattern.pattern(), pattern.flags());
        }

        @Override
        public String toString() {
            String patternString =
                MoreObjects.toStringHelper(pattern)
                    .add("pattern", pattern.pattern())
                    .add("pattern.flags", pattern.flags())
                    .toString();
            return "Predicates.contains(" + patternString + ")";
        }
    }

    /**
     * @see Predicates#in(Collection)
     */
    private static final class InPredicate<T> implements Predicate<T>, Serializable {
        private static final long serialVersionUID = 0;
        private final Collection<?> target;

        private InPredicate(Collection<?> target) {
            this.target = checkNotNull(target);
        }

        @Override
        public boolean test(@Nullable T t) {
            try {
                return target.contains(t);
            } catch (NullPointerException | ClassCastException e) {
                return false;
            }
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof InPredicate) {
                InPredicate<?> that = (InPredicate<?>) obj;
                return target.equals(that.target);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public String toString() {
            return "Predicates.in(" + target + ")";
        }
    }

    /**
     * @see Predicates#instanceOf(Class)
     */
    private static final class InstanceOfPredicate implements Predicate<Object>, Serializable {
        private static final long serialVersionUID = 0;
        private final Class<?> clazz;

        private InstanceOfPredicate(Class<?> clazz) {
            this.clazz = checkNotNull(clazz);
        }

        @Override
        public boolean test(@Nullable Object o) {
            return clazz.isInstance(o);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof InstanceOfPredicate) {
                InstanceOfPredicate that = (InstanceOfPredicate) obj;
                return clazz == that.clazz;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return clazz.hashCode();
        }

        @Override
        public String toString() {
            return "Predicates.instanceOf(" + clazz.getName() + ")";
        }
    }

    /**
     * @see Predicates#equalTo(Object)
     */
    private static final class IsEqualToPredicate<T> implements Predicate<T>, Serializable {
        private static final long serialVersionUID = 0;
        private final T target;

        private IsEqualToPredicate(T target) {
            this.target = target;
        }

        @Override
        public boolean test(T t) {
            return target.equals(t);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof IsEqualToPredicate) {
                IsEqualToPredicate<?> that = (IsEqualToPredicate<?>) obj;
                return target.equals(that.target);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public String toString() {
            return "Predicates.equalTo(" + target + ")";
        }
    }

    /**
     * @see Predicates#not(Predicate)
     */
    private static class NotPredicate<T> implements Predicate<T>, Serializable {
        private static final long serialVersionUID = 0;
        /**
         * The Predicate.
         */
        final Predicate<T> predicate;

        /**
         * Instantiates a new Not predicate.
         *
         * @param predicate the predicate
         */
        NotPredicate(Predicate<T> predicate) {
            this.predicate = checkNotNull(predicate);
        }

        @Override
        public boolean test(@Nullable T t) {
            return !predicate.test(t);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof NotPredicate) {
                NotPredicate<?> that = (NotPredicate<?>) obj;
                return predicate.equals(that.predicate);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return ~predicate.hashCode();
        }

        @Override
        public String toString() {
            return "Predicates.not(" + predicate + ")";
        }
    }

    /**
     * @see Predicates#or(Iterable)
     */
    private static final class OrPredicate<T> implements Predicate<T>, Serializable {
        private static final long serialVersionUID = 0;
        private final List<? extends Predicate<? super T>> components;

        private OrPredicate(List<? extends Predicate<? super T>> components) {
            this.components = components;
        }

        @Override
        public boolean test(@Nullable T t) {
            // Avoid using the Iterator to avoid generating garbage (issue 820).
            for (int i = 0; i < components.size(); i++) {
                if (components.get(i).test(t)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof OrPredicate) {
                OrPredicate<?> that = (OrPredicate<?>) obj;
                return components.equals(that.components);
            }
            return false;
        }

        @Override
        public int hashCode() {
            // add a random number to avoid collisions with AndPredicate
            return components.hashCode() + 0x053c91cf;
        }

        @Override
        public String toString() {
            return toStringHelper("or", components);
        }
    }

    /**
     * @see Predicates#subtypeOf(Class)
     */
    private static final class SubtypeOfPredicate implements Predicate<Class<?>>, Serializable {
        private static final long serialVersionUID = 0;
        private final Class<?> clazz;

        private SubtypeOfPredicate(Class<?> clazz) {
            this.clazz = checkNotNull(clazz);
        }

        @Override
        public boolean test(Class<?> input) {
            return clazz.isAssignableFrom(input);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof SubtypeOfPredicate) {
                SubtypeOfPredicate that = (SubtypeOfPredicate) obj;
                return clazz == that.clazz;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return clazz.hashCode();
        }

        @Override
        public String toString() {
            return "Predicates.subtypeOf(" + clazz.getName() + ")";
        }
    }
}
