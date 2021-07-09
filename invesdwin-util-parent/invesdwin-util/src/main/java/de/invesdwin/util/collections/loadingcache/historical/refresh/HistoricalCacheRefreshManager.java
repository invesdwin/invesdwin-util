package de.invesdwin.util.collections.loadingcache.historical.refresh;

import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

/**
 * WARNING: This class only exists for testing and debugging reasons since it can negatively impact the overall cache
 * performance. If you have problems with new keys not properly arriving after a data update, you should rather look at
 * pushing/pulling adjust key providers in your historical caches. Also try to set an appropriate instance in all of
 * your time sensitive historical caches. It is also a good approach to reuse adjust key providers in dependant
 * historical caches from a parent historical cache.
 * 
 * If you still think you need to use this, then you should know what you are doing and call this only after actual new
 * data has arrived. This might be legitimate when operating a scheduler updater that should update data in the whole
 * process.
 */
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

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(HistoricalCacheRefreshManager.class);

    private static volatile FDate lastRefresh = new FDate();
    private static volatile long lastRefreshMillis = lastRefresh.millisValue();
    @GuardedBy("HistoricalCacheRefreshManager.class")
    private static ScheduledExecutorService executor;

    private HistoricalCacheRefreshManager() {
    }

    public static FDate getLastRefresh() {
        return lastRefresh;
    }

    public static long getLastRefreshMillis() {
        return lastRefreshMillis;
    }

    /**
     * Try every hour if new data is in the cache. Queries may also call webservices.
     * 
     * Calling this manually makes the caches refresh on the next call to get.
     */
    public static synchronized void forceRefresh() {
        //CHECKSTYLE:OFF
        LOG.warn("Forcing refresh on historical caches");
        //CHECKSTYLE:ON
        lastRefresh = new FDate();
        lastRefreshMillis = lastRefresh.millisValue();
    }

    public static boolean maybeRefresh() {
        return maybeRefresh(DEFAULT_REFRESH_INTERVAL);
    }

    public static boolean maybeRefresh(final Duration refreshInterval) {
        if (new Duration(lastRefresh).isGreaterThanOrEqualTo(refreshInterval)
                || !FDates.isSameJulianDay(lastRefresh, new FDate())) {
            forceRefresh();
            return true;
        }
        return false;
    }

    public static synchronized boolean startRefreshScheduler(final Duration refreshInterval,
            final ScheduledExecutorService useExecutor) {
        if (executor == null) {
            //CHECKSTYLE:OFF
            LOG.warn("Starting refresh scheduler with interval: {}", refreshInterval);
            //CHECKSTYLE:ON
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
