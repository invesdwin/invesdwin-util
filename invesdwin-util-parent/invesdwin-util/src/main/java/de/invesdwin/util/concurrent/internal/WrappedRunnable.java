package de.invesdwin.util.concurrent.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.priority.IPriorityProvider;
import de.invesdwin.util.concurrent.priority.IPriorityRunnable;
import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public final class WrappedRunnable implements IPriorityRunnable {

    private final String parentThreadName;
    private final Runnable delegate;
    private final IWrappedExecutorServiceInternal parent;

    private WrappedRunnable(final IWrappedExecutorServiceInternal parent, final Runnable delegate,
            final boolean skipWaitOnFullPendingCount) throws InterruptedException {
        if (delegate instanceof WrappedRunnable) {
            throw new IllegalArgumentException("delegate should not be an instance of " + getClass().getSimpleName());
        }
        this.parentThreadName = Thread.currentThread().getName();
        this.delegate = delegate;
        this.parent = parent;
        if (parent != null) {
            parent.incrementPendingCount(skipWaitOnFullPendingCount);
        }
    }

    @Override
    public void run() {
        final String originalThreadName;
        if (parent != null && parent.isDynamicThreadName()) {
            originalThreadName = Threads.getCurrentRootThreadName();
            Threads.updateParentThreadName(parentThreadName);
        } else {
            originalThreadName = null;
        }
        try {
            delegate.run();
        } catch (final Throwable t) {
            if (parent != null && parent.isLogExceptions()) {
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), t);
            }
            throw Throwables.propagate(t);
        } finally {
            if (parent != null) {
                parent.decrementPendingCount();
            }
            if (originalThreadName != null) {
                Threads.setCurrentThreadName(originalThreadName);
            }
        }
    }

    public static Collection<WrappedRunnable> newInstance(final IWrappedExecutorServiceInternal parent,
            final Collection<Runnable> delegates) throws InterruptedException {
        final List<WrappedRunnable> ret = new ArrayList<WrappedRunnable>(delegates.size());
        boolean skipWaitOnFullPendingCount = false;
        for (final Runnable delegate : delegates) {
            ret.add(new WrappedRunnable(parent, delegate, skipWaitOnFullPendingCount));
            skipWaitOnFullPendingCount = true;
        }
        return ret;
    }

    public static WrappedRunnable newInstance(final IWrappedExecutorServiceInternal parent, final Runnable delegate)
            throws InterruptedException {
        return new WrappedRunnable(parent, delegate, false);
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
