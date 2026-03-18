package de.invesdwin.util.collections.factory.pool.deque;

import java.util.ArrayDeque;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PooledArrayDeque<E> extends ArrayDeque<E> implements ICloseableDeque<E> {

    private final ArrayDequePool<E> pool;

    PooledArrayDeque(final ArrayDequePool<E> pool) {
        this.pool = pool;
    }

    @Override
    public void close() {
        pool.returnObject(this);
    }

    public static <E> ICloseableDeque<E> getInstance() {
        return ArrayDequePool.<E> getInstance().borrowObject();
    }

}
