package de.invesdwin.util.collections.loadingcache.guava.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Optional;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;

/**
 * This is a workaround to make googles ComputingMap work with null values.
 * 
 * @see <a href="http://code.google.com/p/google-collections/issues/detail?id=166">Source</a>
 */
@SuppressWarnings("unchecked")
@NotThreadSafe
public class OptionalValueWrapperMap<K, V> implements Map<K, V> {

    private final Map<K, Optional<V>> delegate;

    public OptionalValueWrapperMap(final Map<K, Optional<V>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * If key == null this method returns false. This is because googles ComputingMaps do not allow null keys.
     */
    @Override
    public boolean containsKey(final Object key) {
        if (key == null) {
            return false;
        } else {
            return delegate.containsKey(key);
        }
    }

    /**
     * If key == null this method returns false. This is because googles ComputingMaps do not allow null value normally.
     */
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            return false;
        } else {
            try {
                return delegate.containsValue(Optional.fromNullable((V) value));
            } catch (final ClassCastException e) {
                return false;
            }
        }
    }

    @Override
    public V get(final Object key) {
        final Optional<V> value = delegate.get(key);
        return maybeGet((K) key, value);
    }

    @Override
    public V put(final K key, final V value) {
        if (isPutAllowed(key, value)) {
            final Optional<V> previousValue = delegate.put(key, Optional.fromNullable(value));
            return maybeGet(key, previousValue);
        } else {
            throw new IllegalArgumentException("isCacheAllowed(value) returned false, check this before using put!");
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        if (isPutAllowed(key, value)) {
            final Optional<V> previousValue = delegate.putIfAbsent(key, Optional.fromNullable(value));
            return maybeGet(key, previousValue);
        } else {
            throw new IllegalArgumentException("isCacheAllowed(value) returned false, check this before using put!");
        }
    }

    @Override
    public V remove(final Object key) {
        final Optional<V> value = delegate.remove(key);
        return maybeGet((K) key, value);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        final Set<V> values = ILockCollectionFactory.getInstance(false).newSet();
        final Collection<Optional<V>> maybeNullValues = delegate.values();
        for (final Optional<V> maybeNullValue : maybeNullValues) {
            final V value = maybeGet(null, maybeNullValue);
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        final Map<K, V> map = ILockCollectionFactory.getInstance(false).newMap();
        for (final Entry<K, Optional<V>> e : delegate.entrySet()) {
            final V value = maybeGet(e.getKey(), e.getValue());
            map.put(e.getKey(), value);
        }
        return map.entrySet();
    }

    protected final V maybeGet(final K key, final Optional<V> value) {
        if (value != null) {
            final V v = value.orNull();
            if (key != null && !isPutAllowed(key, v)) {
                remove(key);
            }
            return v;
        } else {
            return null;
        }
    }

    public boolean isPutAllowed(final K key, final V value) {
        return true;
    }
}
