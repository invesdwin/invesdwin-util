package de.invesdwin.util.collections.loadingcache.map;

import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.eviction.IEvictionMap;

@ThreadSafe
public class SynchronizedEvictionMapLoadingCache<K, V> extends ASynchronizedMapLoadingCache<K, V> {

    public SynchronizedEvictionMapLoadingCache(final Function<K, V> loadValue, final IEvictionMap<K, V> evictionMap) {
        super(loadValue, evictionMap);
    }

    @Override
    public synchronized void increaseMaximumSize(final int maximumSize) {
        final IEvictionMap<K, V> lru = (IEvictionMap<K, V>) map;
        if (lru.getMaximumSize() < maximumSize) {
            lru.setMaximumSize(maximumSize);
        } else {
            throw new IllegalArgumentException("maximumSize [" + maximumSize + "] needs to be greater than current ["
                    + lru.getMaximumSize() + "]");
        }
    }

}
