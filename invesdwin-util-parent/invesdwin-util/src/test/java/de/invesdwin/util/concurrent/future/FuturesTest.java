package de.invesdwin.util.concurrent.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

// CHECKSTYLE:OFF
@NotThreadSafe
public class FuturesTest {
    //CHECKSTYLE:ON

    /**
     * Simulated teardown work a cancelled task body still has to finish after cancel(true) interrupted it; both the
     * await-multiple and the await-one contract require the master to wait for that teardown before returning or
     * rethrowing.
     */
    private static final long TEARDOWN_MILLIS = 750;
    private static final long START_WAIT_SECONDS = 30;
    private static final long MASTER_JOIN_MILLIS = 30000;
    private static final long TERMINATION_SECONDS = 30;
    private static final long AWAIT_TIMEOUT_SECONDS = 30;

    @Test
    public void testWaitNoInterruptJoinsSiblingsOnTaskFailure() throws Exception {
        final WrappedExecutorService executor = Executors.newCachedThreadPool("FuturesTest-fail");
        try {
            final RuntimeException marker = new RuntimeException("FuturesTest-marker");
            final CountDownLatch started = new CountDownLatch(3);
            final AtomicInteger interruptedTasks = new AtomicInteger();
            final List<Future<?>> futures = new ArrayList<>();
            futures.add(submitTracked(executor, () -> {
                started.countDown();
                throw marker;
            }));
            futures.add(submitTracked(executor, blockUntilInterrupted(started, interruptedTasks)));
            futures.add(submitTracked(executor, blockUntilInterrupted(started, interruptedTasks)));
            Assertions.assertThat(started.await(START_WAIT_SECONDS, TimeUnit.SECONDS)).describedAs(
                    "all three tasks must be running").isTrue();
            Throwable caught = null;
            boolean allDoneAtThrow = true;
            try {
                Futures.waitNoInterrupt(futures);
            } catch (final Throwable t) {
                caught = t;
                for (final Future<?> future : futures) {
                    allDoneAtThrow &= future.isDone();
                }
            }
            Assertions.assertThat(caught).describedAs("the failed task must be rethrown").isNotNull();
            Assertions.assertThat(caught.getCause()).describedAs("the original task cause must be preserved").isSameAs(
                    marker);
            Assertions.assertThat(allDoneAtThrow).describedAs(
                    "every sibling future must be cancelled-then-joined (done) by the time waitNoInterrupt rethrows one task's failure").isTrue();
            Assertions.assertThat(interruptedTasks.get()).describedAs(
                    "sibling bodies must have received the cancel interrupt").isEqualTo(2);
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(TERMINATION_SECONDS, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testWaitNoInterruptJoinsSiblingsOnMasterInterrupt() throws Exception {
        final WrappedExecutorService executor = Executors.newCachedThreadPool("FuturesTest-interrupt");
        try {
            final CountDownLatch started = new CountDownLatch(3);
            final AtomicInteger interruptedTasks = new AtomicInteger();
            final List<Future<?>> futures = new ArrayList<>();
            futures.add(submitTracked(executor, blockUntilInterrupted(started, interruptedTasks)));
            futures.add(submitTracked(executor, blockUntilInterrupted(started, interruptedTasks)));
            futures.add(submitTracked(executor, blockUntilInterrupted(started, interruptedTasks)));
            final AtomicReference<Throwable> masterThrown = new AtomicReference<>();
            final AtomicReference<Boolean> allDoneAtThrow = new AtomicReference<>();
            final Thread master = new Thread(() -> {
                try {
                    Futures.waitNoInterrupt(futures);
                } catch (final Throwable t) {
                    masterThrown.set(t);
                    boolean allDone = true;
                    for (final Future<?> future : futures) {
                        allDone &= future.isDone();
                    }
                    allDoneAtThrow.set(allDone);
                }
            }, "FuturesTest-master");
            master.setDaemon(true);
            master.start();
            Assertions.assertThat(started.await(START_WAIT_SECONDS, TimeUnit.SECONDS)).describedAs(
                    "all three tasks must be running").isTrue();
            master.interrupt();
            master.join(MASTER_JOIN_MILLIS);
            Assertions.assertThat(master.isAlive()).describedAs(
                    "the interrupted master must have propagated").isFalse();
            Assertions.assertThat(masterThrown.get()).describedAs(
                    "the master interrupt must be propagated").isInstanceOf(InterruptedException.class);
            Assertions.assertThat(allDoneAtThrow.get()).describedAs(
                    "every sibling future must be cancelled-then-joined (done) by the time the master interrupt propagates").isTrue();
            Assertions.assertThat(interruptedTasks.get()).describedAs(
                    "all sibling bodies must have received the cancel interrupt").isEqualTo(3);
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(TERMINATION_SECONDS, TimeUnit.SECONDS);
        }
    }

    /**
     * Await-one contract: the single-future get/wait paths (submitAndWaitNoInterrupt(Runnable) style) cancel the
     * awaited future when the master is interrupted, but cancel(true) alone leaves the body still unwinding while the
     * caller proceeds. Cancel must be followed by a join, so the future is done when the caller returns or throws.
     * Red at HEAD: isDone() is still false at that point because the body had not finished tearing down.
     */
    @Test
    public void testGetNoInterruptJoinsCancelledBodyOnMasterInterrupt() throws Exception {
        assertAwaitOneJoinsCancelledBodyOnMasterInterrupt("getNoInterrupt", future -> Futures.waitNoInterrupt(future));
    }

    @Test
    public void testGetRethrowingNoInterruptJoinsCancelledBodyOnMasterInterrupt() throws Exception {
        assertAwaitOneJoinsCancelledBodyOnMasterInterrupt("getRethrowingNoInterrupt",
                future -> Futures.waitRethrowingNoInterrupt(future));
    }

    @Test
    public void testGetPropagatingNoInterruptJoinsCancelledBodyOnMasterInterrupt() throws Exception {
        assertAwaitOneJoinsCancelledBodyOnMasterInterrupt("getPropagatingNoInterrupt",
                future -> Futures.waitPropagatingNoInterrupt(future));
    }

    @Test
    public void testGetTimeoutNoInterruptJoinsCancelledBodyOnMasterInterrupt() throws Exception {
        assertAwaitOneJoinsCancelledBodyOnMasterInterrupt("getNoInterruptTimeout",
                future -> Futures.waitNoInterrupt(future, AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    public void testGetRethrowingTimeoutNoInterruptJoinsCancelledBodyOnMasterInterrupt() throws Exception {
        assertAwaitOneJoinsCancelledBodyOnMasterInterrupt("getRethrowingNoInterruptTimeout",
                future -> Futures.waitRethrowingNoInterrupt(future, AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    public void testGetPropagatingTimeoutNoInterruptJoinsCancelledBodyOnMasterInterrupt() throws Exception {
        assertAwaitOneJoinsCancelledBodyOnMasterInterrupt("getPropagatingNoInterruptTimeout",
                future -> Futures.waitPropagatingNoInterrupt(future, AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    private void assertAwaitOneJoinsCancelledBodyOnMasterInterrupt(final String method, final IAwaitOne awaitOne)
            throws Exception {
        final WrappedExecutorService executor = Executors.newCachedThreadPool("FuturesTest-" + method);
        try {
            final CountDownLatch started = new CountDownLatch(1);
            final AtomicInteger interruptedTasks = new AtomicInteger();
            final TeardownTrackingFuture future = submitTracked(executor,
                    blockUntilInterrupted(started, interruptedTasks));
            Assertions.assertThat(started.await(START_WAIT_SECONDS, TimeUnit.SECONDS)).describedAs(
                    "the awaited task must be running").isTrue();
            final AtomicReference<Throwable> masterThrown = new AtomicReference<>();
            final AtomicReference<Boolean> cancelledAtThrow = new AtomicReference<>();
            final AtomicReference<Boolean> doneAtThrow = new AtomicReference<>();
            final Thread master = new Thread(() -> {
                try {
                    awaitOne.await(future);
                } catch (final Throwable t) {
                    masterThrown.set(t);
                    cancelledAtThrow.set(future.isCancelled());
                    doneAtThrow.set(future.isDone());
                }
            }, "FuturesTest-master-" + method);
            master.setDaemon(true);
            master.start();
            master.interrupt();
            master.join(MASTER_JOIN_MILLIS);
            Assertions.assertThat(master.isAlive()).describedAs(
                    "the interrupted master must have propagated from " + method).isFalse();
            Assertions.assertThat(masterThrown.get()).describedAs(
                    "the master interrupt must be propagated from " + method).isInstanceOf(InterruptedException.class);
            Assertions.assertThat(cancelledAtThrow.get()).describedAs(
                    "the awaited future must have been cancelled by " + method).isTrue();
            Assertions.assertThat(doneAtThrow.get()).describedAs(
                    "the cancelled future must be joined (done) by the time " + method
                            + " returns to its caller, so that no awaited task outlives the master").isTrue();
            Assertions.assertThat(interruptedTasks.get()).describedAs(
                    "the task body must have received the cancel interrupt").isEqualTo(1);
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(TERMINATION_SECONDS, TimeUnit.SECONDS);
        }
    }

    /**
     * The collecting join's whole point: every failure is reported and no sibling is cancelled. The aggregate
     * {@code wait*} families cancel-and-join on the first failure, which turns one unit's verdict into a batch-wide abort —
     * measured on a 24-unit reference matrix, where a joining call reported 18-22 units completed instead of 24 because the
     * surviving units never ran.
     */
    @Test
    public void testWaitAllCollectingFailuresReportsEveryFailureAndCancelsNoSibling() throws Exception {
        final WrappedExecutorService executor = Executors.newCachedThreadPool("FuturesTest-collect");
        try {
            final RuntimeException first = new RuntimeException("FuturesTest-collect-1");
            final RuntimeException second = new RuntimeException("FuturesTest-collect-2");
            final CountDownLatch slowStarted = new CountDownLatch(1);
            final AtomicBoolean slowFinished = new AtomicBoolean();
            final List<Future<?>> futures = new ArrayList<>();
            futures.add(submitTracked(executor, () -> {
                throw first;
            }));
            futures.add(submitTracked(executor, () -> {
                throw second;
            }));
            futures.add(submitTracked(executor, () -> {
                slowStarted.countDown();
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (final InterruptedException e) {
                    //the join must not interrupt this body; if it ever does, slowFinished stays false and the cell fails
                    return;
                }
                slowFinished.set(true);
            }));
            Assertions.assertThat(slowStarted.await(START_WAIT_SECONDS, TimeUnit.SECONDS)).describedAs(
                    "the slow sibling must be running while the two failures are collected").isTrue();
            Throwable caught = null;
            try {
                Futures.waitAllCollectingFailures(futures, "reference matrix");
            } catch (final Throwable t) {
                caught = t;
            }
            Assertions.assertThat(caught).describedAs("a batch with failures must not pass").isNotNull();
            Assertions.assertThat(caught.getMessage()).describedAs(
                    "the aggregate names the batch and counts the failures: " + caught.getMessage()).contains(
                            "reference matrix").contains("2 of 3");
            Assertions.assertThat(caught.getSuppressed()).describedAs(
                    "every failure stays attached so each stack is visible").hasSize(2);
            Assertions.assertThat(slowFinished.get()).describedAs(
                    "the running sibling must be allowed to finish instead of being cancelled").isTrue();
            for (final Future<?> future : futures) {
                Assertions.assertThat(future.isCancelled()).describedAs(
                        "the collecting join must never cancel").isFalse();
            }
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(TERMINATION_SECONDS, TimeUnit.SECONDS);
        }
    }

    /**
     * One failure is rethrown as the task's own exception, not wrapped: callers that catch by type keep working, so the
     * collecting join changes what happens to the siblings and nothing else.
     */
    @Test
    public void testWaitAllCollectingFailuresRethrowsASingleFailureUnchanged() throws Exception {
        final WrappedExecutorService executor = Executors.newCachedThreadPool("FuturesTest-single");
        try {
            final RuntimeException marker = new RuntimeException("FuturesTest-single-marker");
            final List<Future<?>> futures = new ArrayList<>();
            futures.add(submitTracked(executor, () -> {
                throw marker;
            }));
            futures.add(submitTracked(executor, () -> {
                //a healthy unit
            }));
            Throwable caught = null;
            try {
                Futures.waitAllCollectingFailures(futures, "reference matrix");
            } catch (final Throwable t) {
                caught = t;
            }
            Assertions.assertThat(caught).describedAs("the failure must be reported").isNotNull();
            Assertions.assertThat(caught).describedAs(
                    "a single failure is the task's own throwable, not an aggregate wrapper").isSameAs(marker);
            Assertions.assertThat(caught.getSuppressed()).describedAs("nothing to aggregate").isEmpty();
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(TERMINATION_SECONDS, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testWaitAllCollectingFailuresOfAnEmptyBatchReturnsNormally() {
        Futures.waitAllCollectingFailures(new ArrayList<Future<?>>(), "reference matrix");
    }

    /**
     * A master interrupt is the one case where this join does unwind its siblings: the awaited-one contract cancels and
     * joins each remaining future rather than blocking on it, so the collecting join always terminates and leaves no task
     * body unwinding behind the caller. The interrupt is still reported per future (collected into the aggregate) and the
     * flag is left armed — contrast with the task-failure cell above, where nothing is cancelled.
     */
    @Test
    public void testWaitAllCollectingFailuresJoinsSiblingsOnMasterInterrupt() throws Exception {
        final WrappedExecutorService executor = Executors.newCachedThreadPool("FuturesTest-collect-interrupt");
        try {
            final CountDownLatch started = new CountDownLatch(3);
            final AtomicInteger interruptedTasks = new AtomicInteger();
            final List<Future<?>> futures = new ArrayList<>();
            futures.add(submitTracked(executor, blockUntilInterrupted(started, interruptedTasks)));
            futures.add(submitTracked(executor, blockUntilInterrupted(started, interruptedTasks)));
            futures.add(submitTracked(executor, blockUntilInterrupted(started, interruptedTasks)));
            final AtomicReference<Throwable> masterThrown = new AtomicReference<>();
            final AtomicBoolean interruptStillArmed = new AtomicBoolean();
            final Thread master = new Thread(() -> {
                try {
                    Futures.waitAllCollectingFailures(futures, "reference matrix");
                } catch (final Throwable t) {
                    masterThrown.set(t);
                    interruptStillArmed.set(Thread.currentThread().isInterrupted());
                }
            }, "FuturesTest-collect-master");
            master.setDaemon(true);
            master.start();
            Assertions.assertThat(started.await(START_WAIT_SECONDS, TimeUnit.SECONDS)).describedAs(
                    "all three tasks must be running").isTrue();
            master.interrupt();
            master.join(MASTER_JOIN_MILLIS);
            Assertions.assertThat(master.isAlive()).describedAs(
                    "the interrupted master must not block on the siblings it does not cancel").isFalse();
            Assertions.assertThat(masterThrown.get()).describedAs("the batch must be reported").isInstanceOf(
                    RuntimeException.class);
            Assertions.assertThat(masterThrown.get().getMessage()).describedAs(
                    "the aggregate counts every interrupted join: " + masterThrown.get().getMessage()).contains(
                            "3 of 3");
            Assertions.assertThat(interruptStillArmed.get()).describedAs(
                    "the master's interrupt flag must still be set when the aggregate is thrown").isTrue();
            Assertions.assertThat(interruptedTasks.get()).describedAs(
                    "on a master interrupt the awaited-one contract cancels-and-joins every remaining sibling").isEqualTo(3);
            boolean allDoneAtThrow = true;
            for (final Future<?> future : futures) {
                allDoneAtThrow &= future.isDone();
            }
            Assertions.assertThat(allDoneAtThrow).describedAs(
                    "every sibling must be joined (done) when the aggregate is thrown, not left running").isTrue();
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(TERMINATION_SECONDS, TimeUnit.SECONDS);
        }
    }

    /**
     * The bound is what keeps the collecting join from becoming a hang: a future that will not finish is cancelled, its
     * body joined, and it is named in the report. The old eager policy escaped the same situation only by accident — it
     * interrupted every sibling — and a fork that dies on `forkedProcessTimeoutInSeconds` reports no tally at all.
     */
    @Test
    public void testBoundedWaitAllCollectingFailuresCancelsAndJoinsAFutureThatWillNotFinish() throws Exception {
        final WrappedExecutorService executor = Executors.newCachedThreadPool("FuturesTest-deadline");
        try {
            final CountDownLatch started = new CountDownLatch(3);
            final AtomicInteger interruptedTasks = new AtomicInteger();
            final List<Future<?>> futures = new ArrayList<>();
            futures.add(submitTracked(executor, blockUntilInterrupted(started, interruptedTasks)));
            futures.add(submitTracked(executor, () -> {
                started.countDown();
                //a healthy unit
            }));
            futures.add(submitTracked(executor, () -> {
                started.countDown();
                //a second healthy unit
            }));
            Assertions.assertThat(started.await(START_WAIT_SECONDS, TimeUnit.SECONDS)).describedAs(
                    "all three units must be submitted before the deadline is judged").isTrue();
            Throwable caught = null;
            final long begin = FDateMillis.nowMillis();
            try {
                Futures.waitAllCollectingFailures(futures, "reference matrix", new Duration(500, FTimeUnit.MILLISECONDS));
            } catch (final Throwable t) {
                caught = t;
            }
            final long elapsed = FDateMillis.nowMillis() - begin;
            Assertions.assertThat(caught).describedAs("the batch must be reported").isNotNull();
            Assertions.assertThat(caught.getMessage()).describedAs(
                    "the report names the unit that did not finish: " + caught.getMessage()).contains("was still running")
                    .contains("1 of 3");
            Assertions.assertThat(elapsed).describedAs(
                    "the join must return at the deadline, not wait for the 30 s body (elapsed " + elapsed + " ms)")
                    .isLessThan(5000L);
            Assertions.assertThat(interruptedTasks.get()).describedAs(
                    "the unfinished body must have been cancelled and joined").isEqualTo(1);
            Assertions.assertThat(futures.get(1).isDone() && futures.get(2).isDone()).describedAs(
                    "the healthy units are still awaited to completion").isTrue();
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(TERMINATION_SECONDS, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testBoundedWaitAllCollectingFailuresCancelsNothingWhenTheBatchFitsTheDeadline() throws Exception {
        final WrappedExecutorService executor = Executors.newCachedThreadPool("FuturesTest-deadline-fits");
        try {
            final RuntimeException marker = new RuntimeException("FuturesTest-deadline-marker");
            final CountDownLatch started = new CountDownLatch(1);
            final AtomicInteger interruptedTasks = new AtomicInteger();
            final List<Future<?>> futures = new ArrayList<>();
            futures.add(submitTracked(executor, () -> {
                throw marker;
            }));
            futures.add(submitTracked(executor, () -> {
                started.countDown();
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (final InterruptedException e) {
                    interruptedTasks.incrementAndGet();
                }
            }));
            Assertions.assertThat(started.await(START_WAIT_SECONDS, TimeUnit.SECONDS)).isTrue();
            Throwable caught = null;
            try {
                Futures.waitAllCollectingFailures(futures, "reference matrix", new Duration(30, FTimeUnit.SECONDS));
            } catch (final Throwable t) {
                caught = t;
            }
            Assertions.assertThat(caught).describedAs("the failure must be reported").isSameAs(marker);
            Assertions.assertThat(interruptedTasks.get()).describedAs(
                    "a batch inside its deadline cancels nothing — the sibling ran to completion").isEqualTo(0);
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(TERMINATION_SECONDS, TimeUnit.SECONDS);
        }
    }

    /**
     * A non-completion is never passed through as if it were the task's own exception: even alone it becomes the named
     * aggregate, because "this unit did not finish in time" is a different claim from "this unit failed", and a caller
     * catching by type must not be handed the latter.
     */
    @Test
    public void testBoundedWaitAllCollectingFailuresNamesASingleNonCompletionAsAnAggregate() throws Exception {
        final WrappedExecutorService executor = Executors.newCachedThreadPool("FuturesTest-deadline-one");
        try {
            final CountDownLatch started = new CountDownLatch(1);
            final AtomicInteger interruptedTasks = new AtomicInteger();
            final List<Future<?>> futures = new ArrayList<>();
            futures.add(submitTracked(executor, blockUntilInterrupted(started, interruptedTasks)));
            Assertions.assertThat(started.await(START_WAIT_SECONDS, TimeUnit.SECONDS)).isTrue();
            Throwable caught = null;
            try {
                Futures.waitAllCollectingFailures(futures, "reference matrix", new Duration(300, FTimeUnit.MILLISECONDS));
            } catch (final Throwable t) {
                caught = t;
            }
            Assertions.assertThat(caught).describedAs("the non-completion must be reported").isNotNull();
            Assertions.assertThat(caught.getMessage()).describedAs(
                    "a lone non-completion is still an aggregate, not a bare rethrow: " + caught.getMessage()).contains(
                            "1 of 1 futures failed").contains("was still running when the batch deadline");
            Assertions.assertThat(caught.getSuppressed()).describedAs("the cause chain stays attached").hasSize(1);
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(TERMINATION_SECONDS, TimeUnit.SECONDS);
        }
    }

    private interface IAwaitOne {
        void await(Future<?> future) throws Exception;
    }

    private static Runnable blockUntilInterrupted(final CountDownLatch started, final AtomicInteger interruptedTasks) {
        return () -> {
            started.countDown();
            try {
                TimeUnit.DAYS.sleep(1);
            } catch (final InterruptedException e) {
                interruptedTasks.incrementAndGet();
                try {
                    //simulated teardown after the cancel interrupt
                    TimeUnit.MILLISECONDS.sleep(TEARDOWN_MILLIS);
                } catch (final InterruptedException ignored) {
                    //teardown races with executor shutdown, harmless
                }
            }
        };
    }

    private static TeardownTrackingFuture submitTracked(final ExecutorService executor, final Runnable task) {
        final AtomicBoolean bodyFinished = new AtomicBoolean();
        final Future<?> delegate = executor.submit(() -> {
            try {
                task.run();
            } finally {
                bodyFinished.set(true);
            }
        });
        return new TeardownTrackingFuture(delegate, bodyFinished);
    }

    /**
     * A cancelled task is only really done once its body finished tearing down; FutureTask.cancel(true) marks the
     * future done while the body is still unwinding, which would hide the missing join.
     */
    private static final class TeardownTrackingFuture implements Future<Object> {

        private final Future<?> delegate;
        private final AtomicBoolean bodyFinished;

        private TeardownTrackingFuture(final Future<?> delegate, final AtomicBoolean bodyFinished) {
            this.delegate = delegate;
            this.bodyFinished = bodyFinished;
        }

        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            return delegate.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return delegate.isCancelled();
        }

        @Override
        public boolean isDone() {
            return delegate.isDone() && bodyFinished.get();
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            return delegate.get();
        }

        @Override
        public Object get(final long timeout, final TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return delegate.get(timeout, unit);
        }
    }

}
