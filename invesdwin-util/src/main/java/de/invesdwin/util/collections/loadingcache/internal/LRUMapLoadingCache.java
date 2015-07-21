package de.invesdwin.util.collections.loadingcache.internal;

import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.collections.map.LRUMap;

@ThreadSafe
public class LRUMapLoadingCache<K, V> extends ASynchronizedLoadingCache<K, V> {

    @SuppressWarnings("unchecked")
    public LRUMapLoadingCache(final Function<K, V> loadValue, final Integer maximumSize) {
        //apache commons LRUMap is faster than adjusted LinkedHashMap!
        super(loadValue, new LRUMap(maximumSize));
    }

}
