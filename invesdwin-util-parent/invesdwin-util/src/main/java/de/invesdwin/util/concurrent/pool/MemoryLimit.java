package de.invesdwin.util.concurrent.pool;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.LoadingCache;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.concurrent.loop.AtomicLoopInterruptedCheck;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class MemoryLimit {

    public static final int CLEAR_CACHE_MIN_COUNT = 2;
    public static final Percent FREE_MEMORY_LIMIT = new Percent(10, PercentScale.PERCENT);
    /**
     * If free memory is below 10%, clear the file buffer cache and load from file for one check period.
     */
    public static final double FREE_MEMORY_LIMIT_RATE = FREE_MEMORY_LIMIT.getRate();
    /**
     * Check each 100ms if we can use the cache again or should clear it again.
     */
    private static final Duration MEMORY_LIMIT_REACHED_CHECK_INTERVAL = new Duration(100, FTimeUnit.MILLISECONDS);
    private static final AtomicLoopInterruptedCheck MEMORY_LIMIT_REACHED_CHECK = new AtomicLoopInterruptedCheck(
            MEMORY_LIMIT_REACHED_CHECK_INTERVAL) {
        @Override
        protected void onInterval() throws InterruptedException {
            //noop
        }
    };
    private static volatile boolean prevMemoryLimitReached = false;
    private static volatile double prevFreeMemoryRate = getFreeMemoryRate();
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MemoryLimit.class);

    private MemoryLimit() {
    }

    public static boolean isMemoryLimitReached() {
        try {
            if (MEMORY_LIMIT_REACHED_CHECK.check()) {
                final double freeMemoryRate = getFreeMemoryRate();
                prevFreeMemoryRate = freeMemoryRate;
                prevMemoryLimitReached = freeMemoryRate < FREE_MEMORY_LIMIT_RATE;
            }
            return prevMemoryLimitReached;
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static double getFreeMemoryRate() {
        final Runtime runtime = Runtime.getRuntime();
        final double freeMemoryRate = Doubles.divide(runtime.freeMemory(), runtime.totalMemory());
        return freeMemoryRate;
    }

    public static void maybeClearCache(final Object holder, final String name, final LoadingCache<?, ?> cache,
            final Lock lock) {
        if (isMemoryLimitReached()) {
            maybeClearCacheUnchecked(holder, name, cache, lock);
        }
    }

    public static void maybeClearCacheUnchecked(final Object holder, final String name, final LoadingCache<?, ?> cache,
            final Lock lock) {
        if (cache.estimatedSize() >= MemoryLimit.CLEAR_CACHE_MIN_COUNT) {
            if (lock.tryLock()) {
                try {
                    if (cache.estimatedSize() >= MemoryLimit.CLEAR_CACHE_MIN_COUNT) {
                        logWarning(holder, name, cache.estimatedSize());
                        cache.asMap().clear();
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    private static void logWarning(final Object holder, final String name, final long size) {
        if (!AHistoricalCache.isDebugAutomaticReoptimization() || !LOGGER.isWarnEnabled()) {
            return;
        }
        final String holderStr;
        if (holder instanceof Class) {
            final Class<?> cHolder = (Class<?>) holder;
            holderStr = cHolder.getSimpleName();
        } else {
            holderStr = Strings.asString(holder);
        }
        //CHECKSTYLE:OFF
        LOGGER.warn("Clearing cache [{}.{}] with [{}] values because free memory limit is exceeded: {} < {}", holderStr,
                name, size, new Percent(prevFreeMemoryRate, PercentScale.RATE).toString(PercentScale.PERCENT),
                FREE_MEMORY_LIMIT);
        //CHECKSTYLE:ON
    }

    public static void maybeClearCache(final Object holder, final String name, final Map<?, ?> cache) {
        if (isMemoryLimitReached()) {
            maybeClearCacheUnchecked(holder, name, cache);
        }
    }

    public static void maybeClearCacheUnchecked(final Object holder, final String name, final Map<?, ?> cache) {
        if (cache.size() >= CLEAR_CACHE_MIN_COUNT) {
            synchronized (cache) {
                if (cache.size() >= CLEAR_CACHE_MIN_COUNT) {
                    logWarning(holder, name, cache.size());
                    cache.clear();
                }
            }
        }
    }

}