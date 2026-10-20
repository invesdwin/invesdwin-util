package de.invesdwin.util.collections.factory;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.queue.ILongLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularLongLargeArrayQueue;
import de.invesdwin.util.collections.array.large.queue.circular.concurrent.SynchronizedLongLargeCircularQueue;

@NotThreadSafe
public class LockCollectionFactoryCircularQueueTest {

    @Test
    public void testDisabledReturnsRaw() {
        final ILockCollectionFactory factory = ILockCollectionFactory.getInstance(false);
        final ILongLargeCircularQueue queue = factory.large().newCircularLongQueue(4);
        Assertions.assertThat(queue).isInstanceOf(CircularLongLargeArrayQueue.class);
        Assertions.assertThat(queue.capacity()).isEqualTo(4);
        queue.circularAdd(1);
        queue.circularAdd(2);
        Assertions.assertThat(queue.getReverse(0)).isEqualTo(2);
    }

    @Test
    public void testSynchronizedReturnsWrapped() {
        final ILockCollectionFactory factory = ILockCollectionFactory.getInstance(true);
        final ILongLargeCircularQueue queue = factory.large().newCircularLongQueue(4);
        Assertions.assertThat(queue).isInstanceOf(SynchronizedLongLargeCircularQueue.class);
        queue.circularAdd(1);
        queue.circularAdd(2);
        queue.circularAdd(3);
        Assertions.assertThat(queue.get(0)).isEqualTo(1);
        Assertions.assertThat(queue.getReverse(0)).isEqualTo(3);
        Assertions.assertThat(queue.evictAdd(4)).isEqualTo(ILongLargeCircularQueue.MISSING_VALUE);
    }

    @Test
    public void testBackingStoreOverload() {
        final ILockCollectionFactory factory = ILockCollectionFactory.getInstance(false);
        final ILongLargeArray values = ILongLargeArray.newInstance(2);
        final ILongLargeCircularQueue queue = factory.large().newCircularLongQueue(values);
        Assertions.assertThat(queue.capacity()).isEqualTo(2);
        queue.circularAdd(7);
        Assertions.assertThat(values.get(0)).isEqualTo(7);
    }

    @Test
    public void testGenericOverloads() {
        final ILockCollectionFactory disabled = ILockCollectionFactory.getInstance(false);
        Assertions.assertThat(disabled.large().newCircularGenericQueue(String.class, 3).capacity()).isEqualTo(3);
        final ILockCollectionFactory synced = ILockCollectionFactory.getInstance(true);
        final de.invesdwin.util.collections.array.large.queue.IGenericLargeCircularQueue<String> queue = synced.large().newCircularGenericQueue(
                String.class, 3);
        queue.circularAdd("a");
        Assertions.assertThat(queue.get(0)).isEqualTo("a");
        Assertions.assertThat(queue.getOrMissing(9)).isNull();
    }

}
