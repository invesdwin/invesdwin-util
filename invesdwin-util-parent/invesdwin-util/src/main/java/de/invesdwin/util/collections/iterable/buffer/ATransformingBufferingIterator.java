package de.invesdwin.util.collections.iterable.buffer;

import java.util.Iterator;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ATransformingIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@NotThreadSafe
public abstract class ATransformingBufferingIterator<S, R> implements IBufferingIterator<R> {

    private final IBufferingIterator<? extends S> delegate;

    public ATransformingBufferingIterator(final IBufferingIterator<? extends S> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public R next() {
        return null;
    }

    @Override
    public ICloseableIterator<R> iterator() {
        return new ATransformingIterator<S, R>(delegate.iterator()) {
            @Override
            protected R transform(final S value) {
                return ATransformingBufferingIterator.this.transform(value);
            }
        };
    }

    protected abstract R transform(S value);

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public R getHead() {
        final S head = delegate.getHead();
        if (head == null) {
            return null;
        }
        return transform(head);
    }

    @Override
    public R getTail() {
        final S tail = delegate.getTail();
        if (tail == null) {
            return null;
        }
        return transform(tail);
    }

    @Deprecated
    @Override
    public boolean prepend(final R element) {
        throw new UnsupportedOperationException("can not add");
    }

    @Deprecated
    @Override
    public boolean add(final R element) {
        throw new UnsupportedOperationException("can not add");
    }

    @Deprecated
    @Override
    public boolean addAll(final Iterator<? extends R> iterator) {
        throw new UnsupportedOperationException("can not add");
    }

    @Deprecated
    @Override
    public boolean addAll(final Iterable<? extends R> iterable) {
        throw new UnsupportedOperationException("can not add");
    }

    @Deprecated
    @Override
    public boolean addAll(final BufferingIterator<R> iterable) {
        throw new UnsupportedOperationException("can not add");
    }

    @Deprecated
    @Override
    public boolean addAll(final ICloseableIterable<? extends R> iterable) {
        throw new UnsupportedOperationException("can not add");
    }

    @Deprecated
    @Override
    public boolean addAll(final ICloseableIterator<? extends R> iterator) {
        throw new UnsupportedOperationException("can not add");
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean consume(final Iterable<? extends R> iterable) {
        throw new UnsupportedOperationException("can not add");
    }

    @Override
    public boolean consume(final Iterator<? extends R> iterator) {
        throw new UnsupportedOperationException("can not add");
    }

    @Override
    public boolean consume(final BufferingIterator<R> iterator) {
        throw new UnsupportedOperationException("can not add");
    }

}
