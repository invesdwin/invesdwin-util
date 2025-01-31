package de.invesdwin.util.concurrent.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.concurrent.loop.SynchronizedLoopInterruptedCheck;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.time.Instant;
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
    /**
     * Each cache should only be cleared once a minute. That way other caches are more likely to be cleared as well and
     * we don't get into a situation where the wrong cache is cleared the permanently without the memory recovering
     * properly. Also we give a change for the GC to try to clear things up between the one minute intervals.
     */
    private static final Duration CLEAR_CACHE_INTERVAL = Duration.ONE_MINUTE;
    private static final SynchronizedLoopInterruptedCheck MEMORY_LIMIT_REACHED_CHECK = new SynchronizedLoopInterruptedCheck(
            MEMORY_LIMIT_REACHED_CHECK_INTERVAL) {
        @Override
        protected boolean onInterval() throws InterruptedException {
            //noop
            return true;
        }
    };
    private static volatile boolean prevMemoryLimitReached = false;
    private static volatile double prevFreeMemoryRate = getFreeMemoryRate();
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MemoryLimit.class);
    private static final ConcurrentMap<Integer, Instant> IDENTITY_LASTCLEAR;

    static {
        IDENTITY_LASTCLEAR = Caffeine.newBuilder().maximumSize(10_000).<Integer, Instant> build().asMap();
    }

    private MemoryLimit() {}

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
        final double freeMemoryRate = Doubles.divide(runtime.freeMemory(), runtime.maxMemory());
        return freeMemoryRate;
    }

    public static void maybeClearCache(final Object holder, final String name, final Cache<?, ?> cache,
            final Lock lock) {
        if (isMemoryLimitReached()) {
            maybeClearCacheUnchecked(holder, name, cache, lock);
        }
    }

    public static void maybeClearCacheUnchecked(final Object holder, final String name, final Cache<?, ?> cache,
            final Lock lock) {
        if (cache.estimatedSize() >= MemoryLimit.CLEAR_CACHE_MIN_COUNT) {
            if (lock.tryLock()) {
                try {
                    if (cache.estimatedSize() >= MemoryLimit.CLEAR_CACHE_MIN_COUNT) {
                        final int cacheIdentity = System.identityHashCode(holder);
                        final Instant lastClear = IDENTITY_LASTCLEAR.get(cacheIdentity);
                        if (lastClear == null || lastClear.isGreaterThan(CLEAR_CACHE_INTERVAL)) {
                            logWarning(holder, name, cache.estimatedSize());
                            cache.asMap().clear();
                            IDENTITY_LASTCLEAR.put(cacheIdentity, new Instant());
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static void maybeClearCache(final Object holder, final String name, final AsyncCache<?, ?> cache,
            final Lock lock) {
        if (isMemoryLimitReached()) {
            maybeClearCacheUnchecked(holder, name, cache, lock);
        }
    }

    public static void maybeClearCacheUnchecked(final Object holder, final String name, final AsyncCache<?, ?> cache,
            final Lock lock) {
        if (cache.asMap().size() >= MemoryLimit.CLEAR_CACHE_MIN_COUNT) {
            if (lock.tryLock()) {
                try {
                    if (cache.asMap().size() >= MemoryLimit.CLEAR_CACHE_MIN_COUNT) {
                        final int cacheIdentity = System.identityHashCode(holder);
                        final Instant lastClear = IDENTITY_LASTCLEAR.get(cacheIdentity);
                        if (lastClear == null || lastClear.isGreaterThan(CLEAR_CACHE_INTERVAL)) {
                            logWarning(holder, name, cache.asMap().size());
                            cache.asMap().clear();
                            IDENTITY_LASTCLEAR.put(cacheIdentity, new Instant());
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static void maybeClearCache(final Object holder, final String name, final Map<?, ?> cache, final Lock lock) {
        if (isMemoryLimitReached()) {
            maybeClearCacheUnchecked(holder, name, cache, lock);
        }
    }

    public static void maybeClearCacheUnchecked(final Object holder, final String name, final Map<?, ?> cache,
            final Lock lock) {
        if (cache.size() >= MemoryLimit.CLEAR_CACHE_MIN_COUNT) {
            if (lock.tryLock()) {
                try {
                    if (cache.size() >= MemoryLimit.CLEAR_CACHE_MIN_COUNT) {
                        final int cacheIdentity = System.identityHashCode(holder);
                        final Instant lastClear = IDENTITY_LASTCLEAR.get(cacheIdentity);
                        if (lastClear == null || lastClear.isGreaterThan(CLEAR_CACHE_INTERVAL)) {
                            logWarning(holder, name, cache.size());
                            cache.clear();
                            IDENTITY_LASTCLEAR.put(cacheIdentity, new Instant());
                        }
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
                    final int cacheIdentity = System.identityHashCode(holder);
                    final Instant lastClear = IDENTITY_LASTCLEAR.get(cacheIdentity);
                    if (lastClear == null || lastClear.isGreaterThan(CLEAR_CACHE_INTERVAL)) {
                        logWarning(holder, name, cache.size());
                        cache.clear();
                        IDENTITY_LASTCLEAR.put(cacheIdentity, new Instant());
                    }
                }
            }
        }
    }

}
