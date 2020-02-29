package de.invesdwin.util.collections.loadingcache;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

public interface ILoadingCache<K, V> {

    V get(K key);

    void clear();

    boolean containsKey(K key);

    V getIfPresent(K key);

    void remove(K key);

    void put(K key, V value);

    Set<Entry<K, V>> entrySet();

    int size();

    boolean isEmpty();

    Set<K> keySet();

    Collection<V> values();

    Map<K, V> asMap();

    void increaseMaximumSize(int maximumSize);

    V computeIfAbsent(K key, Function<K, V> mappingFunction);

}
