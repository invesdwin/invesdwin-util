package de.invesdwin.util.collections.factory.pool.list;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
final class ArrayListPool<E> extends AAgronaObjectPool<ICloseableList<E>> {

    @SuppressWarnings("rawtypes")
    private static final ArrayListPool INSTANCE = new ArrayListPool();

    private ArrayListPool() {
        super(DEFAULT_MAX_POOL_SIZE * 10);
    }

    @SuppressWarnings("unchecked")
    public static <E> ArrayListPool<E> getInstance() {
        return INSTANCE;
    }

    @Override
    protected boolean passivateObject(final ICloseableList<E> element) {
        if (!element.isEmpty()) {
            element.clear();
        }
        return true;
    }

    @Override
    protected ICloseableList<E> newObject() {
        return new PooledArrayList<E>(this);
    }

}
