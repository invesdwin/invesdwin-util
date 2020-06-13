package de.invesdwin.util.concurrent.future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.future.internal.AFuturesStaticFacade;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FTimeUnit;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.concurrent.future.internal.AFuturesStaticFacade", targets = {
        com.google.common.util.concurrent.Futures.class })
public final class Futures extends AFuturesStaticFacade {

    private Futures() {
    }

    public static <T> T get(final Future<T> future) throws InterruptedException {
        try {
            return future.get();
        } catch (final InterruptedException e) {
            future.cancel(true);
            throw e;
        } catch (final ExecutionException e) {
            final InterruptedException iCause = Throwables.getCauseByType(e, InterruptedException.class);
            if (iCause != null) {
                throw iCause;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @SafeVarargs
    public static <T> List<T> get(final Future<T>... futures) throws InterruptedException {
        return get(Arrays.asList(futures));
    }

    public static <T> List<T> get(final Iterable<? extends Future<T>> futures) throws InterruptedException {
        try {
            final List<T> results = new ArrayList<T>();
            for (final Future<T> future : futures) {
                results.add(get(future));
            }
            return results;
        } catch (final InterruptedException e) {
            cancel(futures);
            throw e;
        }
    }

    @SafeVarargs
    public static <T> void cancel(final Future<? extends T>... futures) {
        cancel(Arrays.asList(futures));
    }

    public static <T> void cancel(final Iterable<? extends Future<? extends T>> futures) {
        for (final Future<?> future : futures) {
            cancel(future);
        }
    }

    public static <T> void cancel(final Future<? extends T> future) {
        if (!future.isDone() && !future.isCancelled()) {
            future.cancel(true);
        }
    }

    public static void submitAndWait(final ExecutorService executor, final Runnable task) throws InterruptedException {
        final Future<?> future = executor.submit(task);
        wait(future);
    }

    public static void submitAndWait(final ExecutorService executor, final Collection<? extends Runnable> tasks)
            throws InterruptedException {
        final List<Future<?>> futures = new ArrayList<Future<?>>(tasks.size());

        for (final Runnable task : tasks) {
            futures.add(executor.submit(task));
        }
        wait(futures);
    }

    /**
     * Returns with the first exception and aborts remaining tasks. This is useful for caller runs or similar throttled
     * executors.
     */
    public static void submitAndWaitFailFast(final ExecutorService executor, final Collection<? extends Runnable> tasks)
            throws InterruptedException {
        final List<Future<?>> futures = new CopyOnWriteArrayList<Future<?>>();

        FDate lastFailFastCheck = FDate.MIN_DATE;
        for (final Runnable task : tasks) {
            futures.add(executor.submit(task));
            if (new Duration(lastFailFastCheck, new FDate()).isGreaterThan(5, FTimeUnit.SECONDS)) {
                //failfast on exceptions
                for (final Future<?> future : futures) {
                    if (future.isDone()) {
                        futures.remove(future);
                        try {
                            Assertions.assertThat(get(future)).isNull();
                        } catch (final Throwable t) {
                            for (final Future<?> f : futures) {
                                f.cancel(true);
                            }
                            throw Throwables.propagate(t);
                        }
                    }
                }
                lastFailFastCheck = new FDate();
            }
        }
        wait(futures);
    }

    public static <T> T submitAndGet(final ExecutorService executor, final Callable<T> task)
            throws InterruptedException {
        final Future<T> future = executor.submit(task);
        return get(future);
    }

    public static <T> List<T> submitAndGet(final ExecutorService executor,
            final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        final List<Future<T>> futures = executor.invokeAll(tasks);
        return get(futures);
    }

    @SafeVarargs
    public static <T> void wait(final Future<? extends T>... futures) throws InterruptedException {
        wait(Arrays.asList(futures));
    }

    public static <T> void wait(final Iterable<? extends Future<? extends T>> futures) throws InterruptedException {
        try {
            for (final Future<?> future : futures) {
                wait(future);
            }
        } catch (final InterruptedException e) {
            cancel(futures);
            throw e;
        }
    }

    public static <T> void wait(final Future<? extends T> future) throws InterruptedException {
        Assertions.assertThat(get(future)).isNull();
    }
}
