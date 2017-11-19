package de.invesdwin.util.collections.loadingcache.map;

import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.leastrecent.LRUMap;

@ThreadSafe
public class LRUMapLoadingCache<K, V> extends ASynchronizedLoadingCache<K, V> {

    public LRUMapLoadingCache(final Function<K, V> loadValue, final Integer maximumSize) {
        super(loadValue, new LRUMap<K, V>(maximumSize));
    }

    @Override
    public synchronized void increaseMaximumSize(final int maximumSize) {
        final LRUMap<K, V> lru = (LRUMap<K, V>) map;
        if (lru.maxSize() < maximumSize) {
            lru.setMaxSize(maximumSize);
        } else {
            throw new IllegalArgumentException(
                    "maximumSize [" + maximumSize + "] needs to be greater than current [" + lru.maxSize() + "]");
        }
    }

}
