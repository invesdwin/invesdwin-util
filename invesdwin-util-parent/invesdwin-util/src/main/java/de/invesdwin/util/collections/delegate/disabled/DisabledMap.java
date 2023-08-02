package de.invesdwin.util.collections.delegate.disabled;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

@Immutable
public class DisabledMap<K, V> implements Map<K, V> {

    @SuppressWarnings("rawtypes")
    private static final DisabledMap INSTANCE = new DisabledMap<>();

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean containsKey(final Object key) {
        return false;
    }

    @Override
    public boolean containsValue(final Object value) {
        return false;
    }

    @Override
    public V get(final Object key) {
        return null;
    }

    @Override
    public V put(final K key, final V value) {
        return null;
    }

    @Override
    public V remove(final Object key) {
        return null;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {}

    @Override
    public void clear() {}

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return DisabledCollection.getInstance();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        return null;
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        return false;
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        return false;
    }

    @Override
    public V replace(final K key, final V value) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> DisabledMap<K, V> getInstance() {
        return INSTANCE;
    }

}
