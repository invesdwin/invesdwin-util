package de.invesdwin.util.collections.loadingcache;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public interface ILoadingCache<K, V> {

    V get(K key);

    void clear();

    boolean containsKey(K key);

    void remove(K key);

    void put(K key, V value);

    Set<Entry<K, V>> entrySet();

    int size();

    boolean isEmpty();

    Set<K> keySet();

    Collection<V> values();

    Map<K, V> asMap();

}
