package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedSet<E> extends ReadWriteLockedCollection<E> implements Set<E> {

    public ReadWriteLockedSet(final Set<E> delegate) {
        super(delegate);
    }

    public ReadWriteLockedSet(final Set<E> delegate, final IReadWriteLock lock) {
        super(delegate, lock);
    }

    @Override
    protected Set<E> getDelegate() {
        return (Set<E>) super.getDelegate();
    }
}