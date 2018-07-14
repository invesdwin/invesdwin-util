package de.invesdwin.util.collections.loadingcache.cache2k.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.ThreadSafe;

import org.cache2k.Cache;
import org.cache2k.CacheEntry;
import org.cache2k.CacheManager;
import org.cache2k.CacheOperationCompletionListener;
import org.cache2k.processor.EntryProcessingResult;
import org.cache2k.processor.EntryProcessor;

import com.google.common.collect.ImmutableMap;

/**
 * This is a workaround to make googles ComputingMap work with null values.
 * 
 * @see <a href="http://code.google.com/p/google-collections/issues/detail?id=166">Source</a>
 * 
 */
@ThreadSafe
public class WrapperCache<K, V> implements Cache<K, V> {

    private final Cache<K, V> delegate;

    public WrapperCache(final Cache<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public V peek(final K key) {
        return maybeGet(key, delegate.peek(key));
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
    public ConcurrentMap<K, V> asMap() {
        return new WrapperConcurrentMap<K, V>(delegate.asMap());
    }

    protected final V maybeGet(final K key, final V value) {
        if (value != null) {
            final V v = value;
            if (!isPutAllowed(key, v)) {
                remove(key);
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
    public String getName() {
        return delegate.getName();
    }

    @Override
    public CacheEntry<K, V> getEntry(final K key) {
        return delegate.getEntry(key);
    }

    @Override
    public void prefetch(final K key) {
        delegate.prefetch(key);
    }

    @Override
    public void prefetchAll(final Iterable<? extends K> keys, final CacheOperationCompletionListener listener) {
        delegate.prefetchAll(keys, listener);
    }

    @Override
    public CacheEntry<K, V> peekEntry(final K key) {
        return delegate.peekEntry(key);
    }

    @Override
    public boolean containsKey(final K key) {
        return delegate.containsKey(key);
    }

    @Override
    public V computeIfAbsent(final K key, final Callable<V> callable) {
        return delegate.computeIfAbsent(key, callable);
    }

    @Override
    public boolean putIfAbsent(final K key, final V value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public V peekAndReplace(final K key, final V value) {
        return delegate.peekAndReplace(key, value);
    }

    @Override
    public boolean replace(final K key, final V value) {
        return delegate.replace(key, value);
    }

    @Override
    public boolean replaceIfEquals(final K key, final V oldValue, final V newValue) {
        return delegate.replaceIfEquals(key, oldValue, newValue);
    }

    @Override
    public V peekAndRemove(final K key) {
        return delegate.peekAndRemove(key);
    }

    @Override
    public boolean containsAndRemove(final K key) {
        return delegate.containsAndRemove(key);
    }

    @Override
    public void remove(final K key) {
        delegate.remove(key);
    }

    @Override
    public boolean removeIfEquals(final K key, final V expectedValue) {
        return delegate.removeIfEquals(key, expectedValue);
    }

    @Override
    public void removeAll(final Iterable<? extends K> keys) {
        delegate.removeAll(keys);
    }

    @Override
    public V peekAndPut(final K key, final V value) {
        return delegate.peekAndPut(key, value);
    }

    @Override
    public void expireAt(final K key, final long millis) {
        delegate.expireAt(key, millis);
    }

    @Override
    public void loadAll(final Iterable<? extends K> keys, final CacheOperationCompletionListener listener) {
        delegate.loadAll(keys, listener);
    }

    @Override
    public void reloadAll(final Iterable<? extends K> keys, final CacheOperationCompletionListener listener) {
        delegate.reloadAll(keys, listener);
    }

    @Override
    public <R> R invoke(final K key, final EntryProcessor<K, V, R> entryProcessor) {
        return delegate.invoke(key, entryProcessor);
    }

    @Override
    public <R> Map<K, EntryProcessingResult<R>> invokeAll(final Iterable<? extends K> keys,
            final EntryProcessor<K, V, R> entryProcessor) {
        return delegate.invokeAll(keys, entryProcessor);
    }

    @Override
    public Map<K, V> peekAll(final Iterable<? extends K> keys) {
        return delegate.peekAll(keys);
    }

    @Override
    public Iterable<K> keys() {
        return delegate.keys();
    }

    @Override
    public Iterable<CacheEntry<K, V>> entries() {
        return delegate.entries();
    }

    @Override
    public void removeAll() {
        delegate.removeAll();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public void clearAndClose() {
        delegate.clearAndClose();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public CacheManager getCacheManager() {
        return delegate.getCacheManager();
    }

    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    @Override
    public <X> X requestInterface(final Class<X> type) {
        return delegate.requestInterface(type);
    }

}
