package de.invesdwin.util.collections.iterable.collection.fast;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AFastToListSkippingIterable<E> implements IFastToListCloseableIterable<E> {

    protected final IFastToListCloseableIterable<E> delegate;

    @SuppressWarnings("unchecked")
    public AFastToListSkippingIterable(final IFastToListCloseableIterable<? extends E> delegate) {
        this.delegate = (IFastToListCloseableIterable<E>) delegate;
    }

    @Override
    public IFastToListCloseableIterator<E> iterator() {
        return new AFastToListSkippingIterator<E>(delegate.iterator()) {
            @Override
            protected boolean skip(final E element) {
                return AFastToListSkippingIterable.this.skip(element);
            }
        };
    }

    protected abstract boolean skip(E element);

    @Override
    public List<E> toList() {
        return delegate.toList();
    }

    @Override
    public List<E> toList(final List<E> list) {
        return delegate.toList(list);
    }

    @Override
    public E getHead() {
        return delegate.getHead();
    }

    @Override
    public E getTail() {
        return delegate.getTail();
    }
}
