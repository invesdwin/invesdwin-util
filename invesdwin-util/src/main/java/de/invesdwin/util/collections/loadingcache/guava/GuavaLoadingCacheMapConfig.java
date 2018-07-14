package de.invesdwin.util.collections.loadingcache.guava;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.guava.internal.OptionalValueWrapperLoadingCache;
import de.invesdwin.util.collections.loadingcache.guava.internal.OptionalValueWrapperRemovalListener;
import de.invesdwin.util.collections.loadingcache.guava.internal.WrapperLoadingCacheMap;
import de.invesdwin.util.time.duration.Duration;

@SuppressWarnings({ "unchecked", "rawtypes" })
@NotThreadSafe
public class GuavaLoadingCacheMapConfig {

    /**
     * Scale concurrency with CPUs.
     */
    private Integer concurrencyLevel = Runtime.getRuntime().availableProcessors();
    private Long maximumSize;
    private Duration expireAfterWrite;
    private Duration expireAfterAccess;
    private Boolean softValues;
    private Boolean weakKeys;
    private Boolean weakValues;
    private IRemovalListener removalListener;

    public Integer getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public GuavaLoadingCacheMapConfig withConcurrencyLevel(final Integer concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    public Long getMaximumSize() {
        return maximumSize;
    }

    public GuavaLoadingCacheMapConfig withMaximumSize(final Long maximumSize) {
        this.maximumSize = maximumSize;
        return this;
    }

    public GuavaLoadingCacheMapConfig withMaximumSize(final Integer maximumSize) {
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

    public GuavaLoadingCacheMapConfig withExpireAfterWrite(final Duration expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
        return this;
    }

    public Duration getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public GuavaLoadingCacheMapConfig withExpireAfterAccess(final Duration expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
        return this;
    }

    public GuavaLoadingCacheMapConfig withSoftValues(final Boolean softValues) {
        this.softValues = softValues;
        return this;
    }

    public Boolean getSoftValues() {
        return softValues;
    }

    public GuavaLoadingCacheMapConfig withWeakKeys(final Boolean weakKeys) {
        this.weakKeys = weakKeys;
        return this;
    }

    public Boolean getWeakKeys() {
        return weakKeys;
    }

    public GuavaLoadingCacheMapConfig withWeakValues(final Boolean weakValues) {
        this.weakValues = weakValues;
        return this;
    }

    public Boolean getWeakValues() {
        return weakValues;
    }

    public IRemovalListener getRemovalListener() {
        return removalListener;
    }

    public GuavaLoadingCacheMapConfig withRemovalListener(final IRemovalListener removalListener) {
        this.removalListener = removalListener;
        return this;
    }

    <K, V> Map<K, V> newMap(final AGuavaLoadingCacheMap<K, V> parent) {
        final CacheBuilder<Object, Object> builder = newCacheBuilder();
        final LoadingCache<K, V> delegate = new OptionalValueWrapperLoadingCache<K, V>(
                builder.<K, Optional<V>> build(new CacheLoader<K, Optional<V>>() {
                    @Override
                    public Optional<V> load(final K key) throws Exception {
                        final V value = parent.loadValue(key);
                        return Optional.fromNullable(value);
                    }
                })) {
            @Override
            protected boolean isPutAllowed(final K key, final V value) {
                return parent.isPutAllowed(key, value);
            };
        };
        return new WrapperLoadingCacheMap<K, V>(delegate);
    }

    private <K, V> CacheBuilder<Object, Object> newCacheBuilder() {
        final CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
        if (maximumSize != null) {
            builder.maximumSize(maximumSize);
        }
        if (concurrencyLevel != null) {
            builder.concurrencyLevel(concurrencyLevel);
        }
        if (expireAfterAccess != null) {
            builder.expireAfterAccess(expireAfterAccess.longValue(), expireAfterAccess.getTimeUnit().timeUnitValue());
        }
        if (expireAfterWrite != null) {
            builder.expireAfterWrite(expireAfterWrite.longValue(), expireAfterWrite.getTimeUnit().timeUnitValue());
        }
        configureKeysAndValues(builder);
        if (removalListener != null) {
            Assertions
                    .assertThat(builder.removalListener(new OptionalValueWrapperRemovalListener<K, V>(removalListener)))
                    .isNotNull();
        }
        return builder;
    }

    private void configureKeysAndValues(final CacheBuilder<Object, Object> builder) {
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
