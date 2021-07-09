package de.invesdwin.util.collections.loadingcache.historical.key;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.date.FDate;

public interface IHistoricalCacheShiftKeyProvider<V> {

    FDate calculatePreviousKey(FDate key);

    FDate calculateNextKey(FDate key);

    void clear();

    AHistoricalCache<?> getParent();

    IHistoricalCacheQuery<?> newKeysQueryInterceptor();

    IHistoricalEntry<V> maybeWrap(FDate key, V value);

    IHistoricalEntry<V> maybeWrap(FDate key, IHistoricalEntry<V> value);

    IHistoricalEntry<V> put(FDate previousKey, FDate valueKey, V value, IHistoricalEntry<V> shiftKeyValueEntry,
            FDate nextKey);

    IHistoricalEntry<V> put(FDate key, IHistoricalEntry<V> value);

    IHistoricalEntry<V> put(FDate key, V value);

}
