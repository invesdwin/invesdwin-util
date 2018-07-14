package de.invesdwin.util.collections.loadingcache.guava;

public interface IRemovalListener<K, V> {

    void onRemoval(K key, V value);

}
