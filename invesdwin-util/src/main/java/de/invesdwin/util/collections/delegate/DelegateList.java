package de.invesdwin.util.collections.delegate;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DelegateList<E> extends ADelegateList<E> {

    public DelegateList(final List<E> delegate) {
        super(delegate);
    }

    @Deprecated
    @Override
    protected List<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
