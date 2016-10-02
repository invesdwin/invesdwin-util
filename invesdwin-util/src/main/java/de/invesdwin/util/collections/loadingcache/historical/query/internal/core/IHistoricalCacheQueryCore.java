package de.invesdwin.util.collections.loadingcache.historical.query.internal.core;

import java.util.List;
import java.util.Map.Entry;

import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheAssertValue;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheQueryCore<V> {

    Entry<FDate, V> getPreviousEntry(IHistoricalCacheQueryInternalMethods<V> query, FDate key, int shiftBackUnits);

    List<Entry<FDate, V>> getPreviousEntries(IHistoricalCacheQueryInternalMethods<V> query, FDate key,
            int shiftBackUnits);

    IHistoricalCacheInternalMethods<V> getParent();

    void clear();

    void increaseMaximumSize(int maximumSize);

    V getValue(IHistoricalCacheQueryInternalMethods<V> query, FDate key, HistoricalCacheAssertValue assertValue);

    Entry<FDate, V> getEntry(IHistoricalCacheQueryInternalMethods<V> query, FDate key,
            HistoricalCacheAssertValue assertValue);

    HistoricalCacheQuery<V> newQuery();

}
