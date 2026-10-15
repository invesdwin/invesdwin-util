package de.invesdwin.util.collections.array.large.queue.circular;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.primitive.LongPrimitiveAsLargeArray;
import de.invesdwin.util.collections.array.large.queue.ILongLargeQueue;
import de.invesdwin.util.collections.array.primitive.heap.HeapLongPrimitiveArray;

@NotThreadSafe
public class CircularLargeArrayQueueTest {

    private static final int TURNS = 10;
    private static final int COUNT = 3;

    @Test
    public void testAppend() {
        final CircularLongLargeArrayQueue array = new CircularLongLargeArrayQueue(5);
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
        final CircularIntegerLargeArrayQueue array = new CircularIntegerLargeArrayQueue(5);
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
        final CircularLongLargeArrayQueue array = new CircularLongLargeArrayQueue(2);
        //not full, so nothing is evicted and the missing value is returned
        Assertions.assertThat(array.evictAdd(1L)).isEqualTo(ILongLargeQueue.MISSING_VALUE);
        Assertions.assertThat(array.evictPrepend(0L)).isEqualTo(Long.MIN_VALUE);
        Assertions.assertThat(array.size()).isEqualTo(2L);
        //full, so the head and the tail are evicted, the queue is [0, 1] now
        Assertions.assertThat(array.evictAdd(2L)).isEqualTo(0L);
        Assertions.assertThat(array.toString()).isEqualTo("[1, 2]");
        Assertions.assertThat(array.evictPrepend(3L)).isEqualTo(2L);
        Assertions.assertThat(array.toString()).isEqualTo("[3, 1]");
    }

    @Test
    public void testMissingValue() {
        final CircularDoubleLargeArrayQueue array = new CircularDoubleLargeArrayQueue(2);
        Assertions.assertThat(Double.isNaN(array.poll())).isTrue();
        Assertions.assertThat(Double.isNaN(array.peek())).isTrue();
        array.circularAdd(Double.NaN);
        //a NaN element is not empty, so element and remove must not fail
        Assertions.assertThat(array.size()).isEqualTo(1L);
        Assertions.assertThat(Double.isNaN(array.element())).isTrue();
        Assertions.assertThat(Double.isNaN(array.remove())).isTrue();
        Assertions.assertThat(array.isEmpty()).isTrue();

        final CircularBooleanLargeArrayQueue booleans = new CircularBooleanLargeArrayQueue(2);
        Assertions.assertThat(booleans.isEmpty()).isTrue();
        Assertions.assertThat(booleans.peek()).isFalse();
        booleans.circularAdd(true);
        Assertions.assertThat(booleans.peek()).isTrue();
        Assertions.assertThat(booleans.poll()).isTrue();
        Assertions.assertThat(booleans.isEmpty()).isTrue();
    }

    @Test
    public void testToStringOffsets() {
        final CircularIntegerLargeArrayQueue array = new CircularIntegerLargeArrayQueue(4);
        array.circularAdd(1);
        array.circularAdd(2);
        array.circularAdd(3);
        array.circularAdd(4);
        Assertions.assertThat(array.toString()).isEqualTo("[1, 2, 3, 4]");
        Assertions.assertThat(array.toString(2, 4)).isEqualTo("[3, 4]");
        Assertions.assertThat(array.toStringReverse(2, 4)).isEqualTo("[2, 1]");
        Assertions.assertThat(array.toArray(new int[6])).isEqualTo(new int[] { 1, 2, 3, 4, Integer.MIN_VALUE, 0 });
    }

    @Test
    public void testCapacityValidation() {
        Assertions.assertThatThrownBy(() -> new CircularLongLargeArrayQueue(0)).isInstanceOf(
                IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> new CircularDoubleLargeArrayQueue(-1)).isInstanceOf(
                IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> new CircularIntegerLargeArrayQueue(-1)).isInstanceOf(
                IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> new CircularBooleanLargeArrayQueue(0)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    public void testExternalArray() {
        final ILongLargeArray values = ILongLargeArray.newInstance(2);
        final CircularLongLargeArrayQueue queue = new CircularLongLargeArrayQueue(values);
        Assertions.assertThat(queue.capacity()).isEqualTo(2L);
        queue.circularAdd(1L);
        queue.circularAdd(2L);
        //the given array is the backing store
        Assertions.assertThat(values.get(0)).isEqualTo(1L);
        Assertions.assertThat(values.get(1)).isEqualTo(2L);
        queue.circularAdd(3L);
        Assertions.assertThat(queue.toString()).isEqualTo("[2, 3]");
        Assertions.assertThat(values.get(0)).isEqualTo(3L);
        queue.clear();
        //clear leaves the values alone, they are simply not read anymore
        Assertions.assertThat(values.get(0)).isEqualTo(3L);
        //a primitive array can serve as the backing store of a large queue
        final long[] primitiveValues = new long[2];
        final CircularLongLargeArrayQueue adapted = new CircularLongLargeArrayQueue(
                new LongPrimitiveAsLargeArray(new HeapLongPrimitiveArray(primitiveValues)));
        adapted.circularAdd(4L);
        Assertions.assertThat(primitiveValues[0]).isEqualTo(4L);
    }

}
