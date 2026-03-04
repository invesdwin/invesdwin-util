package de.invesdwin.util.collections.loadingcache.guava.internal;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Optional;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;

/**
 * This is a workaround to make googles ComputingMap work with null values.
 * 
 * @see <a href="http://code.google.com/p/google-collections/issues/detail?id=166">Source</a>
 * 
 */
@ThreadSafe
public class OptionalValueWrapperLoadingCache<K, V> implements LoadingCache<K, V> {

    private final LoadingCache<K, Optional<V>> delegate;

    public OptionalValueWrapperLoadingCache(final LoadingCache<K, Optional<V>> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V getIfPresent(final Object key) {
        return maybeGet((K) key, delegate.getIfPresent(key));
    }

    @Override
    public V get(final K key, final Callable<? extends V> valueLoader) throws ExecutionException {
        return maybeGet(key, delegate.get(key, new Callable<Optional<V>>() {
            @SuppressWarnings("unchecked")
            @Override
            public Optional<V> call() throws Exception {
                return (Optional<V>) Optional.fromNullable(valueLoader.call());
            }
        }));
    }

    @Override
    public ImmutableMap<K, V> getAllPresent(final Iterable<?> keys) {
        final ImmutableMap<K, Optional<V>> allPresent = delegate.getAllPresent(keys);
        final Map<K, V> nonnullAllPresent = ILockCollectionFactory.getInstance(false).newMap();
        for (final Entry<K, Optional<V>> e : allPresent.entrySet()) {
            final V value = maybeGet(e.getKey(), e.getValue());
            if (value != null) {
                nonnullAllPresent.put(e.getKey(), value);
            }
        }
        return ImmutableMap.copyOf(nonnullAllPresent);
    }

    @Override
    public void put(final K key, final V value) {
        delegate.put(key, Optional.fromNullable(value));
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        final Map<K, Optional<V>> newMap = ILockCollectionFactory.getInstance(false).newMap(m.size());
        for (final Entry<? extends K, ? extends V> e : m.entrySet()) {
            final K key = e.getKey();
            final V value = e.getValue();
            newMap.put(key, Optional.fromNullable(value));
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
    public long size() {
        return delegate.size();
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
    public V get(final K key) throws ExecutionException {
        return maybeGet(key, delegate.get(key));
    }

    @Override
    public V getUnchecked(final K key) {
        return maybeGet(key, delegate.getUnchecked(key));
    }

    @Override
    public ImmutableMap<K, V> getAll(final Iterable<? extends K> keys) throws ExecutionException {
        final ImmutableMap<K, Optional<V>> all = delegate.getAll(keys);
        final Map<K, V> nonnullAll = ILockCollectionFactory.getInstance(false).newMap();
        for (final Entry<K, Optional<V>> e : all.entrySet()) {
            final V value = maybeGet(e.getKey(), e.getValue());
            if (value != null) {
                nonnullAll.put(e.getKey(), value);
            }
        }
        return ImmutableMap.copyOf(nonnullAll);
    }

    @Override
    public V apply(final K key) {
        return maybeGet(key, delegate.apply(key));
    }

    @Override
    public void refresh(final K key) {
        delegate.refresh(key);
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
        return new OptionalValueWrapperConcurrentMap<K, V>(delegate.asMap());
    }

    protected final V maybeGet(final K key, final Optional<V> value) {
        if (value != null) {
            final V v = value.orNull();
            if (!isPutAllowed(key, v)) {
                invalidate(key);
            }
            return v;
        } else {
            return (V) null;
        }
    }

    protected boolean isPutAllowed(final K key, final V value) {
        return true;
    }

}
