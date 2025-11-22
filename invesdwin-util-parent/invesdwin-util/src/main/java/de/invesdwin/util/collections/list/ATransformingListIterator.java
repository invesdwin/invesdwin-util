package de.invesdwin.util.collections.list;

import java.util.ListIterator;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ATransformingListIterator<I, O> implements ListIterator<O> {
    private final ListIterator<I> delegate;

    public ATransformingListIterator(final ListIterator<I> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public O next() {
        return transform(delegate.next());
    }

    protected abstract O transform(I element);

    @Override
    public boolean hasPrevious() {
        return delegate.hasPrevious();
    }

    @Override
    public O previous() {
        return transform(delegate.previous());
    }

    @Override
    public int nextIndex() {
        return delegate.nextIndex();
    }

    @Override
    public int previousIndex() {
        return delegate.previousIndex();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(final O e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final O e) {
        throw new UnsupportedOperationException();
    }
}