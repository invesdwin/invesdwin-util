package de.invesdwin.util.concurrent.lambda.callable;

import java.io.Closeable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.loop.SynchronizedLoopInterruptedCheck;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class AsyncThrottledCallable<T> implements Callable<T>, Closeable {

    @GuardedBy("this")
    private WrappedExecutorService executor;
    private final SynchronizedLoopInterruptedCheck throttleCheck;
    private final Callable<T> callable;
    private volatile Future<T> pendingValue;
    private volatile Future<T> throttledValue;

    private AsyncThrottledCallable(final WrappedExecutorService executor, final Duration throttleInterval,
            final Callable<T> callable) {
        this.executor = executor;
        this.throttleCheck = new SynchronizedLoopInterruptedCheck(throttleInterval) {
            @Override
            protected long getInitialNanoTime() {
                return Long.MIN_VALUE;
            }
        };
        this.callable = callable;
    }

    @Override
    public T call() throws Exception {
        checkPendingValue();
        if (pendingValue == null || throttleCheck.checkClockNoInterrupt()) {
            synchronized (this) {
                if (pendingValue == null && !isClosed()) {
                    final Future<T> newPendingValue = executor.submit(callable);
                    if (throttledValue == null) {
                        /*
                         * initially we wait always, afterwards we don't wait but instead use checkPendingValue to
                         * replace the throttledValue
                         */
                        throttledValue = newPendingValue;
                    }
                    pendingValue = newPendingValue;
                }
            }
        }
        try {
            return throttledValue.get();
        } catch (final ExecutionException e) {
            throw Throwables.propagate(e.getCause());
        }
    }

    private void checkPendingValue() {
        final Future<T> pendingValueCopy = pendingValue;
        if (pendingValueCopy != null && pendingValueCopy.isDone()) {
            synchronized (this) {
                if (pendingValue == pendingValueCopy) {
                    throttledValue = pendingValueCopy;
                    pendingValue = null;
                }
            }
        }
    }

    public static <T> AsyncThrottledCallable<T> of(final WrappedExecutorService executor,
            final Duration throttleInterval, final Callable<T> callable) {
        return new AsyncThrottledCallable<>(executor, throttleInterval, callable);
    }

    public synchronized boolean isClosed() {
        return executor == null;
    }

    @Override
    public synchronized void close() {
        if (isClosed()) {
            return;
        }
        executor = null;
        final Future<T> pendingValueCopy = pendingValue;
        if (pendingValueCopy != null) {
            pendingValueCopy.cancel(true);
            pendingValue = null;
        }
    }

}
