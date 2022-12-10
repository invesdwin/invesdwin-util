package java.util.concurrent;

import java.util.concurrent.ConcurrentHashMap.KeySetView;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ConcurrentMapKeySetView<K, V> extends KeySetView<K, V> {

    public ConcurrentMapKeySetView(final ConcurrentHashMap<K, V> map, final V value) {
        super(map, value);
    }

}
