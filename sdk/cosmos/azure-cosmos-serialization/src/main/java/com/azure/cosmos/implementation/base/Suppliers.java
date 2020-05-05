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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.azure.cosmos.implementation.base.Preconditions.checkArgument;
import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * Useful suppliers.
 *
 * <p>All methods return serializable suppliers as long as they're given serializable parameters.
 *
 * @author Laurence Gonsalves
 * @author Harry Heymann
 * @since 2.0
 */
public final class Suppliers {
    private Suppliers() {
    }

    /**
     * Returns a new supplier which is the composition of the provided function and supplier. In other words, the new
     * supplier's value will be computed by retrieving the value from {@code supplier}, and then applying {@code
     * function} to that value. Note that the resulting supplier will not call {@code supplier} or invoke {@code
     * function} until it is called.
     *
     * @param <F> the type parameter
     * @param <T> the type parameter
     * @param function the function
     * @param supplier the supplier
     *
     * @return the supplier
     */
    public static <F, T> Supplier<T> compose(Function<? super F, T> function, Supplier<F> supplier) {
        return new SupplierComposition<>(function, supplier);
    }

    /**
     * Returns a supplier which caches the instance retrieved during the first call to {@code get()} and returns that
     * value on subsequent calls to {@code get()}. See:
     * <a href="http://en.wikipedia.org/wiki/Memoization">memoization</a>
     *
     * <p>The returned supplier is thread-safe. The delegate's {@code get()} method will be invoked at
     * most once unless the underlying {@code get()} throws an exception. The supplier's serialized form does not
     * contain the cached value, which will be recalculated when {@code get()} is called on the reserialized instance.
     *
     * <p>When the underlying delegate throws an exception then this memoizing supplier will keep
     * delegating calls until it returns valid data.
     *
     * <p>If {@code delegate} is an instance created by an earlier call to {@code memoize}, it is
     * returned directly.
     *
     * @param <T> the type parameter
     * @param delegate the delegate
     *
     * @return the supplier
     */
    public static <T> Supplier<T> memoize(Supplier<T> delegate) {
        if (delegate instanceof NonSerializableMemoizingSupplier
            || delegate instanceof MemoizingSupplier) {
            return delegate;
        }
        return delegate instanceof Serializable
            ? new MemoizingSupplier<T>(delegate)
            : new NonSerializableMemoizingSupplier<T>(delegate);
    }

    /**
     * Returns a supplier that caches the instance supplied by the delegate and removes the cached value after the
     * specified time has passed. Subsequent calls to {@code get()} return the cached value if the expiration time has
     * not passed. After the expiration time, a new value is retrieved, cached, and returned. See: <a
     * href="http://en.wikipedia.org/wiki/Memoization">memoization</a>
     *
     * <p>The returned supplier is thread-safe. The supplier's serialized form does not contain the
     * cached value, which will be recalculated when {@code get()} is called on the reserialized instance. The actual
     * memoization does not happen when the underlying delegate throws an exception.
     *
     * <p>When the underlying delegate throws an exception then this memoizing supplier will keep
     * delegating calls until it returns valid data.
     *
     * @param <T> the type parameter
     * @param delegate the delegate
     * @param duration the length of time after a value is created that it should stop being returned     by subsequent
     * {@code get()} calls
     * @param unit the unit that {@code duration} is expressed in
     *
     * @return the supplier
     *
     * @throws IllegalArgumentException if {@code duration} is not positive
     * @since 2.0
     */
    @SuppressWarnings("GoodTime") // should accept a java.time.Duration
    public static <T> Supplier<T> memoizeWithExpiration(
        Supplier<T> delegate, long duration, TimeUnit unit) {
        return new ExpiringMemoizingSupplier<T>(delegate, duration, unit);
    }

    /**
     * Returns a supplier that always supplies {@code instance}.
     *
     * @param <T> the type parameter
     * @param instance the instance
     *
     * @return the supplier
     */
    public static <T> Supplier<T> ofInstance(@Nullable T instance) {
        return new SupplierOfInstance<T>(instance);
    }

    /**
     * Returns a function that accepts a supplier and returns the result of invoking {@link Supplier#get}* on that
     * supplier.
     *
     * <p><b>Java 8 users:</b> use the method reference {@code Supplier::get} instead.
     *
     * @param <T> the type parameter
     *
     * @return the function
     *
     * @since 8.0
     */
    public static <T> Function<Supplier<T>, T> supplierFunction() {
        @SuppressWarnings("unchecked") // implementation is "fully variant"
            SupplierFunction<T> sf = (SupplierFunction<T>) SupplierFunctionImpl.INSTANCE;
        return sf;
    }

    /**
     * Returns a supplier whose {@code get()} method synchronizes on {@code delegate} before calling it, making it
     * thread-safe.
     *
     * @param <T> the type parameter
     * @param delegate the delegate
     *
     * @return the supplier
     */
    public static <T> Supplier<T> synchronizedSupplier(Supplier<T> delegate) {
        return new ThreadSafeSupplier<T>(delegate);
    }

    /**
     * The type Expiring memoizing supplier.
     *
     * @param <T> the type parameter
     */
    @SuppressWarnings("GoodTime") // lots of violations
    static class ExpiringMemoizingSupplier<T> implements Supplier<T> {

        /**
         * The Delegate.
         */
        transient final Supplier<T> delegate;
        /**
         * The Duration nanos.
         */
        final long durationNanos;
        /**
         * The Expiration nanos.
         */
        // The special value 0 means "not yet initialized".
        transient volatile long expirationNanos;
        /**
         * The Value.
         */
        transient volatile @Nullable T value;

        /**
         * Instantiates a new Expiring memoizing supplier.
         *
         * @param delegate the delegate
         * @param duration the duration
         * @param unit the unit
         */
        ExpiringMemoizingSupplier(Supplier<T> delegate, long duration, TimeUnit unit) {
            this.delegate = checkNotNull(delegate);
            this.durationNanos = unit.toNanos(duration);
            checkArgument(duration > 0, "duration (%s %s) must be > 0", duration, unit);
        }

        @Override
        public T get() {
            // Another variant of Double Checked Locking.
            //
            // We use two volatile reads. We could reduce this to one by
            // putting our fields into a holder class, but (at least on x86)
            // the extra memory consumption and indirection are more
            // expensive than the extra volatile reads.
            long nanos = expirationNanos;
            long now = Platform.systemNanoTime();
            if (nanos == 0 || now - nanos >= 0) {
                synchronized (this) {
                    if (nanos == expirationNanos) { // recheck for lost race
                        T t = delegate.get();
                        value = t;
                        nanos = now + durationNanos;
                        // In the very unlikely event that nanos is 0, set it to 1;
                        // no one will notice 1 ns of tardiness.
                        expirationNanos = (nanos == 0) ? 1 : nanos;
                        return t;
                    }
                }
            }
            return value;
        }

        @Override
        public String toString() {
            // This is a little strange if the unit the user provided was not NANOS,
            // but we don't want to store the unit just for toString
            return "Suppliers.memoizeWithExpiration(" + delegate + ", " + durationNanos + ", NANOS)";
        }
    }

    /**
     * The type Memoizing supplier.
     *
     * @param <T> the type parameter
     */
    @SuppressFBWarnings("IS2_INCONSISTENT_SYNC")
    static class MemoizingSupplier<T> implements Supplier<T> {

        /**
         * The Delegate.
         */
        final Supplier<T> delegate;
        /**
         * The Initialized.
         */
        transient volatile boolean initialized;
        /**
         * The Value.
         */
        // "value" does not need to be volatile; visibility piggy-backs
        // on volatile read of "initialized".
        transient @Nullable T value;

        /**
         * Instantiates a new Memoizing supplier.
         *
         * @param delegate the delegate
         */
        MemoizingSupplier(Supplier<T> delegate) {
            this.delegate = checkNotNull(delegate);
        }

        @Override
        public T get() {
            // A 2-field variant of Double Checked Locking.
            if (!initialized) {
                synchronized (this) {
                    if (!initialized) {
                        T t = delegate.get();
                        value = t;
                        initialized = true;
                        return t;
                    }
                }
            }
            return value;
        }

        @Override
        public String toString() {
            return "Suppliers.memoize("
                + (initialized ? "<supplier that returned " + value + ">" : delegate)
                + ")";
        }
    }

    /**
     * The type Non serializable memoizing supplier.
     *
     * @param <T> the type parameter
     */
    @SuppressFBWarnings("IS2_INCONSISTENT_SYNC")
    static class NonSerializableMemoizingSupplier<T> implements Supplier<T> {
        /**
         * The Delegate.
         */
        volatile Supplier<T> delegate;
        /**
         * The Initialized.
         */
        volatile boolean initialized;
        /**
         * The Value.
         */
        // "value" does not need to be volatile; visibility piggy-backs
        // on volatile read of "initialized".
        @Nullable T value;

        /**
         * Instantiates a new Non serializable memoizing supplier.
         *
         * @param delegate the delegate
         */
        NonSerializableMemoizingSupplier(Supplier<T> delegate) {
            this.delegate = checkNotNull(delegate);
        }

        @Override
        public T get() {
            // A 2-field variant of Double Checked Locking.
            if (!initialized) {
                synchronized (this) {
                    if (!initialized) {
                        T t = delegate.get();
                        value = t;
                        initialized = true;
                        // Release the delegate to GC.
                        delegate = null;
                        return t;
                    }
                }
            }
            return value;
        }

        @Override
        public String toString() {
            Supplier<T> delegate = this.delegate;
            return "Suppliers.memoize("
                + (delegate == null ? "<supplier that returned " + value + ">" : delegate)
                + ")";
        }
    }

    private enum SupplierFunctionImpl implements SupplierFunction<Object> {
        /**
         * Instance supplier function.
         */
        INSTANCE;

        // Note: This makes T a "pass-through type"
        @Override
        public Object apply(Supplier<Object> input) {
            return input.get();
        }

        @Override
        public String toString() {
            return "Suppliers.supplierFunction()";
        }
    }

    private interface SupplierFunction<T> extends Function<Supplier<T>, T> {
    }

    private static class SupplierComposition<F, T> implements Supplier<T> {

        /**
         * The Function.
         */
        final Function<? super F, T> function;
        /**
         * The Supplier.
         */
        final Supplier<F> supplier;

        /**
         * Instantiates a new Supplier composition.
         *
         * @param function the function
         * @param supplier the supplier
         */
        SupplierComposition(Function<? super F, T> function, Supplier<F> supplier) {
            this.function = checkNotNull(function);
            this.supplier = checkNotNull(supplier);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof SupplierComposition) {
                SupplierComposition<?, ?> that = (SupplierComposition<?, ?>) obj;
                return function.equals(that.function) && supplier.equals(that.supplier);
            }
            return false;
        }

        @Override
        public T get() {
            return function.apply(supplier.get());
        }

        @Override
        public int hashCode() {
            return Objects.hash(function, supplier);
        }

        @Override
        public String toString() {
            return "Suppliers.compose(" + function + ", " + supplier + ")";
        }
    }

    private static class SupplierOfInstance<T> implements Supplier<T>, Serializable {
        private static final long serialVersionUID = 0;
        /**
         * The Instance.
         */
        final @Nullable T instance;

        /**
         * Instantiates a new Supplier of instance.
         *
         * @param instance the instance
         */
        SupplierOfInstance(@Nullable T instance) {
            this.instance = instance;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof SupplierOfInstance) {
                SupplierOfInstance<?> that = (SupplierOfInstance<?>) obj;
                return Objects.equals(instance, that.instance);
            }
            return false;
        }

        @Override
        public T get() {
            return instance;
        }

        @Override
        public int hashCode() {
            return Objects.hash(instance);
        }

        @Override
        public String toString() {
            return "Suppliers.ofInstance(" + instance + ")";
        }
    }

    private static class ThreadSafeSupplier<T> implements Supplier<T> {
        private static final long serialVersionUID = 0;
        /**
         * The Delegate.
         */
        final Supplier<T> delegate;

        /**
         * Instantiates a new Thread safe supplier.
         *
         * @param delegate the delegate
         */
        ThreadSafeSupplier(Supplier<T> delegate) {
            this.delegate = checkNotNull(delegate);
        }

        @Override
        public T get() {
            synchronized (delegate) {
                return delegate.get();
            }
        }

        @Override
        public String toString() {
            return "Suppliers.synchronizedSupplier(" + delegate + ")";
        }
    }
}
