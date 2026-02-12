package de.invesdwin.util.collections.factory.pool.list.linked;

import java.util.LinkedList;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.factory.pool.list.ICloseableList;

@NotThreadSafe
public class PooledLinkedList<E> extends LinkedList<E> implements ICloseableList<E> {

    private final LinkedListPool<E> pool;

    PooledLinkedList(final LinkedListPool<E> pool) {
        this.pool = pool;
    }

    @Override
    public void close() {
        pool.returnObject(this);
    }

    public static <E> ICloseableList<E> getInstance() {
        return LinkedListPool.<E> getInstance().borrowObject();
    }

}
