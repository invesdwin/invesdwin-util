package de.invesdwin.util.collections.loadingcache;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.eviction.EvictionMode;

@NotThreadSafe
public abstract class ALoadingCacheConfig<K, V> {

    public static final Integer DEFAULT_INITIAL_MAXIMUM_SIZE = null;
    public static final boolean DEFAULT_HIGH_CONCURRENCY = false;
    public static final boolean DEFAULT_THREAD_SAFE = true;
    public static final EvictionMode DEFAULT_EVICTION_MODE = EvictionMode.LeastRecentlyUsed;
    public static final boolean DEFAULT_PREVENT_RECURSIVE_LOAD = false;

    private boolean initialMaximumSizeOverrideEnabled = false;
    private Integer initialMaximumSizeOverride;
    private Boolean highConcurrencyOverride;
    private Boolean threadSafeOverride;
    private EvictionMode evictionModeOverride;
    private Boolean preventRecursiveLoadOverride;

    /**
     * default unlimited size
     */
    protected Integer getInitialMaximumSize() {
        return DEFAULT_INITIAL_MAXIMUM_SIZE;
    }

    public void setInitialMaximumSizeOverride(final Integer initialMaximumSizeOverride) {
        initialMaximumSizeOverrideEnabled = true;
        this.initialMaximumSizeOverride = initialMaximumSizeOverride;
    }

    /**
     * default is false, since this comes at a cost
     * 
     * When true this will make sure that per key the loadValue function called exactly only once.
     */
    protected boolean isHighConcurrency() {
        return DEFAULT_HIGH_CONCURRENCY;
    }

    public void setHighConcurrencyOverride(final boolean highConcurrencyOverride) {
        this.highConcurrencyOverride = highConcurrencyOverride;
    }

    /**
     * If this is false, no synchronization will occur (highConcurrency=true will override this)
     */
    protected boolean isThreadSafe() {
        return DEFAULT_THREAD_SAFE;
    }

    public void setThreadSafeOverride(final boolean threadSafeOverride) {
        this.threadSafeOverride = threadSafeOverride;
    }

    /**
     * default is least recently used, you might consider to evict the least recently added element
     */
    protected EvictionMode getEvictionMode() {
        return DEFAULT_EVICTION_MODE;
    }

    public void setEvictionModeOverride(final EvictionMode evictionModeOverride) {
        this.evictionModeOverride = evictionModeOverride;
    }

    /**
     * default is false, since this comes at a cost
     */
    protected boolean isPreventRecursiveLoad() {
        return DEFAULT_PREVENT_RECURSIVE_LOAD;
    }

    protected abstract V loadValue(K key);

    public ALoadingCache<K, V> newInstance() {
        final Integer initialMaximumSize;
        if (initialMaximumSizeOverrideEnabled) {
            initialMaximumSize = initialMaximumSizeOverride;
        } else {
            initialMaximumSize = getInitialMaximumSize();
        }
        final boolean highConcurrency;
        if (highConcurrencyOverride != null) {
            highConcurrency = highConcurrencyOverride;
        } else {
            highConcurrency = isHighConcurrency();
        }
        final boolean threadSafe;
        if (threadSafeOverride != null) {
            threadSafe = threadSafeOverride;
        } else {
            threadSafe = isThreadSafe();
        }
        final EvictionMode evictionMode;
        if (evictionModeOverride != null) {
            evictionMode = evictionModeOverride;
        } else {
            evictionMode = getEvictionMode();
        }
        final boolean preventRecursiveLoad;
        if (preventRecursiveLoadOverride != null) {
            preventRecursiveLoad = preventRecursiveLoadOverride;
        } else {
            preventRecursiveLoad = isPreventRecursiveLoad();
        }
        return new ALoadingCache<K, V>() {

            @Override
            protected Integer getInitialMaximumSize() {
                return initialMaximumSize;
            }

            @Override
            protected boolean isHighConcurrency() {
                return highConcurrency;
            }

            @Override
            protected boolean isThreadSafe() {
                return threadSafe;
            }

            @Override
            protected EvictionMode getEvictionMode() {
                return evictionMode;
            }

            @Override
            protected boolean isPreventRecursiveLoad() {
                return preventRecursiveLoad;
            }

            @Override
            protected V loadValue(final K key) {
                return ALoadingCacheConfig.this.loadValue(key);
            }

        };
    }

}
