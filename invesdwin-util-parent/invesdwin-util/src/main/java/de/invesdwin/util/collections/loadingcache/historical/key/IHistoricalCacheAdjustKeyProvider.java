package de.invesdwin.util.collections.loadingcache.historical.key;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.date.FDate;

public interface IHistoricalCacheAdjustKeyProvider {

    boolean isAdjustedKey(FDate key);

    FDate adjustKey(FDate key);

    FDate maybeAdjustKey(FDate key);

    FDate newAlreadyAdjustedKey(FDate key);

    void clear();

    FDate getHighestAllowedKey();

    FDate getPreviousHighestAllowedKey();

    boolean registerHistoricalCache(AHistoricalCache<?> historicalCache);

    AHistoricalCache<?> getParent();

    <T> IHistoricalCacheQuery<T> newQuery(
            de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods<T> queryCore);

    boolean isAlreadyAdjustingKey();

}
