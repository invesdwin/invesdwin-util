package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.List;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.FilterDuplicateKeysList;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.time.date.FDate;

public interface IHistoricalCacheQueryCore<V> {

    IHistoricalEntry<V> getPreviousEntry(IHistoricalCacheQueryInternalMethods<V> query, FDate key, int shiftBackUnits);

    ICloseableIterable<IHistoricalEntry<V>> getPreviousEntries(IHistoricalCacheQueryInternalMethods<V> query, FDate key,
            int shiftBackUnits);

    IHistoricalCacheInternalMethods<V> getParent();

    void clear();

    void increaseMaximumSize(int maximumSize);

    V getValue(IHistoricalCacheQueryInternalMethods<V> query, FDate key, HistoricalCacheAssertValue assertValue);

    IHistoricalEntry<V> getEntry(IHistoricalCacheQueryInternalMethods<V> query, FDate key,
            HistoricalCacheAssertValue assertValue);

    ICloseableIterable<IHistoricalEntry<V>> getNextEntries(IHistoricalCacheQueryInternalMethods<V> query, FDate key,
            int shiftForwardUnits);

    IHistoricalEntry<V> getNextEntry(IHistoricalCacheQueryInternalMethods<V> query, FDate key, int shiftForwardUnits);

    IHistoricalEntry<V> computeEntry(HistoricalCacheQuery<V> historicalCacheQuery, FDate key,
            HistoricalCacheAssertValue assertValue);

    void putPrevious(FDate previousKey, V value, FDate valueKey);

    void putPreviousKey(FDate previousKey, FDate valueKey);

    default List<IHistoricalEntry<V>> newEntriesList(final int shiftBackUnits) {
        return new FilterDuplicateKeysList<>(shiftBackUnits);
    }

}
