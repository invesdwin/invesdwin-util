package de.invesdwin.util.collections.array.primitive.queue.circular;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGenerators;
import de.invesdwin.util.collections.array.primitive.queue.ILongPrimitiveQueue;

/**
 * Int-indexed mirror of {@code CircularLargeQueueApiTest}: differential against an ArrayList oracle across capacities
 * (conditional-subtract index math with the long-guarded int cursor), clamped reads, window copy with int srcIndex
 * overflow probing, pretend bookkeeping and the generic eviction guard.
 */
@NotThreadSafe
public class CircularPrimitiveQueueApiTest {

    private static final int[] CAPACITIES = { 1, 2, 3, 5, 7 };

    @Test
    public void testOracleDifferentialAllCapacities() {
        for (final int capacity : CAPACITIES) {
            final CircularLongPrimitiveArrayQueue queue = new CircularLongPrimitiveArrayQueue(capacity);
            final List<Long> oracle = new ArrayList<>();
            final IRandomGenerator rand = PseudoRandomGenerators.newPseudoRandom(42 + capacity);
            for (int op = 0; op < 200; op++) {
                final long value = rand.nextInt(0, 1000);
                //pretend* not mixed in here: it exposes physical slot contents no logical oracle can model.
                switch (rand.nextInt(0, 6)) {
                case 0:
                case 1: {
                    queue.circularAdd(value);
                    oracle.add(value);
                    if (oracle.size() > capacity) {
                        oracle.remove(0);
                    }
                    break;
                }
                case 2: {
                    queue.circularPrepend(value);
                    oracle.add(0, value);
                    if (oracle.size() > capacity) {
                        oracle.remove(oracle.size() - 1);
                    }
                    break;
                }
                case 3: {
                    final long evicted = queue.evictAdd(value);
                    final long expectedEvicted = oracle.size() == capacity ? oracle.remove(0)
                            : ILongPrimitiveQueue.MISSING_VALUE;
                    oracle.add(value);
                    Assertions.assertThat(evicted).isEqualTo(expectedEvicted);
                    break;
                }
                case 4: {
                    final long evicted = queue.evictPrepend(value);
                    final long expectedEvicted = oracle.size() == capacity ? oracle.remove(oracle.size() - 1)
                            : ILongPrimitiveQueue.MISSING_VALUE;
                    oracle.add(0, value);
                    Assertions.assertThat(evicted).isEqualTo(expectedEvicted);
                    break;
                }
                case 5: {
                    if (!queue.isEmpty()) {
                        final long first = queue.poll();
                        final long expected = oracle.remove(0);
                        Assertions.assertThat(first).isEqualTo(expected);
                    } else {
                        queue.clear();
                        oracle.clear();
                    }
                    break;
                }
                default:
                    throw new IllegalStateException();
                }
                Assertions.assertThat(queue.size()).as("size @cap" + capacity).isEqualTo(oracle.size());
                Assertions.assertThat(queue.toString()).as("toString @cap" + capacity).isEqualTo(oracle.toString());
                for (int i = 0; i < oracle.size(); i++) {
                    Assertions.assertThat(queue.get(i)).isEqualTo(oracle.get(i));
                    Assertions.assertThat(queue.getOrMissing(i)).isEqualTo(oracle.get(i));
                    Assertions.assertThat(queue.getReverse(oracle.size() - 1 - i)).isEqualTo(oracle.get(i));
                    Assertions.assertThat(queue.getReverseOrMissing(oracle.size() - 1 - i)).isEqualTo(oracle.get(i));
                }
            }
        }
    }

    @Test
    public void testClampedReadsNeverThrow() {
        final CircularLongPrimitiveArrayQueue queue = new CircularLongPrimitiveArrayQueue(3);
        for (final int idx : new int[] { -1, 0, 1, 2, 3, Integer.MIN_VALUE, Integer.MAX_VALUE }) {
            Assertions.assertThat(queue.getOrMissing(idx)).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
            Assertions.assertThat(queue.getReverseOrMissing(idx)).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
            Assertions.assertThat(queue.isInRange(idx)).isFalse();
        }
        queue.circularAdd(10);
        queue.circularAdd(20);
        Assertions.assertThat(queue.getOrMissing(0)).isEqualTo(10);
        Assertions.assertThat(queue.getOrMissing(2)).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        Assertions.assertThat(queue.getReverseOrMissing(0)).isEqualTo(20);
        Assertions.assertThatThrownBy(() -> queue.get(2)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void testWindowCopy() {
        final CircularLongPrimitiveArrayQueue queue = new CircularLongPrimitiveArrayQueue(5);
        for (long i = 1; i <= 7; i++) {
            //leaves [3, 4, 5, 6, 7] after wrap
            queue.circularAdd(i);
        }
        final long[] dst = new long[8];
        Assertions.assertThat(queue.getLongs(dst, 0, 0, 5)).isEqualTo(5);
        Assertions.assertThat(dst[0]).isEqualTo(3);
        Assertions.assertThat(dst[4]).isEqualTo(7);
        //window reaching past the tail: MISSING fill, real count only
        java.util.Arrays.fill(dst, 0);
        Assertions.assertThat(queue.getLongs(dst, 0, 3, 5)).isEqualTo(2);
        Assertions.assertThat(dst[2]).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        //window starting before zero: MISSING fill on the left
        java.util.Arrays.fill(dst, 0);
        Assertions.assertThat(queue.getLongs(dst, 2, -2, 4)).isEqualTo(2);
        Assertions.assertThat(dst[2]).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        Assertions.assertThat(dst[3]).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        Assertions.assertThat(dst[4]).isEqualTo(3);
        Assertions.assertThat(dst[5]).isEqualTo(4);
        //int overflow on srcIndex + length must not throw or wrap into range: all MISSING fill
        java.util.Arrays.fill(dst, 0);
        Assertions.assertThat(queue.getLongs(dst, 0, Integer.MAX_VALUE - 1, 4)).isEqualTo(0);
        Assertions.assertThat(dst[0]).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        //destination guards
        Assertions.assertThatThrownBy(() -> queue.getLongs(new long[4], 2, 0, 3))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> queue.getLongs(new long[4], -1, 0, 1))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> queue.getLongs(new long[4], 0, 0, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testPretendBookkeeping() {
        final CircularLongPrimitiveArrayQueue queue = new CircularLongPrimitiveArrayQueue(3);
        queue.pretendAdd();
        Assertions.assertThat(queue.size()).isEqualTo(1);
        queue.pretendAdd();
        queue.pretendAdd();
        Assertions.assertThat(queue.isFull()).isTrue();
        queue.pretendAdd();
        Assertions.assertThat(queue.size()).isEqualTo(3);
        for (int i = 0; i < 3; i++) {
            Assertions.assertThat(queue.get(i)).isEqualTo(0L);
        }
        queue.pretendClear();
        Assertions.assertThat(queue.isEmpty()).isTrue();
        queue.pretendPrepend();
        queue.circularAdd(5);
        Assertions.assertThat(queue.size()).isEqualTo(2);
        Assertions.assertThat(queue.get(1)).isEqualTo(5);
        Assertions.assertThat(queue.getReverse(0)).isEqualTo(5);
    }

    @Test
    public void testGenericEvictGuardNoStaleGhost() {
        final CircularGenericPrimitiveArrayQueue<String> queue = new CircularGenericPrimitiveArrayQueue<>(String.class,
                2);
        Assertions.assertThat(queue.evictAdd("a")).isNull();
        queue.pretendClear();
        Assertions.assertThat(queue.evictAdd("b")).as("stale slot must not be reported as evicted").isNull();
        Assertions.assertThat(queue.evictAdd("c")).isNull();
        Assertions.assertThat(queue.evictAdd("d")).isEqualTo("b");
        Assertions.assertThat(queue.toString()).isEqualTo("[c, d]");
        Assertions.assertThat(queue.evictPrepend("e")).isEqualTo("d");
        Assertions.assertThat(queue.toString()).isEqualTo("[e, c]");
    }

    @Test
    public void testCapacityOneWrapAround() {
        final CircularLongPrimitiveArrayQueue queue = new CircularLongPrimitiveArrayQueue(1);
        for (long i = 0; i < 10; i++) {
            queue.circularAdd(i);
            Assertions.assertThat(queue.size()).isEqualTo(1);
            Assertions.assertThat(queue.get(0)).isEqualTo(i);
            Assertions.assertThat(queue.getReverse(0)).isEqualTo(i);
            final long[] dst = new long[2];
            Assertions.assertThat(queue.getLongs(dst, 0, 0, 2)).isEqualTo(1);
            Assertions.assertThat(dst[1]).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        }
        Assertions.assertThat(queue.capacity()).isEqualTo(1);
        Assertions.assertThat(queue.isFull()).isTrue();
    }

    @Test
    public void testCapacityValidation() {
        //checkCapacity now runs in the base constructor - pin the rejection path for both ctor shapes
        Assertions.assertThatThrownBy(() -> new CircularLongPrimitiveArrayQueue(0))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> new CircularDoublePrimitiveArrayQueue(-1))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> new CircularGenericPrimitiveArrayQueue<String>(String.class, 0))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions
                .assertThatThrownBy(() -> new CircularLongPrimitiveArrayQueue(
                        de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray.newInstance(0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
