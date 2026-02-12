package de.invesdwin.util.collections.factory.pool.set.linked;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.factory.pool.set.ICloseableSet;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

@NotThreadSafe
public class PooledLinkedSet<E> extends ObjectLinkedOpenHashSet<E> implements ICloseableSet<E> {

    private final SetLinkedPool<E> pool;

    PooledLinkedSet(final SetLinkedPool<E> pool) {
        this.pool = pool;
    }

    @Override
    public void close() {
        pool.returnObject(this);
    }

    public static <E> ICloseableSet<E> getInstance() {
        return SetLinkedPool.<E> getInstance().borrowObject();
    }

}
