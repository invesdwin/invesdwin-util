package de.invesdwin.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.internal.WrappedCallable;
import de.invesdwin.util.concurrent.internal.WrappedRunnable;

@ThreadSafe
public class WrappedScheduledExecutorService extends WrappedExecutorService implements ScheduledExecutorService {

    WrappedScheduledExecutorService(final java.util.concurrent.ScheduledThreadPoolExecutor delegate,
            final String name) {
        super(delegate, name);
        withLogExceptions(true);
    }

    @Override
    public java.util.concurrent.ScheduledThreadPoolExecutor getWrappedInstance() {
        return (java.util.concurrent.ScheduledThreadPoolExecutor) super.getWrappedInstance();
    }

    @Override
    public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
        try {
            return getWrappedInstance().schedule(WrappedRunnable.newInstance(internal, command), delay, unit);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<Object>();
        }
    }

    @Override
    public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit unit) {
        try {
            return getWrappedInstance().schedule(WrappedCallable.newInstance(internal, callable), delay, unit);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<V>();
        }
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period,
            final TimeUnit unit) {
        try {
            return getWrappedInstance().scheduleAtFixedRate(WrappedRunnable.newInstance(internal, command),
                    initialDelay, period, unit);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<Object>();
        }
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay,
            final TimeUnit unit) {
        try {
            return getWrappedInstance().scheduleWithFixedDelay(WrappedRunnable.newInstance(internal, command),
                    initialDelay, delay, unit);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<Object>();
        }
    }

    @Override
    public WrappedScheduledExecutorService withLogExceptions(final boolean logExceptions) {
        return (WrappedScheduledExecutorService) super.withLogExceptions(logExceptions);
    }

    @Override
    public WrappedScheduledExecutorService withWaitOnFullPendingCount(final boolean waitOnFullPendingCount) {
        return (WrappedScheduledExecutorService) super.withWaitOnFullPendingCount(waitOnFullPendingCount);
    }

    @Override
    public WrappedScheduledExecutorService withDynamicThreadName(final boolean dynamicThreadName) {
        return (WrappedScheduledExecutorService) super.withDynamicThreadName(dynamicThreadName);
    }

}
