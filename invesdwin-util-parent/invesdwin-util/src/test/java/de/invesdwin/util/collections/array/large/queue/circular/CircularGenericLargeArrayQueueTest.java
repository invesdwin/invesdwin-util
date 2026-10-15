package de.invesdwin.util.collections.array.large.queue.circular;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;

@NotThreadSafe
public class CircularGenericLargeArrayQueueTest {

    private static final int TURNS = 10;
    private static final int COUNT = 3;

    @Test
    public void testAppend() {
        final CircularGenericLargeArrayQueue<String> array = new CircularGenericLargeArrayQueue<String>(String.class,
                5);
        final List<String> fromStartList = new ArrayList<>();
        final List<String> fromEndList = new ArrayList<>();
        for (int turn = 0; turn < TURNS; turn++) {
            for (int i = 0; i < COUNT; i++) {
                final String value = "v" + i;
                array.circularAdd(value);
                Assertions.assertThat(array.getReverse(0)).isEqualTo(value);
                Assertions.assertThat(array.getTail()).isEqualTo(value);
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
                Assertions.assertThat(array.getHead()).isEqualTo(fromStartList.get(0));
                Assertions.assertThat(array.size()).isEqualTo(fromStartList.size());
            }
        }
    }

    @Test
    public void testPrepend() {
        final CircularGenericLargeArrayQueue<String> array = new CircularGenericLargeArrayQueue<String>(new String[0],
                5);
        final List<String> fromStartList = new ArrayList<>();
        for (int turn = 0; turn < TURNS; turn++) {
            for (int i = 0; i < COUNT; i++) {
                final String value = "v" + i;
                array.circularPrepend(value);
                Assertions.assertThat(array.get(0)).isEqualTo(value);
                Assertions.assertThat(array.getHead()).isEqualTo(value);
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
    public void testEvictionAndPollLast() {
        final CircularGenericLargeArrayQueue<String> array = new CircularGenericLargeArrayQueue<String>(String.class,
                2);
        //not full, so nothing is evicted and null is returned
        Assertions.assertThat(array.evictAdd("a")).isNull();
        Assertions.assertThat(array.evictPrepend("b")).isNull();
        Assertions.assertThat(array.toString()).isEqualTo("[b, a]");
        //full, so the head and the tail are evicted
        Assertions.assertThat(array.evictAdd("c")).isEqualTo("b");
        Assertions.assertThat(array.toString()).isEqualTo("[a, c]");
        Assertions.assertThat(array.pollLast()).isEqualTo("c");
        Assertions.assertThat(array.poll()).isEqualTo("a");
        Assertions.assertThat(array.isEmpty()).isTrue();
        Assertions.assertThat(array.pollLast()).isNull();
        Assertions.assertThat(array.poll()).isNull();
        Assertions.assertThat(array.getHead()).isNull();
        Assertions.assertThat(array.getTail()).isNull();
        Assertions.assertThatThrownBy(() -> array.removeLast()).isInstanceOf(java.util.NoSuchElementException.class);
    }

    @Test
    public void testClearDropsReferences() {
        final CircularGenericLargeArrayQueue<String> array = new CircularGenericLargeArrayQueue<String>(String.class,
                3);
        array.circularAdd("a");
        array.circularAdd("b");
        array.circularAdd("c");
        array.clear();
        Assertions.assertThat(array.isEmpty()).isTrue();
        Assertions.assertThat(array.peek()).isNull();
        Assertions.assertThat(array.size()).isEqualTo(0L);
        array.circularAdd("d");
        Assertions.assertThat(array.toString()).isEqualTo("[d]");
    }

    @Test
    public void testToArrayAndOffsets() {
        final CircularGenericLargeArrayQueue<String> array = new CircularGenericLargeArrayQueue<String>(String.class,
                4);
        array.circularAdd("a");
        array.circularAdd("b");
        array.circularAdd("c");
        array.circularAdd("d");
        Assertions.assertThat(array.toString(2, 4)).isEqualTo("[c, d]");
        Assertions.assertThat(array.toStringReverse(2, 4)).isEqualTo("[b, a]");
        Assertions.assertThat(array.toArray(new String[6])).isEqualTo(new String[] { "a", "b", "c", "d", null, null });
        Assertions.assertThat(array.toArray(new String[0])).isEqualTo(new String[] { "a", "b", "c", "d" });
    }

    @Test
    public void testCapacityValidation() {
        Assertions.assertThatThrownBy(() -> new CircularGenericLargeArrayQueue<String>(String.class, 0))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> new CircularGenericLargeArrayQueue<String>(new String[0], -1))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> new CircularGenericLargeArrayQueue<String>(String.class, 2).get(0))
                .isInstanceOf(IndexOutOfBoundsException.class);
        Assertions
                .assertThat(Arrays
                        .toString(new CircularGenericLargeArrayQueue<String>(String.class, 1).toArray(new String[1])))
                .isEqualTo("[null]");
    }

    @Test
    public void testExternalArray() {
        final IGenericLargeArray<String> values = IGenericLargeArray.newInstance(String.class, 2);
        final CircularGenericLargeArrayQueue<String> queue = new CircularGenericLargeArrayQueue<String>(values);
        Assertions.assertThat(queue.capacity()).isEqualTo(2L);
        queue.circularAdd("a");
        queue.circularAdd("b");
        //the given array is the backing store
        Assertions.assertThat(values.get(0)).isEqualTo("a");
        Assertions.assertThat(values.get(1)).isEqualTo("b");
        queue.circularAdd("c");
        Assertions.assertThat(queue.toString()).isEqualTo("[b, c]");
        queue.clear();
        Assertions.assertThat(values.get(0)).isNull();
        Assertions.assertThat(values.get(1)).isNull();
    }

}
