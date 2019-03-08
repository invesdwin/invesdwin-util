package de.invesdwin.util.collections.delegate;

import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DelegateSet<E> extends ADelegateSet<E> {

    public DelegateSet(final Set<E> delegate) {
        super(delegate);
    }

    @Deprecated
    @Override
    protected Set<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
