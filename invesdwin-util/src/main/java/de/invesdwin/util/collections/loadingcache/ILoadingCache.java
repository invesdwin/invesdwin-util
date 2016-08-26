package de.invesdwin.util.collections.loadingcache;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public interface ILoadingCache<K, V> {

    V get(final K key);

    void clear();

    boolean containsKey(final K key);

    void remove(final K key);

    void put(K key, V value);

    Set<Entry<K, V>> entrySet();

    int size();

    boolean isEmpty();

    Set<K> keySet();

    Collection<V> values();

    Map<K, V> asMap();

}
