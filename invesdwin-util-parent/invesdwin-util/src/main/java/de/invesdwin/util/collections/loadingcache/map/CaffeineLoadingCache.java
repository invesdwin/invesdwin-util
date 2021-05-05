package de.invesdwin.util.collections.loadingcache.map;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.collections.loadingcache.caffeine.ACaffeineLoadingCacheMap;
import de.invesdwin.util.collections.loadingcache.caffeine.CaffeineLoadingCacheMapConfig;

@ThreadSafe
public class CaffeineLoadingCache<K, V> implements ILoadingCache<K, V> {

    private final ACaffeineLoadingCacheMap<K, V> delegate = new ACaffeineLoadingCacheMap<K, V>() {
        @Override
        protected V loadValue(final K key) {
            return loadValue.apply(key);
        }

        @Override
        protected CaffeineLoadingCacheMapConfig newConfig() {
            return CaffeineLoadingCache.this.getConfig();
        }
    };
    private final Function<K, V> loadValue;
    private final Integer maximumSize;

    public CaffeineLoadingCache(final Function<K, V> loadValue, final Integer maximumSize) {
        this.loadValue = loadValue;
        this.maximumSize = maximumSize;
    }

    protected CaffeineLoadingCacheMapConfig getConfig() {
        return new CaffeineLoadingCacheMapConfig().withMaximumSize(maximumSize);
    }

    @Override
    public V get(final K key) {
        return delegate.get(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean containsKey(final K key) {
        return delegate.containsKey(key);
    }

    @Override
    public void remove(final K key) {
        delegate.remove(key);
    }

    @Override
    public void put(final K key, final V value) {
        delegate.put(key, value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Map<K, V> asMap() {
        return Collections.unmodifiableMap(delegate);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        //ignore
    }

    @Override
    public V getIfPresent(final K key) {
        return delegate.getIfPresent(key);
    }

    @Override
    public V computeIfAbsent(final K key, final Function<K, V> mappingFunction) {
        return delegate.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
