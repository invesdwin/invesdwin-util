package de.invesdwin.util.collections.loadingcache.internal;

import java.lang.reflect.Field;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.collections4.map.LRUMap;

import de.invesdwin.util.lang.Reflections;

@ThreadSafe
public class LRUMapLoadingCache<K, V> extends ASynchronizedLoadingCache<K, V> {

    public LRUMapLoadingCache(final Function<K, V> loadValue, final Integer maximumSize) {
        //apache commons LRUMap is faster than adjusted LinkedHashMap!
        super(loadValue, new LRUMap<K, V>(maximumSize));
    }

    public synchronized void increaseMaximumSize(final int maximumSize) {
        final LRUMap<K, V> lru = (LRUMap<K, V>) map;
        if (lru.maxSize() < maximumSize) {
            final Field field = Reflections.findField(LRUMap.class, "maxSize");
            Reflections.makeAccessible(field);
            Reflections.setField(field, map, maximumSize);
        } else {
            throw new IllegalArgumentException(
                    "maximumSize [" + maximumSize + "] needs to be greater than current [" + lru.maxSize() + "]");
        }
    }

}
