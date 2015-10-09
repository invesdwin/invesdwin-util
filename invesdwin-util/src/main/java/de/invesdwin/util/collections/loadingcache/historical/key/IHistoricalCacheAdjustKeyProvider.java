package de.invesdwin.util.collections.loadingcache.historical.key;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheAdjustKeyProvider {

    FDate adjustKey(FDate key);

    void clear();

    FDate getHighestAllowedKey();

    boolean registerHistoricalCache(AHistoricalCache<?> historicalCache);

}
