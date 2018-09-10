package de.invesdwin.util.collections.loadingcache.historical.query.internal.filter;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;

@ThreadSafe
public abstract class AThreadLocalElementFilterCloseableIterable<E, V> implements ICloseableIterable<E> {

    private final IHistoricalCacheQuery<V> delegate;
    private final IHistoricalCacheQueryElementFilter<V> existing;
    private final IHistoricalCacheQueryElementFilter<V> wrapper;

    public AThreadLocalElementFilterCloseableIterable(final IHistoricalCacheQuery<V> delegate) {
        this.delegate = delegate;
        this.existing = delegate.getThreadLocalElementFilter();
        this.wrapper = newWrapper(existing);
    }

    protected abstract IHistoricalCacheQueryElementFilter<V> newWrapper(IHistoricalCacheQueryElementFilter<V> existing);

    protected abstract ICloseableIterable<E> newIterable();

    @Override
    public ICloseableIterator<E> iterator() {
        return new ICloseableIterator<E>() {

            private final ICloseableIterator<E> parent = newIterator();

            private ICloseableIterator<E> newIterator() {
                delegate.withThreadLocalElementFilter(wrapper);
                try {
                    return newIterable().iterator();
                } finally {
                    delegate.withThreadLocalElementFilter(existing);
                }
            }

            @Override
            public E next() {
                delegate.withThreadLocalElementFilter(wrapper);
                try {
                    return parent.next();
                } finally {
                    delegate.withThreadLocalElementFilter(existing);
                }
            }

            @Override
            public boolean hasNext() {
                delegate.withThreadLocalElementFilter(wrapper);
                try {
                    return parent.hasNext();
                } finally {
                    delegate.withThreadLocalElementFilter(existing);
                }
            }

            @Override
            public void close() {
                parent.close();
            }
        };
    }

}
