package de.invesdwin.util.concurrent.pool;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import de.invesdwin.util.collections.loadingcache.historical.AHistoricalCache;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;
import de.invesdwin.util.concurrent.loop.SynchronizedLoopInterruptedCheck;
import de.invesdwin.util.concurrent.reference.IReference;
import de.invesdwin.util.concurrent.reference.ImmutableReference;
import de.invesdwin.util.concurrent.reference.ImmutableWeakReference;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGenerators;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class MemoryLimit {

    public static final int DEFAULT_CLEAR_CACHE_MIN_COUNT = 2;
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
    private static final ILock CLEAR_CACHE_SWEEP_LOCK = Locks
            .newReentrantLock(MemoryLimit.class.getSimpleName() + "clearCacheSweepLock");
    private static volatile boolean prevMemoryLimitReached = false;
    private static volatile double prevFreeMemoryRate = getFreeMemoryRate();
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MemoryLimit.class);
    private static final ConcurrentMap<Integer, AMemoryLimitClearable<?>> IDENTITY_CLEARABLE;
    private static long lastClearCacheSweepNanos = System.nanoTime();

    static {
        IDENTITY_CLEARABLE = Caffeine.newBuilder()
                .maximumSize(10_000)
                .<Integer, AMemoryLimitClearable<?>> build()
                .asMap();
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
        final long maxMemory = runtime.maxMemory();
        final long freeMemory = runtime.freeMemory();
        final long totalMemory = runtime.totalMemory();
        final long unallocatedMemory = maxMemory - totalMemory;
        final long freeAndUnallocatedMemory = freeMemory + unallocatedMemory;
        final double freeMemoryRate = Doubles.divide(freeAndUnallocatedMemory, maxMemory);
        return freeMemoryRate;
    }

    public static void maybeClearCacheSweep() {
        if (isMemoryLimitReached()) {
            maybeClearCacheSweepUnchecked();
        }
    }

    @SuppressWarnings("rawtypes")
    public static boolean maybeClearCacheSweepUnchecked() {
        if (!CLEAR_CACHE_SWEEP_LOCK.tryLock()) {
            return false;
        }
        try {
            if (CLEAR_CACHE_INTERVAL.isGreaterThanNanos(System.nanoTime() - lastClearCacheSweepNanos)) {
                return false;
            }
            if (IDENTITY_CLEARABLE.isEmpty()) {
                return false;
            }
            boolean cleared = false;
            final AMemoryLimitClearable[] clearables = IDENTITY_CLEARABLE.values()
                    .toArray(AMemoryLimitClearable.EMPTY_ARRAY);
            //go with 20% of caches so that we recover within about 5 minutes
            final int countRandomClears = Integers.max(10, clearables.length / 5);
            for (int i = 0; i < clearables.length && i < countRandomClears; i++) {
                final IRandomGenerator random = PseudoRandomGenerators.getThreadLocalPseudoRandom();
                final int randomIndex = random.nextInt(0, clearables.length);
                final AMemoryLimitClearable clearable = clearables[randomIndex];
                if (clearable != null && clearable.maybeClear()) {
                    clearable.evict();
                    cleared = true;
                }
                clearables[randomIndex] = null;
            }
            lastClearCacheSweepNanos = System.nanoTime();
            return cleared;
        } finally {
            CLEAR_CACHE_SWEEP_LOCK.unlock();
        }
    }

    public static void maybeClearCache(final Object holder, final String name, final Cache<?, ?> cache,
            final Lock lock) {
        if (isMemoryLimitReached()) {
            maybeClearCacheUnchecked(holder, name, cache, lock);
        }
    }

    public static boolean maybeClearCacheUnchecked(final Object holder, final String name, final Cache<?, ?> cache,
            final Lock lock) {
        return maybeClearCacheUnchecked(holder, name, cache, lock, DEFAULT_CLEAR_CACHE_MIN_COUNT);
    }

    public static boolean maybeClearCacheUnchecked(final Object holder, final String name, final Cache<?, ?> cache,
            final Lock lock, final int clearCacheMinCount) {
        boolean cleared = false;
        if (cache.estimatedSize() >= clearCacheMinCount) {
            if (lock.tryLock()) {
                try {
                    if (cache.estimatedSize() >= clearCacheMinCount) {
                        final int holderIdentity = System.identityHashCode(holder);
                        AMemoryLimitClearable<?> lastClear = IDENTITY_CLEARABLE.get(holderIdentity);
                        if (lastClear == null
                                || lastClear.shouldReplace(holder, name, cache, lock, clearCacheMinCount)) {
                            lastClear = new CacheMemoryLimitClearable(holderIdentity, holder, name, cache, lock,
                                    clearCacheMinCount);
                            IDENTITY_CLEARABLE.put(holderIdentity, lastClear);
                        }
                        if (lastClear.maybeClear()) {
                            cleared = true;
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        if (maybeClearCacheSweepUnchecked()) {
            cleared = true;
        }
        return cleared;
    }

    public static void maybeClearCache(final Object holder, final String name, final AsyncCache<?, ?> cache,
            final Lock lock) {
        if (isMemoryLimitReached()) {
            maybeClearCacheUnchecked(holder, name, cache, lock);
        }
    }

    public static boolean maybeClearCacheUnchecked(final Object holder, final String name, final AsyncCache<?, ?> cache,
            final Lock lock) {
        return maybeClearCacheUnchecked(holder, name, cache, lock, DEFAULT_CLEAR_CACHE_MIN_COUNT);
    }

    public static boolean maybeClearCacheUnchecked(final Object holder, final String name, final AsyncCache<?, ?> cache,
            final Lock lock, final int clearCacheMinCount) {
        boolean cleared = false;
        if (cache.asMap().size() >= clearCacheMinCount) {
            if (lock.tryLock()) {
                try {
                    if (cache.asMap().size() >= clearCacheMinCount) {
                        final int holderIdentity = System.identityHashCode(holder);
                        AMemoryLimitClearable<?> lastClear = IDENTITY_CLEARABLE.get(holderIdentity);
                        if (lastClear == null
                                || lastClear.shouldReplace(holder, name, cache, lock, clearCacheMinCount)) {
                            lastClear = new AsyncCacheMemoryLimitClearable(holderIdentity, holder, name, cache, lock,
                                    clearCacheMinCount);
                            IDENTITY_CLEARABLE.put(holderIdentity, lastClear);
                        }
                        if (lastClear.maybeClear()) {
                            cleared = true;
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        if (maybeClearCacheSweepUnchecked()) {
            cleared = true;
        }
        return cleared;
    }

    public static void maybeClearCache(final Object holder, final String name, final Map<?, ?> cache, final Lock lock) {
        if (isMemoryLimitReached()) {
            maybeClearCacheUnchecked(holder, name, cache, lock);
        }
    }

    public static boolean maybeClearCacheUnchecked(final Object holder, final String name, final Map<?, ?> cache,
            final Lock lock) {
        return maybeClearCacheUnchecked(holder, name, cache, lock, DEFAULT_CLEAR_CACHE_MIN_COUNT);
    }

    public static boolean maybeClearCacheUnchecked(final Object holder, final String name, final Map<?, ?> cache,
            final Lock lock, final int clearCacheMinCount) {
        boolean cleared = false;
        if (cache.size() >= clearCacheMinCount) {
            if (lock.tryLock()) {
                try {
                    if (cache.size() >= clearCacheMinCount) {
                        final int holderIdentity = System.identityHashCode(holder);
                        AMemoryLimitClearable<?> lastClear = IDENTITY_CLEARABLE.get(holderIdentity);
                        if (lastClear == null
                                || lastClear.shouldReplace(holder, name, cache, lock, clearCacheMinCount)) {
                            lastClear = new MapMemoryLimitClearable(holderIdentity, holder, name, cache, lock,
                                    clearCacheMinCount);
                            IDENTITY_CLEARABLE.put(holderIdentity, lastClear);
                        }
                        if (lastClear.maybeClear()) {
                            cleared = true;
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        if (maybeClearCacheSweepUnchecked()) {
            cleared = true;
        }
        return cleared;
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

    public static boolean maybeClearCache(final Object holder, final String name, final Map<?, ?> cache) {
        if (isMemoryLimitReached()) {
            return maybeClearCacheUnchecked(holder, name, cache);
        } else {
            return false;
        }
    }

    public static boolean maybeClearCacheUnchecked(final Object holder, final String name, final Map<?, ?> cache) {
        return maybeClearCacheUnchecked(holder, name, cache, DEFAULT_CLEAR_CACHE_MIN_COUNT);
    }

    public static boolean maybeClearCacheUnchecked(final Object holder, final String name, final Map<?, ?> cache,
            final int clearCacheMinCount) {
        boolean cleared = false;
        if (cache.size() >= clearCacheMinCount) {
            synchronized (cache) {
                if (cache.size() >= clearCacheMinCount) {
                    final int holderIdentity = System.identityHashCode(holder);
                    AMemoryLimitClearable<?> lastClear = IDENTITY_CLEARABLE.get(holderIdentity);
                    if (lastClear == null || lastClear.shouldReplace(holder, name, cache, null, clearCacheMinCount)) {
                        lastClear = new MapMemoryLimitClearable(holderIdentity, holder, name, cache, null,
                                clearCacheMinCount);
                        IDENTITY_CLEARABLE.put(holderIdentity, lastClear);
                    }
                    if (lastClear.maybeClear()) {
                        cleared = true;
                    }
                }
            }
        }
        if (maybeClearCacheSweepUnchecked()) {
            cleared = true;
        }
        return cleared;
    }

    private abstract static class AMemoryLimitClearable<V> {

        @SuppressWarnings("rawtypes")
        public static final AMemoryLimitClearable[] EMPTY_ARRAY = new AMemoryLimitClearable[0];

        private final int holderIdentity;
        private final WeakReference<Object> holderRef;
        private final WeakReference<V> cacheRef;
        private volatile String name;
        private final IReference<Lock> lockRef;
        private volatile int clearCacheMinCount;
        private volatile long lastClearedNanos = Instant.DUMMY_NANOS;

        protected AMemoryLimitClearable(final Integer holderIdentity, final Object holder, final String name,
                final V cache, final Lock lock, final int clearCacheMinCount) {
            this.holderIdentity = holderIdentity;
            this.holderRef = new WeakReference<>(holder);
            this.name = name;
            this.cacheRef = new WeakReference<>(cache);
            if (lock != null) {
                this.lockRef = new ImmutableWeakReference<>(lock);
            } else {
                this.lockRef = ImmutableReference.of(
                        Locks.newReentrantLock(getClass().getSimpleName() + ": " + holderIdentity + "[" + name + "]"));
            }
            this.clearCacheMinCount = clearCacheMinCount;
        }

        public boolean shouldReplace(final Object holder, final String name, final Object cache, final Lock lock,
                final int clearCacheMinCount) {
            if (holder != holderRef.get()) {
                return true;
            }
            if (cache != cacheRef.get()) {
                return true;
            }
            if ((lock == null) != (lockRef == null)) {
                return true;
            }
            if (lock != lockRef.get()) {
                return true;
            }
            this.name = name;
            this.clearCacheMinCount = clearCacheMinCount;
            return false;
        }

        public boolean maybeClear() {
            if (CLEAR_CACHE_INTERVAL.isGreaterThanNanos(System.nanoTime() - lastClearedNanos)) {
                return false;
            }
            final Lock lock;
            if (lockRef != null) {
                lock = lockRef.get();
                if (lock == null) {
                    evict();
                    return false;
                }
                if (!lock.tryLock()) {
                    return false;
                }
            } else {
                lock = DisabledLock.INSTANCE;
            }
            try {
                final Object holder = holderRef.get();
                if (holder == null) {
                    evict();
                    return false;
                }
                final V cache = cacheRef.get();
                if (cache == null) {
                    evict();
                    return false;
                }
                final long size = internalSize(cache);
                if (size < clearCacheMinCount) {
                    return false;
                }
                logWarning(holder, name, size);
                internalClear(cache);
                lastClearedNanos = System.nanoTime();
                return true;
            } finally {
                lock.unlock();
            }
        }

        public void evict() {
            IDENTITY_CLEARABLE.remove(holderIdentity);
        }

        public abstract long internalSize(V cache);

        public abstract void internalClear(V cache);
    }

    private static final class CacheMemoryLimitClearable extends AMemoryLimitClearable<Cache<?, ?>> {

        private CacheMemoryLimitClearable(final int holderIdentity, final Object holder, final String name,
                final Cache<?, ?> cache, final Lock lock, final int clearCacheMinCount) {
            super(holderIdentity, holder, name, cache, lock, clearCacheMinCount);
        }

        @Override
        public long internalSize(final Cache<?, ?> cache) {
            return cache.estimatedSize();
        }

        @Override
        public void internalClear(final Cache<?, ?> cache) {
            cache.asMap().clear();
        }

    }

    private static final class AsyncCacheMemoryLimitClearable extends AMemoryLimitClearable<AsyncCache<?, ?>> {

        private AsyncCacheMemoryLimitClearable(final int holderIdentity, final Object holder, final String name,
                final AsyncCache<?, ?> cache, final Lock lock, final int clearCacheMinCount) {
            super(holderIdentity, holder, name, cache, lock, clearCacheMinCount);
        }

        @Override
        public long internalSize(final AsyncCache<?, ?> cache) {
            return cache.asMap().size();
        }

        @Override
        public void internalClear(final AsyncCache<?, ?> cache) {
            cache.asMap().clear();
        }

    }

    private static final class MapMemoryLimitClearable extends AMemoryLimitClearable<Map<?, ?>> {

        private MapMemoryLimitClearable(final int holderIdentity, final Object holder, final String name,
                final Map<?, ?> cache, final Lock lock, final int clearCacheMinCount) {
            super(holderIdentity, holder, name, cache, lock, clearCacheMinCount);
        }

        @Override
        public long internalSize(final Map<?, ?> cache) {
            return cache.size();
        }

        @Override
        public void internalClear(final Map<?, ?> cache) {
            cache.clear();
        }

    }

}
