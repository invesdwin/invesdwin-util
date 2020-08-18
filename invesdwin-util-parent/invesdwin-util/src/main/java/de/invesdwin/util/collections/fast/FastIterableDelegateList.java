package de.invesdwin.util.collections.fast;

import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class FastIterableDelegateList<E> extends AFastIterableDelegateList<E> {

    public FastIterableDelegateList(final List<E> delegate) {
        super(delegate);
    }

    @Override
    protected List<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
