package de.invesdwin.util.collections.factory.pool.set.identity;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.DelegateSet;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.factory.pool.set.ICloseableSet;

@NotThreadSafe
public class PooledIdentitySet<E> extends DelegateSet<E> implements ICloseableSet<E> {

    private final IdentitySetPool<E> pool;

    PooledIdentitySet(final IdentitySetPool<E> pool) {
        super(ILockCollectionFactory.getInstance(false).newIdentitySet());
        this.pool = pool;
    }

    @Override
    public void close() {
        pool.returnObject(this);
    }

    public static <E> ICloseableSet<E> getInstance() {
        return IdentitySetPool.<E> getInstance().borrowObject();
    }

}
