package de.invesdwin.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
final class WrappedCallable<V> implements Callable<V> {

    private final String parentThreadName;
    private final Callable<V> delegate;
    private final WrappedExecutorService parent;

    private WrappedCallable(final WrappedExecutorService parent, final Callable<V> delegate,
            final boolean skipWaitOnFullPendingCount) throws InterruptedException {
        this.parentThreadName = Thread.currentThread().getName();
        this.delegate = delegate;
        this.parent = parent;
        parent.incrementPendingCount(skipWaitOnFullPendingCount);
    }

    @Override
    public V call() throws Exception {
        Threads.updateParentThreadName(parentThreadName);
        try {
            return delegate.call();
        } finally {
            parent.decrementPendingCount();
        }
    }

    static <T> Collection<WrappedCallable<T>> newInstance(final WrappedExecutorService parent,
            final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        final List<WrappedCallable<T>> ret = new ArrayList<WrappedCallable<T>>(tasks.size());
        boolean skipWaitOnFullPendingCount = false;
        for (final Callable<T> delegate : tasks) {
            ret.add(new WrappedCallable<T>(parent, delegate, skipWaitOnFullPendingCount));
            skipWaitOnFullPendingCount = true;
        }
        return ret;
    }

    static <T> WrappedCallable<T> newInstance(final WrappedExecutorService parent, final Callable<T> delegate)
            throws InterruptedException {
        return new WrappedCallable<T>(parent, delegate, false);
    }

}
