package de.invesdwin.util.collections.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.IBufferingIterator;

@NotThreadSafe
public class DelegateBufferingIterator<E> extends ADelegateBufferingIterator<E> {

    public DelegateBufferingIterator(final IBufferingIterator<E> delegate) {
        super(delegate);
    }

    @Deprecated
    @Override
    protected IBufferingIterator<E> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
