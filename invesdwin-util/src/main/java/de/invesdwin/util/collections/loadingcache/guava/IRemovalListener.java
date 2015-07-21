package de.invesdwin.util.collections.loadingcache.guava;

import com.google.common.cache.RemovalCause;

public interface IRemovalListener<K, V> {

    void onRemoval(K key, V value, RemovalCause cause);

}
