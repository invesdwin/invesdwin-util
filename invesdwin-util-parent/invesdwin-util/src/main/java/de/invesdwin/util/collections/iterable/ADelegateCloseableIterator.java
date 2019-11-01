package de.invesdwin.util.collections.iterable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ADelegateCloseableIterator<E> implements ICloseableIterator<E> {

    private ICloseableIterator<E> delegate;

    @SuppressWarnings("unchecked")
    protected ICloseableIterator<E> getDelegate() {
        if (delegate == null) {
            delegate = (ICloseableIterator<E>) newDelegate();
        }
        return delegate;
    }

    protected abstract ICloseableIterator<? extends E> newDelegate();

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
