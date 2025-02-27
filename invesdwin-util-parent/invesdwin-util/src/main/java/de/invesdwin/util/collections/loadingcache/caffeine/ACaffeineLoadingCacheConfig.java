package de.invesdwin.util.collections.loadingcache.caffeine;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.eviction.EvictionMode;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ALoadingCacheConfig;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public abstract class ACaffeineLoadingCacheConfig<K, V> extends ALoadingCacheConfig<K, V> {

    public static final boolean DEFAULT_HIGH_CONCURRENCY = true;
    public static final boolean DEFAULT_THREAD_SAFE = true;

    public static final Duration DEFAULT_EXPIRE_AFTER_ACCESS = null;
    public static final Duration DEFAULT_EXPIRE_AFTER_WRITE = null;
    public static final Duration DEFAULT_REFRESH_AFTER_WRITE = null;
    public static final Boolean DEFAULT_WEAK_KEYS = null;
    public static final Boolean DEFAULT_WEAK_VALUES = null;
    public static final Boolean DEFAULT_SOFT_VALUES = null;

    private Duration expireAfterAccessOverride;
    private Duration expireAfterWriteOverride;
    private Duration refreshAfterWriteOverride;
    private Boolean weakKeysOverride;
    private Boolean weakValuesOverride;
    private Boolean softValuesOverride;

    public void setExpireAfterAccess(final Duration expireAfterAccessOverride) {
        this.expireAfterAccessOverride = expireAfterAccessOverride;
    }

    @Override
    protected final boolean isHighConcurrency() {
        return DEFAULT_HIGH_CONCURRENCY;
    }

    @Override
    protected final boolean isThreadSafe() {
        return DEFAULT_THREAD_SAFE;
    }

    public Duration getExpireAfterAccess() {
        return DEFAULT_EXPIRE_AFTER_ACCESS;
    }

    public void setExpireAfterWrite(final Duration expireAfterWriteOverride) {
        this.expireAfterWriteOverride = expireAfterWriteOverride;
    }

    public Duration getExpireAfterWrite() {
        return DEFAULT_EXPIRE_AFTER_WRITE;
    }

    public void setRefreshAfterWrite(final Duration refreshAfterWriteOverride) {
        this.refreshAfterWriteOverride = refreshAfterWriteOverride;
    }

    public Duration getRefreshAfterWrite() {
        return DEFAULT_REFRESH_AFTER_WRITE;
    }

    public void setWeakKeysOverride(final Boolean weakKeysOverride) {
        this.weakKeysOverride = weakKeysOverride;
    }

    public Boolean getWeakKeys() {
        return DEFAULT_WEAK_KEYS;
    }

    public void setWeakValuesOverride(final Boolean weakValuesOverride) {
        this.weakValuesOverride = weakValuesOverride;
    }

    public Boolean getWeakValues() {
        return DEFAULT_WEAK_VALUES;
    }

    public void setSoftValuesOverride(final Boolean softValuesOverride) {
        this.softValuesOverride = softValuesOverride;
    }

    public Boolean getSoftValues() {
        return DEFAULT_SOFT_VALUES;
    }

    @Override
    public ALoadingCache<K, V> newInstance() {
        final Integer initialMaximumSize = determineInitialMaximumSize();
        final boolean highConcurrency = determineHighConcurrency();
        final boolean threadSafe = determineThreadSafe();
        final EvictionMode evictionMode = determineEvictionMode();
        final boolean preventRecursiveLoad = determinePreventRecursiveLoad();
        final Duration expireAfterAccess = determineExpireAfterAccess();
        final Duration expireAfterWrite = determineExpireAfterWrite();
        final Duration refreshAfterWrite = determineRefreshAfterWrite();
        final Boolean weakKeys = determineWeakKeys();
        final Boolean weakValues = determineWeakValues();
        final Boolean softValues = determineSoftValues();
        return new ACaffeineLoadingCache<K, V>() {

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
            protected Duration getExpireAfterAccess() {
                return expireAfterAccess;
            }

            @Override
            protected Duration getExpireAfterWrite() {
                return expireAfterWrite;
            }

            @Override
            protected Duration getRefreshAfterWrite() {
                return refreshAfterWrite;
            }

            @Override
            protected Boolean getWeakKeys() {
                return weakKeys;
            }

            @Override
            protected Boolean getWeakValues() {
                return weakValues;
            }

            @Override
            protected Boolean getSoftValues() {
                return softValues;
            }

            @Override
            protected V loadValue(final K key) {
                return ACaffeineLoadingCacheConfig.this.loadValue(key);
            }

        };
    }

    protected Boolean determineSoftValues() {
        final Boolean softValues;
        if (softValuesOverride != null) {
            softValues = softValuesOverride;
        } else {
            softValues = getSoftValues();
        }
        return softValues;
    }

    protected Boolean determineWeakValues() {
        final Boolean weakValues;
        if (weakValuesOverride != null) {
            weakValues = weakValuesOverride;
        } else {
            weakValues = getWeakValues();
        }
        return weakValues;
    }

    protected Boolean determineWeakKeys() {
        final Boolean weakKeys;
        if (weakKeysOverride != null) {
            weakKeys = weakKeysOverride;
        } else {
            weakKeys = getWeakKeys();
        }
        return weakKeys;
    }

    protected Duration determineRefreshAfterWrite() {
        final Duration refreshAfterWrite;
        if (refreshAfterWriteOverride != null) {
            refreshAfterWrite = refreshAfterWriteOverride;
        } else {
            refreshAfterWrite = getRefreshAfterWrite();
        }
        return refreshAfterWrite;
    }

    protected Duration determineExpireAfterWrite() {
        final Duration expireAfterWrite;
        if (expireAfterWriteOverride != null) {
            expireAfterWrite = expireAfterWriteOverride;
        } else {
            expireAfterWrite = getExpireAfterWrite();
        }
        return expireAfterWrite;
    }

    protected Duration determineExpireAfterAccess() {
        final Duration expireAfterAccess;
        if (expireAfterAccessOverride != null) {
            expireAfterAccess = expireAfterAccessOverride;
        } else {
            expireAfterAccess = getExpireAfterAccess();
        }
        return expireAfterAccess;
    }

}
