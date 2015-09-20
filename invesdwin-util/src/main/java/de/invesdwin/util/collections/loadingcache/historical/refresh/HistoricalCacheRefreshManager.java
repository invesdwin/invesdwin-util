package de.invesdwin.util.collections.loadingcache.historical.refresh;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedScheduledExecutorService;
import de.invesdwin.util.time.Duration;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public final class HistoricalCacheRefreshManager {

    /**
     * The system should ensure that at least every hour the caches are refreshed (e.g. by running the scheduler)
     */
    public static final long DEFAULT_REFRESH_INTERVAL_MILLIS = 1 * Duration.MINUTES_IN_HOUR
            * Duration.SECONDS_IN_MINUTE * Duration.MILLISECONDS_IN_SECOND;
    public static final Duration DEFAULT_REFRESH_INTERVAL = new Duration(DEFAULT_REFRESH_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS);

    public static final HistoricalCacheRefreshManager INSTANCE = new HistoricalCacheRefreshManager();

    private static volatile FDate lastRefresh = new FDate();
    @GuardedBy("this.class")
    private static WrappedScheduledExecutorService executor;

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

    public static boolean maybeRefresh() {
        return maybeRefresh(DEFAULT_REFRESH_INTERVAL);
    }

    public static boolean maybeRefresh(final Duration refreshInterval) {
        if (new Duration(lastRefresh).isGreaterThanOrEqualTo(refreshInterval)) {
            refresh();
            return true;
        }
        return false;
    }

    public static synchronized boolean startRefreshScheduler(final Duration refreshInterval) {
        if (executor == null) {
            executor = Executors.newScheduledThreadPool(HistoricalCacheRefreshManager.class.getSimpleName(), 1);
            final long period = refreshInterval.longValue(TimeUnit.MILLISECONDS);
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    maybeRefresh(refreshInterval);
                }
            }, period, period, TimeUnit.MILLISECONDS);
            return true;
        }
        return false;
    }

    public static boolean startRefreshScheduler() {
        return startRefreshScheduler(DEFAULT_REFRESH_INTERVAL);
    }

    public static synchronized boolean isRefreshSchedulerRunning() {
        return executor != null;
    }

}
