package de.invesdwin.util.collections.iterable.buffer;

import java.util.Iterator;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ATransformingCloseableIterable;
import de.invesdwin.util.collections.iterable.ATransformingCloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterator;

@NotThreadSafe
public abstract class ATransformingBufferingIterator<S, R> implements IBufferingIterator<R> {

    private final IBufferingIterator<S> delegate;

    @SuppressWarnings("unchecked")
    public ATransformingBufferingIterator(final IBufferingIterator<? extends S> delegate) {
        this.delegate = (IBufferingIterator<S>) delegate;
    }

    protected abstract R transformSource(S value);

    protected abstract S transformResult(R value);

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
        return transformSource(delegate.next());
    }

    @Override
    public ICloseableIterator<R> iterator() {
        return new ATransformingCloseableIterator<S, R>(delegate.iterator()) {
            @Override
            protected R transform(final S value) {
                return transformSource(value);
            }
        };
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public R getHead() {
        return transformSource(delegate.getHead());
    }

    @Override
    public R getTail() {
        return transformSource(delegate.getTail());
    }

    @Override
    public void add(final R element) {
        delegate.add(transformResult(element));
    }

    @Override
    public void addAll(final Iterable<? extends R> iterable) {
        delegate.addAll(new ATransformingCloseableIterable<R, S>(WrapperCloseableIterable.maybeWrap(iterable)) {
            @Override
            protected S transform(final R value) {
                return transformResult(value);
            }
        });
    }

    @Override
    public void addAll(final Iterator<? extends R> iterator) {
        delegate.addAll(new ATransformingCloseableIterator<R, S>(WrapperCloseableIterator.maybeWrap(iterator)) {
            @Override
            protected S transform(final R value) {
                return transformResult(value);
            }
        });
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int size() {
        return delegate.size();
    }

}
