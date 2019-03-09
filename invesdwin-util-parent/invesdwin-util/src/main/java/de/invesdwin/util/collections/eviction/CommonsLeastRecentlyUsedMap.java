package de.invesdwin.util.collections.eviction;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Reflections;

/**
 * apache commons LRUMap is faster than adjusted LinkedHashMap
 */
@NotThreadSafe
public class CommonsLeastRecentlyUsedMap<K, V> extends org.apache.commons.collections4.map.LRUMap<K, V>
        implements IEvictionMap<K, V> {

    private static final MethodHandle LRUMAP_MAXSIZE_SETTER;

    static {
        final Field field = Reflections.findField(CommonsLeastRecentlyUsedMap.class, "maxSize");
        Reflections.makeAccessible(field);
        try {
            LRUMAP_MAXSIZE_SETTER = MethodHandles.lookup().unreflectSetter(field);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CommonsLeastRecentlyUsedMap(final int maximumSize) {
        super(maximumSize);
    }

    @Override
    public EvictionMode getEvictionMode() {
        return EvictionMode.LeastRecentlyUsed;
    }

    @Override
    public int getMaximumSize() {
        return maxSize();
    }

    @Override
    public void setMaximumSize(final int maximumSize) {
        try {
            LRUMAP_MAXSIZE_SETTER.invoke(this, maximumSize);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

}