package de.invesdwin.util.collections.loadingcache.internal;

import java.lang.reflect.Field;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.collections.map.LRUMap;

import de.invesdwin.util.lang.Reflections;

@ThreadSafe
public class LRUMapLoadingCache<K, V> extends ASynchronizedLoadingCache<K, V> {

    @SuppressWarnings("unchecked")
    public LRUMapLoadingCache(final Function<K, V> loadValue, final Integer maximumSize) {
        //apache commons LRUMap is faster than adjusted LinkedHashMap!
        super(loadValue, new LRUMap(maximumSize));
    }

    public void increaseMaximumSize(final int maximumSize) {
        final LRUMap lru = (LRUMap) map;
        if (lru.maxSize() < maximumSize) {
            final Field field = Reflections.findField(LRUMap.class, "maxSize");
            Reflections.makeAccessible(field);
            Reflections.setField(field, map, maximumSize);
        }
    }

}
