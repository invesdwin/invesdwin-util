package de.invesdwin.util.collections.factory.pool.map.identity;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.DelegateMap;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.factory.pool.map.ICloseableMap;

@NotThreadSafe
public class PooledIdentityMap<K, V> extends DelegateMap<K, V> implements ICloseableMap<K, V> {

    private final IdentityMapPool<K, V> pool;

    PooledIdentityMap(final IdentityMapPool<K, V> pool) {
        super(ILockCollectionFactory.getInstance(false).newIdentityMap());
        this.pool = pool;
    }

    @Override
    public void close() {
        pool.returnObject(this);
    }

    public static <K, V> ICloseableMap<K, V> getInstance() {
        return IdentityMapPool.<K, V> getInstance().borrowObject();
    }

}
