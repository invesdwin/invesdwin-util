package de.invesdwin.util.collections.loadingcache.historical.refresh;

import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDates;
import de.invesdwin.util.time.fdate.FTimeUnit;

@ThreadSafe
public final class HistoricalCacheRefreshManager {

    /**
     * The system should ensure that at least every hour the caches are refreshed (e.g. by running the scheduler)
     */
    public static final long DEFAULT_REFRESH_INTERVAL_MILLIS = 1 * FTimeUnit.MINUTES_IN_HOUR
            * FTimeUnit.SECONDS_IN_MINUTE * FTimeUnit.MILLISECONDS_IN_SECOND;
    public static final Duration DEFAULT_REFRESH_INTERVAL = new Duration(DEFAULT_REFRESH_INTERVAL_MILLIS,
            FTimeUnit.MILLISECONDS);

    public static final HistoricalCacheRefreshManager INSTANCE = new HistoricalCacheRefreshManager();

    private static volatile FDate lastRefresh = new FDate();
    @GuardedBy("this.class")
    private static ScheduledExecutorService executor;

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
        if (new Duration(lastRefresh).isGreaterThanOrEqualTo(refreshInterval)
                || !FDates.isSameDay(lastRefresh, new FDate())) {
            refresh();
            return true;
        }
        return false;
    }

    public static synchronized boolean startRefreshScheduler(final Duration refreshInterval,
            final ScheduledExecutorService useExecutor) {
        if (executor == null) {
            if (useExecutor != null) {
                executor = useExecutor;
            } else {
                executor = Executors.newScheduledThreadPool(HistoricalCacheRefreshManager.class.getSimpleName(), 1);
            }
            final long period = refreshInterval.longValue(FTimeUnit.MILLISECONDS);
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    maybeRefresh(refreshInterval);
                }
            }, period, period, FTimeUnit.MILLISECONDS.timeUnitValue());
            return true;
        }
        return false;
    }

    public static boolean startRefreshScheduler() {
        return startRefreshScheduler(DEFAULT_REFRESH_INTERVAL, null);
    }

    public static boolean startRefreshScheduler(final ScheduledExecutorService useExecutor) {
        return startRefreshScheduler(DEFAULT_REFRESH_INTERVAL, useExecutor);
    }

    public static synchronized boolean isRefreshSchedulerRunning() {
        return executor != null;
    }

}
