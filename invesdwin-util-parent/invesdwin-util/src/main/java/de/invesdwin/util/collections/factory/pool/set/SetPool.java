package de.invesdwin.util.collections.factory.pool.set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
final class SetPool<E> extends AAgronaObjectPool<ICloseableSet<E>> {

    @SuppressWarnings("rawtypes")
    private static final SetPool INSTANCE = new SetPool();

    private SetPool() {
        super(DEFAULT_MAX_POOL_SIZE * 10);
    }

    @SuppressWarnings("unchecked")
    public static <E> SetPool<E> getInstance() {
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
        return new PooledSet<E>(this);
    }

}
