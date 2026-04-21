package de.invesdwin.util.collections.factory.pool.list.longs;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.longs.LongArrayList;

@NotThreadSafe
public class PooledLongArrayList extends LongArrayList implements ICloseableLongList {

    private final LongArrayListPool pool;

    PooledLongArrayList(final LongArrayListPool pool) {
        this.pool = pool;
    }

    @Override
    public void close() {
        pool.returnObject(this);
    }

    public static ICloseableLongList getInstance() {
        return LongArrayListPool.getInstance().borrowObject();
    }

}
