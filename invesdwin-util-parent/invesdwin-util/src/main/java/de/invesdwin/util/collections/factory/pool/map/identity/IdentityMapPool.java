package de.invesdwin.util.collections.factory.pool.map.identity;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.pool.map.ICloseableMap;
import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
final class IdentityMapPool<K, V> extends AAgronaObjectPool<ICloseableMap<K, V>> {

    //CHECKSTYLE:OFF
    @SuppressWarnings("rawtypes")
    private static final IdentityMapPool INSTANCE = new IdentityMapPool();
    //CHECKSTYLE:ON

    private IdentityMapPool() {
        super(DEFAULT_MAX_POOL_SIZE * 10);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> IdentityMapPool<K, V> getInstance() {
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
        return new PooledIdentityMap<K, V>(this);
    }

}
