package de.invesdwin.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
final class WrappedRunnable implements Runnable {

    private final Runnable delegate;
    private final WrappedExecutorService parent;

    private WrappedRunnable(final WrappedExecutorService parent, final Runnable delegate,
            final boolean skipWaitOnFullPendingCount) throws InterruptedException {
        this.delegate = delegate;
        this.parent = parent;
        parent.incrementPendingCount(skipWaitOnFullPendingCount);
    }

    @Override
    public void run() {
        try {
            delegate.run();
        } finally {
            parent.decrementPendingCount();
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
