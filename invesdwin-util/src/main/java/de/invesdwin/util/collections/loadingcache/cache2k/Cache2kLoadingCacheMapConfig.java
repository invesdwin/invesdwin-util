package de.invesdwin.util.collections.loadingcache.cache2k;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.integration.CacheLoader;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.cache2k.internal.WrapperCache;
import de.invesdwin.util.collections.loadingcache.cache2k.internal.WrapperLoadingCacheMap;
import de.invesdwin.util.collections.loadingcache.cache2k.internal.WrapperRemovalListener;
import de.invesdwin.util.collections.loadingcache.guava.IRemovalListener;
import de.invesdwin.util.time.duration.Duration;

@SuppressWarnings({ "unchecked", "rawtypes" })
@NotThreadSafe
public class Cache2kLoadingCacheMapConfig {

    private Long maximumSize;
    private Duration expireAfterWrite;
    private IRemovalListener removalListener;

    public Long getMaximumSize() {
        return maximumSize;
    }

    public Cache2kLoadingCacheMapConfig withMaximumSize(final Long maximumSize) {
        this.maximumSize = maximumSize;
        return this;
    }

    public Cache2kLoadingCacheMapConfig withMaximumSize(final Integer maximumSize) {
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

    public Cache2kLoadingCacheMapConfig withExpireAfterWrite(final Duration expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
        return this;
    }

    public IRemovalListener getRemovalListener() {
        return removalListener;
    }

    public Cache2kLoadingCacheMapConfig withRemovalListener(final IRemovalListener removalListener) {
        this.removalListener = removalListener;
        return this;
    }

    <K, V> Map<K, V> newMap(final ACache2kLoadingCacheMap<K, V> parent) {
        final Cache2kBuilder<K, V> builder = newCacheBuilder();
        final CacheLoader<K, V> loader = new CacheLoader<K, V>() {
            @Override
            public V load(final K key) throws Exception {
                final V value = parent.loadValue(key);
                return value;
            }
        };
        builder.loader(loader);
        final Cache<K, V> impl = builder.build();
        final Cache<K, V> delegate = new WrapperCache<K, V>(impl) {
            @Override
            protected boolean isPutAllowed(final K key, final V value) {
                return parent.isPutAllowed(key, value);
            };
        };
        return new WrapperLoadingCacheMap<K, V>(delegate);
    }

    private <K, V> Cache2kBuilder<K, V> newCacheBuilder() {
        final Cache2kBuilder<K, V> builder = Cache2kBuilder.forUnknownTypes();
        builder.eternal(true);
        builder.storeByReference(true);
        builder.permitNullValues(true);
        builder.disableStatistics(true);
        builder.disableLastModificationTime(true);
        //        builder.loaderExecutor(ImmediateEventExecutor.INSTANCE);
        if (maximumSize != null) {
            builder.entryCapacity(maximumSize);
        }
        if (expireAfterWrite != null) {
            builder.expireAfterWrite(expireAfterWrite.longValue(), expireAfterWrite.getTimeUnit().timeUnitValue());
        }
        if (removalListener != null) {
            Assertions.assertThat(builder.addListener(new WrapperRemovalListener<K, V>(removalListener))).isNotNull();
        }
        return builder;
    }

}
