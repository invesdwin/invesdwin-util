package de.invesdwin.util.collections.loadingcache.guava.internal;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Optional;

/**
 * This is a workaround to make googles ComputingMap work with null values.
 * 
 * @see <a href="http://code.google.com/p/google-collections/issues/detail?id=166">Source</a>
 */
@ThreadSafe
public class OptionalValueWrapperConcurrentMap<K, V> extends OptionalValueWrapperMap<K, V> implements
        ConcurrentMap<K, V> {

    private final ConcurrentMap<K, Optional<V>> delegate;

    public OptionalValueWrapperConcurrentMap(final ConcurrentMap<K, Optional<V>> delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        return maybeGet(key, delegate.putIfAbsent(key, Optional.fromNullable(value)));
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        return delegate.remove(key, Optional.fromNullable(value));
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        return delegate.replace(key, Optional.fromNullable(oldValue), Optional.fromNullable(newValue));
    }

    @Override
    public V replace(final K key, final V value) {
        return maybeGet(key, delegate.replace(key, Optional.fromNullable(value)));
    }
}
