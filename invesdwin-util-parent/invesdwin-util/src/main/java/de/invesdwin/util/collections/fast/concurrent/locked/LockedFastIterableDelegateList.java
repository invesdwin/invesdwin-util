package de.invesdwin.util.collections.fast.concurrent.locked;

import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedFastIterableDelegateList<E> extends ALockedFastIterableDelegateList<E> {

    public LockedFastIterableDelegateList(final List<E> delegate) {
        super(delegate);
    }

    public LockedFastIterableDelegateList(final List<E> delegate, final ILock lock) {
        super(delegate, lock);
    }

    @Override
    protected List<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
