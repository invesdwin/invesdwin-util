/*
 * Copyright 2015-2017 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v2.0 which accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package de.invesdwin.util.assertions.type.internal.junit;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Executable;
import de.invesdwin.util.assertions.ThrowingSupplier;
import de.invesdwin.util.error.Throwables;

@Immutable
public final class AssertTimeout {

    private AssertTimeout() {
    }

    public static void assertTimeout(final java.time.Duration timeout, final Executable executable) {
        assertTimeout(timeout, executable, () -> null);
    }

    public static void assertTimeout(final java.time.Duration timeout, final Executable executable,
            final String message) {
        assertTimeout(timeout, executable, () -> message);
    }

    public static void assertTimeout(final java.time.Duration timeout, final Executable executable,
            final Supplier<String> messageSupplier) {
        assertTimeout(timeout, () -> {
            executable.execute();
            return null;
        }, messageSupplier);
    }

    public static <T> T assertTimeout(final java.time.Duration timeout, final ThrowingSupplier<T> supplier) {
        return assertTimeout(timeout, supplier, () -> null);
    }

    public static <T> T assertTimeout(final java.time.Duration timeout, final ThrowingSupplier<T> supplier,
            final String message) {
        return assertTimeout(timeout, supplier, () -> message);
    }

    public static <T> T assertTimeout(final java.time.Duration timeout, final ThrowingSupplier<T> supplier,
            final Supplier<String> messageSupplier) {
        final long timeoutInMillis = timeout.toMillis();
        final long start = System.currentTimeMillis();
        T result = null;
        try {
            result = supplier.get();
        } catch (final Throwable ex) {
            throw Throwables.propagate(ex);
        }

        final long timeElapsed = System.currentTimeMillis() - start;
        if (timeElapsed > timeoutInMillis) {
            AssertionUtils.fail(AssertionUtils.buildPrefix(AssertionUtils.nullSafeGet(messageSupplier))
                    + "execution exceeded timeout of " + timeoutInMillis + " ms by " + (timeElapsed - timeoutInMillis)
                    + " ms");
        }
        return result;
    }

    public static void assertTimeoutPreemptively(final java.time.Duration timeout, final Executable executable) {
        assertTimeoutPreemptively(timeout, executable, () -> null);
    }

    public static void assertTimeoutPreemptively(final java.time.Duration timeout, final Executable executable,
            final String message) {
        assertTimeoutPreemptively(timeout, executable, () -> message);
    }

    public static void assertTimeoutPreemptively(final java.time.Duration timeout, final Executable executable,
            final Supplier<String> messageSupplier) {
        assertTimeoutPreemptively(timeout, () -> {
            executable.execute();
            return null;
        }, messageSupplier);
    }

    public static <T> T assertTimeoutPreemptively(final java.time.Duration timeout,
            final ThrowingSupplier<T> supplier) {
        return assertTimeoutPreemptively(timeout, supplier, () -> null);
    }

    public static <T> T assertTimeoutPreemptively(final java.time.Duration timeout, final ThrowingSupplier<T> supplier,
            final String message) {
        return assertTimeoutPreemptively(timeout, supplier, () -> message);
    }

    public static <T> T assertTimeoutPreemptively(final java.time.Duration timeout, final ThrowingSupplier<T> supplier,
            final Supplier<String> messageSupplier) {
        final ExecutorService executorService = java.util.concurrent.Executors.newSingleThreadExecutor();

        try {
            final Future<T> future = executorService.submit(() -> {
                try {
                    return supplier.get();
                } catch (final Throwable throwable) {
                    throw Throwables.propagate(throwable);
                }
            });

            final long timeoutInMillis = timeout.toMillis();
            try {
                return future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
            } catch (final TimeoutException ex) {
                throw new AssertionError(AssertionUtils.buildPrefix(AssertionUtils.nullSafeGet(messageSupplier))
                        + "execution timed out after " + timeoutInMillis + " ms");
            } catch (final ExecutionException ex) {
                throw Throwables.propagate(ex.getCause());
            } catch (final Throwable ex) {
                throw Throwables.propagate(ex);
            }
        } finally {
            executorService.shutdownNow();
        }
    }

}
