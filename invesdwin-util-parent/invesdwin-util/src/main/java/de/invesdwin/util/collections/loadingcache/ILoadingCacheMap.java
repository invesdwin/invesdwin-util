package de.invesdwin.util.collections.loadingcache;

import java.util.Map;

public interface ILoadingCacheMap<K, V> extends Map<K, V> {

    V getIfPresent(K key);

}
