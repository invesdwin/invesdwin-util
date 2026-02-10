package de.invesdwin.util.collections.primitive.readwrite;

import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import org.jspecify.annotations.Nullable;

import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.padded.CloseableReentrantReadWriteLock;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

/**
 * Origin:
 * https://github.com/magicprinc/fastutil-concurrent-wrapper/blob/master/src/main/java/com/trivago/fastutilconcurrentwrapper/objkey/SynchronizedObj2ObjLinkedHashMap.java
 * 
 * @see java.util.Collections#synchronizedMap(Map)
 * @see java.util.LinkedHashMap
 */
@ThreadSafe
public class ReadWriteLockedObj2ObjLinkedHashMap<K, V> implements Object2ObjectSortedMap<K, V> {
    protected final Object2ObjectLinkedOpenHashMap<K, V> m;
    protected final CloseableReentrantReadWriteLock lock = new CloseableReentrantReadWriteLock();

    public ReadWriteLockedObj2ObjLinkedHashMap(final int expected, final float f) {
        m = new Object2ObjectLinkedOpenHashMap<>(expected, f);
    }//new

    public ReadWriteLockedObj2ObjLinkedHashMap() {
        m = new Object2ObjectLinkedOpenHashMap<>();
    }//new

    protected ICloseableLock read() {
        return lock.read();
    }

    protected ICloseableLock write() {
        return lock.write();
    }

    @Override
    public int size() {
        try (ICloseableLock lock = read()) {
            return m.size();
        }
    }

    @Override
    public boolean isEmpty() {
        try (ICloseableLock lock = read()) {
            return m.isEmpty();
        }
    }

    public K[] keyArray(final K[] keyArray) {
        try (ICloseableLock lock = read()) {
            return m.keySet().toArray(keyArray);
        }
    }

    public V[] valueArray(final V[] valueArray) {
        try (ICloseableLock lock = read()) {
            return m.values().toArray(valueArray);
        }
    }

    public void forEachKey(final Consumer<K> action) {
        try (ICloseableLock lock = read()) {
            m.keySet().forEach(action);
        }
    }

    public void forEachValue(final Consumer<V> action) {
        try (ICloseableLock lock = read()) {
            m.values().forEach(action);
        }
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        try (ICloseableLock lock = read()) {
            m.forEach(action);
        }
    }

    @Override
    public K firstKey() {
        try (ICloseableLock lock = read()) {
            return m.firstKey();
        }
    }

    @Override
    public K lastKey() {
        try (ICloseableLock lock = read()) {
            return m.lastKey();
        }
    }

    @Override
    public boolean containsKey(final Object key) {
        try (ICloseableLock lock = read()) {
            return m.containsKey(key);
        }
    }

    @Override
    public V get(final Object key) {
        try (ICloseableLock lock = read()) {
            return m.get(key);
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        try (ICloseableLock lock = read()) {
            return m.containsValue(value);
        }
    }

    @Override
    public String toString() {
        try (ICloseableLock lock = read()) {
            return m.toString();
        }
    }

    @Override
    public int hashCode() {
        try (ICloseableLock lock = read()) {
            return m.hashCode();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        try (ICloseableLock lock = read()) {
            return m.equals(obj);
        }
    }

    @Override
    public Comparator<? super K> comparator() {
        return m.comparator();
    }

    /** Full copy ~ snapshot! */
    @Override
    public ObjectSortedSet<K> keySet() {
        try (ICloseableLock lock = read()) {
            return new ObjectLinkedOpenHashSet<>(m.keySet());
        }
    }

    /** Full copy ~ snapshot! */
    @Override
    public ObjectArrayList<V> values() {
        try (ICloseableLock lock = read()) {
            return new ObjectArrayList<>(m.values());
        }
    }

    /** Full copy ~ snapshot! */
    @Override
    public ObjectSortedSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
        return new ObjectLinkedOpenHashSet<>(m.object2ObjectEntrySet());
    }

    @Override
    public void defaultReturnValue(final V rv) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable V defaultReturnValue() {
        return null;
    }

    @Override
    public Object2ObjectSortedMap<K, V> subMap(final K fromKey, final K toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object2ObjectSortedMap<K, V> headMap(final K toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object2ObjectSortedMap<K, V> tailMap(final K fromKey) {
        throw new UnsupportedOperationException();
    }

    public void forEachKeyWrite(final Consumer<K> action) {
        try (ICloseableLock lock = write()) {
            m.keySet().forEach(action);
        }
    }

    public void forEachValueWrite(final Consumer<V> action) {
        try (ICloseableLock lock = write()) {
            m.values().forEach(action);
        }
    }

    public void forEachEntryWrite(final BiConsumer<K, V> action) {
        try (ICloseableLock lock = write()) {
            m.forEach(action);
        }
    }

    //CHECKSTYLE:OFF
    public <R> R withWriteLock(final Function<Object2ObjectLinkedOpenHashMap<K, V>, R> exclusiveAccess) {
        //CHECKSTYLE:ON
        try (ICloseableLock lock = write()) {
            return exclusiveAccess.apply(m);
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> from) {
        try (ICloseableLock lock = write()) {
            m.putAll(from);
        }
    }

    @Override
    public void clear() {
        try (ICloseableLock lock = write()) {
            m.clear();
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        try (ICloseableLock lock = write()) {
            return m.putIfAbsent(key, value);
        }
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        try (ICloseableLock lock = write()) {
            return m.remove(key, value);
        }
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        try (ICloseableLock lock = write()) {
            return m.replace(key, oldValue, newValue);
        }
    }

    @Override
    public V replace(final K key, final V value) {
        try (ICloseableLock lock = write()) {
            return m.replace(key, value);
        }
    }

    @Override
    public V computeIfAbsent(final K key, final Object2ObjectFunction<? super K, ? extends V> mappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfAbsent(key, mappingFunction);
        }
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfPresent(key, remappingFunction);
        }
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.compute(key, remappingFunction);
        }
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.merge(key, value, remappingFunction);
        }
    }

    @Override
    public V put(final K key, final V value) {
        try (ICloseableLock lock = write()) {
            return m.put(key, value);
        }
    }

    @Override
    public V remove(final Object key) {
        try (ICloseableLock lock = write()) {
            return m.remove(key);
        }
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        try (ICloseableLock lock = write()) {
            m.replaceAll(function);
        }
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfAbsent(key, mappingFunction);
        }
    }
}