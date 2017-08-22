package de.invesdwin.util.concurrent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FTimeUnit;

@Immutable
public final class Futures {

    private Futures() {}

    public static <T> T get(final Future<T> future) throws InterruptedException {
        try {
            return future.get();
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

    public static <T> List<T> get(final List<Future<T>> futures) throws InterruptedException {
        final List<T> resultate = new ArrayList<T>(futures.size());
        for (final Future<T> future : futures) {
            resultate.add(get(future));
        }
        return resultate;
    }

    public static void submitAndWait(final ExecutorService executor, final Runnable task) throws InterruptedException {
        final Future<?> future = executor.submit(task);
        wait(future);
    }

    public static void submitAndWait(final ExecutorService executor, final List<? extends Runnable> tasks)
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
    public static void submitAndWaitFailFast(final ExecutorService executor, final List<? extends Runnable> tasks)
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

    public static <T> List<T> submitAndGet(final ExecutorService executor, final List<? extends Callable<T>> tasks)
            throws InterruptedException {
        final List<Future<T>> futures = executor.invokeAll(tasks);
        return get(futures);
    }

    public static void wait(final Future<?>... futures) throws InterruptedException {
        wait(Arrays.asList(futures));
    }

    public static void wait(final List<? extends Future<?>> futures) throws InterruptedException {
        for (final Future<?> future : futures) {
            wait(future);
        }
    }

    public static void wait(final Future<?> future) throws InterruptedException {
        Assertions.assertThat(get(future)).isNull();
    }
}
