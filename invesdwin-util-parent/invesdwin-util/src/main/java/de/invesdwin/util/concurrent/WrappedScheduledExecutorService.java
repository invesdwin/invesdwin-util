package de.invesdwin.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import de.invesdwin.util.concurrent.future.InterruptingFuture;
import de.invesdwin.util.concurrent.handler.IExecutorExceptionHandler;
import de.invesdwin.util.concurrent.internal.IWrappedExecutorServiceInternal;
import de.invesdwin.util.concurrent.internal.WrappedCallable;
import de.invesdwin.util.concurrent.internal.WrappedRunnable;

@ThreadSafe
public class WrappedScheduledExecutorService extends WrappedExecutorService
        implements ListeningScheduledExecutorService {

    private final IWrappedExecutorServiceInternal scheduledInternal = new IWrappedExecutorServiceInternal() {

        @Override
        public IExecutorExceptionHandler getExecutorExceptionHandler() {
            return internal.getExecutorExceptionHandler();
        }

        @Override
        public boolean isKeepThreadLocals() {
            return internal.isKeepThreadLocals();
        }

        @Override
        public AtomicBoolean getDynamicThreadName() {
            return internal.getDynamicThreadName();
        }

        @Override
        public void incrementPendingCount(final boolean skipWaitOnFullPendingCount) throws InterruptedException {
            //noop, don't count scheduled tasks, would not work anyway
        }

        @Override
        public String getName() {
            return internal.getName();
        }

        @Override
        public void decrementPendingCount() {
            //noop, don't count scheduled tasks, would not work anyway
        }
    };

    WrappedScheduledExecutorService(final java.util.concurrent.ScheduledThreadPoolExecutor delegate,
            final String name) {
        super(delegate, name);
    }

    @Override
    protected ListeningExecutorService decorate(final ExecutorService delegate) {
        final java.util.concurrent.ScheduledThreadPoolExecutor scheduled = (java.util.concurrent.ScheduledThreadPoolExecutor) delegate;
        return MoreExecutors.listeningDecorator(scheduled);
    }

    @Override
    public ListeningScheduledExecutorService getWrappedInstance() {
        return (ListeningScheduledExecutorService) super.getWrappedInstance();
    }

    @Override
    public ListenableScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
        try {
            return getWrappedInstance().schedule(WrappedRunnable.newInstance(scheduledInternal, false, command), delay,
                    unit);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<Object>();
        }
    }

    @Override
    public <V> ListenableScheduledFuture<V> schedule(final Callable<V> callable, final long delay,
            final TimeUnit unit) {
        try {
            return getWrappedInstance().schedule(WrappedCallable.newInstance(scheduledInternal, false, callable), delay,
                    unit);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<V>();
        }
    }

    @Override
    public ListenableScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay,
            final long period, final TimeUnit unit) {
        try {
            return getWrappedInstance().scheduleAtFixedRate(
                    WrappedRunnable.newInstance(scheduledInternal, false, command), initialDelay, period, unit);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<Object>();
        }
    }

    @Override
    public ListenableScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay,
            final long delay, final TimeUnit unit) {
        try {
            return getWrappedInstance().scheduleWithFixedDelay(
                    WrappedRunnable.newInstance(scheduledInternal, false, command), initialDelay, delay, unit);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return new InterruptingFuture<Object>();
        }
    }

    @Override
    public WrappedScheduledExecutorService setExecutorExceptionHandler(
            final IExecutorExceptionHandler exceptionHandler) {
        return (WrappedScheduledExecutorService) super.setExecutorExceptionHandler(exceptionHandler);
    }

    @Override
    public WrappedScheduledExecutorService setWaitOnFullPendingCount(final boolean waitOnFullPendingCount) {
        return (WrappedScheduledExecutorService) super.setWaitOnFullPendingCount(waitOnFullPendingCount);
    }

    @Override
    public WrappedScheduledExecutorService setDynamicThreadName(final boolean dynamicThreadName) {
        return (WrappedScheduledExecutorService) super.setDynamicThreadName(dynamicThreadName);
    }

}
