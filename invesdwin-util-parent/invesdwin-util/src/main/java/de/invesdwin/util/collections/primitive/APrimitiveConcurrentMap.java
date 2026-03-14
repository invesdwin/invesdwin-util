package de.invesdwin.util.collections.primitive;

import java.util.AbstractMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.util.BucketHashUtil;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.padded.PaddedCloseableReentrantReadWriteLock;
import de.invesdwin.util.concurrent.lock.strategy.ILockingStrategy;
import it.unimi.dsi.fastutil.Function;

/**
 * Origin:
 * https://github.com/magicprinc/fastutil-concurrent-wrapper/blob/master/src/main/java/com/trivago/fastutilconcurrentwrapper/PrimitiveConcurrentMap.java
 * 
 * @see it.unimi.dsi.fastutil.Function
 * @see com.google.common.util.concurrent.Striped
 * @see org.jctools.maps.NonBlockingHashMapLong
 */
@ThreadSafe
public abstract class APrimitiveConcurrentMap<K, V> extends AbstractMap<K, V> implements IPrimitiveConcurrentMap {
    protected final PaddedCloseableReentrantReadWriteLock[] locks;
    protected final AtomicInteger size = new AtomicInteger();
    private final ILockingStrategy lockingStrategy;

    protected APrimitiveConcurrentMap(final PrimitiveConcurrentMapConfig config) {
        final int concurrencyLevel = config.getConcurrencyLevel();
        if (concurrencyLevel < 1 || concurrencyLevel > 100_000_000) {
            throw new IllegalArgumentException(
                    "concurrencyLevel must be between 1 and 100_000_000, but: " + concurrencyLevel);
        }
        this.locks = new PaddedCloseableReentrantReadWriteLock[concurrencyLevel];
        for (int i = 0; i < concurrencyLevel; i++) {
            locks[i] = new PaddedCloseableReentrantReadWriteLock();
        }
        this.lockingStrategy = config.getLockingStrategy();
    }//new

    /** Lock must be held! */
    protected abstract Function<K, V> mapAt(int index);

    protected ICloseableLock readAt(final int lockIndex) {
        return locks[lockIndex].readLocked(lockingStrategy);
    }

    protected ICloseableLock writeAt(final int lockIndex) {
        return locks[lockIndex].writeLocked(lockingStrategy);
    }

    @Override
    public int size() {
        return size.get();
    }

    /**
     * WARNING: should not be needed since size estimate should be good enough.
     */
    @Deprecated
    public int sizeForced() {
        int sum = 0;
        for (int i = 0; i < locks.length; i++) {
            try (ICloseableLock lock = readAt(i)) {
                sum += mapAt(i).size();
            }
        }
        return sum;
    }

    @Override
    public final boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public final void clear() {
        if (isEmpty()) {
            return;
        }
        synchronized (this) {
            if (isEmpty()) {
                return;
            }
            internalClear();
        }
    }

    /**
     * WARNING: should not be needed since size estimate should be good enough.
     */
    @Deprecated
    public final synchronized void clearForced() {
        internalClear();
    }

    private void internalClear() {
        for (int i = 0; i < locks.length; i++) {
            try (ICloseableLock lock = writeAt(i)) {
                mapAt(i).clear();
            }
        }
        size.set(0);
    }

    protected int getBucket(final long key) {
        return BucketHashUtil.bucket(key, locks.length);
    }

    protected int getBucket(final int key) {
        return BucketHashUtil.bucket(key, locks.length);// Integer.hashCode(key) == key
    }

    protected int getBucket(final Object key) {
        return BucketHashUtil.bucket(key, locks.length);
    }

    public static UnsupportedOperationException newUnmodifiableException() {
        return new UnsupportedOperationException("Unmodifiable, only reading methods supported");
    }

}