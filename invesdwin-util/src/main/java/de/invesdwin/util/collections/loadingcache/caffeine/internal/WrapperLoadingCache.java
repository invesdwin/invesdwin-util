package de.invesdwin.util.collections.loadingcache.caffeine.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.common.collect.ImmutableMap;

/**
 * This is a workaround to make googles ComputingMap work with null values.
 * 
 * @see <a href="http://code.google.com/p/google-collections/issues/detail?id=166">Source</a>
 * 
 */
@ThreadSafe
public class WrapperLoadingCache<K, V> implements LoadingCache<K, V> {

    private final LoadingCache<K, V> delegate;

    public WrapperLoadingCache(final LoadingCache<K, V> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V getIfPresent(final Object key) {
        return maybeGet((K) key, delegate.getIfPresent(key));
    }

    @Override
    public V get(final K key, final Function<? super K, ? extends V> mappingFunction) {
        return maybeGet(key, delegate.get(key, new Function<K, V>() {
            @Override
            public V apply(final K t) {
                return mappingFunction.apply(t);
            }
        }));
    }

    @Override
    public ImmutableMap<K, V> getAllPresent(final Iterable<?> keys) {
        final Map<K, V> allPresent = delegate.getAllPresent(keys);
        final Map<K, V> nonnullAllPresent = new HashMap<K, V>();
        for (final Entry<K, V> e : allPresent.entrySet()) {
            final V value = maybeGet(e.getKey(), e.getValue());
            if (value != null) {
                nonnullAllPresent.put(e.getKey(), value);
            }
        }
        return ImmutableMap.copyOf(nonnullAllPresent);
    }

    @Override
    public void put(final K key, final V value) {
        delegate.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        final Map<K, V> newMap = new HashMap<K, V>();
        for (final Entry<? extends K, ? extends V> e : m.entrySet()) {
            final K key = e.getKey();
            final V value = e.getValue();
            newMap.put(key, value);
        }
        delegate.putAll(newMap);
    }

    @Override
    public void invalidate(final Object key) {
        delegate.invalidate(key);
    }

    @Override
    public void invalidateAll(final Iterable<?> keys) {
        delegate.invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        delegate.invalidateAll();
    }

    @Override
    public long estimatedSize() {
        return delegate.estimatedSize();
    }

    @Override
    public CacheStats stats() {
        return delegate.stats();
    }

    @Override
    public void cleanUp() {
        delegate.cleanUp();
    }

    @Override
    public V get(final K key) {
        return maybeGet(key, delegate.get(key));
    }

    @Override
    public ImmutableMap<K, V> getAll(final Iterable<? extends K> keys) {
        final Map<K, V> all = delegate.getAll(keys);
        final Map<K, V> nonnullAll = new HashMap<K, V>();
        for (final Entry<K, V> e : all.entrySet()) {
            final V value = maybeGet(e.getKey(), e.getValue());
            if (value != null) {
                nonnullAll.put(e.getKey(), value);
            }
        }
        return ImmutableMap.copyOf(nonnullAll);
    }

    @Override
    public void refresh(final K key) {
        delegate.refresh(key);
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
        return new WrapperConcurrentMap<K, V>(delegate.asMap());
    }

    protected final V maybeGet(final K key, final V value) {
        if (value != null) {
            final V v = value;
            if (!isPutAllowed(key, v)) {
                invalidate(key);
            }
            return v;
        } else {
            return null;
        }
    }

    protected boolean isPutAllowed(final K key, final V value) {
        return true;
    }

    @Override
    public Policy<K, V> policy() {
        return delegate.policy();
    }

}
