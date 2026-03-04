package de.invesdwin.util.collections.primitive.readwrite;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.ObjIntConsumer;

import javax.annotation.concurrent.ThreadSafe;

import org.jspecify.annotations.Nullable;

import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.padded.CloseableReentrantReadWriteLock;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * Origin:
 * https://github.com/magicprinc/fastutil-concurrent-wrapper/blob/master/src/main/java/com/trivago/fastutilconcurrentwrapper/intkey/SynchronizedInt2ObjHashMap.java
 * 
 * @see java.util.Collections#synchronizedMap(Map)
 * @see java.util.HashMap
 * @see it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 */
@ThreadSafe
public class ReadWriteLockedInt2IntMap implements Int2IntMap {
    protected final Int2IntOpenHashMap m;
    protected final CloseableReentrantReadWriteLock lock = new CloseableReentrantReadWriteLock();

    public ReadWriteLockedInt2IntMap(final int expected, final float f) {
        m = new Int2IntOpenHashMap(expected, f);
    }//new

    public ReadWriteLockedInt2IntMap() {
        m = new Int2IntOpenHashMap();
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

    public int[] valueArray(final int[] valueArray) {
        try (ICloseableLock lock = read()) {
            return m.values().toArray(valueArray);
        }
    }

    public void forEachKey(final IntConsumer action) {
        try (ICloseableLock lock = read()) {
            m.keySet().forEach(action);
        }
    }

    public void forEachValue(final Consumer action) {
        try (ICloseableLock lock = read()) {
            m.values().forEach(action);
        }
    }

    public void forEachEntry(final ObjIntConsumer action) {
        try (ICloseableLock lock = read()) {
            for (final Int2IntMap.Entry e : m.int2IntEntrySet()) {
                action.accept(e.getValue(), e.getIntKey());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void forEach(final BiConsumer<? super Integer, ? super Integer> action) {
        try (ICloseableLock lock = read()) {
            m.forEach(action);
        }
    }

    @Override
    public boolean containsKey(final int key) {
        try (ICloseableLock lock = read()) {
            return m.containsKey(key);
        }
    }

    @Override
    public int get(final int key) {
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
    public boolean containsValue(final int value) {
        try (ICloseableLock lock = read()) {
            return m.containsValue(value);
        }
    }

    @Override
    @Deprecated
    public Integer get(final Object key) {
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

    /** Full copy ~ snapshot! */
    @Override
    public IntSet keySet() {
        try (ICloseableLock lock = read()) {
            return new IntOpenHashSet(m.keySet());
        }
    }

    /** Full copy ~ snapshot! */
    @Override
    public IntCollection values() {
        try (ICloseableLock lock = read()) {
            return new IntArrayList(m.values());
        }
    }

    /** Full copy ~ snapshot! */
    @Override
    public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
        return new ObjectOpenHashSet<>(m.int2IntEntrySet());
    }

    @Override
    public void defaultReturnValue(final int rv) {
        m.defaultReturnValue(rv);
    }

    @Override
    public @Nullable int defaultReturnValue() {
        return m.defaultReturnValue();
    }

    public void forEachKeyWrite(final IntConsumer action) {
        try (ICloseableLock lock = write()) {
            m.keySet().forEach(action);
        }
    }

    public void forEachValueWrite(final Consumer<Integer> action) {
        try (ICloseableLock lock = write()) {
            m.values().forEach(action);
        }
    }

    //CHECKSTYLE:OFF
    public <R> R withWriteLock(final Function<Int2IntOpenHashMap, R> exclusiveAccess) {
        //CHECKSTYLE:ON
        try (ICloseableLock lock = write()) {
            return exclusiveAccess.apply(m);
        }
    }

    @Override
    public void putAll(final Map<? extends Integer, ? extends Integer> from) {
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
    public int putIfAbsent(final int key, final int value) {
        try (ICloseableLock lock = write()) {
            return m.putIfAbsent(key, value);
        }
    }

    @Override
    public boolean remove(final int key, final int value) {
        try (ICloseableLock lock = write()) {
            return m.remove(key, value);
        }
    }

    @Override
    public boolean replace(final int key, final int oldValue, final int newValue) {
        try (ICloseableLock lock = write()) {
            return m.replace(key, oldValue, newValue);
        }
    }

    @Override
    public int replace(final int key, final int value) {
        try (ICloseableLock lock = write()) {
            return m.replace(key, value);
        }
    }

    @Override
    public int computeIfAbsent(final int key, final Int2IntFunction mappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfAbsent(key, mappingFunction);
        }
    }

    @Override
    public int computeIfPresent(final int key,
            final BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfPresent(key, remappingFunction);
        }
    }

    @Override
    public int compute(final int key,
            final BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.compute(key, remappingFunction);
        }
    }

    @Override
    public int merge(final int key, final int value,
            final BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.merge(key, value, remappingFunction);
        }
    }

    @Override
    public int put(final int key, final int value) {
        try (ICloseableLock lock = write()) {
            return m.put(key, value);
        }
    }

    @Override
    public int remove(final int key) {
        try (ICloseableLock lock = write()) {
            return m.remove(key);
        }
    }

    @Override
    @Deprecated
    public Integer remove(final Object key) {
        try (ICloseableLock lock = write()) {
            return m.remove(key);
        }
    }

    @Override
    @Deprecated
    public Integer put(final Integer key, final Integer value) {
        try (ICloseableLock lock = write()) {
            return m.put(key, value);
        }
    }

    @Override
    public void replaceAll(final BiFunction<? super Integer, ? super Integer, ? extends Integer> function) {
        try (ICloseableLock lock = write()) {
            m.replaceAll(function);
        }
    }

    @Override
    public Integer putIfAbsent(final Integer key, final Integer value) {
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
    public boolean replace(final Integer key, final Integer oldValue, final Integer newValue) {
        try (ICloseableLock lock = write()) {
            return m.replace(key, oldValue, newValue);
        }
    }

    @Override
    public Integer replace(final Integer key, final Integer value) {
        try (ICloseableLock lock = write()) {
            return m.replace(key, value);
        }
    }

    @Override
    public Integer computeIfAbsent(final Integer key,
            final Function<? super Integer, ? extends Integer> mappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfAbsent(key, mappingFunction);
        }
    }

    @Override
    public Integer computeIfPresent(final Integer key,
            final BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.computeIfPresent(key, remappingFunction);
        }
    }

    @Override
    public Integer compute(final Integer key,
            final BiFunction<? super Integer, ? super @Nullable Integer, ? extends Integer> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.compute(key, remappingFunction);
        }
    }

    @Override
    public Integer merge(final Integer key, final Integer value,
            final BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        try (ICloseableLock lock = write()) {
            return m.merge(key, value, remappingFunction);
        }
    }
}