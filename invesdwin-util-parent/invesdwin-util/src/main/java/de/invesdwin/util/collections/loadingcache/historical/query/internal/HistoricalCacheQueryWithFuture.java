package de.invesdwin.util.collections.loadingcache.historical.query.internal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryElementFilter;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class HistoricalCacheQueryWithFuture<V> extends HistoricalCacheQuery<V>
        implements IHistoricalCacheQueryWithFuture<V> {

    public HistoricalCacheQueryWithFuture(final IHistoricalCacheInternalMethods<V> internalMethods) {
        super(internalMethods);
    }

    @Override
    public IHistoricalCacheQueryWithFuture<V> withElementFilter(
            final IHistoricalCacheQueryElementFilter<V> elementFilter) {
        return (IHistoricalCacheQueryWithFuture<V>) super.withElementFilter(elementFilter);
    }

    @Deprecated
    @Override
    public IHistoricalCacheQueryWithFuture<V> withFutureNull() {
        throw new IllegalStateException(
                "withFuture() has already been called. Please create a new query for this and call withFutureNull() directly");
    }

    @Override
    public V getValue(final FDate key) {
        return super.getValue(key);
    }

    @Override
    public final FDate getNextKey(final FDate key, final int shiftForwardUnits) {
        final IHistoricalCacheQuery<?> interceptor = getKeysQueryInterceptor();
        if (interceptor != null) {
            return interceptor.withFuture().getNextKey(key, shiftForwardUnits);
        }
        assertShiftUnitsPositive(shiftForwardUnits);
        return IHistoricalEntry.unwrapEntryKey(getNextEntry(key, shiftForwardUnits));
    }

    @Override
    public ICloseableIterable<FDate> getNextKeys(final FDate key, final int shiftForwardUnits) {
        final IHistoricalCacheQuery<?> interceptor = getKeysQueryInterceptor();
        if (interceptor != null) {
            return interceptor.withFuture().getNextKeys(key, shiftForwardUnits);
        }
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
        return new ICloseableIterable<FDate>() {
            @Override
            public ICloseableIterator<FDate> iterator() {
                return new ICloseableIterator<FDate>() {
                    private final ICloseableIterator<IHistoricalEntry<V>> nextEntries = getNextEntries(key,
                            shiftForwardUnits).iterator();

                    @Override
                    public boolean hasNext() {
                        return nextEntries.hasNext();
                    }

                    @Override
                    public FDate next() {
                        return nextEntries.next().getKey();
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
    public IHistoricalEntry<V> getNextEntry(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositive(shiftForwardUnits);
        return internalMethods.getQueryCore().getNextEntry(this, key, shiftForwardUnits);
    }

    @Override
    public V getNextValue(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositive(shiftForwardUnits);
        return IHistoricalEntry.unwrapEntryValue(getNextEntry(key, shiftForwardUnits));
    }

    @Override
    public ICloseableIterable<IHistoricalEntry<V>> getNextEntries(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
        return internalMethods.getQueryCore().getNextEntries(this, key, shiftForwardUnits);
    }

    @Override
    public ICloseableIterable<V> getNextValues(final FDate key, final int shiftForwardUnits) {
        assertShiftUnitsPositiveNonZero(shiftForwardUnits);
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
    protected HistoricalCacheQueryWithFuture<V> newFutureQuery() {
        return this;
    }

}
