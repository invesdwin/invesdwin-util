package de.invesdwin.util.collections.loadingcache.historical.key;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheAdjustKeyProvider {

    boolean isAdjustedKey(FDate key);

    FDate adjustKey(FDate key);

    FDate maybeAdjustKey(FDate key);

    FDate newAlreadyAdjustedKey(FDate key);

    void clear();

    FDate getHighestAllowedKey();

    boolean registerHistoricalCache(AHistoricalCache<?> historicalCache);

    AHistoricalCache<?> getParent();

    <T> IHistoricalCacheQuery<T> newQuery(
            de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods<T> queryCore);

    boolean isAlreadyAdjustingKey();

}
