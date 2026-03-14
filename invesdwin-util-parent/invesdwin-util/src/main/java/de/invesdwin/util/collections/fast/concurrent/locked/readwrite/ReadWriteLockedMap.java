package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedMap<K, V> implements Map<K, V> {
    private final Map<K, V> delegate;
    private final IReadWriteLock lock;
    private transient Set<K> keySet;
    private transient Collection<V> values;
    private transient Set<Map.Entry<K, V>> entrySet;

    public ReadWriteLockedMap(final Map<K, V> delegate) {
        this.delegate = delegate;
        this.lock = Locks.newReentrantReadWriteLock(getClass().getSimpleName());
    }

    public ReadWriteLockedMap(final Map<K, V> delegate, final IReadWriteLock lock) {
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
        lock.writeLock().lock();
        try {
            getDelegate().clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean containsKey(final Object key) {
        lock.readLock().lock();
        try {
            return getDelegate().containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        lock.readLock().lock();
        try {
            return getDelegate().containsValue(value);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        lock.readLock().lock();
        try {
            if (entrySet == null) {
                entrySet = new ReadWriteLockedSet<Entry<K, V>>(getDelegate().entrySet(), lock);
            }
            return entrySet;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        lock.readLock().lock();
        try {
            getDelegate().forEach(action);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public V get(final Object key) {
        lock.readLock().lock();
        try {
            return getDelegate().get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        lock.readLock().lock();
        try {
            return getDelegate().getOrDefault(key, defaultValue);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return getDelegate().isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        lock.readLock().lock();
        try {
            if (keySet == null) {
                keySet = new ReadWriteLockedSet<K>(getDelegate().keySet(), lock);
            }
            return keySet;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public V put(final K key, final V value) {
        lock.writeLock().lock();
        try {
            return getDelegate().put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        lock.writeLock().lock();
        try {
            return getDelegate().putIfAbsent(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        lock.writeLock().lock();
        try {
            return getDelegate().replace(key, oldValue, newValue);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V replace(final K key, final V value) {
        lock.writeLock().lock();
        try {
            return getDelegate().replace(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        lock.writeLock().lock();
        try {
            return getDelegate().computeIfAbsent(key, mappingFunction);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        lock.writeLock().lock();
        try {
            return getDelegate().computeIfPresent(key, remappingFunction);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        lock.writeLock().lock();
        try {
            return getDelegate().compute(key, remappingFunction);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        lock.writeLock().lock();
        try {
            return getDelegate().merge(key, value, remappingFunction);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        lock.writeLock().lock();
        try {
            getDelegate().putAll(map);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        lock.writeLock().lock();
        try {
            getDelegate().replaceAll(function);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V remove(final Object key) {
        lock.writeLock().lock();
        try {
            return getDelegate().remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        lock.writeLock().lock();
        try {
            return getDelegate().remove(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return getDelegate().size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Collection<V> values() {
        lock.readLock().lock();
        try {
            if (values == null) {
                values = new ReadWriteLockedCollection<V>(getDelegate().values(), lock);
            }
            return values;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        lock.readLock().lock();
        try {
            return getDelegate().equals(o);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int hashCode() {
        lock.readLock().lock();
        try {
            return getDelegate().hashCode();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return delegate.toString();
        } finally {
            lock.readLock().unlock();
        }
    }

}