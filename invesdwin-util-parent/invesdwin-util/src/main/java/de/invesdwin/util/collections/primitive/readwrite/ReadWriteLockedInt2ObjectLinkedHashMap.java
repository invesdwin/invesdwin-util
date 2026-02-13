package de.invesdwin.util.collections.primitive.readwrite;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;

import javax.annotation.concurrent.ThreadSafe;

import org.jspecify.annotations.Nullable;

import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.padded.CloseableReentrantReadWriteLock;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

/**
 * Origin:
 * https://github.com/magicprinc/fastutil-concurrent-wrapper/blob/master/src/main/java/com/trivago/fastutilconcurrentwrapper/intkey/SynchronizedInt2ObjLinkedHashMap.java
 * 
 * @see java.util.Collections#synchronizedMap(Map)
 * @see java.util.LinkedHashMap
 * @see it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 */
@ThreadSafe
public class ReadWriteLockedInt2ObjectLinkedHashMap<V> implements Int2ObjectSortedMap<V> {
    protected final Int2ObjectLinkedOpenHashMap<V> m;
    protected final CloseableReentrantReadWriteLock lock = new CloseableReentrantReadWriteLock();

    public ReadWriteLockedInt2ObjectLinkedHashMap(final int expected, final float f) {
        m = new Int2ObjectLinkedOpenHashMap<>(expected, f);
    }//new

    public ReadWriteLockedInt2ObjectLinkedHashMap() {
        m = new Int2ObjectLinkedOpenHashMap<>();
    }//new

    protected ICloseableLock read() {
        return lock.readLocked();
    }

    protected ICloseableLock write() {
        return lock.writeLocked();
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

    public int[] keyArray() {
        try (ICloseableLock lock = read()) {
            return m.keySet().toIntArray();
        }
    }

    public V[] valueArray(final V[] valueArray) {
        try (ICloseableLock lock = read()) {
            return m.values().toArray(valueArray);
        }
    }

    public void forEachKey(final IntConsumer action) {
        try (ICloseableLock lock = read()) {
            m.keySet().forEach(action);
        }
    }

    public void forEachValue(final Consumer<V> action) {
        try (ICloseableLock lock = read()) {
            m.values().forEach(action);
        }
    }

    public void forEachEntry(final ObjIntConsumer<V> action) {
        try (ICloseableLock lock = read()) {
            for (final Int2ObjectMap.Entry<V> e : m.int2ObjectEntrySet()) {
                action.accept(e.getValue(), e.getIntKey());
            }
        }
    }

    @Override
    public void forEach(final BiConsumer<? super Integer, ? super V> action) {
        try (ICloseableLock lock = read()) {
            m.forEach(action);
        }
    }

    @Override
    public int firstIntKey() {
        try (ICloseableLock lock = read()) {
            return m.firstIntKey();
        }
    }

    @Override
    public int lastIntKey() {
        try (ICloseableLock lock = read()) {
            return m.lastIntKey();
        }
    }

    @Override
    public boolean containsKey(final int key) {
        try (ICloseableLock lock = read()) {
            return m.containsKey(key);
        }
    }

    @Override
    public V get(final int key) {
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
    @Deprecated
    public V get(final Object key) {
        try (ICloseableLock lock = read()) {
            return m.get(key);
        }
    }

    @Override
    @Deprecated
    public boolean containsKey(final Object key) {
        try (ICloseableLock lock = read()) {
            return m.containsKey(key);
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
    public IntComparator comparator() {
        return m.comparator();
    }

    /** Full copy ~ snapshot! */
    @Override
    public IntSortedSet keySet() {
        try (ICloseableLock lock = read()) {
            return new IntLinkedOpenHashSet(m.keySet());
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
    public ObjectSortedSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
        return new ObjectLinkedOpenHashSet<>(m.int2ObjectEntrySet());
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
    public Int2ObjectSortedMap<V> subMap(final int fromKey, final int toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Int2ObjectSortedMap<V> headMap(final int toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Int2ObjectSortedMap<V> tailMap(final int fromKey) {
        throw new UnsupportedOperationException();
    }

    public void forEachKeyWrite(final IntConsumer action) {
        try (ICloseableLock lock = write()) {
            m.keySet().forEach(action);
        }
    }

    public void forEachValueWrite(final Consumer<V> action) {
        try (ICloseableLock lock = write()) {
            m.values().forEach(action);
        }
    }

    public void forEachEntryWrite(final ObjIntConsumer<V> action) {
        try (ICloseableLock lock = write()) {
            for (final Int2ObjectMap.Entry<V> e : m.int2ObjectEntrySet()) {
                action.accept(e.getValue(), e.getIntKey());
            }
        }
    }

    //CHECKSTYLE:OFF
    public <R> R withWriteLock(final Function<Int2ObjectLinkedOpenHashMap<V>, R> exclusiveAccess) {
        //CHECKSTYLE:ON
        try (ICloseableLock lock = write()) {
            return exclusiveAccess.apply(m);
        }
    }

    @Override
    public void putAll(final Map<? extends Integer, ? extends V> from) {
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
    public V putIfAbsent(final int key, final V value) {
        try (ICloseableLock lock = write()) {
            return m.putIfAbsent(key, value);
        }
    }

    @Override
    public boolean remove(final int key, final Object value) {
        try (ICloseableLock lock = write()) {
            return m.remove(key, value);
        }
    }

    @Override
    public boolean replace(final int key, final V oldValue, final V newValue) {
        try (ICloseableLock lock = write()) {
            return m.replace(key, oldValue, newValue);
        }
    }

    @Override
    public V replace(final int key, final V value) {
        try (ICloseableLock lock = write()) {
            return m.replace(key, value);
        }
    }

    @Override
    public V computeIfAbsent(final int key, final IntFunction<? extends V> mappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfAbsent(key, mappingFunction);
        }
    }

    @Override
    public V computeIfAbsent(final int key, final Int2ObjectFunction<? extends V> mappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfAbsent(key, mappingFunction);
        }
    }

    @Override
    public V computeIfPresent(final int key,
            final BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfPresent(key, remappingFunction);
        }
    }

    @Override
    public V compute(final int key, final BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.compute(key, remappingFunction);
        }
    }

    @Override
    public V merge(final int key, final V value,
            final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.merge(key, value, remappingFunction);
        }
    }

    @Override
    public V put(final int key, final V value) {
        try (ICloseableLock lock = write()) {
            return m.put(key, value);
        }
    }

    @Override
    public V remove(final int key) {
        try (ICloseableLock lock = write()) {
            return m.remove(key);
        }
    }

    @Override
    @Deprecated
    public V remove(final Object key) {
        try (ICloseableLock lock = write()) {
            return m.remove(key);
        }
    }

    @Override
    @Deprecated
    public V put(final Integer key, final V value) {
        try (ICloseableLock lock = write()) {
            return m.put(key, value);
        }
    }

    @Override
    public void replaceAll(final BiFunction<? super Integer, ? super V, ? extends V> function) {
        try (ICloseableLock lock = write()) {
            m.replaceAll(function);
        }
    }

    @Override
    public V putIfAbsent(final Integer key, final V value) {
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
    public boolean replace(final Integer key, final V oldValue, final V newValue) {
        try (ICloseableLock lock = write()) {
            return m.replace(key, oldValue, newValue);
        }
    }

    @Override
    public V replace(final Integer key, final V value) {
        try (ICloseableLock lock = write()) {
            return m.replace(key, value);
        }
    }

    @Override
    public V computeIfAbsent(final Integer key, final Function<? super Integer, ? extends V> mappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfAbsent(key, mappingFunction);
        }
    }

    @Override
    public V computeIfPresent(final Integer key,
            final BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfPresent(key, remappingFunction);
        }
    }

    @Override
    public V compute(final Integer key,
            final BiFunction<? super Integer, ? super @Nullable V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.compute(key, remappingFunction);
        }
    }

    @Override
    public V merge(final Integer key, final V value,
            final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.merge(key, value, remappingFunction);
        }
    }
}