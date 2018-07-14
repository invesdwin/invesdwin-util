package de.invesdwin.util.collections.loadingcache.cache2k.internal;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.ThreadSafe;

/**
 * This is a workaround to make googles ComputingMap work with null values.
 * 
 * @see <a href="http://code.google.com/p/google-collections/issues/detail?id=166">Source</a>
 */
@ThreadSafe
public class WrapperConcurrentMap<K, V> extends WrapperMap<K, V> implements ConcurrentMap<K, V> {

    private final ConcurrentMap<K, V> delegate;

    public WrapperConcurrentMap(final ConcurrentMap<K, V> delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        return maybeGet(key, delegate.putIfAbsent(key, value));
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        return delegate.remove(key, value);
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(final K key, final V value) {
        return maybeGet(key, delegate.replace(key, value));
    }
}
