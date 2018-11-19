package de.invesdwin.util.collections.loadingcache.map;

import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@ThreadSafe
public class SynchronizedUnlimitedCachingLoadingCache<K, V> extends ASynchronizedMapLoadingCache<K, V> {

    public SynchronizedUnlimitedCachingLoadingCache(final Function<K, V> loadValue) {
        super(loadValue, new Object2ObjectOpenHashMap<>());
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        //ignore
    }

}
