package de.invesdwin.util.concurrent.pool;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.LoadingCache;

import de.invesdwin.util.concurrent.loop.AtomicLoopInterruptedCheck;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class MemoryLimit {

    public static final int CLEAR_CACHE_MIN_COUNT = 10;
    /**
     * If free memory is below 10%, clear the file buffer cache and load from file for one check period.
     */
    private static final double FREE_MEMORY_LIMIT_REACHED_RATE = 0.1D;
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
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MemoryLimit.class);

    private MemoryLimit() {
    }

    public static boolean isMemoryLimitReached() {
        try {
            if (MEMORY_LIMIT_REACHED_CHECK.check()) {
                final Runtime runtime = Runtime.getRuntime();
                final double freeMemoryRate = Doubles.divide(runtime.freeMemory(), runtime.totalMemory());
                prevMemoryLimitReached = freeMemoryRate < FREE_MEMORY_LIMIT_REACHED_RATE;
            }
            return prevMemoryLimitReached;
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void maybeClearCache(final Object holder, final String name, final LoadingCache<?, ?> cache,
            final Lock lock) {
        if (isMemoryLimitReached()) {
            maybeClearCacheUnchecked(holder, name, cache, lock);
        }
    }

    public static void maybeClearCacheUnchecked(final Object holder, final String name, final LoadingCache<?, ?> cache,
            final Lock lock) {
        if (cache.estimatedSize() > MemoryLimit.CLEAR_CACHE_MIN_COUNT) {
            if (lock.tryLock()) {
                try {
                    if (cache.estimatedSize() > MemoryLimit.CLEAR_CACHE_MIN_COUNT) {
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
        final String holderStr;
        if (holder instanceof Class) {
            final Class<?> cHolder = (Class<?>) holder;
            holderStr = cHolder.getSimpleName();
        } else {
            holderStr = Strings.asString(holder);
        }
        //CHECKSTYLE:OFF
        LOGGER.warn("Clearing cache [{}.{}] with [{}] values because memory limit is exceeded.", holderStr, name, size);
        //CHECKSTYLE:ON
    }

    public static void maybeClearCache(final Object holder, final String name, final Map<?, ?> cache) {
        if (isMemoryLimitReached()) {
            maybeClearCacheUnchecked(holder, name, cache);
        }
    }

    public static void maybeClearCacheUnchecked(final Object holder, final String name, final Map<?, ?> cache) {
        if (cache.size() > CLEAR_CACHE_MIN_COUNT) {
            synchronized (cache) {
                if (cache.size() > CLEAR_CACHE_MIN_COUNT) {
                    logWarning(holder, name, cache.size());
                    cache.clear();
                }
            }
        }
    }

}
