package de.invesdwin.util.collections.fast.concurrent;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;

@ThreadSafe
public class LockedMap<K, V> implements Map<K, V> {
    private final Map<K, V> delegate;
    private final ILock lock;
    private transient Set<K> keySet;
    private transient Collection<V> values;
    private transient Set<Map.Entry<K, V>> entrySet;

    public LockedMap(final Map<K, V> delegate) {
        this.delegate = delegate;
        this.lock = Locks.newReentrantLock(getClass().getSimpleName());
    }

    public LockedMap(final Map<K, V> delegate, final ILock lock) {
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
        lock.lock();
        try {
            getDelegate().clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsKey(final Object key) {
        lock.lock();
        try {
            return getDelegate().containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        lock.lock();
        try {
            return getDelegate().containsValue(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        lock.lock();
        try {
            if (entrySet == null) {
                entrySet = new LockedSet<Entry<K, V>>(getDelegate().entrySet(), lock);
            }
            return entrySet;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        lock.lock();
        try {
            getDelegate().forEach(action);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V get(final Object key) {
        lock.lock();
        try {
            return getDelegate().get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        lock.lock();
        try {
            return getDelegate().getOrDefault(key, defaultValue);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            return getDelegate().isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        lock.lock();
        try {
            if (keySet == null) {
                keySet = new LockedSet<K>(getDelegate().keySet(), lock);
            }
            return keySet;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(final K key, final V value) {
        lock.lock();
        try {
            return getDelegate().put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        lock.lock();
        try {
            return getDelegate().putIfAbsent(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        lock.lock();
        try {
            return getDelegate().replace(key, oldValue, newValue);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V replace(final K key, final V value) {
        lock.lock();
        try {
            return getDelegate().replace(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        lock.lock();
        try {
            return getDelegate().computeIfAbsent(key, mappingFunction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        lock.lock();
        try {
            return getDelegate().computeIfPresent(key, remappingFunction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        lock.lock();
        try {
            return getDelegate().compute(key, remappingFunction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        lock.lock();
        try {
            return getDelegate().merge(key, value, remappingFunction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        lock.lock();
        try {
            getDelegate().putAll(map);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        lock.lock();
        try {
            getDelegate().replaceAll(function);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(final Object key) {
        lock.lock();
        try {
            return getDelegate().remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        lock.lock();
        try {
            return getDelegate().remove(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return getDelegate().size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<V> values() {
        lock.lock();
        try {
            if (values == null) {
                values = new LockedCollection<V>(getDelegate().values(), lock);
            }
            return values;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        lock.lock();
        try {
            return getDelegate().equals(o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int hashCode() {
        lock.lock();
        try {
            return getDelegate().hashCode();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return delegate.toString();
        } finally {
            lock.unlock();
        }
    }

}