package de.invesdwin.util.collections.factory.pool.set;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@NotThreadSafe
public class PooledSet<E> extends ObjectOpenHashSet<E> implements ICloseableSet<E> {

    private final SetPool<E> pool;

    PooledSet(final SetPool<E> pool) {
        this.pool = pool;
    }

    @Override
    public void close() {
        pool.returnObject(this);
    }

    public static <E> ICloseableSet<E> getInstance() {
        return SetPool.<E> getInstance().borrowObject();
    }

}
