package de.invesdwin.util.collections.loadingcache.caffeine;

import com.github.benmanes.caffeine.cache.RemovalCause;

public interface IRemovalListener<K, V> {

    void onRemoval(K key, V value, RemovalCause cause);

}
