package de.invesdwin.util.collections.fast;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class FastIterableDelegateSet<E> extends AFastIterableDelegateSet<E> {

    public FastIterableDelegateSet(final Set<E> delegate) {
        super(delegate);
    }

    @Override
    protected Set<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
