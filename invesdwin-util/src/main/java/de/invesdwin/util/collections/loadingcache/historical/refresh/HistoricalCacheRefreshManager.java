package de.invesdwin.util.collections.loadingcache.historical.refresh;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public final class HistoricalCacheRefreshManager {

    public static final HistoricalCacheRefreshManager INSTANCE = new HistoricalCacheRefreshManager();

    private static volatile FDate lastRefresh = new FDate();

    private HistoricalCacheRefreshManager() {}

    public static FDate getLastRefresh() {
        return lastRefresh;
    }

    /**
     * Try every 3 hours if new data is in the cache. Queries may also call webservices.
     * 
     * Calling this manually makes the caches refresh themselves on their next call to get().
     */
    public static void refresh() {
        lastRefresh = new FDate();
    }

}
