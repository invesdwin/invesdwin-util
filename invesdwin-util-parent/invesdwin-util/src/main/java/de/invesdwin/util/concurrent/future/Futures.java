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

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.concurrent.future.internal.AFuturesStaticFacade;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.concurrent.future.internal.AFuturesStaticFacade", targets = {
        com.google.common.util.concurrent.Futures.class })
public final class Futures extends AFuturesStaticFacade {

    /**
     * Default bound for a collecting batch join, sized from the measured normal (a 24-unit reference matrix completes in
     * about 5 minutes at 05:0x CET) against the surefire fork ceiling of 30 minutes in invesdwin-bom.
     */
    public static final Duration BATCH_JOIN_DEADLINE_DEFAULT = new Duration(15, FTimeUnit.MINUTES);

    @SuppressWarnings("rawtypes")
    public static final FutureTask[] FUTURETASK_EMPTY_ARRAY = new FutureTask[0];

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
     * Awaits every future of a batch and reports all failures together instead of only the first one, bounded by
     * {@link #BATCH_JOIN_DEADLINE_DEFAULT}; the contract is on
     * {@link #waitAllCollectingFailures(Iterable, Object, Duration)}.
     */
    public static <T> void waitAllCollectingFailures(final Iterable<? extends Future<? extends T>> futures,
            final Object batchLabel) {
        waitAllCollectingFailures(futures, batchLabel, BATCH_JOIN_DEADLINE_DEFAULT);
    }

    /**
     * Awaits every future of a batch and reports all failures together instead of only the first one.
     * <p>
     * The aggregate {@code wait*} families above share one policy — on the first failure {@link #cancelAndJoin} cancels and
     * joins the rest. That is right for interchangeable tasks and wrong for a caller whose answer includes HOW MANY units
     * failed. Measured on a 24-unit reference matrix: joining the columns with
     * {@link #waitPropagatingNoInterrupt(Iterable)} reported 24 of 24 units completed when nothing threw and 18 to 22 once a
     * single unit raised a supply refusal — the gap is units that never ran, so the run could not say how wide the failure
     * was and left their result artifacts missing. Measured across the suites that use this form: every batch there holds 2,
     * 4 or 24 units, so the cancelled siblings are real units rather than an empty list.
     * <p>
     * One failure is rethrown unchanged, so a caller catching by type still sees the task's own exception. Two or more give
     * one aggregate whose message counts them ("N of M") and lists each failure, with every failure attached as a suppressed
     * cause so each stack stays visible.
     * <p>
     * A FAILED task cancels nothing: a sibling still running when the join reaches it is awaited to completion, and one not
     * started yet still runs. That is the point — and it is also what the old eager policy guarded against by accident,
     * since {@code cancel(true)} interrupted a stuck body. Hence the deadline: a future that has not completed within the
     * remaining budget is cancelled and then joined (a cancel must be followed by a join, so no body is left unwinding
     * behind the caller) and reported as a named non-completion. A lone non-completion still becomes the aggregate — "did
     * not finish in time" is a different claim from "failed", and a caller catching by type must not be handed the latter.
     * The budget is for the batch, not per future, so a future awaited late in a slow batch gets the remaining slice; size
     * it above the measured normal and below the surefire fork timeout, because a fork that dies on
     * {@code forkedProcessTimeoutInSeconds} reports no tally at all. A master INTERRUPT behaves the same way: each remaining
     * future is cancelled-and-joined, every interruption is collected, and the flag is left armed.
     */
    public static <T> void waitAllCollectingFailures(final Iterable<? extends Future<? extends T>> futures,
            final Object batchLabel, final Duration deadline) {
        final List<Throwable> failures = new ArrayList<>();
        int total = 0;
        int incomplete = 0;
        final long endMillis = FDateMillis.nowMillis() + deadline.millisValue();
        for (final Future<? extends T> future : futures) {
            total++;
            final long remainingMillis = endMillis - FDateMillis.nowMillis();
            try {
                if (future.isDone()) {
                    //a future that already finished costs nothing, deadline or not: its outcome is the answer
                    waitPropagating(future);
                } else if (remainingMillis <= 0) {
                    throw new TimeoutException("the batch deadline of " + deadline
                            + " passed while this future was still running");
                } else {
                    waitPropagating(future, new Duration(remainingMillis, FTimeUnit.MILLISECONDS));
                }
            } catch (final TimeoutException e) {
                incomplete++;
                //a future that will not finish must not hold the report hostage: cancel it, join the body, and name it
                final RuntimeException notCompleted = new RuntimeException("future " + total + " of " + batchLabel
                        + " was still running when the batch deadline of " + deadline + " passed", e);
                cancelAndJoin(future, notCompleted);
                failures.add(notCompleted);
            } catch (final Throwable t) {
                //the number of failures is part of the answer, so one failure must not end the join
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
        if (failures.size() == 1 && incomplete == 0) {
            throw Throwables.propagate(failures.get(0));
        }
        final StringBuilder builder = new StringBuilder(batchLabel + ": " + failures.size() + " of " + total
                + " futures failed" + (incomplete > 0 ? " (" + incomplete + " did not complete within " + deadline + ")" : ""));
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
