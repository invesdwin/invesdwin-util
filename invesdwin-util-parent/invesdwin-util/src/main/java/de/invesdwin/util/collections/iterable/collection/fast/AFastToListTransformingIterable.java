package de.invesdwin.util.collections.iterable.collection.fast;

import java.util.List;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class AFastToListTransformingIterable<S, R> implements IFastToListCloseableIterable<R> {

    private final IFastToListCloseableIterable<? extends S> delegate;

    public AFastToListTransformingIterable(final IFastToListCloseableIterable<? extends S> delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate must not be null");
        }
        this.delegate = delegate;
    }

    protected abstract R transform(S value);

    @Override
    public IFastToListCloseableIterator<R> iterator() {
        return new AFastToListTransformingIterator<S, R>(delegate.iterator()) {

            @Override
            protected R transform(final S value) {
                return AFastToListTransformingIterable.this.transform(value);
            }

        };
    }

    @Override
    public List<R> toList() {
        return iterator().toList();
    }

    @Override
    public List<R> toList(final List<R> list) {
        return iterator().toList(list);
    }

    @Override
    public R getHead() {
        return iterator().getHead();
    }

    @Override
    public R getTail() {
        return iterator().getTail();
    }

}
