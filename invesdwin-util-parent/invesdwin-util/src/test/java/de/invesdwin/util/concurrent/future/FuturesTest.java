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
