package de.invesdwin.util.collections.loadingcache.historical.query.internal.filter;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class FilteringHistoricalCacheQueryWithFuture<V> extends FilteringHistoricalCacheQuery<V>
        implements IHistoricalCacheQueryWithFuture<V> {

    private final IHistoricalCacheQueryWithFuture<V> delegate;

    protected FilteringHistoricalCacheQueryWithFuture(final IHistoricalCacheInternalMethods<V> internalMethods,
            final IHistoricalCacheQueryWithFuture<V> delegate) {
        super(internalMethods, delegate);
        this.delegate = delegate;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withElementFilter(
            final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        delegate.withElementFilter(elementFilter);
        return this;
    }

    @Deprecated
    @Override
    public IHistoricalCacheQueryWithFuture<V> withFutureNull() {
        delegate.withFutureNull();
        return this;
    }

    @Override
    public FDate getNextKey(final FDate key, final int shiftForwardUnits) {
        final FDate result = delegate.getNextKey(key, shiftForwardUnits);
        if (result == null || result.isBefore(key)) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public V getNextValue(final FDate key, final int shiftForwardUnits) {
        final IHistoricalEntry<V> result = getNextEntry(key, shiftForwardUnits);
        return IHistoricalEntry.unwrapEntryValue(result);
    }

    @Override
    public IHistoricalEntry<V> getNextEntry(final FDate key, final int shiftForwardUnits) {
        final IHistoricalEntry<V> result = delegate.getNextEntry(key, shiftForwardUnits);
        if (result == null || result.getKey().isBefore(key)) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public ICloseableIterable<V> getNextValues(final FDate key, final int shiftForwardUnits) {
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {

                    private final ICloseableIterator<IHistoricalEntry<V>> entriesIterator;

                    {
                        final ICloseableIterable<IHistoricalEntry<V>> entries = getNextEntries(key, shiftForwardUnits);
                        entriesIterator = entries.iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return entriesIterator.next().getValue();
                    }

                    @Override
                    public void close() {
                        entriesIterator.close();
                    }

                };
            }
        };
    }

    @Override
    public ICloseableIterable<FDate> getNextKeys(final FDate key, final int shiftForwardUnits) {
        final ICloseableIterable<FDate> result = delegate.getNextKeys(key, shiftForwardUnits);
        return new AFilterSkippingIterable<FDate>(result) {
            @Override
            protected boolean skip(final FDate element) {
                return element.isBefore(key);
            }
        };
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        final ICloseableIterable<IHistoricalEntry<V>> result = delegate.getNextEntries(key, shiftForwardUnits);
        return new AFilterSkippingIterable<IHistoricalEntry<V>>(result) {
            @Override
            protected boolean skip(final IHistoricalEntry<V> element) {
                return element.getKey().isBefore(key);
            }
        };
    }

    @Override
    protected FilteringHistoricalCacheQueryWithFuture<V> newFutureQuery() {
        return this;
    }

}
