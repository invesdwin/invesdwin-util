package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedFastIterableDelegateList<E> extends AReadWriteLockedFastIterableDelegateList<E> {

    public ReadWriteLockedFastIterableDelegateList(final List<E> delegate) {
        super(delegate);
    }

    public ReadWriteLockedFastIterableDelegateList(final List<E> delegate, final IReadWriteLock lock) {
        super(delegate, lock);
    }

    @Override
    protected List<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
