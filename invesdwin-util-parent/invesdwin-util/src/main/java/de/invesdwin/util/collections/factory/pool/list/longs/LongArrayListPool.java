package de.invesdwin.util.collections.factory.pool.list.longs;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
final class LongArrayListPool extends AAgronaObjectPool<ICloseableLongList> {

    private static final LongArrayListPool INSTANCE = new LongArrayListPool();

    private LongArrayListPool() {
        super(DEFAULT_MAX_POOL_SIZE * 10);
    }

    public static LongArrayListPool getInstance() {
        return INSTANCE;
    }

    @Override
    protected boolean passivateObject(final ICloseableLongList element) {
        if (!element.isEmpty()) {
            element.clear();
        }
        return true;
    }

    @Override
    protected ICloseableLongList newObject() {
        return new PooledLongArrayList(this);
    }

}
