package de.invesdwin.util.collections.factory.pool.deque;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
final class ArrayDequePool<E> extends AAgronaObjectPool<ICloseableDeque<E>> {

    @SuppressWarnings("rawtypes")
    private static final ArrayDequePool INSTANCE = new ArrayDequePool();

    private ArrayDequePool() {
        super(DEFAULT_MAX_POOL_SIZE * 10);
    }

    @SuppressWarnings("unchecked")
    public static <E> ArrayDequePool<E> getInstance() {
        return INSTANCE;
    }

    @Override
    protected boolean passivateObject(final ICloseableDeque<E> element) {
        if (!element.isEmpty()) {
            element.clear();
        }
        return true;
    }

    @Override
    protected ICloseableDeque<E> newObject() {
        return new PooledArrayDeque<E>(this);
    }

}
