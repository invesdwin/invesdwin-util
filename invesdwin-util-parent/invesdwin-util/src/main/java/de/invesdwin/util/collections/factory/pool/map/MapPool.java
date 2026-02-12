package de.invesdwin.util.collections.factory.pool.map;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
final class MapPool<K, V> extends AAgronaObjectPool<ICloseableMap<K, V>> {

    @SuppressWarnings("rawtypes")
    private static final MapPool INSTANCE = new MapPool();

    private MapPool() {
        super(DEFAULT_MAX_POOL_SIZE * 10);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> MapPool<K, V> getInstance() {
        return INSTANCE;
    }

    @Override
    protected boolean passivateObject(final ICloseableMap<K, V> element) {
        if (!element.isEmpty()) {
            element.clear();
        }
        return true;
    }

    @Override
    protected ICloseableMap<K, V> newObject() {
        return new PooledMap<K, V>(this);
    }

}
