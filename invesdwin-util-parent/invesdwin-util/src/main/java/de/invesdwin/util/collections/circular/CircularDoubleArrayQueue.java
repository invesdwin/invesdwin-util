package de.invesdwin.util.collections.circular;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.accessor.IDoubleArrayAccessor;
import de.invesdwin.util.collections.queue.IDoubleQueue;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;

/**
 * Similar to org.apache.commons.collections4.queue.CircularFifoQueue<E> or
 * org.jctools.queues.ConcurrentCircularArrayQueue<E>
 * 
 * Though with classic queue semantics and special functions for using the circular features with specialities for
 * memory management.
 */
@NotThreadSafe
public class CircularDoubleArrayQueue implements IDoubleQueue, IDoubleArrayAccessor {

    private final double[] array;
    private final int capacity;
    private final int lastPossibleIndex;
    private int size = 0;
    private int startArrayIndex = -1;
    private int endArrayIndex = -1;

    public CircularDoubleArrayQueue(final int capacity) {
        this.array = new double[capacity];
        Arrays.fill(array, Double.NaN);
        this.capacity = array.length;
        this.lastPossibleIndex = capacity - 1;
        if (capacity == 0) {
            throw new IllegalArgumentException("size should not be 0");
        }
    }

    /**
     * The last value has index 0, the value before the last has index 1.
     */
    public double getReverse(final int index) {
        return array[arrayIndexReverse(index)];
    }

    /**
     * The first value has index 0, the value after the first has index 1.
     */
    @Override
    public double get(final int index) {
        return array[arrayIndex(index)];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    @Override
    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    /**
     * Rolls over one element higher without adding or removing anything. Useful to circle the indexes around the
     * existing values (e.g. when reusing the values in the circular array).
     */
    public void pretendAdd() {
        endArrayIndex++;
        if (endArrayIndex >= capacity) {
            endArrayIndex = 0;
        }
        if (size == 0) {
            startArrayIndex = 0;
            size++;
        } else if (size == capacity) {
            incrementStartArrayIndex();
        } else {
            size++;
        }
    }

    public void pretendPrepend() {
        startArrayIndex--;
        if (startArrayIndex < 0) {
            startArrayIndex = lastPossibleIndex;
        }
        if (size == 0) {
            endArrayIndex = lastPossibleIndex;
            size++;
        } else if (size == capacity) {
            decrementEndArrayIndex();
        } else {
            size++;
        }
    }

    private void incrementStartArrayIndex() {
        startArrayIndex++;
        if (startArrayIndex >= capacity) {
            startArrayIndex = 0;
        }
    }

    private void decrementEndArrayIndex() {
        endArrayIndex--;
        if (endArrayIndex < 0) {
            endArrayIndex = lastPossibleIndex;
        }
    }

    private int arrayIndexReverse(final int index) {
        if (index >= size) {
            throw FastIndexOutOfBoundsException.getInstance("index[%s] should not be greater or equal to size[%s]",
                    index, size);
        }
        if (index < 0) {
            throw FastIndexOutOfBoundsException.getInstance("index[%s] should not be negative", index);
        }
        final int idx = (endArrayIndex - index);
        if (idx < 0) {
            final int adjIdx = (capacity + idx);
            return adjIdx;
        } else {
            return idx;
        }
    }

    private int arrayIndex(final int index) {
        if (index >= size) {
            throw FastIndexOutOfBoundsException.getInstance("index[%s] should not be greater or equal to size[%s]",
                    index, size);
        }
        if (index < 0) {
            throw FastIndexOutOfBoundsException.getInstance("index[%s] should not be negative", index);
        }
        final int idx = (startArrayIndex + index) % capacity;
        return idx;
    }

    @Override
    public String toString() {
        return toString(0, size);
    }

    public String toString(final int index, final int size) {
        final StringBuilder sb = new StringBuilder("[");
        for (int i = index; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    public String toStringReverse(final int index, final int size) {
        final StringBuilder sb = new StringBuilder("[");
        for (int i = index; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(getReverse(i));
        }
        sb.append("]");
        return sb.toString();
    }

    public double[] toArray(final double[] a) {
        if (a.length < size) {
            // Make a new array of a's runtime type, but my contents:
            final double[] array = new double[size];
            for (int i = 0; i < array.length; i++) {
                array[i] = get(i);
            }
            return array;
        }
        for (int i = 0; i < size; i++) {
            a[i] = get(i);
        }
        if (a.length > size) {
            a[size] = Double.NaN;
        }
        return a;
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced silently by the rollover (so
     * that it can be garbage collected).
     */
    public void circularAdd(final double value) {
        pretendAdd();
        array[endArrayIndex] = value;
    }

    public void circularPrepend(final double value) {
        pretendPrepend();
        array[startArrayIndex] = value;
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced by the rollover and returned
     * (e.g. to clean up the instance).
     */

    public double evictAdd(final double value) {
        pretendAdd();
        final double evicted = array[endArrayIndex];
        array[endArrayIndex] = value;
        return evicted;
    }

    public double evictPrepend(final double value) {
        pretendPrepend();
        final double evicted = array[startArrayIndex];
        array[startArrayIndex] = value;
        return evicted;
    }

    @Override
    public boolean offer(final double value) {
        if (isFull()) {
            return false;
        } else {
            circularAdd(value);
            return true;
        }
    }

    public boolean offerPrepend(final double value) {
        if (isFull()) {
            return false;
        } else {
            circularPrepend(value);
            return true;
        }
    }

    public boolean prepend(final double e) {
        if (offerPrepend(e)) {
            return true;
        } else {
            throw new IllegalStateException("offer returned false");
        }
    }

    @Override
    public double peek() {
        if (isEmpty()) {
            return Double.NaN;
        }
        final double first = array[startArrayIndex];
        return first;
    }

    @Override
    public double poll() {
        if (isEmpty()) {
            return Double.NaN;
        }
        final double first = array[startArrayIndex];
        array[startArrayIndex] = Double.NaN;
        size--;
        if (isEmpty()) {
            startArrayIndex = -1;
            endArrayIndex = -1;
        } else {
            incrementStartArrayIndex();
        }
        return first;
    }

    @Override
    public void clear() {
        pretendClear();
        Arrays.fill(array, Double.NaN);
    }

    public void pretendClear() {
        this.size = 0;
        this.startArrayIndex = -1;
        this.endArrayIndex = -1;
    }

}
