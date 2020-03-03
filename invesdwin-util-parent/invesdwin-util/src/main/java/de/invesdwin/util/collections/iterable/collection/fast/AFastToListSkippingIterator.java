package de.invesdwin.util.collections.iterable.collection.fast;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ASkippingIterator;

@NotThreadSafe
public abstract class AFastToListSkippingIterator<E> extends ASkippingIterator<E>
        implements IFastToListCloseableIterator<E> {

    private final IFastToListCloseableIterator<E> delegate;

    @SuppressWarnings("unchecked")
    public AFastToListSkippingIterator(final IFastToListCloseableIterator<? extends E> delegate) {
        super(delegate);
        this.delegate = (IFastToListCloseableIterator<E>) delegate;
    }

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
