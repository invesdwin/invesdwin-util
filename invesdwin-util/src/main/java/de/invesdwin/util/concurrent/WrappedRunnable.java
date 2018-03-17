package de.invesdwin.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.Throwables;

@NotThreadSafe
final class WrappedRunnable implements Runnable {

    private final String parentThreadName;
    private final Runnable delegate;
    private final WrappedExecutorService parent;

    private WrappedRunnable(final WrappedExecutorService parent, final Runnable delegate,
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
        final String originalThreadName = Threads.getCurrentThreadName();
        Threads.updateParentThreadName(parentThreadName);
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
            Threads.setCurrentThreadName(originalThreadName);
        }
    }

    static Collection<WrappedRunnable> newInstance(final WrappedExecutorService parent,
            final Collection<Runnable> delegates) throws InterruptedException {
        final List<WrappedRunnable> ret = new ArrayList<WrappedRunnable>(delegates.size());
        boolean skipWaitOnFullPendingCount = false;
        for (final Runnable delegate : delegates) {
            ret.add(new WrappedRunnable(parent, delegate, skipWaitOnFullPendingCount));
            skipWaitOnFullPendingCount = true;
        }
        return ret;
    }

    static WrappedRunnable newInstance(final WrappedExecutorService parent, final Runnable delegate)
            throws InterruptedException {
        return new WrappedRunnable(parent, delegate, false);
    }

}
