package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedFastIterableDelegateSet<E> extends AReadWriteLockedFastIterableDelegateSet<E> {

    public ReadWriteLockedFastIterableDelegateSet(final Set<E> delegate) {
        super(delegate);
    }

    public ReadWriteLockedFastIterableDelegateSet(final Set<E> delegate, final IReadWriteLock lock) {
        super(delegate, lock);
    }

    @Override
    protected Set<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
