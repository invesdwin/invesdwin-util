package de.invesdwin.util.collections.loadingcache.historical.key.internal;

import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.time.fdate.FDate;

public interface IHistoricalCacheShiftKeyProvider {

    FDate calculatePreviousKey(FDate key);

    FDate calculateNextKey(FDate key);

    void clear();

    ILoadingCache<FDate, FDate> getPreviousKeysCache();

    ILoadingCache<FDate, FDate> getNextKeysCache();

    AHistoricalCache<?> getParent();

}
