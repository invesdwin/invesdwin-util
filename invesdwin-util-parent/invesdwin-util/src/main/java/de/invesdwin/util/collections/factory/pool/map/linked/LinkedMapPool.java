package de.invesdwin.util.collections.factory.pool.map.linked;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.pool.map.ICloseableMap;
import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
final class LinkedMapPool<K, V> extends AAgronaObjectPool<ICloseableMap<K, V>> {

    @SuppressWarnings("rawtypes")
    private static final LinkedMapPool INSTANCE = new LinkedMapPool();

    private LinkedMapPool() {
        super(DEFAULT_MAX_POOL_SIZE * 10);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> LinkedMapPool<K, V> getInstance() {
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
        return new PooledLinkedMap<K, V>(this);
    }

}
