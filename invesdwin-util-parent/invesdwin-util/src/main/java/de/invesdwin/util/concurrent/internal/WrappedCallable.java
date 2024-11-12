package de.invesdwin.util.concurrent.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.priority.IPriorityProvider;
import de.invesdwin.util.error.Throwables;
import io.netty.util.concurrent.FastThreadLocal;

@NotThreadSafe
public final class WrappedCallable<V> implements Callable<V>, IPriorityProvider {

    private final String parentThreadName;
    private final boolean executeCalledWithoutFuture;
    private final Callable<V> delegate;
    private final IWrappedExecutorServiceInternal parent;
    private final boolean threadRetryDisabled;
    private volatile boolean started;

    private WrappedCallable(final IWrappedExecutorServiceInternal parent, final boolean executeCalledWithoutFuture,
            final Callable<V> delegate, final boolean skipWaitOnFullPendingCount) throws InterruptedException {
        if (delegate instanceof WrappedCallable) {
            throw new IllegalArgumentException("delegate should not be an instance of " + getClass().getSimpleName());
        }
        this.parent = parent;
        this.parentThreadName = Thread.currentThread().getName();
        this.executeCalledWithoutFuture = executeCalledWithoutFuture;
        this.delegate = delegate;
        if (parent != null) {
            parent.incrementPendingCount(skipWaitOnFullPendingCount);
        }
        this.threadRetryDisabled = Threads.isThreadRetryDisabled();
    }

    @Override
    public V call() throws Exception {
        started = true;
        final String originalThreadName;
        if (parent != null && parent.getDynamicThreadName().get()) {
            originalThreadName = Threads.getCurrentRootThreadName();
            Threads.updateParentThreadName(parentThreadName);
        } else {
            originalThreadName = null;
        }
        try {
            if (threadRetryDisabled) {
                final Boolean registerThreadRetryDisabled = Threads.registerThreadRetryDisabled();
                try {
                    return delegate.call();
                } finally {
                    Threads.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
                }
            } else {
                return delegate.call();
            }
        } catch (final Throwable t) {
            if (parent != null) {
                final Throwable handled = parent.getExecutorExceptionHandler()
                        .handleExecutorException(t, executeCalledWithoutFuture, true);
                if (handled != null) {
                    throw Throwables.propagate(handled);
                } else {
                    return null;
                }
            } else {
                throw Throwables.propagate(t);
            }
        } finally {
            if (parent != null) {
                parent.decrementPendingCount();
                if (!parent.isKeepThreadLocals()) {
                    FastThreadLocal.removeAll();
                }
            }
            if (originalThreadName != null) {
                Threads.setCurrentThreadName(originalThreadName);
            }
        }
    }

    public void maybeCancelled() {
        if (parent != null && !started) {
            parent.decrementPendingCount();
            if (!parent.isKeepThreadLocals()) {
                FastThreadLocal.removeAll();
            }
        }
    }

    public static <T> List<WrappedCallable<T>> newInstance(final IWrappedExecutorServiceInternal parent,
            final boolean executeCalledWithoutFuture, final Collection<? extends Callable<T>> tasks)
            throws InterruptedException {
        final List<WrappedCallable<T>> ret = new ArrayList<WrappedCallable<T>>(tasks.size());
        boolean skipWaitOnFullPendingCount = false;
        for (final Callable<T> delegate : tasks) {
            ret.add(new WrappedCallable<T>(parent, executeCalledWithoutFuture, delegate, skipWaitOnFullPendingCount));
            skipWaitOnFullPendingCount = true;
        }
        return ret;
    }

    public static <T> WrappedCallable<T> newInstance(final IWrappedExecutorServiceInternal parent,
            final boolean executeCalledWithoutFuture, final Callable<T> delegate) throws InterruptedException {
        return new WrappedCallable<T>(parent, executeCalledWithoutFuture, delegate, false);
    }

    @Override
    public double getPriority() {
        if (delegate instanceof IPriorityProvider) {
            final IPriorityProvider cDelegate = (IPriorityProvider) delegate;
            return cDelegate.getPriority();
        }
        return MISSING_PRIORITY;
    }

}
