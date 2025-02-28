package de.invesdwin.util.collections.delegate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;

@NotThreadSafe
public abstract class ADelegateMap<K, V> implements Map<K, V>, ISerializableValueObject {

    private final Map<K, V> delegate;

    public ADelegateMap() {
        this.delegate = newDelegate();
    }

    protected ADelegateMap(final Map<K, V> delegate) {
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
        if (value == null) {
            return getDelegate().remove(key);
        } else if (isPutAllowed(key, value)) {
            return getDelegate().put(key, value);
        } else {
            return getDelegate().get(key);
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        if (value == null) {
            return getDelegate().remove(key);
        } else if (isPutAllowed(key, value)) {
            return getDelegate().putIfAbsent(key, value);
        } else {
            return getDelegate().get(key);
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

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return getDelegate().compute(key, remappingFunction);
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        return getDelegate().computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return getDelegate().computeIfPresent(key, remappingFunction);
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        return getDelegate().getOrDefault(key, defaultValue);
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return getDelegate().merge(key, value, remappingFunction);
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        return getDelegate().remove(key, value);
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        if (isPutAllowed(key, newValue)) {
            return getDelegate().replace(key, oldValue, newValue);
        } else {
            return false;
        }
    }

    @Override
    public V replace(final K key, final V value) {
        if (isPutAllowed(key, value)) {
            return getDelegate().replace(key, value);
        } else {
            return getDelegate().get(key);
        }
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        getDelegate().replaceAll(function);
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        getDelegate().forEach(action);
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
