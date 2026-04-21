package de.invesdwin.util.collections.factory.pool.list.ints;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.ints.IntArrayList;

@NotThreadSafe
public class PooledIntArrayList extends IntArrayList implements ICloseableIntList {

    private final IntArrayListPool pool;

    PooledIntArrayList(final IntArrayListPool pool) {
        this.pool = pool;
    }

    @Override
    public void close() {
        pool.returnObject(this);
    }

    public static ICloseableIntList getInstance() {
        return IntArrayListPool.getInstance().borrowObject();
    }

}
