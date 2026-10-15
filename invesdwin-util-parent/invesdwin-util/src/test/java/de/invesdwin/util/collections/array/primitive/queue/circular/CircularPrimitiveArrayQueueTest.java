package de.invesdwin.util.collections.array.primitive.queue.circular;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.primitive.buffer.BufferLongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.queue.IBooleanPrimitiveQueue;
import de.invesdwin.util.collections.array.primitive.queue.IIntegerPrimitiveQueue;
import de.invesdwin.util.collections.array.primitive.queue.ILongPrimitiveQueue;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@NotThreadSafe
public class CircularPrimitiveArrayQueueTest {

    private static final int TURNS = 10;
    private static final int COUNT = 3;

    @Test
    public void testAppend() {
        final CircularLongPrimitiveArrayQueue array = new CircularLongPrimitiveArrayQueue(5);
        final List<Long> fromStartList = new ArrayList<>();
        final List<Long> fromEndList = new ArrayList<>();
        for (int turn = 0; turn < TURNS; turn++) {
            for (int i = 0; i < COUNT; i++) {
                final long value = i;
                array.circularAdd(value);
                Assertions.assertThat(array.getReverse(0)).isEqualTo(value);
                fromStartList.add(value);
                if (fromStartList.size() > array.capacity()) {
                    fromStartList.remove(0);
                }
                fromEndList.add(0, value);
                if (fromEndList.size() > array.capacity()) {
                    fromEndList.remove(fromEndList.size() - 1);
                }
                Assertions.assertThat(array.toString()).isEqualTo(fromStartList.toString());
                Assertions.assertThat(array.toStringReverse(0, array.size())).isEqualTo(fromEndList.toString());
                Assertions.assertThat(array.size()).isEqualTo(fromStartList.size());
                for (int rel = 0; rel < array.size(); rel++) {
                    Assertions.assertThat(array.get(rel)).isEqualTo(fromStartList.get(rel));
                    Assertions.assertThat(array.getReverse(array.size() - 1 - rel)).isEqualTo(fromStartList.get(rel));
                }
            }
        }
    }

    @Test
    public void testPrepend() {
        final CircularIntegerPrimitiveArrayQueue array = new CircularIntegerPrimitiveArrayQueue(5);
        final List<Integer> fromStartList = new ArrayList<>();
        for (int turn = 0; turn < TURNS; turn++) {
            for (int i = 0; i < COUNT; i++) {
                final int value = i;
                array.circularPrepend(value);
                Assertions.assertThat(array.get(0)).isEqualTo(value);
                fromStartList.add(0, value);
                if (fromStartList.size() > array.capacity()) {
                    fromStartList.remove(fromStartList.size() - 1);
                }
                Assertions.assertThat(array.toString()).isEqualTo(fromStartList.toString());
                Assertions.assertThat(array.size()).isEqualTo(fromStartList.size());
            }
        }
    }

    @Test
    public void testEviction() {
        final CircularLongPrimitiveArrayQueue array = new CircularLongPrimitiveArrayQueue(2);
        //not full, so nothing is evicted and the missing value is returned
        Assertions.assertThat(array.evictAdd(1L)).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        Assertions.assertThat(array.evictPrepend(0L)).isEqualTo(Long.MIN_VALUE);
        Assertions.assertThat(array.size()).isEqualTo(2);
        //full, so the head and the tail are evicted, the queue is [0, 1] now
        Assertions.assertThat(array.evictAdd(2L)).isEqualTo(0L);
        Assertions.assertThat(array.toString()).isEqualTo("[1, 2]");
        Assertions.assertThat(array.evictPrepend(3L)).isEqualTo(2L);
        Assertions.assertThat(array.toString()).isEqualTo("[3, 1]");
    }

    @Test
    public void testMissingValue() {
        final CircularDoublePrimitiveArrayQueue array = new CircularDoublePrimitiveArrayQueue(2);
        Assertions.assertThat(Double.isNaN(array.poll())).isTrue();
        Assertions.assertThat(Double.isNaN(array.peek())).isTrue();
        array.circularAdd(Double.NaN);
        //a NaN element is not empty, so element and remove must not fail
        Assertions.assertThat(array.size()).isEqualTo(1);
        Assertions.assertThat(Double.isNaN(array.element())).isTrue();
        Assertions.assertThat(Double.isNaN(array.remove())).isTrue();
        Assertions.assertThat(array.isEmpty()).isTrue();
        //NaN is the missing value, so it is what a fresh slot evicts
        Assertions.assertThat(Double.isNaN(array.evictAdd(1.5D))).isTrue();

        final CircularBooleanPrimitiveArrayQueue booleans = new CircularBooleanPrimitiveArrayQueue(2);
        Assertions.assertThat(booleans.isEmpty()).isTrue();
        Assertions.assertThat(booleans.peek()).isEqualTo(IBooleanPrimitiveQueue.MISSING_VALUE);
        booleans.circularAdd(true);
        Assertions.assertThat(booleans.peek()).isTrue();
        Assertions.assertThat(booleans.poll()).isTrue();
        Assertions.assertThat(booleans.isEmpty()).isTrue();
        //false is a valid element, so peek cannot distinguish an empty queue
        booleans.circularAdd(false);
        Assertions.assertThat(booleans.isEmpty()).isFalse();
        Assertions.assertThat(booleans.peek()).isFalse();
    }

    @Test
    public void testOfferAndPoll() {
        final CircularIntegerPrimitiveArrayQueue array = new CircularIntegerPrimitiveArrayQueue(2);
        Assertions.assertThat(array.offer(1)).isTrue();
        Assertions.assertThat(array.offerPrepend(0)).isTrue();
        Assertions.assertThat(array.isFull()).isTrue();
        Assertions.assertThat(array.offer(2)).isFalse();
        Assertions.assertThatThrownBy(() -> array.prepend(2)).isInstanceOf(IllegalStateException.class);
        Assertions.assertThat(array.toString()).isEqualTo("[0, 1]");
        Assertions.assertThat(array.element()).isEqualTo(0);
        Assertions.assertThat(array.poll()).isEqualTo(0);
        Assertions.assertThat(array.poll()).isEqualTo(1);
        Assertions.assertThat(array.poll()).isEqualTo(IIntegerPrimitiveQueue.MISSING_VALUE);
        Assertions.assertThat(array.isEmpty()).isTrue();
        Assertions.assertThatThrownBy(array::element).isInstanceOf(java.util.NoSuchElementException.class);
    }

    @Test
    public void testClearAndPretendClear() {
        final CircularLongPrimitiveArrayQueue array = new CircularLongPrimitiveArrayQueue(2);
        array.circularAdd(1L);
        array.circularAdd(2L);
        array.clear();
        Assertions.assertThat(array.isEmpty()).isTrue();
        //nothing is evicted while the queue is not full, so the missing value is reported
        Assertions.assertThat(array.evictAdd(3L)).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        array.pretendClear();
        Assertions.assertThat(array.isEmpty()).isTrue();
        //untouched slots are never read, so they are not reported as evicted elements
        Assertions.assertThat(array.evictAdd(4L)).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        Assertions.assertThat(array.evictAdd(5L)).isEqualTo(ILongPrimitiveQueue.MISSING_VALUE);
        //full now, so the head is evicted and returned
        Assertions.assertThat(array.evictAdd(6L)).isEqualTo(4L);
        Assertions.assertThat(array.toString()).isEqualTo("[5, 6]");
    }

    @Test
    public void testToStringOffsets() {
        final CircularIntegerPrimitiveArrayQueue array = new CircularIntegerPrimitiveArrayQueue(4);
        array.circularAdd(1);
        array.circularAdd(2);
        array.circularAdd(3);
        array.circularAdd(4);
        Assertions.assertThat(array.toString()).isEqualTo("[1, 2, 3, 4]");
        Assertions.assertThat(array.toString(2, 4)).isEqualTo("[3, 4]");
        Assertions.assertThat(array.toStringReverse(2, 4)).isEqualTo("[2, 1]");
        Assertions.assertThat(array.toArray(new int[6])).isEqualTo(
                new int[] { 1, 2, 3, 4, Integer.MIN_VALUE, 0 });
        Assertions.assertThat(array.toArray(new int[2])).isEqualTo(new int[] { 1, 2, 3, 4 });
    }

    @Test
    public void testCapacityValidation() {
        Assertions.assertThatThrownBy(() -> new CircularLongPrimitiveArrayQueue(0)).isInstanceOf(
                IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> new CircularDoublePrimitiveArrayQueue(-1)).isInstanceOf(
                IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> new CircularIntegerPrimitiveArrayQueue(-1)).isInstanceOf(
                IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> new CircularBooleanPrimitiveArrayQueue(0)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    public void testExternalArray() {
        final long[] values = new long[] { 5L, 6L };
        final CircularLongPrimitiveArrayQueue seeded = new CircularLongPrimitiveArrayQueue(values);
        Assertions.assertThat(seeded.capacity()).isEqualTo(2);
        Assertions.assertThat(seeded.isEmpty()).isTrue();
        //the content of the given array is kept as it is, so index 1 still holds the seeded value
        seeded.circularAdd(1L);
        Assertions.assertThat(values).isEqualTo(new long[] { 1L, 6L });
        seeded.clear();
        //clear leaves the values alone, they are simply not read anymore
        Assertions.assertThat(values).isEqualTo(new long[] { 1L, 6L });
        seeded.circularAdd(1L);
        seeded.circularAdd(2L);
        seeded.circularAdd(3L);
        Assertions.assertThat(seeded.toString()).isEqualTo("[2, 3]");
        Assertions.assertThat(values).isEqualTo(new long[] { 3L, 2L });
        //the cleared array can be reused by another instance without resetting it
        seeded.clear();
        final CircularLongPrimitiveArrayQueue other = new CircularLongPrimitiveArrayQueue(values);
        other.circularAdd(4L);
        Assertions.assertThat(other.toString()).isEqualTo("[4]");
        Assertions.assertThat(values).isEqualTo(new long[] { 4L, 2L });
        Assertions.assertThatThrownBy(() -> new CircularIntegerPrimitiveArrayQueue(new int[0])).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    public void testInterfaceBacking() {
        //the interface based constructor allows using an off heap backed array
        final CircularLongPrimitiveArrayQueue queue = new CircularLongPrimitiveArrayQueue(
                new BufferLongPrimitiveArray(ByteBuffers.allocate(2 * Long.BYTES)));
        Assertions.assertThat(queue.capacity()).isEqualTo(2);
        queue.circularAdd(1L);
        queue.circularAdd(2L);
        queue.circularAdd(3L);
        Assertions.assertThat(queue.toString()).isEqualTo("[2, 3]");
        Assertions.assertThat(queue.toArray(new long[2])).isEqualTo(new long[] { 2L, 3L });
        queue.clear();
        //clear resets the off heap values to the missing value as well
        Assertions.assertThat(queue.evictAdd(4L)).isEqualTo(Long.MIN_VALUE);
    }

}
