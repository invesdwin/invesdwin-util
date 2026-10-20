package de.invesdwin.util.collections.array.primitive.queue.circular.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.primitive.queue.ILongPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.ILongPrimitiveQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularLongPrimitiveArrayQueue;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.factory.SynchronizedLockCollectionFactory;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class SynchronizedPrimitiveCircularQueueTest {

    @Test
    public void testSequentialInterface() {
        final ILongPrimitiveCircularQueue queue = new SynchronizedLongPrimitiveCircularQueue(
                new CircularLongPrimitiveArrayQueue(3));
        queue.circularAdd(1);
        queue.circularAdd(2);
        Assertions.assertThat(queue.get(0)).isEqualTo(1);
        Assertions.assertThat(queue.getReverse(0)).isEqualTo(2);
        Assertions.assertThat(queue.getOrMissing(5)).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        final long[] dst = new long[4];
        Assertions.assertThat(queue.getLongs(dst, 0, 0, 4)).isEqualTo(2);
        Assertions.assertThat(queue.evictAdd(3)).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        Assertions.assertThat(queue.isFull()).isTrue();
        Assertions.assertThat(queue.evictAdd(4)).isEqualTo(1);
        Assertions.assertThat(queue.offer(5)).isFalse();
        Assertions.assertThat(queue.element()).isEqualTo(2);
        Assertions.assertThat(queue.remove()).isEqualTo(2);
        queue.clear();
        Assertions.assertThat(queue.isEmpty()).isTrue();
        //element/remove on empty must throw, never return MISSING
        Assertions.assertThatThrownBy(() -> queue.remove()).isInstanceOf(FastNoSuchElementException.class);
        Assertions.assertThatThrownBy(() -> queue.element()).isInstanceOf(FastNoSuchElementException.class);
    }

    @Test
    public void testConcurrentRemoveNeverSeesTornState() throws InterruptedException {
        //drains a full ring from two threads: every remove() must either return a real value or throw --
        //returning MISSING would prove the check-then-act raced despite the wrapper
        final int capacity = 1000;
        final ILongPrimitiveCircularQueue queue = new SynchronizedLongPrimitiveCircularQueue(
                new CircularLongPrimitiveArrayQueue(capacity));
        for (long i = 0; i < capacity; i++) {
            queue.circularAdd(i);
        }
        final AtomicInteger removed = new AtomicInteger();
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final CountDownLatch start = new CountDownLatch(1);
        final Runnable drain = () -> {
            try {
                start.await();
                while (true) {
                    try {
                        final long value = queue.remove();
                        if (value == ILongPrimitiveQueue.MISSING_VALUE) {
                            //0 is a legitimate element, but MISSING for long is Long.MIN -- never stored here
                            throw new IllegalStateException("torn state: remove returned MISSING");
                        }
                        removed.incrementAndGet();
                    } catch (final FastNoSuchElementException e) {
                        return;
                    }
                }
            } catch (final Throwable e) {
                failure.compareAndSet(null, e);
            }
        };
        final Thread t1 = new Thread(drain, "drain-1");
        final Thread t2 = new Thread(drain, "drain-2");
        t1.start();
        t2.start();
        start.countDown();
        t1.join(30000);
        t2.join(30000);
        Assertions.assertThat(failure.get()).isNull();
        Assertions.assertThat(removed.get()).isEqualTo(capacity);
        Assertions.assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    public void testFactoryPrimitiveQueue() {
        final ILockCollectionFactory disabled = ILockCollectionFactory.getInstance(false);
        Assertions.assertThat(disabled.primitive().newCircularLongQueue(4)).isInstanceOf(
                CircularLongPrimitiveArrayQueue.class);
        Assertions.assertThat(disabled.primitive().newCircularGenericQueue(String.class, 3).capacity()).isEqualTo(3);
        final ILockCollectionFactory synced = SynchronizedLockCollectionFactory.INSTANCE;
        final ILongPrimitiveCircularQueue queue = synced.primitive().newCircularLongQueue(2);
        Assertions.assertThat(queue).isInstanceOf(SynchronizedLongPrimitiveCircularQueue.class);
        queue.circularAdd(7);
        queue.circularAdd(8);
        Assertions.assertThat(queue.getReverse(0)).isEqualTo(8);
        Assertions.assertThat(queue.evictAdd(9)).isEqualTo(7);
        Assertions.assertThat(synced.primitive().newCircularGenericQueue(String.class, 2).getOrMissing(9)).isNull();
        final de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray store = de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray.newInstance(
                2);
        final ILongPrimitiveCircularQueue backed = disabled.primitive().newCircularLongQueue(store);
        backed.circularAdd(4);
        Assertions.assertThat(store.get(0)).isEqualTo(4);
    }


    @Test
    public void testConcurrentRemoveNeverSeesTornStateOnLockedVariant() throws InterruptedException {
        //same torture for the ILock-based sibling
        //drains a full ring from two threads: every remove() must either return a real value or throw --
        //returning MISSING would prove the check-then-act raced despite the wrapper
        final int capacity = 1000;
        final ILongPrimitiveCircularQueue queue = new LockedLongPrimitiveCircularQueue(
                new CircularLongPrimitiveArrayQueue(capacity));
        for (long i = 0; i < capacity; i++) {
            queue.circularAdd(i);
        }
        final AtomicInteger removed = new AtomicInteger();
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final CountDownLatch start = new CountDownLatch(1);
        final Runnable drain = () -> {
            try {
                start.await();
                while (true) {
                    try {
                        final long value = queue.remove();
                        if (value == ILongPrimitiveQueue.MISSING_VALUE) {
                            //0 is a legitimate element, but MISSING for long is Long.MIN -- never stored here
                            throw new IllegalStateException("torn state: remove returned MISSING");
                        }
                        removed.incrementAndGet();
                    } catch (final FastNoSuchElementException e) {
                        return;
                    }
                }
            } catch (final Throwable e) {
                failure.compareAndSet(null, e);
            }
        };
        final Thread t1 = new Thread(drain, "drain-1");
        final Thread t2 = new Thread(drain, "drain-2");
        t1.start();
        t2.start();
        start.countDown();
        t1.join(30000);
        t2.join(30000);
        Assertions.assertThat(failure.get()).isNull();
        Assertions.assertThat(removed.get()).isEqualTo(capacity);
        Assertions.assertThat(queue.isEmpty()).isTrue();
    }
}
