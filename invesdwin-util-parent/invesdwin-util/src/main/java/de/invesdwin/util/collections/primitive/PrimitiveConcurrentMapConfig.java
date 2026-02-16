package de.invesdwin.util.collections.primitive;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.strategy.DefaultLockingStrategy;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import de.invesdwin.util.math.Integers;

@NotThreadSafe
public class PrimitiveConcurrentMapConfig extends AValueObject {
    public static final PrimitiveConcurrentMapConfig DEFAULT = new PrimitiveConcurrentMapConfig() {

        private UnsupportedOperationException newImmutableException() {
            return new UnsupportedOperationException("Default config is immutable");
        }

        @Override
        public PrimitiveConcurrentMapConfig setConcurrencyLevel(final int concurrencyLevel) {
            throw newImmutableException();
        }

        @Override
        public PrimitiveConcurrentMapConfig setInitialCapacity(final int initialCapacity) {
            throw newImmutableException();
        }

        @Override
        public PrimitiveConcurrentMapConfig setLoadFactor(final float loadFactor) {
            throw newImmutableException();
        }

        @Override
        public void setLockingStrategy(final ILockingStrategy lockingStrategy) {
            throw newImmutableException();
        }
    };
    protected int concurrencyLevel = ILockCollectionFactory.DEFAULT_CONCURRENCY_LEVEL;
    protected int initialCapacity = ILockCollectionFactory.DEFAULT_INITIAL_SIZE;
    protected float loadFactor = ILockCollectionFactory.DEFAULT_LOAD_FACTOR;
    protected ILockingStrategy lockingStrategy = Locks.getLockingStrategy();

    public PrimitiveConcurrentMapConfig() {}

    public PrimitiveConcurrentMapConfig setConcurrencyLevel(final int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public void setLockingStrategy(final ILockingStrategy lockingStrategy) {
        if (lockingStrategy == null) {
            this.lockingStrategy = DefaultLockingStrategy.INSTANCE;
        } else {
            this.lockingStrategy = lockingStrategy;
        }
    }

    public ILockingStrategy getLockingStrategy() {
        return lockingStrategy;
    }

    public PrimitiveConcurrentMapConfig setInitialCapacity(final int initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    public PrimitiveConcurrentMapConfig setLoadFactor(final float loadFactor) {
        this.loadFactor = loadFactor;
        return this;
    }

    public float getLoadFactor() {
        return loadFactor;
    }

    public int getBucketCapacity() {
        return newBucketCapacity(initialCapacity, concurrencyLevel);
    }

    public static int newBucketCapacity(final int initialCapacity, final int concurrencyLevel) {
        return Integers.max(initialCapacity / concurrencyLevel, ILockCollectionFactory.DEFAULT_INITIAL_SIZE);
    }

}