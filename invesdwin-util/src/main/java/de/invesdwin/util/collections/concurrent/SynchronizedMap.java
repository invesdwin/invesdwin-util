package de.invesdwin.util.collections.concurrent;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedMap<K, V> implements Map<K, V> {
    private final Map<K, V> delegate;
    private final Object lock;
    private transient Set<K> keySet;
    private transient Collection<V> values;
    private transient Set<Map.Entry<K, V>> entrySet;

    public SynchronizedMap(final Map<K, V> delegate) {
        this.delegate = delegate;
        this.lock = this;
    }

    public SynchronizedMap(final Map<K, V> delegate, final Object lock) {
        this.delegate = delegate;
        this.lock = lock;
    }

    protected Map<K, V> getDelegate() {
        return delegate;
    }

    protected Object getLock() {
        return lock;
    }

    @Override
    public void clear() {
        synchronized (lock) {
            getDelegate().clear();
        }
    }

    @Override
    public boolean containsKey(final Object key) {
        synchronized (lock) {
            return getDelegate().containsKey(key);
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        synchronized (lock) {
            return getDelegate().containsValue(value);
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        synchronized (lock) {
            if (entrySet == null) {
                entrySet = new SynchronizedSet<Entry<K, V>>(getDelegate().entrySet(), lock);
            }
            return entrySet;
        }
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        synchronized (lock) {
            getDelegate().forEach(action);
        }
    }

    @Override
    public V get(final Object key) {
        synchronized (lock) {
            return getDelegate().get(key);
        }
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        synchronized (lock) {
            return getDelegate().getOrDefault(key, defaultValue);
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return getDelegate().isEmpty();
        }
    }

    @Override
    public Set<K> keySet() {
        synchronized (lock) {
            if (keySet == null) {
                keySet = new SynchronizedSet<K>(getDelegate().keySet(), lock);
            }
            return keySet;
        }
    }

    @Override
    public V put(final K key, final V value) {
        synchronized (lock) {
            return getDelegate().put(key, value);
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        synchronized (lock) {
            return getDelegate().putIfAbsent(key, value);
        }
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        synchronized (lock) {
            return getDelegate().replace(key, oldValue, newValue);
        }
    }

    @Override
    public V replace(final K key, final V value) {
        synchronized (lock) {
            return getDelegate().replace(key, value);
        }
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        synchronized (lock) {
            return getDelegate().computeIfAbsent(key, mappingFunction);
        }
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        synchronized (lock) {
            return getDelegate().computeIfPresent(key, remappingFunction);
        }
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        synchronized (lock) {
            return getDelegate().compute(key, remappingFunction);
        }
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        synchronized (lock) {
            return getDelegate().merge(key, value, remappingFunction);
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        synchronized (lock) {
            getDelegate().putAll(map);
        }
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        synchronized (lock) {
            getDelegate().replaceAll(function);
        }
    }

    @Override
    public V remove(final Object key) {
        synchronized (lock) {
            return getDelegate().remove(key);
        }
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        synchronized (lock) {
            return getDelegate().remove(key, value);
        }
    }

    @Override
    public int size() {
        synchronized (lock) {
            return getDelegate().size();
        }
    }

    @Override
    public Collection<V> values() {
        synchronized (lock) {
            if (values == null) {
                values = new SynchronizedCollection<V>(getDelegate().values(), lock);
            }
            return values;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        synchronized (lock) {
            return getDelegate().equals(o);
        }
    }

    @Override
    public int hashCode() {
        synchronized (lock) {
            return getDelegate().hashCode();
        }
    }

    @Override
    public String toString() {
        synchronized (lock) {
            return delegate.toString();
        }
    }

}