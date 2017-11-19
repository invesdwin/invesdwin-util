package de.invesdwin.util.collections;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.collections4.map.LRUMap;

import de.invesdwin.util.lang.Reflections;

/**
 * Reconfigures apache commons LRUMap to remove the least recently added element instead of the least recently used.
 * Only the first time an element is added is counted. This improves performance a bit.
 */
@NotThreadSafe
public class LRAMap<K, V> extends LRUMap<K, V> {

    private static final MethodHandle LRUMAP_MAXSIZE_SETTER;

    static {
        final Field field = Reflections.findField(LRUMap.class, "maxSize");
        Reflections.makeAccessible(field);
        try {
            LRUMAP_MAXSIZE_SETTER = MethodHandles.lookup().unreflectSetter(field);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LRAMap(final int maxSize) {
        super(maxSize);
    }

    public void setMaxSize(final int maximumSize) {
        try {
            LRUMAP_MAXSIZE_SETTER.invoke(this, maximumSize);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void moveToMRU(final LinkEntry<K, V> entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void updateEntry(final HashEntry<K, V> entry, final V newValue) {
        entry.setValue(newValue);
    }

    @Override
    public V get(final Object key) {
        final LinkEntry<K, V> entry = getEntry(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    @Deprecated
    @Override
    public V get(final Object key, final boolean updateToMRU) {
        throw new UnsupportedOperationException();
    }
}