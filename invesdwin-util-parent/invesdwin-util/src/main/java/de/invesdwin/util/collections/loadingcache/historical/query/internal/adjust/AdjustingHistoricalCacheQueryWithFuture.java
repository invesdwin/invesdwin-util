package de.invesdwin.util.collections.loadingcache.historical.query.internal.adjust;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.skip.ASkippingIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class AdjustingHistoricalCacheQueryWithFuture<V> extends AdjustingHistoricalCacheQuery<V>
        implements IHistoricalCacheQueryWithFuture<V> {

    private final IHistoricalCacheQueryWithFuture<V> delegate;

    public AdjustingHistoricalCacheQueryWithFuture(final IHistoricalCacheInternalMethods<V> internalMethods) {
        this(internalMethods, new HistoricalCacheQueryWithFuture<V>(internalMethods));
    }

    protected AdjustingHistoricalCacheQueryWithFuture(final IHistoricalCacheInternalMethods<V> internalMethods,
            final IHistoricalCacheQueryWithFuture<V> delegate) {
        super(internalMethods, delegate);
        this.delegate = delegate;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> setElementFilter(
            final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        delegate.setElementFilter(elementFilter);
        return this;
    }

    @Deprecated
    @Override
    public IHistoricalCacheQueryWithFuture<V> setFutureNullEnabled() {
        delegate.setFutureNullEnabled();
        return this;
    }

    @Override
    public FDate getNextKey(final FDate key, final int shiftForwardUnits) {
        final FDate nextKey = delegate.getNextKey(adjustKey(key), shiftForwardUnits);
        if (isFutureKey(nextKey)) {
            return null;
        } else {
            return nextKey;
        }
    }

    @Override
    public ICloseableIterable<FDate> getNextKeys(final FDate key, final int shiftForwardUnits) {
        final ICloseableIterable<FDate> result = delegate.getNextKeys(adjustKey(key), shiftForwardUnits);
        return new ASkippingIterable<FDate>(result) {
            @Override
            protected boolean skip(final FDate element) {
                if (isFutureKey(element)) {
                    throw new FastNoSuchElementException("future data reached");
                }
                return false;
            }
        };
    }

    @Override
    public IHistoricalEntry<V> getNextEntry(final FDate key, final int shiftForwardUnits) {
        final IHistoricalEntry<V> nextEntry = delegate.getNextEntry(adjustKey(key), shiftForwardUnits);
        if (isFutureEntry(nextEntry)) {
            return null;
        } else {
            return nextEntry;
        }
    }

    @Override
    public ICloseableIterable<V> getNextValues(final FDate key, final int shiftForwardUnits) {
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {
                    private final ICloseableIterator<IHistoricalEntry<V>> nextEntries = getNextEntries(key,
                            shiftForwardUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return nextEntries.hasNext();
                    }

                    @Override
                    public V next() {
                        return nextEntries.next().getValue();
                    }

                    @Override
                    public void close() {
                        nextEntries.close();
                    }
                };
            }
        };
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        final ICloseableIterable<IHistoricalEntry<V>> result = delegate.getNextEntries(adjustKey(key),
                shiftForwardUnits);
        return new ASkippingIterable<IHistoricalEntry<V>>(result) {
            @Override
            protected boolean skip(final IHistoricalEntry<V> element) {
                if (isFutureEntry(element)) {
                    throw new FastNoSuchElementException("future data reached");
                }
                return false;
            }
        };
    }

    @Override
    public V getNextValue(final FDate key, final int shiftForwardUnits) {
        return IHistoricalEntry.unwrapEntryValue(getNextEntry(key, shiftForwardUnits));
    }

    @Override
    protected AdjustingHistoricalCacheQueryWithFuture<V> newFutureQuery() {
        return this;
    }

}
