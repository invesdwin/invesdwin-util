package de.invesdwin.util.collections.fast.concurrent.locked;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedFastIterableDelegateSet<E> extends ALockedFastIterableDelegateSet<E> {

    public LockedFastIterableDelegateSet(final Set<E> delegate) {
        super(delegate);
    }

    public LockedFastIterableDelegateSet(final Set<E> delegate, final ILock lock) {
        super(delegate, lock);
    }

    @Override
    protected Set<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
