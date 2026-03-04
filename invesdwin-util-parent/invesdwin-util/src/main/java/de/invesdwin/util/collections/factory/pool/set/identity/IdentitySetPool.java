package de.invesdwin.util.collections.factory.pool.set.identity;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.pool.set.ICloseableSet;
import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
final class IdentitySetPool<E> extends AAgronaObjectPool<ICloseableSet<E>> {

    //CHECKSTYLE:OFF
    @SuppressWarnings("rawtypes")
    private static final IdentitySetPool INSTANCE = new IdentitySetPool();
    //CHECKSTYLE:ON

    private IdentitySetPool() {
        super(DEFAULT_MAX_POOL_SIZE * 10);
    }

    @SuppressWarnings("unchecked")
    public static <E> IdentitySetPool<E> getInstance() {
        return INSTANCE;
    }

    @Override
    protected boolean passivateObject(final ICloseableSet<E> element) {
        if (!element.isEmpty()) {
            element.clear();
        }
        return true;
    }

    @Override
    protected ICloseableSet<E> newObject() {
        return new PooledIdentitySet<E>(this);
    }

}
