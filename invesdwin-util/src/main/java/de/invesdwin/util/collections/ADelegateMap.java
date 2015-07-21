package de.invesdwin.util.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ADelegateMap<K, V> implements Map<K, V> {

    private Map<K, V> delegate;

    protected abstract Map<K, V> createDelegate();

    protected synchronized Map<K, V> getDelegate() {
        if (delegate == null) {
            this.delegate = createDelegate();
        }
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
        getDelegate().putAll(m);
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

}
