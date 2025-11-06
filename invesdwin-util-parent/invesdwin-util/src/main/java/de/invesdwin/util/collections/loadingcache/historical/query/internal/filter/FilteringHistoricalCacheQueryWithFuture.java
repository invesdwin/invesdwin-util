package de.invesdwin.util.collections.loadingcache.historical.query.internal.filter;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.time.date.FDate;

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
        final FDate curKey = getKey(key);
        if (curKey == null) {
            return null;
        }
        final int adjShiftForwardUnits;
        if (curKey.isBefore(key)) {
            adjShiftForwardUnits = shiftForwardUnits + 1;
        } else {
            if (shiftForwardUnits == 0) {
                return curKey;
            }
            adjShiftForwardUnits = shiftForwardUnits;
        }
        final FDate result = delegate.getNextKey(curKey, adjShiftForwardUnits);
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
        final IHistoricalEntry<V> curEntry = getEntry(key);
        if (curEntry == null) {
            return null;
        }
        final int adjShiftForwardUnits;
        if (curEntry.getKey().isBefore(key)) {
            adjShiftForwardUnits = shiftForwardUnits + 1;
        } else {
            if (shiftForwardUnits == 0) {
                return curEntry;
            }
            adjShiftForwardUnits = shiftForwardUnits;
        }
        final IHistoricalEntry<V> result = delegate.getNextEntry(curEntry.getKey(), adjShiftForwardUnits);
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
                        final ICloseableIterable<IHistoricalEntry<V>> entries = IHistoricalEntry
                                .skipEmpty(getNextEntries(key, shiftForwardUnits));
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
        return new AFilterSkippingIterable<FDate>(result) {
            @Override
            protected boolean skip(final FDate element) {
                return element.isBefore(key);
            }
        };
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        final IHistoricalEntry<V> curEntry = getEntry(key);
        if (curEntry == null) {
            return null;
        }
        final int adjShiftForwardUnits;
        if (curEntry.getKey().isBefore(key)) {
            adjShiftForwardUnits = shiftForwardUnits + 1;
        } else {
            adjShiftForwardUnits = shiftForwardUnits;
        }
        final ICloseableIterable<IHistoricalEntry<V>> result = delegate.getNextEntries(curEntry.getKey(),
                adjShiftForwardUnits);
        return new AFilterSkippingIterable<IHistoricalEntry<V>>(result) {
            @Override
            protected boolean skip(final IHistoricalEntry<V> element) {
                return element.getKey().isBefore(key);
            }
        };
    }

    @Override
    public FDate getNextKeyCached(final FDate key, final int shiftForwardUnits) {
        final FDate curKey = getKey(key);
        if (curKey == null) {
            return null;
        }
        final int adjShiftForwardUnits;
        if (curKey.isBefore(key)) {
            adjShiftForwardUnits = shiftForwardUnits + 1;
        } else {
            if (shiftForwardUnits == 0) {
                return curKey;
            }
            adjShiftForwardUnits = shiftForwardUnits;
        }
        final FDate result = delegate.getNextKeyCached(curKey, adjShiftForwardUnits);
        if (result == null || result.isBefore(key)) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public V getNextValueCached(final FDate key, final int shiftForwardUnits) {
        final IHistoricalEntry<V> result = getNextEntryCached(key, shiftForwardUnits);
        return IHistoricalEntry.unwrapEntryValue(result);
    }

    @Override
    public IHistoricalEntry<V> getNextEntryCached(final FDate key, final int shiftForwardUnits) {
        final IHistoricalEntry<V> curEntry = getEntry(key);
        if (curEntry == null) {
            return null;
        }
        final int adjShiftForwardUnits;
        if (curEntry.getKey().isBefore(key)) {
            adjShiftForwardUnits = shiftForwardUnits + 1;
        } else {
            if (shiftForwardUnits == 0) {
                return curEntry;
            }
            adjShiftForwardUnits = shiftForwardUnits;
        }
        final IHistoricalEntry<V> result = delegate.getNextEntryCached(curEntry.getKey(), adjShiftForwardUnits);
        if (result == null || result.getKey().isBefore(key)) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public ICloseableIterable<V> getNextValuesCached(final FDate key, final int shiftForwardUnits) {
        return new ICloseableIterable<V>() {
            @Override
            public ICloseableIterator<V> iterator() {
                return new ICloseableIterator<V>() {

                    private final ICloseableIterator<IHistoricalEntry<V>> entriesIterator;

                    {
                        final ICloseableIterable<IHistoricalEntry<V>> entries = IHistoricalEntry
                                .skipEmpty(getNextEntriesCached(key, shiftForwardUnits));
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
    public ICloseableIterable<FDate> getNextKeysCached(final FDate key, final int shiftForwardUnits) {
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
        final ICloseableIterable<FDate> result = delegate.getNextKeysCached(curKey, adjShiftForwardUnits);
        return new AFilterSkippingIterable<FDate>(result) {
            @Override
            protected boolean skip(final FDate element) {
                return element.isBefore(key);
            }
        };
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getNextEntriesCached(final FDate key, final int shiftForwardUnits) {
        final IHistoricalEntry<V> curEntry = getEntry(key);
        if (curEntry == null) {
            return null;
        }
        final int adjShiftForwardUnits;
        if (curEntry.getKey().isBefore(key)) {
            adjShiftForwardUnits = shiftForwardUnits + 1;
        } else {
            adjShiftForwardUnits = shiftForwardUnits;
        }
        final ICloseableIterable<IHistoricalEntry<V>> result = delegate.getNextEntriesCached(curEntry.getKey(),
                adjShiftForwardUnits);
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
