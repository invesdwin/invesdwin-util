package de.invesdwin.util.concurrent.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.RetryThreads;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.priority.IPriorityProvider;
import de.invesdwin.util.concurrent.priority.IPriorityRunnable;
import de.invesdwin.util.error.Throwables;
import io.netty.util.concurrent.FastThreadLocal;

@NotThreadSafe
public final class WrappedRunnable implements IPriorityRunnable {

    private final IWrappedExecutorServiceInternal parent;
    private final String parentThreadName;
    private final boolean executeCalledWithoutFuture;
    private final Runnable delegate;
    private final boolean threadRetryDisabled;
    private volatile boolean started;

    private WrappedRunnable(final IWrappedExecutorServiceInternal parent, final boolean executeCalledWithoutFuture,
            final Runnable delegate, final boolean skipWaitOnFullPendingCount) throws InterruptedException {
        if (delegate instanceof WrappedRunnable) {
            throw new IllegalArgumentException("delegate should not be an instance of " + getClass().getSimpleName());
        }
        this.parent = parent;
        this.parentThreadName = Thread.currentThread().getName();
        this.executeCalledWithoutFuture = executeCalledWithoutFuture;
        this.delegate = delegate;
        if (parent != null) {
            parent.incrementPendingCount(skipWaitOnFullPendingCount);
        }
        this.threadRetryDisabled = RetryThreads.isThreadRetryDisabled();
    }

    @Override
    public void run() {
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
                final Boolean registerThreadRetryDisabled = RetryThreads.registerThreadRetryDisabled();
                try {
                    delegate.run();
                } finally {
                    RetryThreads.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
                }
            } else {
                delegate.run();
            }
        } catch (final Throwable t) {
            final Throwable handled = parent.getExecutorExceptionHandler()
                    .handleExecutorException(t, executeCalledWithoutFuture, false);
            if (handled != null) {
                throw Throwables.propagate(handled);
            } else {
                return;
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

    public static Collection<WrappedRunnable> newInstance(final IWrappedExecutorServiceInternal parent,
            final boolean executeCalledWithoutFuture, final Collection<Runnable> delegates)
            throws InterruptedException {
        final List<WrappedRunnable> ret = new ArrayList<WrappedRunnable>(delegates.size());
        boolean skipWaitOnFullPendingCount = false;
        for (final Runnable delegate : delegates) {
            ret.add(new WrappedRunnable(parent, executeCalledWithoutFuture, delegate, skipWaitOnFullPendingCount));
            skipWaitOnFullPendingCount = true;
        }
        return ret;
    }

    public static WrappedRunnable newInstance(final IWrappedExecutorServiceInternal parent,
            final boolean executeCalledWithoutFuture, final Runnable delegate) throws InterruptedException {
        return new WrappedRunnable(parent, executeCalledWithoutFuture, delegate, false);
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
