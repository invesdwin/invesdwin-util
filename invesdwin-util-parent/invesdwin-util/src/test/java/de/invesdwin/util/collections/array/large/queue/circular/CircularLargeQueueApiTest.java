package de.invesdwin.util.collections.array.large.queue.circular;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGenerators;
import de.invesdwin.util.collections.array.large.queue.ILongLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.ILongLargeQueue;

/**
 * Differential tests for the circular queue API against an ArrayList oracle (the semantics pinned by
 * {@link CircularLargeArrayQueueTest}), covering the shared cursor base (conditional subtract instead of modulo for ALL
 * capacities, especially non power-of-two), the clamped reads, the window copy and the generic eviction guard.
 */
@NotThreadSafe
public class CircularLargeQueueApiTest {

    private static final long[] CAPACITIES = { 1, 2, 3, 5, 7 };

    @Test
    public void testOracleDifferentialAllCapacities() {
        for (final long capacity : CAPACITIES) {
            final CircularLongLargeArrayQueue queue = new CircularLongLargeArrayQueue(capacity);
            final List<Long> oracle = new ArrayList<>();
            final IRandomGenerator rand = PseudoRandomGenerators.newPseudoRandom(42 + capacity);
            for (int op = 0; op < 200; op++) {
                final long value = rand.nextInt(0, 1000);
                //NOTE: pretendAdd/Prepend deliberately NOT mixed in here - they expose physical slot contents
                //that no logical oracle can model (stale values after poll/clear); pinned separately.
                switch (rand.nextInt(0, 6)) {
                case 0:
                case 1: {
                    queue.circularAdd(value);
                    oracle.add(value);
                    if (oracle.size() > capacity) {
                        //append overflow drops the head
                        oracle.remove(0);
                    }
                    break;
                }
                case 2: {
                    queue.circularPrepend(value);
                    oracle.add(0, value);
                    if (oracle.size() > capacity) {
                        //prepend overflow drops the tail
                        oracle.remove(oracle.size() - 1);
                    }
                    break;
                }
                case 3: {
                    final long evicted = queue.evictAdd(value);
                    final long expectedEvicted = oracle.size() == capacity ? oracle.remove(0)
                            : ILongLargeQueue.MISSING_VALUE;
                    oracle.add(value);
                    Assertions.assertThat(evicted).isEqualTo(expectedEvicted);
                    break;
                }
                case 4: {
                    final long evicted = queue.evictPrepend(value);
                    final long expectedEvicted = oracle.size() == capacity ? oracle.remove(oracle.size() - 1)
                            : ILongLargeQueue.MISSING_VALUE;
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
                //full positional equivalence between queue and oracle
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
        final CircularLongLargeArrayQueue queue = new CircularLongLargeArrayQueue(3);
        //empty: everything out of range
        for (final long idx : new long[] { -1, 0, 1, 2, 3, Long.MIN_VALUE, Long.MAX_VALUE }) {
            Assertions.assertThat(queue.getOrMissing(idx)).isEqualTo(ILongLargeQueue.MISSING_VALUE);
            Assertions.assertThat(queue.getReverseOrMissing(idx)).isEqualTo(ILongLargeQueue.MISSING_VALUE);
            Assertions.assertThat(queue.isInRange(idx)).isFalse();
        }
        queue.circularAdd(10);
        queue.circularAdd(20);
        Assertions.assertThat(queue.getOrMissing(0)).isEqualTo(10);
        Assertions.assertThat(queue.getOrMissing(1)).isEqualTo(20);
        Assertions.assertThat(queue.getOrMissing(2)).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        Assertions.assertThat(queue.getOrMissing(-2)).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        Assertions.assertThat(queue.getReverseOrMissing(0)).isEqualTo(20);
        Assertions.assertThat(queue.getReverseOrMissing(1)).isEqualTo(10);
        Assertions.assertThat(queue.isInRange(1)).isTrue();
        Assertions.assertThat(queue.isInRange(2)).isFalse();
        //the throwing reads keep their contract
        Assertions.assertThatThrownBy(() -> queue.get(2)).isInstanceOf(IndexOutOfBoundsException.class);
        Assertions.assertThatThrownBy(() -> queue.getReverse(2)).isInstanceOf(IndexOutOfBoundsException.class);
        Assertions.assertThatThrownBy(() -> queue.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void testClampedReadsNaNAndFalse() {
        final CircularDoubleLargeArrayQueue doubles = new CircularDoubleLargeArrayQueue(2);
        Assertions.assertThat(Double.isNaN(doubles.getOrMissing(0))).isTrue();
        doubles.circularAdd(1.5);
        Assertions.assertThat(doubles.getOrMissing(0)).isEqualTo(1.5);
        Assertions.assertThat(Double.isNaN(doubles.getOrMissing(5))).isTrue();

        final CircularBooleanLargeArrayQueue booleans = new CircularBooleanLargeArrayQueue(2);
        Assertions.assertThat(booleans.getOrMissing(0)).isFalse();
        booleans.circularAdd(true);
        Assertions.assertThat(booleans.getOrMissing(0)).isTrue();
        Assertions.assertThat(booleans.getReverseOrMissing(1)).isFalse();
    }

    @Test
    public void testWindowCopy() {
        final CircularLongLargeArrayQueue queue = new CircularLongLargeArrayQueue(5);
        for (long i = 1; i <= 7; i++) {
            //leaves [3, 4, 5, 6, 7] after wrap
            queue.circularAdd(i);
        }
        final long[] dst = new long[8];
        //whole window
        Assertions.assertThat(queue.getLongs(dst, 0, 0, 5)).isEqualTo(5);
        Assertions.assertThat(dst[0]).isEqualTo(3);
        Assertions.assertThat(dst[4]).isEqualTo(7);
        //window reaching past the tail: MISSING fill, real count only
        java.util.Arrays.fill(dst, 0);
        Assertions.assertThat(queue.getLongs(dst, 0, 3, 5)).isEqualTo(2);
        Assertions.assertThat(dst[0]).isEqualTo(6);
        Assertions.assertThat(dst[1]).isEqualTo(7);
        Assertions.assertThat(dst[2]).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        //window starting before zero: MISSING fill on the left
        java.util.Arrays.fill(dst, 0);
        Assertions.assertThat(queue.getLongs(dst, 2, -2, 4)).isEqualTo(2);
        Assertions.assertThat(dst[2]).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        Assertions.assertThat(dst[3]).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        Assertions.assertThat(dst[4]).isEqualTo(3);
        Assertions.assertThat(dst[5]).isEqualTo(4);
        //destination guards are overflow-safe subtraction forms
        Assertions.assertThatThrownBy(() -> queue.getLongs(new long[4], 2, 0, 3))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> queue.getLongs(new long[4], -1, 0, 1))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> queue.getLongs(new long[4], 0, 0, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testGenericEvictGuardNoStaleGhost() {
        final CircularGenericLargeArrayQueue<String> queue = new CircularGenericLargeArrayQueue<>(String.class, 2);
        Assertions.assertThat(queue.evictAdd("a")).isNull();
        //clear() drops references; pretendClear() does not - the guard must not report ghosts either way
        queue.pretendClear();
        Assertions.assertThat(queue.evictAdd("b")).as("stale slot must not be reported as evicted").isNull();
        Assertions.assertThat(queue.evictAdd("c")).isNull();
        //now full: rolling reports the real eviction
        Assertions.assertThat(queue.evictAdd("d")).isEqualTo("b");
        Assertions.assertThat(queue.toString()).isEqualTo("[c, d]");
        Assertions.assertThat(queue.evictPrepend("e")).isEqualTo("d");
        Assertions.assertThat(queue.toString()).isEqualTo("[e, c]");
    }

    @Test
    public void testTypedEvictGuardRegression() {
        final CircularLongLargeArrayQueue queue = new CircularLongLargeArrayQueue(2);
        queue.circularAdd(7);
        queue.pretendClear();
        //slot 0 still physically holds 7, but nothing was rolled over -> MISSING
        Assertions.assertThat(queue.evictAdd(9)).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        Assertions.assertThat(queue.evictAdd(10)).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        Assertions.assertThat(queue.evictAdd(11)).isEqualTo(9);
    }

    @Test
    public void testPretendBookkeeping() {
        final CircularLongLargeArrayQueue queue = new CircularLongLargeArrayQueue(3);
        Assertions.assertThat(queue.isEmpty()).isTrue();
        queue.pretendAdd();
        Assertions.assertThat(queue.size()).isEqualTo(1);
        queue.pretendAdd();
        queue.pretendAdd();
        Assertions.assertThat(queue.isFull()).isTrue();
        //rolling past capacity keeps size pinned and moves the window
        queue.pretendAdd();
        Assertions.assertThat(queue.size()).isEqualTo(3);
        //a fresh backing array holds type defaults
        for (int i = 0; i < 3; i++) {
            Assertions.assertThat(queue.get(i)).isEqualTo(0L);
        }
        queue.pretendPrepend();
        Assertions.assertThat(queue.size()).isEqualTo(3);
        queue.pretendClear();
        Assertions.assertThat(queue.isEmpty()).isTrue();
        Assertions.assertThat(queue.getOrMissing(0)).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        //from empty the prepend side anchors at the last slot; that first slot is exposed UNINITIALIZED
        //(documented pretend* reuse semantics - pretendAdd/Prepend circle indexes without writing), so the next
        //append lands behind it: window = [default slot, 5]
        queue.pretendPrepend();
        Assertions.assertThat(queue.size()).isEqualTo(1);
        queue.circularAdd(5);
        Assertions.assertThat(queue.size()).isEqualTo(2);
        Assertions.assertThat(queue.get(1)).isEqualTo(5);
        Assertions.assertThat(queue.getReverse(0)).isEqualTo(5);
    }

    @Test
    public void testCapacityOneWrapAround() {
        final CircularLongLargeArrayQueue queue = new CircularLongLargeArrayQueue(1);
        for (long i = 0; i < 10; i++) {
            queue.circularAdd(i);
            Assertions.assertThat(queue.size()).isEqualTo(1);
            Assertions.assertThat(queue.get(0)).isEqualTo(i);
            Assertions.assertThat(queue.getReverse(0)).isEqualTo(i);
            final long[] dst = new long[2];
            Assertions.assertThat(queue.getLongs(dst, 0, 0, 2)).isEqualTo(1);
            Assertions.assertThat(dst[0]).isEqualTo(i);
            Assertions.assertThat(dst[1]).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        }
        Assertions.assertThat(((ILongLargeCircularQueue) queue).capacity()).isEqualTo(1);
        Assertions.assertThat(queue.isFull()).isTrue();
    }

}
