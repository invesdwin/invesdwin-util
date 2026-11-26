package de.invesdwin.util.collections.array.large.queue.circular.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.large.queue.ILongLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.ILongLargeQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularLongLargeArrayQueue;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class SynchronizedCircularLargeQueueTest {

    @Test
    public void testSequentialInterface() {
        final ILongLargeCircularQueue queue = new SynchronizedLongLargeCircularQueue(
                new CircularLongLargeArrayQueue(3));
        queue.circularAdd(1);
        queue.circularAdd(2);
        Assertions.assertThat(queue.get(0)).isEqualTo(1);
        Assertions.assertThat(queue.getReverse(0)).isEqualTo(2);
        Assertions.assertThat(queue.getOrMissing(5)).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        Assertions.assertThat(queue.isInRange(1)).isTrue();
        final long[] dst = new long[4];
        Assertions.assertThat(queue.getLongs(dst, 0, 0, 4)).isEqualTo(2);
        Assertions.assertThat(dst[0]).isEqualTo(1);
        Assertions.assertThat(dst[2]).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        Assertions.assertThat(queue.evictAdd(3)).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        Assertions.assertThat(queue.isFull()).isTrue();
        Assertions.assertThat(queue.evictAdd(4)).isEqualTo(1);
        Assertions.assertThat(queue.offer(5)).isFalse();
        Assertions.assertThat(queue.poll()).isEqualTo(2);
        queue.clear();
        Assertions.assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    public void testConcurrentSmoke() throws InterruptedException {
        final int capacity = 1000;
        final int perThread = 10000;
        final ILongLargeCircularQueue queue = new SynchronizedLongLargeCircularQueue(
                new CircularLongLargeArrayQueue(capacity));
        final CountDownLatch start = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final Thread[] threads = new Thread[2];
        for (int t = 0; t < threads.length; t++) {
            final int id = t;
            threads[t] = new Thread(() -> {
                try {
                    start.await();
                    for (int i = 0; i < perThread; i++) {
                        queue.circularAdd((long) id * perThread + i);
                        if (i % 100 == 0) {
                            //exercise clamped reads and window copy concurrently
                            queue.getOrMissing(i);
                            queue.getReverseOrMissing(i);
                            queue.getLongs(new long[8], 0, i - 4L, 8);
                        }
                    }
                } catch (final Throwable e) {
                    failure.compareAndSet(null, e);
                }
            }, "locked-queue-test-" + t);
            threads[t].start();
        }
        start.countDown();
        for (final Thread thread : threads) {
            thread.join(30000);
            Assertions.assertThat(thread.isAlive()).as("thread " + thread.getName() + " must finish").isFalse();
        }
        Assertions.assertThat(failure.get()).isNull();
        Assertions.assertThat(queue.size()).isEqualTo(capacity);
    }

    @Test
    public void testLockedVariantKeptBesideMonitor() {
        //ILock-based sibling stays supported (Edwin): default ctor builds its own lock, shared
        //ILock ctor is the series-seam; atomic remove()/element() overrides must hold there too
        final ILongLargeCircularQueue own = new LockedLongLargeCircularQueue(new CircularLongLargeArrayQueue(3));
        own.circularAdd(1);
        own.circularAdd(2);
        Assertions.assertThat(own.get(1)).isEqualTo(2);
        own.clear();
        Assertions.assertThatThrownBy(() -> own.remove()).isInstanceOf(FastNoSuchElementException.class);
        Assertions.assertThatThrownBy(() -> own.element()).isInstanceOf(FastNoSuchElementException.class);
        final ILock shared = Locks.newReentrantLock("sharedSeriesLock");
        final ILongLargeCircularQueue q1 = new LockedLongLargeCircularQueue(new CircularLongLargeArrayQueue(2), shared);
        final ILongLargeCircularQueue q2 = new LockedLongLargeCircularQueue(new CircularLongLargeArrayQueue(2), shared);
        q1.circularAdd(7);
        q2.circularAdd(8);
        Assertions.assertThat(q1.get(0)).isEqualTo(7);
        Assertions.assertThat(q2.getReverse(0)).isEqualTo(8);
        Assertions.assertThat(q1.remove()).isEqualTo(7);
    }

}
