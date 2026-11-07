package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;

/**
 * The two reader counters of a read-write lock are different questions and must not be conflated by the wrappers:
 * {@code getReadHoldCount()} is what THIS thread holds (the count a caller must release before it may take the write
 * lock), while {@code getReadLockCount()} is how many read locks are held for the lock ACROSS THREADS (what a writer
 * asks when it wants to know whether readers of its table are active at all). Every wrapper used to answer the second
 * question with the first, so a thread that holds nothing reported zero readers no matter how many were reading - a
 * writer's own diagnostic of a table being held by iterators silently claimed nobody held it.
 */
@NotThreadSafe
public class ReentrantReadWriteLockReaderCountsTest {

    @Test
    public void testReadLockCountIsCrossThreadAndReadHoldCountIsPerThread() throws Exception {
        final IReentrantReadWriteLock lock = Locks.newReentrantReadWriteLock("readerCountsTest");
        final ILock ownReadLock = lock.readLock();
        ownReadLock.lock();
        ownReadLock.lock();
        try {
            Assertions.checkEquals(2, lock.getReadHoldCount(),
                    "this thread's two nested read holds must be counted per thread");
            Assertions.checkEquals(2, lock.getReadLockCount(),
                    "the cross-thread reader count must see this thread's read locks");

            final CountDownLatch foreignHolding = new CountDownLatch(1);
            final CountDownLatch release = new CountDownLatch(1);
            final Thread foreign = new Thread(() -> {
                final ILock foreignReadLock = lock.readLock();
                foreignReadLock.lock();
                try {
                    foreignHolding.countDown();
                    if (!awaitQuietly(release)) {
                        return;
                    }
                } finally {
                    foreignReadLock.unlock();
                }
            }, "test-foreign-reader");
            foreign.setDaemon(true);
            foreign.start();
            try {
                Assertions.checkTrue(foreignHolding.await(10, TimeUnit.SECONDS),
                        "the foreign reader must hold a read lock before the counts are checked");
                Assertions.checkEquals(2, lock.getReadHoldCount(),
                        "another thread's read hold must not enter this thread's hold count");
                Assertions.checkEquals(3, lock.getReadLockCount(),
                        "the cross-thread reader count must see the foreign reader as well");
            } finally {
                release.countDown();
                foreign.join(30000);
            }
            Assertions.checkEquals(2, lock.getReadLockCount(),
                    "the foreign reader must have left the cross-thread count again: " + foreign.isAlive());
        } finally {
            ownReadLock.unlock();
            ownReadLock.unlock();
        }
        Assertions.checkEquals(0, lock.getReadLockCount(), "no reader may remain after the unlocks");
        Assertions.checkEquals(0, lock.getReadHoldCount(), "no hold may remain for this thread after the unlocks");
    }

    private static boolean awaitQuietly(final CountDownLatch latch) {
        try {
            latch.await(60, TimeUnit.SECONDS);
            return true;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

}
