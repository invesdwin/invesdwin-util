package de.invesdwin.util.collections.loadingcache.caffeine;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.caffeine.internal.WrapperLoadingCache;
import de.invesdwin.util.collections.loadingcache.caffeine.internal.WrapperLoadingCacheMap;
import de.invesdwin.util.time.duration.Duration;

@SuppressWarnings({ "unchecked", "rawtypes" })
@NotThreadSafe
public class CaffeineLoadingCacheMapConfig {

    private Long maximumSize;
    private Duration expireAfterWrite;
    private Duration expireAfterAccess;
    private Boolean softValues;
    private Boolean weakKeys;
    private Boolean weakValues;
    private IRemovalListener removalListener;

    public Long getMaximumSize() {
        return maximumSize;
    }

    public CaffeineLoadingCacheMapConfig withMaximumSize(final Long maximumSize) {
        this.maximumSize = maximumSize;
        return this;
    }

    public CaffeineLoadingCacheMapConfig withMaximumSize(final Integer maximumSize) {
        if (this.maximumSize == null) {
            this.maximumSize = null;
        } else {
            this.maximumSize = maximumSize.longValue();
        }
        return this;
    }

    public Duration getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public CaffeineLoadingCacheMapConfig withExpireAfterWrite(final Duration expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
        return this;
    }

    public Duration getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public CaffeineLoadingCacheMapConfig withExpireAfterAccess(final Duration expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
        return this;
    }

    public CaffeineLoadingCacheMapConfig withSoftValues(final Boolean softValues) {
        this.softValues = softValues;
        return this;
    }

    public Boolean getSoftValues() {
        return softValues;
    }

    public CaffeineLoadingCacheMapConfig withWeakKeys(final Boolean weakKeys) {
        this.weakKeys = weakKeys;
        return this;
    }

    public Boolean getWeakKeys() {
        return weakKeys;
    }

    public CaffeineLoadingCacheMapConfig withWeakValues(final Boolean weakValues) {
        this.weakValues = weakValues;
        return this;
    }

    public Boolean getWeakValues() {
        return weakValues;
    }

    public IRemovalListener getRemovalListener() {
        return removalListener;
    }

    public CaffeineLoadingCacheMapConfig withRemovalListener(final IRemovalListener removalListener) {
        this.removalListener = removalListener;
        return this;
    }

    <K, V> Map<K, V> newMap(final ACaffeineLoadingCacheMap<K, V> parent) {
        final Caffeine<Object, Object> builder = newCacheBuilder();
        final LoadingCache<K, V> delegate = new WrapperLoadingCache<K, V>(builder.<K, V> build(new CacheLoader<K, V>() {
            @Override
            public V load(final K key) throws Exception {
                final V value = parent.loadValue(key);
                return value;
            }
        })) {
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
        if (maximumSize != null) {
            builder.maximumSize(maximumSize);
        }
        if (expireAfterAccess != null) {
            builder.expireAfterAccess(expireAfterAccess.longValue(), expireAfterAccess.getTimeUnit().timeUnitValue());
        }
        if (expireAfterWrite != null) {
            builder.expireAfterWrite(expireAfterWrite.longValue(), expireAfterWrite.getTimeUnit().timeUnitValue());
        }
        configureKeysAndValues(builder);
        if (removalListener != null) {
            Assertions.assertThat(builder.removalListener(new RemovalListener<K, V>() {
                private final IRemovalListener<K, V> delegate = removalListener;

                @Override
                public void onRemoval(final K key, final V value, final RemovalCause cause) {
                    delegate.onRemoval(key, value, cause);

                }
            })).isNotNull();
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
