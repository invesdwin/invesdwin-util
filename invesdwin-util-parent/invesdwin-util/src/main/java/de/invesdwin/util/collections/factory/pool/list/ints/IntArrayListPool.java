package de.invesdwin.util.collections.factory.pool.list.ints;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
final class IntArrayListPool extends AAgronaObjectPool<ICloseableIntList> {

    private static final IntArrayListPool INSTANCE = new IntArrayListPool();

    private IntArrayListPool() {
        super(DEFAULT_MAX_POOL_SIZE * 10);
    }

    public static IntArrayListPool getInstance() {
        return INSTANCE;
    }

    @Override
    protected boolean passivateObject(final ICloseableIntList element) {
        if (!element.isEmpty()) {
            element.clear();
        }
        return true;
    }

    @Override
    protected ICloseableIntList newObject() {
        return new PooledIntArrayList(this);
    }

}
