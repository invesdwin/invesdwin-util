package de.invesdwin.util.collections.fast.concurrent;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedFastIterableDelegateSet<E> extends ASynchronizedFastIterableDelegateSet<E> {

    public SynchronizedFastIterableDelegateSet(final Set<E> delegate) {
        super(delegate);
    }

    @Override
    protected Set<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
