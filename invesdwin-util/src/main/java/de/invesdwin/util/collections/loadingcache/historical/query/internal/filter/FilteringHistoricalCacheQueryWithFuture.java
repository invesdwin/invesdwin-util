package de.invesdwin.util.collections.loadingcache.historical.query.internal.filter;

import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ASkippingIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class FilteringHistoricalCacheQueryWithFuture<V> extends FilteringHistoricalCacheQuery<V>
        implements IHistoricalCacheQueryWithFuture<V> {

    private final IHistoricalCacheQueryWithFuture<V> delegate;

    protected FilteringHistoricalCacheQueryWithFuture(final IHistoricalCacheQueryCore<V> core,
            final IHistoricalCacheQueryWithFuture<V> delegate) {
        super(core, delegate);
        this.delegate = delegate;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withElementFilter(
            final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        delegate.withElementFilter(elementFilter);
        return this;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withFilterDuplicateKeys(final boolean filterDuplicateKeys) {
        delegate.withFilterDuplicateKeys(filterDuplicateKeys);
        return this;
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withFutureNull() {
        delegate.withFutureNull();
        return this;
    }

    @Override
    public FDate getNextKey(final FDate key, final int shiftForwardUnits) {
        final Entry<FDate, V> result = getNextEntry(key, shiftForwardUnits);
        return HistoricalCacheAssertValue.unwrapEntryKey(result);
    }

    @Override
    public V getNextValue(final FDate key, final int shiftForwardUnits) {
        final Entry<FDate, V> result = getNextEntry(key, shiftForwardUnits);
        return HistoricalCacheAssertValue.unwrapEntryValue(result);
    }

    @Override
    public Entry<FDate, V> getNextEntry(final FDate key, final int shiftForwardUnits) {
        final Entry<FDate, V> curEntry = getEntry(key);
        if (curEntry == null) {
            return null;
        }
        final int adjShiftForwardUnits;
        if (curEntry.getKey().isBefore(key)) {
            adjShiftForwardUnits = shiftForwardUnits + 1;
        } else {
            adjShiftForwardUnits = shiftForwardUnits;
        }
        final Entry<FDate, V> result = delegate.getNextEntry(curEntry.getKey(), adjShiftForwardUnits);
        if (result != null && result.getKey().isBefore(key)) {
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

                    private final ICloseableIterator<Entry<FDate, V>> entriesIterator;

                    {
                        final ICloseableIterable<Entry<FDate, V>> entries = getNextEntries(key, shiftForwardUnits);
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
        /*
         * need to go directly against getKey so that recursive queries work properly with shift keys delegates
         */
        final FDate curKey = getKey(key);
        if (curKey == null) {
            return null;
        }
        final int adjShiftForwardUnits;
        if (curKey.isBefore(key)) {
            adjShiftForwardUnits = shiftForwardUnits + 1;
        } else {
            adjShiftForwardUnits = shiftForwardUnits;
        }
        final ICloseableIterable<FDate> result = delegate.getNextKeys(curKey, adjShiftForwardUnits);
        return new ASkippingIterable<FDate>(result) {
            @Override
            protected boolean skip(final FDate element) {
                return element.isBefore(key);
            }
        };
    }

    @Override
    public ICloseableIterable<Entry<FDate, V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        final Entry<FDate, V> curEntry = getEntry(key);
        if (curEntry == null) {
            return null;
        }
        final int adjShiftForwardUnits;
        if (curEntry.getKey().isBefore(key)) {
            adjShiftForwardUnits = shiftForwardUnits + 1;
        } else {
            adjShiftForwardUnits = shiftForwardUnits;
        }
        final ICloseableIterable<Entry<FDate, V>> result = delegate.getNextEntries(curEntry.getKey(),
                adjShiftForwardUnits);
        return new ASkippingIterable<Entry<FDate, V>>(result) {
            @Override
            protected boolean skip(final Entry<FDate, V> element) {
                return element.getKey().isBefore(key);
            }
        };
    }

    @Override
    protected FilteringHistoricalCacheQueryWithFuture<V> newFutureQuery() {
        return this;
    }

}
