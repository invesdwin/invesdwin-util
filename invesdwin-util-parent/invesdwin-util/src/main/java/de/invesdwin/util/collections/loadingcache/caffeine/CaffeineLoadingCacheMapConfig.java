package de.invesdwin.util.collections.loadingcache.caffeine;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.BooleanUtils;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.ILoadingCacheMap;
import de.invesdwin.util.collections.loadingcache.caffeine.internal.WrapperLoadingCache;
import de.invesdwin.util.collections.loadingcache.caffeine.internal.WrapperLoadingCacheMap;
import de.invesdwin.util.collections.loadingcache.caffeine.internal.WrapperRemovalListener;
import de.invesdwin.util.collections.loadingcache.guava.IRemovalListener;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.time.duration.Duration;

@SuppressWarnings({ "unchecked", "rawtypes" })
@NotThreadSafe
public class CaffeineLoadingCacheMapConfig {

    public static final WrappedExecutorService DISABLED_EXECUTOR = Executors
            .newDisabledExecutor(CaffeineLoadingCacheMapConfig.class.getSimpleName() + "_DISABLED");
    private static final WrappedExecutorService RECURSIVE_EXECUTOR = Executors
            .newCachedThreadPool(CaffeineLoadingCacheMapConfig.class.getSimpleName() + "_RECURSIVE")
            .setDynamicThreadName(false);

    private Long maximumSize;
    private Duration refreshAfterWrite;
    private Duration expireAfterWrite;
    private Duration expireAfterAccess;
    private Boolean softValues;
    private Boolean weakKeys;
    private Boolean weakValues;
    private Boolean recursiveLoading;
    private IRemovalListener removalListener;

    public Long getMaximumSize() {
        return maximumSize;
    }

    public CaffeineLoadingCacheMapConfig setMaximumSize(final Long maximumSize) {
        this.maximumSize = maximumSize;
        return this;
    }

    public CaffeineLoadingCacheMapConfig setMaximumSize(final Integer maximumSize) {
        if (maximumSize == null) {
            this.maximumSize = null;
        } else {
            this.maximumSize = maximumSize.longValue();
        }
        return this;
    }

    public Duration getRefreshAfterWrite() {
        return refreshAfterWrite;
    }

    public CaffeineLoadingCacheMapConfig setRefreshAfterWrite(final Duration refreshAfterWrite) {
        this.refreshAfterWrite = refreshAfterWrite;
        return this;
    }

    public Duration getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public CaffeineLoadingCacheMapConfig setExpireAfterWrite(final Duration expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
        return this;
    }

    public Duration getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public CaffeineLoadingCacheMapConfig setExpireAfterAccess(final Duration expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
        return this;
    }

    public CaffeineLoadingCacheMapConfig setSoftValues(final Boolean softValues) {
        this.softValues = softValues;
        return this;
    }

    public Boolean getSoftValues() {
        return softValues;
    }

    public CaffeineLoadingCacheMapConfig setWeakKeys(final Boolean weakKeys) {
        this.weakKeys = weakKeys;
        return this;
    }

    public Boolean getWeakKeys() {
        return weakKeys;
    }

    public CaffeineLoadingCacheMapConfig setWeakValues(final Boolean weakValues) {
        this.weakValues = weakValues;
        return this;
    }

    public Boolean getWeakValues() {
        return weakValues;
    }

    public IRemovalListener getRemovalListener() {
        return removalListener;
    }

    public CaffeineLoadingCacheMapConfig setRemovalListener(final IRemovalListener removalListener) {
        this.removalListener = removalListener;
        return this;
    }

    public Boolean getRecursiveLoading() {
        return recursiveLoading;
    }

    public CaffeineLoadingCacheMapConfig setRecursiveLoading(final Boolean recursiveLoading) {
        this.recursiveLoading = recursiveLoading;
        return this;
    }

    <K, V> ILoadingCacheMap<K, V> newMap(final ACaffeineLoadingCacheMap<K, V> parent) {
        final Caffeine<Object, Object> builder = newCacheBuilder();
        final CacheLoader<K, V> loader = new CacheLoader<K, V>() {
            @Override
            public V load(final K key) throws Exception {
                final V value = parent.loadValue(key);
                return value;
            }
        };
        final LoadingCache<K, V> impl;
        if (BooleanUtils.isTrue(recursiveLoading)) {
            impl = builder.<K, V> buildAsync(loader).synchronous();
        } else {
            impl = builder.<K, V> build(loader);
        }
        final LoadingCache<K, V> delegate = new WrapperLoadingCache<K, V>(impl) {
            @Override
            protected boolean isPutAllowed(final K key, final V value) {
                return parent.isPutAllowed(key, value);
            };
        };
        return new WrapperLoadingCacheMap<K, V>(delegate);
    }

    @SuppressWarnings("null")
    private <K, V> Caffeine<Object, Object> newCacheBuilder() {
        final Caffeine<Object, Object> builder = Caffeine.newBuilder();
        if (BooleanUtils.isTrue(recursiveLoading)) {
            builder.executor(RECURSIVE_EXECUTOR);
        } else {
            builder.executor(DISABLED_EXECUTOR);
        }
        //System.out.println("TODO: reenable this as soon as possible");
        //        if (maximumSize != null) {
        //            builder.maximumSize(maximumSize);
        //        }
        if (expireAfterAccess != null) {
            builder.expireAfterAccess(expireAfterAccess.longValue(), expireAfterAccess.getTimeUnit().timeUnitValue());
        }
        if (expireAfterWrite != null) {
            builder.expireAfterWrite(expireAfterWrite.longValue(), expireAfterWrite.getTimeUnit().timeUnitValue());
        }
        if (refreshAfterWrite != null) {
            builder.refreshAfterWrite(refreshAfterWrite.longValue(), refreshAfterWrite.getTimeUnit().timeUnitValue());
        }
        configureKeysAndValues(builder);
        if (removalListener != null) {
            Assertions.assertThat(builder.removalListener(new WrapperRemovalListener<K, V>(removalListener)))
                    .isNotNull();
        }
        return builder;
    }

    private void configureKeysAndValues(final Caffeine<Object, Object> builder) {
        if (softValues != null && softValues) {
            builder.softValues();
        }
        if (weakKeys != null && weakKeys) {
            builder.weakKeys();
        }
        if (weakValues != null && weakValues) {
            builder.weakValues();
        }
    }

}
