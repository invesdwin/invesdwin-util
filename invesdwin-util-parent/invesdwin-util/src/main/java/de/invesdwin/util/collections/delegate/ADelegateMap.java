package de.invesdwin.util.collections.delegate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;

@NotThreadSafe
public abstract class ADelegateMap<K, V> implements Map<K, V>, ISerializableValueObject {

    private final Map<K, V> delegate;

    public ADelegateMap() {
        this.delegate = newDelegate();
    }

    ADelegateMap(final Map<K, V> delegate) {
        this.delegate = delegate;
    }

    protected abstract Map<K, V> newDelegate();

    protected Map<K, V> getDelegate() {
        return delegate;
    }

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return getDelegate().containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return getDelegate().containsValue(value);
    }

    @Override
    public V get(final Object key) {
        return getDelegate().get(key);
    }

    @Override
    public V put(final K key, final V value) {
        if (isPutAllowed(key, value)) {
            return getDelegate().put(key, value);
        } else {
            throw new IllegalArgumentException("isPutAllowed returned false! Check this before using put!");
        }
    }

    @Override
    public V remove(final Object key) {
        return getDelegate().remove(key);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public Set<K> keySet() {
        return getDelegate().keySet();
    }

    @Override
    public Collection<V> values() {
        return getDelegate().values();
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return getDelegate().entrySet();
    }

    public boolean isPutAllowed(final K key, final V value) {
        return true;
    }

    public static <K, V> Map<K, V> maybeUnwrapToRoot(final Map<K, V> map) {
        Map<K, V> cur = map;
        while (cur instanceof ADelegateMap) {
            final ADelegateMap<K, V> c = (ADelegateMap<K, V>) map;
            cur = c.getDelegate();
        }
        return cur;
    }

}
