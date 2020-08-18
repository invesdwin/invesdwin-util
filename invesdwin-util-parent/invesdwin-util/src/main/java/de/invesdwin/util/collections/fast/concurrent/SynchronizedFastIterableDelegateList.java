package de.invesdwin.util.collections.fast.concurrent;

import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedFastIterableDelegateList<E> extends ASynchronizedFastIterableDelegateList<E> {

    public SynchronizedFastIterableDelegateList(final List<E> delegate) {
        super(delegate);
    }

    @Override
    protected List<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
