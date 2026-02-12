package de.invesdwin.util.collections.factory.pool.list;

import java.util.ArrayList;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PooledArrayList<E> extends ArrayList<E> implements ICloseableList<E> {

    private final ArrayListPool<E> pool;

    PooledArrayList(final ArrayListPool<E> pool) {
        this.pool = pool;
    }

    @Override
    public void close() {
        pool.returnObject(this);
    }

    public static <E> ICloseableList<E> getInstance() {
        return ArrayListPool.<E> getInstance().borrowObject();
    }

}
