package de.invesdwin.util.collections.iterable.collection.fast;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ATransformingIterator;

@NotThreadSafe
public abstract class AFastToListTransformingIterator<S, R> extends ATransformingIterator<S, R>
        implements IFastToListCloseableIterator<R> {

    private final IFastToListCloseableIterator<S> delegate;

    @SuppressWarnings("unchecked")
    public AFastToListTransformingIterator(final IFastToListCloseableIterator<? extends S> delegate) {
        super(delegate);
        this.delegate = (IFastToListCloseableIterator<S>) delegate;
    }

    @Override
    public List<R> toList() {
        throw new UnsupportedOperationException("do this from the iterable");
    }

    @Override
    public List<R> toList(final List<R> list) {
        throw new UnsupportedOperationException("do this from the iterable");
    }

    @Override
    public R getHead() {
        return transform(delegate.getHead());
    }

    @Override
    public R getTail() {
        return transform(delegate.getTail());
    }

}
