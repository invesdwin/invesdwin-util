package de.invesdwin.util.collections.loadingcache.map;

import java.util.HashMap;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class UnlimitedCachingLoadingCache<K, V> extends ASynchronizedLoadingCache<K, V> {

    public UnlimitedCachingLoadingCache(final Function<K, V> loadValue) {
        super(loadValue, new HashMap<K, V>());
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        //ignore
    }

}
