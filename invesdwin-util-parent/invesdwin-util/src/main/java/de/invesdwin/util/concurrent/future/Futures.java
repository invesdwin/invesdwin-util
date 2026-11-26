package de.invesdwin.util.concurrent.future;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.Immutable;

import org.slf4j.helpers.Reporter;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.concurrent.future.internal.AFuturesStaticFacade;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.concurrent.future.internal.AFuturesStaticFacade", targets = {
        com.google.common.util.concurrent.Futures.class })
public final class Futures extends AFuturesStaticFacade {

    @SuppressWarnings("rawtypes")
    public static final FutureTask[] FUTURETASK_EMPTY_ARRAY = new FutureTask[0];

    /**
     * How long the join keeps waiting for a unit that is still running once ANOTHER unit has already failed. Bounded on
     * purpose: past a failure the wait exists to let in-flight siblings finish work already started, not to fund the rest
     * of the run — and a fork that dies on {@code forkedProcessTimeoutInSeconds} reports no tally at all. While nothing
     * has failed there is no bound at all, so a good run takes as long as it takes.
     */
    private static final Duration POST_FAILURE_GRACE = new Duration(5, FTimeUnit.MINUTES);

    private static final long CANCEL_JOIN_CHECK_MILLIS = 100L;

    private Futures() {}

    //////////// GET ////////////

    public static <T> T get(final Future<T> future) throws InterruptedException {
        try {
            while (true) {
                try {
                    return future.get(1, TimeUnit.SECONDS);
                } catch (final TimeoutException e) {
                    /*
                     * retry, we use polling to prevent deadlock in TrustedListenableFutureTask (despite the value being
                     * set already it sometimes waits endlessly; though might only happen during debugging)
                     */
                }
            }
        } catch (final InterruptedException e) {
            cancelAndJoin(future, e);
            throw e;
        } catch (final ExecutionException e) {
            final InterruptedException iCause = Throwables.getCauseByType(e, InterruptedException.class);
            if (iCause != null) {
                throw iCause;
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    public static <T> T getRethrowing(final Future<T> future) throws Exception {
        try {
            while (true) {
                try {
                    return future.get(1, TimeUnit.SECONDS);
                } catch (final TimeoutException e) {
                    /*
                     * retry, we use polling to prevent deadlock in TrustedListenableFutureTask (despite the value being
                     * set already it sometimes waits endlessly; though might only happen during debugging)
                     */
                }
            }
        } catch (final InterruptedException e) {
            cancelAndJoin(future, e);
            throw e;
        } catch (final ExecutionException e) {
            final InterruptedException iCause = Throwables.getCauseByType(e, InterruptedException.class);
            if (iCause != null) {
                throw iCause;
            } else {
                final Throwable cause = e.getCause();
                if (cause instanceof Exception) {
                    throw (Exception) cause;
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }
    }

    public static <T> T getPropagating(final Future<T> future) throws InterruptedException {
        try {
            while (true) {
                try {
                    return future.get(1, TimeUnit.SECONDS);
                } catch (final TimeoutException e) {
                    /*
                     * retry, we use polling to prevent deadlock in TrustedListenableFutureTask (despite the value being
                     * set already it sometimes waits endlessly; though might only happen during debugging)
                     */
                }
            }
        } catch (final InterruptedException e) {
            cancelAndJoin(future, e);
            throw e;
        } catch (final ExecutionException e) {
            final InterruptedException iCause = Throwables.getCauseByType(e, InterruptedException.class);
            if (iCause != null) {
                throw iCause;
            } else {
                final Throwable cause = e.getCause();
                throw Throwables.propagate(cause);
            }
        }
    }

    public static <T> T getNoInterrupt(final Future<T> future) {
        try {
            return get(future);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    public static <T> T getRethrowingNoInterrupt(final Future<T> future) throws Exception {
        try {
            return getRethrowing(future);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    public static <T> T getPropagatingNoInterrupt(final Future<T> future) {
        try {
            return getPropagating(future);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    //////////// GET TIMEOUT UNIT

    public static <T> T get(final Future<T> future, final long timeout, final TimeUnit unit)
            throws InterruptedException, TimeoutException {
        try {
            return future.get(timeout, unit);
        } catch (final InterruptedException e) {
            cancelAndJoin(future, e);
            throw e;
        } catch (final ExecutionException e) {
            final InterruptedException iCause = Throwables.getCauseByType(e, InterruptedException.class);
            if (iCause != null) {
                throw iCause;
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    public static <T> T getRethrowing(final Future<T> future, final long timeout, final TimeUnit unit)
            throws Exception {
        try {
            return future.get(timeout, unit);
        } catch (final InterruptedException e) {
            cancelAndJoin(future, e);
            throw e;
        } catch (final ExecutionException e) {
            final InterruptedException iCause = Throwables.getCauseByType(e, InterruptedException.class);
            if (iCause != null) {
                throw iCause;
            } else {
                final Throwable cause = e.getCause();
                if (cause instanceof Exception) {
                    throw (Exception) cause;
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }
    }

    public static <T> T getPropagating(final Future<T> future, final long timeout, final TimeUnit unit)
            throws TimeoutException, InterruptedException {
        try {
            return future.get(timeout, unit);
        } catch (final InterruptedException e) {
            cancelAndJoin(future, e);
            throw e;
        } catch (final ExecutionException e) {
            final InterruptedException iCause = Throwables.getCauseByType(e, InterruptedException.class);
            if (iCause != null) {
                throw iCause;
            } else {
                final Throwable cause = e.getCause();
                throw Throwables.propagate(cause);
            }
        }
    }

    public static <T> T getNoInterrupt(final Future<T> future, final long timeout, final TimeUnit unit)
            throws TimeoutException {
        try {
            return get(future, timeout, unit);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    public static <T> T getRethrowingNoInterrupt(final Future<T> future, final long timeout, final TimeUnit unit)
            throws Exception {
        try {
            return getRethrowing(future, timeout, unit);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    public static <T> T getPropagatingNoInterrupt(final Future<T> future, final long timeout, final TimeUnit unit)
            throws TimeoutException {
        try {
            return getPropagating(future, timeout, unit);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    //////////// GET DURATION

    public static <T> T get(final Future<T> future, final Duration timeout)
            throws InterruptedException, TimeoutException {
        return get(future, timeout.longValue(), timeout.getTimeUnit().timeUnitValue());
    }

    public static <T> T getRethrowing(final Future<T> future, final Duration timeout) throws Exception {
        return getRethrowing(future, timeout.longValue(), timeout.getTimeUnit().timeUnitValue());
    }

    public static <T> T getPropagating(final Future<T> future, final Duration timeout)
            throws TimeoutException, InterruptedException {
        return getPropagating(future, timeout.longValue(), timeout.getTimeUnit().timeUnitValue());
    }

    public static <T> T getNoInterrupt(final Future<T> future, final Duration timeout) throws TimeoutException {
        return getNoInterrupt(future, timeout.longValue(), timeout.getTimeUnit().timeUnitValue());
    }

    public static <T> T getRethrowingNoInterrupt(final Future<T> future, final Duration timeout) throws Exception {
        return getRethrowingNoInterrupt(future, timeout.longValue(), timeout.getTimeUnit().timeUnitValue());
    }

    public static <T> T getPropagatingNoInterrupt(final Future<T> future, final Duration timeout)
            throws TimeoutException {
        return getPropagatingNoInterrupt(future, timeout.longValue(), timeout.getTimeUnit().timeUnitValue());
    }

    //////////// GET ARRAY

    @SafeVarargs
    public static <T> List<T> get(final Future<T>... futures) throws InterruptedException, TimeoutException {
        return get(Arrays.asList(futures));
    }

    @SafeVarargs
    public static <T> List<T> getRethrowing(final Future<T>... futures) throws Exception {
        return getRethrowing(Arrays.asList(futures));
    }

    @SafeVarargs
    public static <T> List<T> getPropagating(final Future<T>... futures) throws TimeoutException, InterruptedException {
        return getPropagating(Arrays.asList(futures));
    }

    @SafeVarargs
    public static <T> List<T> getNoInterrupt(final Future<T>... futures) throws TimeoutException {
        return getNoInterrupt(Arrays.asList(futures));
    }

    @SafeVarargs
    public static <T> List<T> getRethrowingNoInterrupt(final Future<T>... futures) throws Exception {
        return getRethrowingNoInterrupt(Arrays.asList(futures));
    }

    @SafeVarargs
    public static <T> List<T> getPropagatingNoInterrupt(final Future<T>... futures) throws TimeoutException {
        return getPropagatingNoInterrupt(Arrays.asList(futures));
    }

    //////////// GET ITERABLE

    public static <T> List<T> get(final Iterable<? extends Future<T>> futures) throws InterruptedException {
        try {
            final List<T> results = new ArrayList<T>();
            for (final Future<T> future : futures) {
                results.add(get(future));
            }
            return results;
        } catch (final Throwable e) {
            cancelAndJoin(futures, e);
            throw e;
        }
    }

    public static <T> List<T> getRethrowing(final Iterable<? extends Future<T>> futures) throws Exception {
        try {
            final List<T> results = new ArrayList<T>();
            for (final Future<T> future : futures) {
                results.add(getRethrowing(future));
            }
            return results;
        } catch (final Throwable e) {
            cancelAndJoin(futures, e);
            throw e;
        }
    }

    public static <T> List<T> getPropagating(final Iterable<? extends Future<T>> futures) throws InterruptedException {
        try {
            final List<T> results = new ArrayList<T>();
            for (final Future<T> future : futures) {
                results.add(getPropagating(future));
            }
            return results;
        } catch (final InterruptedException e) {
            cancelAndJoin(futures, e);
            throw e;
        } catch (final Throwable e) {
            cancelAndJoin(futures, e);
            throw Throwables.propagate(e);
        }
    }

    public static <T> List<T> getNoInterrupt(final Iterable<? extends Future<T>> futures) {
        try {
            return get(futures);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    public static <T> List<T> getRethrowingNoInterrupt(final Iterable<? extends Future<T>> futures) throws Exception {
        try {
            return getRethrowing(futures);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    public static <T> List<T> getPropagatingNoInterrupt(final Iterable<? extends Future<T>> futures) {
        try {
            return getPropagating(futures);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    //////////// CANCEL ////////////

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

    /**
     * Await-multiple loops must not abandon siblings: on an exceptional exit all unfinished futures are cancelled
     * best effort and then joined (waited until done) before the original throwable is rethrown, so that when this
     * class returns or throws no awaited task can outlive the master. Join errors are attached as suppressed
     * exceptions to the original throwable; an interrupt arriving while joining is preserved (re-armed or propagated
     * with the original) and never swallowed.
     */
    private static void cancelAndJoin(final Iterable<? extends Future<?>> futures, final Throwable original) {
        for (final Future<?> future : futures) {
            try {
                cancel(future);
            } catch (final Throwable t) {
                Throwables.addSuppressed(original, t);
            }
        }
        boolean joinInterrupted = false;
        for (final Future<?> future : futures) {
            boolean done = false;
            while (!done) {
                try {
                    done = future.isDone();
                } catch (final Throwable t) {
                    //best effort join, a future that cannot answer isDone() must not deadlock the master
                    Throwables.addSuppressed(original, t);
                    break;
                }
                if (!done) {
                    try {
                        Thread.sleep(CANCEL_JOIN_CHECK_MILLIS);
                    } catch (final InterruptedException e) {
                        joinInterrupted = true;
                    }
                }
            }
        }
        if (joinInterrupted && !(original instanceof InterruptedException)) {
            //the original throwable is rethrown as-is, so keep the interrupt visible to the caller
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Await-one paths have the same contract as the await-multiple ones: a single awaited future is cancelled best
     * effort and then joined (waited until done) before the throwable is rethrown, so that the caller never proceeds
     * while the cancelled task body is still unwinding (FutureTask.cancel(true) marks the future done before its run
     * method has returned).
     */
    private static void cancelAndJoin(final Future<?> future, final Throwable original) {
        cancelAndJoin(Arrays.asList(future), original);
    }

    //////////// SUBMIT AND GET

    public static <T> T submitAndGet(final ExecutorService executor, final Callable<T> task)
            throws InterruptedException {
        final Future<T> future = executor.submit(task);
        return get(future);
    }

    public static <T> T submitAndGetRethrowing(final ExecutorService executor, final Callable<T> task)
            throws Exception {
        final Future<T> future = executor.submit(task);
        return getRethrowing(future);
    }

    public static <T> T submitAndGetPropagating(final ExecutorService executor, final Callable<T> task)
            throws Exception {
        final Future<T> future = executor.submit(task);
        return getPropagating(future);
    }

    public static <T> T submitAndGetNoInterrupt(final ExecutorService executor, final Callable<T> task) {
        final Future<T> future = executor.submit(task);
        return getNoInterrupt(future);
    }

    public static <T> T submitAndGetRethrowingNoInterrupt(final ExecutorService executor, final Callable<T> task)
            throws Exception {
        final Future<T> future = executor.submit(task);
        return getRethrowingNoInterrupt(future);
    }

    public static <T> T submitAndGetPropagatingNoInterrupt(final ExecutorService executor, final Callable<T> task) {
        final Future<T> future = executor.submit(task);
        return getPropagatingNoInterrupt(future);
    }

    //////////// SUBMIT AND GET COLLECTION

    public static <T> List<T> submitAndGet(final ExecutorService executor,
            final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        final List<Future<T>> futures = executor.invokeAll(tasks);
        return get(futures);
    }

    public static <T> List<T> submitAndGetRethrowing(final ExecutorService executor,
            final Collection<? extends Callable<T>> tasks) throws Exception {
        final List<Future<T>> futures = executor.invokeAll(tasks);
        return getRethrowing(futures);
    }

    public static <T> List<T> submitAndGetPropagating(final ExecutorService executor,
            final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        final List<Future<T>> futures = executor.invokeAll(tasks);
        return getPropagating(futures);
    }

    public static <T> List<T> submitAndGetNoInterrupt(final ExecutorService executor,
            final Collection<? extends Callable<T>> tasks) {
        try {
            final List<Future<T>> futures = executor.invokeAll(tasks);
            return getNoInterrupt(futures);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    public static <T> List<T> submitAndGetRethrowingNoInterrupt(final ExecutorService executor,
            final Collection<? extends Callable<T>> tasks) throws Exception {
        try {
            final List<Future<T>> futures = executor.invokeAll(tasks);
            return getRethrowingNoInterrupt(futures);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    public static <T> List<T> submitAndGetPropagatingNoInterrupt(final ExecutorService executor,
            final Collection<? extends Callable<T>> tasks) {
        try {
            final List<Future<T>> futures = executor.invokeAll(tasks);
            return getPropagatingNoInterrupt(futures);
        } catch (final InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    //////////// WAIT ////////////

    public static <T> void wait(final Future<? extends T> future) throws InterruptedException {
        Assertions.checkNull(get(future));
    }

    public static <T> void waitRethrowing(final Future<? extends T> future) throws Exception {
        Assertions.checkNull(getRethrowing(future));
    }

    public static <T> void waitPropagating(final Future<? extends T> future) throws InterruptedException {
        Assertions.checkNull(getPropagating(future));
    }

    public static <T> void waitNoInterrupt(final Future<? extends T> future) {
        Assertions.checkNull(getNoInterrupt(future));
    }

    public static <T> void waitRethrowingNoInterrupt(final Future<? extends T> future) throws Exception {
        Assertions.checkNull(getRethrowingNoInterrupt(future));
    }

    public static <T> void waitPropagatingNoInterrupt(final Future<? extends T> future) {
        Assertions.checkNull(getPropagatingNoInterrupt(future));
    }

    //////////// WAIT TIMEOUT UNIT

    public static <T> void wait(final Future<T> future, final long timeout, final TimeUnit unit)
            throws InterruptedException, TimeoutException {
        Assertions.checkNull(get(future, timeout, unit));
    }

    public static <T> void waitRethrowing(final Future<T> future, final long timeout, final TimeUnit unit)
            throws Exception {
        Assertions.checkNull(getRethrowing(future, timeout, unit));
    }

    public static <T> void waitPropagating(final Future<T> future, final long timeout, final TimeUnit unit)
            throws InterruptedException, TimeoutException {
        Assertions.checkNull(getPropagating(future, timeout, unit));
    }

    public static <T> void waitNoInterrupt(final Future<T> future, final long timeout, final TimeUnit unit)
            throws TimeoutException {
        Assertions.checkNull(getNoInterrupt(future, timeout, unit));
    }

    public static <T> void waitRethrowingNoInterrupt(final Future<T> future, final long timeout, final TimeUnit unit)
            throws Exception {
        Assertions.checkNull(getRethrowingNoInterrupt(future, timeout, unit));
    }

    public static <T> void waitPropagatingNoInterrupt(final Future<T> future, final long timeout, final TimeUnit unit)
            throws TimeoutException {
        Assertions.checkNull(getPropagatingNoInterrupt(future, timeout, unit));
    }

    //////////// WAIT DURATION

    public static <T> void wait(final Future<T> future, final Duration timeout)
            throws InterruptedException, TimeoutException {
        Assertions.checkNull(get(future, timeout));
    }

    public static <T> void waitRethrowing(final Future<T> future, final Duration timeout) throws Exception {
        Assertions.checkNull(getRethrowing(future, timeout));
    }

    public static <T> void waitPropagating(final Future<T> future, final Duration timeout)
            throws InterruptedException, TimeoutException {
        Assertions.checkNull(getPropagating(future, timeout));
    }

    public static <T> void waitNoInterrupt(final Future<T> future, final Duration timeout) throws TimeoutException {
        Assertions.checkNull(getNoInterrupt(future, timeout));
    }

    public static <T> void waitRethrowingNoInterrupt(final Future<T> future, final Duration timeout) throws Exception {
        Assertions.checkNull(getRethrowingNoInterrupt(future, timeout));
    }

    public static <T> void waitPropagatingNoInterrupt(final Future<T> future, final Duration timeout)
            throws TimeoutException {
        Assertions.checkNull(getPropagatingNoInterrupt(future, timeout));
    }

    //////////// WAIT ARRAY

    @SafeVarargs
    public static <T> void wait(final Future<? extends T>... futures) throws InterruptedException {
        wait(Arrays.asList(futures));
    }

    @SafeVarargs
    public static <T> void waitRethrowing(final Future<? extends T>... futures) throws Exception {
        waitRethrowing(Arrays.asList(futures));
    }

    @SafeVarargs
    public static <T> void waitPropagating(final Future<? extends T>... futures) throws InterruptedException {
        waitPropagating(Arrays.asList(futures));
    }

    @SafeVarargs
    public static <T> void waitNoInterrupt(final Future<? extends T>... futures) {
        waitNoInterrupt(Arrays.asList(futures));
    }

    @SafeVarargs
    public static <T> void waitRethrowingNoInterrupt(final Future<? extends T>... futures) throws Exception {
        waitRethrowingNoInterrupt(Arrays.asList(futures));
    }

    @SafeVarargs
    public static <T> void waitPropagatingNoInterrupt(final Future<? extends T>... futures) {
        waitPropagatingNoInterrupt(Arrays.asList(futures));
    }

    //////////// WAIT ITERABLE

    public static <T> void wait(final Iterable<? extends Future<? extends T>> futures) throws InterruptedException {
        try {
            for (final Future<?> future : futures) {
                wait(future);
            }
        } catch (final InterruptedException e) {
            cancelAndJoin(futures, e);
            throw e;
        } catch (final Throwable e) {
            cancelAndJoin(futures, e);
            throw e;
        }
    }

    public static <T> void waitRethrowing(final Iterable<? extends Future<? extends T>> futures) throws Exception {
        try {
            for (final Future<?> future : futures) {
                waitRethrowing(future);
            }
        } catch (final InterruptedException e) {
            cancelAndJoin(futures, e);
            throw e;
        } catch (final Throwable e) {
            cancelAndJoin(futures, e);
            throw e;
        }
    }

    public static <T> void waitPropagating(final Iterable<? extends Future<? extends T>> futures)
            throws InterruptedException {
        try {
            for (final Future<?> future : futures) {
                waitPropagating(future);
            }
        } catch (final InterruptedException e) {
            cancelAndJoin(futures, e);
            throw e;
        } catch (final Throwable e) {
            cancelAndJoin(futures, e);
            throw Throwables.propagate(e);
        }
    }

    public static <T> void waitNoInterrupt(final Iterable<? extends Future<? extends T>> futures) {
        try {
            for (final Future<?> future : futures) {
                wait(future);
            }
        } catch (final InterruptedException e) {
            cancelAndJoin(futures, e);
            throw Throwables.propagate(e);
        } catch (final Throwable e) {
            cancelAndJoin(futures, e);
            throw Throwables.propagate(e);
        }
    }

    public static <T> void waitRethrowingNoInterrupt(final Iterable<? extends Future<? extends T>> futures)
            throws Exception {
        try {
            for (final Future<?> future : futures) {
                waitRethrowing(future);
            }
        } catch (final InterruptedException e) {
            cancelAndJoin(futures, e);
            throw Throwables.propagate(e);
        } catch (final Throwable e) {
            cancelAndJoin(futures, e);
            throw e;
        }
    }

    public static <T> void waitPropagatingNoInterrupt(final Iterable<? extends Future<? extends T>> futures) {
        try {
            for (final Future<?> future : futures) {
                waitPropagating(future);
            }
        } catch (final InterruptedException e) {
            cancelAndJoin(futures, e);
            throw Throwables.propagate(e);
        } catch (final Throwable e) {
            cancelAndJoin(futures, e);
            throw Throwables.propagate(e);
        }
    }

    //////////// WAIT ALL, COLLECTING FAILURES

    /**
     * Awaits every future of a batch and reports all failures together instead of only the first one: unbounded while
     * the batch is healthy, and {@link #POST_FAILURE_GRACE} per remaining future once something has failed.
     */
    public static <T> void waitAllCollectingFailures(final Iterable<? extends Future<? extends T>> futures,
            final Object batchLabel) {
        waitAllCollectingFailures(futures, batchLabel, POST_FAILURE_GRACE);
    }

    /**
     * The collecting join. Package-private for the cells that pin the post-failure path on a measurable timescale — a
     * caller does not choose a budget, and the 5-minute grace is the fleet's answer for that.
     * <p>
     * A healthy batch is never cut short: a slow unit is indistinguishable from a stuck one until something has failed,
     * and no budget can be picked for a run nobody has measured. After the first failure, each future that has not
     * already finished gets up to {@code postFailureGrace}; one that overruns it is cancelled and then joined (a cancel
     * must be followed by a join, so no body unwinds behind the caller) and counted as a unit that was still running,
     * which is a different claim from a unit that failed.
     */
    static <T> void waitAllCollectingFailures(final Iterable<? extends Future<? extends T>> futures,
            final Object batchLabel, final Duration postFailureGrace) {
        //An iterable may be single-pass, so the batch is materialised: after the first failure the still-unawaited
        //futures must be reachable in order to cancel and join them.
        final List<Future<? extends T>> batch = new ArrayList<>();
        for (final Future<? extends T> future : futures) {
            batch.add(future);
        }
        final List<Throwable> failures = new ArrayList<>();
        final int total = batch.size();
        int cancelledAfterFailure = 0;
        for (int i = 0; i < batch.size(); i++) {
            final Future<? extends T> future = batch.get(i);
            try {
                if (failures.isEmpty() || future.isDone()) {
                    //Nothing has failed yet — and a slow unit is indistinguishable from a stuck one at that point — so a
                    //healthy batch is awaited as long as it takes; no budget can be chosen for it from out here. A
                    //future that has already finished costs nothing either way.
                    waitPropagating(future);
                } else {
                    //Something HAS failed, so a unit that never returns now costs the report rather than the whole fork.
                    waitPropagating(future, postFailureGrace);
                }
            } catch (final TimeoutException e) {
                //This unit was still running when the post-failure grace expired: cancel it, then join its body (a cancel
                //must be followed by a join, so nothing unwinds behind the caller), and count it separately — "this unit
                //was stopped after an earlier one failed" is not "this unit failed".
                cancelledAfterFailure++;
                Reporter.error("Futures.waitAllCollectingFailures: " + batchLabel + ": unit " + (i + 1) + " of " + total
                        + " was still running " + postFailureGrace + " after the first failure; cancelled and joined");
                failures.add(new RuntimeException("future " + (i + 1) + " of " + batchLabel
                        + " was still running when the post-failure grace of " + postFailureGrace + " expired", e));
                cancelAndJoin(future, e);
            } catch (final Throwable t) {
                //the number of failures is part of the answer, so one failure must not end the join
                if (failures.isEmpty()) {
                    //Reported where the run can still see it: from here on the batch keeps awaiting the rest, and a
                    //suite that takes minutes to fail gives nobody a clue unless the first failure says so at the time
                    //it happens. Without this line a hung or slow join is indistinguishable from a hung suite.
                    //util sits below invesdwin-context, so there is no house Log here; Reporter is util's own
                    //precedent (TextDescriptionFormatter:218) and it prints even with no binding configured, which is
                    //what makes it usable as a hang diagnostic.
                    Reporter.error("Futures.waitAllCollectingFailures: " + batchLabel + ": unit " + (i + 1) + " of "
                            + total + " failed; still awaiting " + (total - i - 1) + " more, each with a grace of "
                            + postFailureGrace, t);
                }
                failures.add(t);
                if (Throwables.getCauseByType(t, InterruptedException.class) != null) {
                    //keep the interrupt armed: without it the next get() would block again and the join could not finish
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (failures.isEmpty()) {
            return;
        }
        if (failures.size() == 1 && cancelledAfterFailure == 0) {
            throw Throwables.propagate(failures.get(0));
        }
        final StringBuilder builder = new StringBuilder(batchLabel + ": " + failures.size() + " of " + total
                + " futures failed"
                + (cancelledAfterFailure > 0 ? " (" + cancelledAfterFailure
                        + " were still running when the post-failure grace expired and were cancelled)" : ""));
        for (final Throwable failure : failures) {
            builder.append("\n  - ").append(Throwables.getMessagesAndStackTraceIfDebugEnabled(failure));
        }
        final RuntimeException aggregate = new RuntimeException(builder.toString());
        for (final Throwable failure : failures) {
            Throwables.addSuppressed(aggregate, failure);
        }
        throw aggregate;
    }

    //////////// SUBMIT AND WAIT

    public static void submitAndWait(final ExecutorService executor, final Runnable task) throws InterruptedException {
        final Future<?> future = executor.submit(task);
        wait(future);
    }

    public static void submitAndWaitRethrowing(final ExecutorService executor, final Runnable task) throws Exception {
        final Future<?> future = executor.submit(task);
        waitRethrowing(future);
    }

    public static void submitAndWaitPropagating(final ExecutorService executor, final Runnable task)
            throws InterruptedException {
        final Future<?> future = executor.submit(task);
        waitPropagating(future);
    }

    public static void submitAndWaitNoInterrupt(final ExecutorService executor, final Runnable task) {
        final Future<?> future = executor.submit(task);
        waitNoInterrupt(future);
    }

    public static void submitAndWaitRethrowingNoInterrupt(final ExecutorService executor, final Runnable task)
            throws Exception {
        final Future<?> future = executor.submit(task);
        waitRethrowingNoInterrupt(future);
    }

    public static void submitAndWaitPropagatingNoInterrupt(final ExecutorService executor, final Runnable task) {
        final Future<?> future = executor.submit(task);
        waitPropagatingNoInterrupt(future);
    }

    //////////// SUBMIT AND WAIT COLLECTION

    public static void submitAndWait(final ExecutorService executor, final Collection<? extends Runnable> tasks)
            throws InterruptedException {
        final List<Future<?>> futures = submit(executor, tasks);
        wait(futures);
    }

    public static void submitAndWaitRethrowing(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) throws Exception {
        final List<Future<?>> futures = submit(executor, tasks);
        waitRethrowing(futures);
    }

    public static void submitAndWaitPropagating(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) throws InterruptedException {
        final List<Future<?>> futures = submit(executor, tasks);
        waitPropagating(futures);
    }

    public static void submitAndWaitNoInterrupt(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) {
        final List<Future<?>> futures = submit(executor, tasks);
        waitNoInterrupt(futures);
    }

    public static void submitAndWaitRethrowingNoInterrupt(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) throws Exception {
        final List<Future<?>> futures = submit(executor, tasks);
        waitRethrowingNoInterrupt(futures);
    }

    public static void submitAndWaitPropagatingNoInterrupt(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) {
        final List<Future<?>> futures = submit(executor, tasks);
        waitPropagatingNoInterrupt(futures);
    }

    public static List<Future<?>> submit(final ExecutorService executor, final Collection<? extends Runnable> tasks) {
        final List<Future<?>> futures = new ArrayList<Future<?>>(tasks.size());

        for (final Runnable task : tasks) {
            futures.add(executor.submit(task));
        }
        return futures;
    }

    //////////// SUBMIT AND WAIT FAIL FAST COLLECTION

    /**
     * Returns with the first exception and aborts remaining tasks. This is useful for caller runs or similar throttled
     * executors.
     */
    public static void submitAndWaitFailFast(final ExecutorService executor, final Collection<? extends Runnable> tasks)
            throws InterruptedException {
        final List<Future<?>> futures = checkFailFast(executor, tasks);
        wait(futures);
    }

    public static void submitAndWaitRethrowingFailFast(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) throws Exception {
        final List<Future<?>> futures = checkFailFast(executor, tasks);
        waitRethrowing(futures);
    }

    public static void submitAndWaitPropagatingFailFast(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) throws InterruptedException {
        final List<Future<?>> futures = checkFailFast(executor, tasks);
        waitPropagating(futures);
    }

    /**
     * Returns with the first exception and aborts remaining tasks. This is useful for caller runs or similar throttled
     * executors.
     */
    public static void submitAndWaitFailFastNoInterrupt(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) {
        final List<Future<?>> futures = checkFailFast(executor, tasks);
        waitNoInterrupt(futures);
    }

    public static void submitAndWaitRethrowingFailFastNoInterrupt(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) throws Exception {
        final List<Future<?>> futures = checkFailFastRethrowing(executor, tasks);
        waitRethrowingNoInterrupt(futures);
    }

    public static void submitAndWaitPropagatingFailFastNoInterrupt(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) {
        final List<Future<?>> futures = checkFailFastPropagating(executor, tasks);
        waitPropagatingNoInterrupt(futures);
    }

    private static List<Future<?>> checkFailFast(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) {
        final List<Future<?>> futures = new CopyOnWriteArrayList<Future<?>>();

        FDate lastFailFastCheck = FDates.MIN_DATE;
        for (final Runnable task : tasks) {
            futures.add(executor.submit(task));
            if (new Duration(lastFailFastCheck, FDate.now()).isGreaterThan(5, FTimeUnit.SECONDS)) {
                //failfast on exceptions
                for (final Future<?> future : futures) {
                    if (future.isDone()) {
                        futures.remove(future);
                        try {
                            Assertions.checkNull(Futures.get(future));
                        } catch (final Throwable t) {
                            cancelAndJoin(futures, t);
                            throw Throwables.propagate(t);
                        }
                    }
                }
                lastFailFastCheck = FDate.now();
            }
        }
        return futures;
    }

    private static List<Future<?>> checkFailFastRethrowing(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) throws Exception {
        final List<Future<?>> futures = new CopyOnWriteArrayList<Future<?>>();

        FDate lastFailFastCheck = FDates.MIN_DATE;
        for (final Runnable task : tasks) {
            futures.add(executor.submit(task));
            if (new Duration(lastFailFastCheck, FDate.now()).isGreaterThan(5, FTimeUnit.SECONDS)) {
                //failfast on exceptions
                for (final Future<?> future : futures) {
                    if (future.isDone()) {
                        futures.remove(future);
                        try {
                            Assertions.checkNull(Futures.getRethrowing(future));
                        } catch (final Throwable t) {
                            cancelAndJoin(futures, t);
                            throw t;
                        }
                    }
                }
                lastFailFastCheck = FDate.now();
            }
        }
        return futures;
    }

    private static List<Future<?>> checkFailFastPropagating(final ExecutorService executor,
            final Collection<? extends Runnable> tasks) {
        final List<Future<?>> futures = new CopyOnWriteArrayList<Future<?>>();

        FDate lastFailFastCheck = FDates.MIN_DATE;
        for (final Runnable task : tasks) {
            futures.add(executor.submit(task));
            if (new Duration(lastFailFastCheck, FDate.now()).isGreaterThan(5, FTimeUnit.SECONDS)) {
                //failfast on exceptions
                for (final Future<?> future : futures) {
                    if (future.isDone()) {
                        futures.remove(future);
                        try {
                            Assertions.checkNull(Futures.getPropagating(future));
                        } catch (final Throwable t) {
                            cancelAndJoin(futures, t);
                            throw Throwables.propagate(t);
                        }
                    }
                }
                lastFailFastCheck = FDate.now();
            }
        }
        return futures;
    }
}
