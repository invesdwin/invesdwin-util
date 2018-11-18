package de.invesdwin.util.collections.fast.concurrent;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedSet<E> extends SynchronizedCollection<E> implements Set<E> {

    public LockedSet(final Set<E> delegate) {
        super(delegate);
    }

    public LockedSet(final Set<E> delegate, final ILock lock) {
        super(delegate, lock);
    }

    @Override
    protected Set<E> getDelegate() {
        return (Set<E>) super.getDelegate();
    }
}