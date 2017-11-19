package de.invesdwin.util.collections.loadingcache.internal;

import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.LRAMap;

@ThreadSafe
public class LRAMapLoadingCache<K, V> extends ASynchronizedLoadingCache<K, V> {

    public LRAMapLoadingCache(final Function<K, V> loadValue, final Integer maximumSize) {
        /*
         * apache commons LRUMap is faster than adjusted LinkedHashMap and we can even make it faster by making it
         * remove only the least recently added element
         */
        super(loadValue, new LRAMap<K, V>(maximumSize));
    }

    public synchronized void increaseMaximumSize(final int maximumSize) {
        final LRAMap<K, V> lru = (LRAMap<K, V>) map;
        if (lru.maxSize() < maximumSize) {
            lru.setMaxSize(maximumSize);
        } else {
            throw new IllegalArgumentException(
                    "maximumSize [" + maximumSize + "] needs to be greater than current [" + lru.maxSize() + "]");
        }
    }

}
