package de.invesdwin.util.collections.primitive;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.primitive.util.BucketHashUtil;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.concurrent.lock.padded.PaddedCloseableReentrantReadWriteLock;
import it.unimi.dsi.fastutil.Function;
import jakarta.validation.constraints.Positive;

/**
 * Origin:
 * https://github.com/magicprinc/fastutil-concurrent-wrapper/blob/master/src/main/java/com/trivago/fastutilconcurrentwrapper/PrimitiveConcurrentMap.java
 * 
 * @see it.unimi.dsi.fastutil.Function
 * @see com.google.common.util.concurrent.Striped
 * @see org.jctools.maps.NonBlockingHashMapLong
 */
@ThreadSafe
public abstract class APrimitiveConcurrentMap<K, V> implements IPrimitiveConcurrentMap {
    private final PaddedCloseableReentrantReadWriteLock[] locks;

    protected APrimitiveConcurrentMap(@Positive final int concurrencyLevel) {
        if (concurrencyLevel < 1 || concurrencyLevel > 100_000_000) {
            throw new IllegalArgumentException(
                    "concurrencyLevel must be between 1 and 100_000_000, but: " + concurrencyLevel);
        }
        this.locks = new PaddedCloseableReentrantReadWriteLock[concurrencyLevel];
        for (int i = 0; i < concurrencyLevel; i++) {
            locks[i] = new PaddedCloseableReentrantReadWriteLock();
        }
    }//new

    /** Lock must be held! */
    protected abstract Function<K, V> mapAt(int index);

    protected ICloseableLock readAt(final int lockIndex) {
        return locks[lockIndex].readLocked();
    }

    protected ICloseableLock writeAt(final int lockIndex) {
        return locks[lockIndex].writeLocked();
    }

    protected ReentrantReadWriteLock.ReadLock readLock(final int lockIndex) {
        return locks[lockIndex].readLock();
    }

    protected ReentrantReadWriteLock.WriteLock writeLock(final int lockIndex) {
        return locks[lockIndex].writeLock();
    }

    @Override
    public int size() {
        int sum = 0;
        for (int i = 0; i < locks.length; i++) {
            try (ICloseableLock lock = readAt(i)) {
                sum += mapAt(i).size();
            }
        }
        return sum;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < locks.length; i++) {
            try (ICloseableLock lock = readAt(i)) {
                final boolean nonEmpty = mapAt(i).size() > 0;
                if (nonEmpty) {
                    return false;
                }
            }
        }
        return true;// all sub-maps are empty
    }

    @Override
    public void clear() {
        for (int i = 0; i < locks.length; i++) {
            try (ICloseableLock lock = writeAt(i)) {
                mapAt(i).clear();
            }
        }
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
}