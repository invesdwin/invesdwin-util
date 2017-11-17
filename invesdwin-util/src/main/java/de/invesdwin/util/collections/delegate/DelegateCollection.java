package de.invesdwin.util.collections.delegate;

import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DelegateCollection<E> extends ADelegateCollection<E> {

    public DelegateCollection(final Collection<E> delegate) {
        super(delegate);
    }

    @Deprecated
    @Override
    protected Collection<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
