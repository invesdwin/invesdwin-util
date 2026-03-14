package de.invesdwin.util.collections.factory.pool.map.linked;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.factory.pool.map.ICloseableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

@NotThreadSafe
public class PooledLinkedMap<K, V> extends Object2ObjectLinkedOpenHashMap<K, V> implements ICloseableMap<K, V> {

    private final LinkedMapPool<K, V> pool;

    PooledLinkedMap(final LinkedMapPool<K, V> pool) {
        this.pool = pool;
    }

    @Override
    public void close() {
        pool.returnObject(this);
    }

    public static <K, V> ICloseableMap<K, V> getInstance() {
        return LinkedMapPool.<K, V> getInstance().borrowObject();
    }

}
