package de.invesdwin.util.collections.factory.pool.map;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@NotThreadSafe
public class PooledMap<K, V> extends Object2ObjectOpenHashMap<K, V> implements ICloseableMap<K, V> {

    private final MapPool<K, V> pool;

    PooledMap(final MapPool<K, V> pool) {
        this.pool = pool;
    }

    @Override
    public void close() {
        pool.returnObject(this);
    }

    public static <K, V> ICloseableMap<K, V> getInstance() {
        return MapPool.<K, V> getInstance().borrowObject();
    }

}
