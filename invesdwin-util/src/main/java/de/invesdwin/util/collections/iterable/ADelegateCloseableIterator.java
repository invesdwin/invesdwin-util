package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ADelegateCloseableIterator<E> implements ICloseableIterator<E> {

    private ICloseableIterator<E> delegate;

    protected ICloseableIterator<E> getDelegate() {
        if (delegate == null) {
            delegate = newDelegate();
        }
        return delegate;
    }

    protected abstract ICloseableIterator<E> newDelegate();

    @Override
    public boolean hasNext() {
        return getDelegate().hasNext();
    }

    @Override
    public E next() {
        return getDelegate().next();
    }

    @Override
    public void close() {
        if (delegate != null) {
            delegate.close();
        }
        delegate = EmptyCloseableIterator.getInstance();
    }

}
