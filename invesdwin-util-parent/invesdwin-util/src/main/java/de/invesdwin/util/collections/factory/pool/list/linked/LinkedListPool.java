package de.invesdwin.util.collections.factory.pool.list.linked;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.pool.list.ICloseableList;
import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
final class LinkedListPool<E> extends AAgronaObjectPool<ICloseableList<E>> {

    @SuppressWarnings("rawtypes")
    private static final LinkedListPool INSTANCE = new LinkedListPool();

    private LinkedListPool() {
        super(DEFAULT_MAX_POOL_SIZE * 10);
    }

    @SuppressWarnings("unchecked")
    public static <E> LinkedListPool<E> getInstance() {
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
        return new PooledLinkedList<E>(this);
    }

}
