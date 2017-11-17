package de.invesdwin.util.collections.concurrent;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {

    public SynchronizedSet(final Set<E> delegate) {
        super(delegate);
    }

    public SynchronizedSet(final Set<E> delegate, final Object lock) {
        super(delegate, lock);
    }

    @Override
    protected Set<E> getDelegate() {
        return (Set<E>) super.getDelegate();
    }
}