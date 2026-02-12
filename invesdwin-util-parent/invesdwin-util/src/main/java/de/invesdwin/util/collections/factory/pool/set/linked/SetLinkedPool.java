package de.invesdwin.util.collections.factory.pool.set.linked;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.pool.set.ICloseableSet;
import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
final class SetLinkedPool<E> extends AAgronaObjectPool<ICloseableSet<E>> {

    @SuppressWarnings("rawtypes")
    private static final SetLinkedPool INSTANCE = new SetLinkedPool();

    private SetLinkedPool() {
        super(DEFAULT_MAX_POOL_SIZE * 10);
    }

    @SuppressWarnings("unchecked")
    public static <E> SetLinkedPool<E> getInstance() {
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
        return new PooledLinkedSet<E>(this);
    }

}
