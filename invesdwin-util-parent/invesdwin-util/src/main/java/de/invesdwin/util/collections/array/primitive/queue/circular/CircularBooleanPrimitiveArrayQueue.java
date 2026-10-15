package de.invesdwin.util.collections.array.primitive.queue.circular;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.accessor.IBooleanPrimitiveArrayAccessor;
import de.invesdwin.util.collections.array.primitive.queue.IBooleanPrimitiveQueue;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;

/**
 * Similar to org.apache.commons.collections4.queue.CircularFifoQueue<E> or
 * org.jctools.queues.ConcurrentCircularArrayQueue<E>
 * 
 * Though with classic queue semantics and special functions for using the circular features with specialities for
 * memory management.
 */
@NotThreadSafe
public class CircularBooleanPrimitiveArrayQueue implements IBooleanPrimitiveQueue, IBooleanPrimitiveArrayAccessor {

    private final IBooleanPrimitiveArray array;
    private final int capacity;
    private final int lastPossibleIndex;
    private int size = 0;
    private int startArrayIndex = -1;
    private int endArrayIndex = -1;

    public CircularBooleanPrimitiveArrayQueue(final int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity should not be negative or 0: " + capacity);
        }
        this.array = IBooleanPrimitiveArray.newInstance(capacity);
        this.capacity = array.size();
        this.lastPossibleIndex = capacity - 1;
    }

    /**
     * Uses the given array as the backing store, so the capacity is the size of the array and it can be replaced by
     * another implementation (e.g. an off heap based one). Values of a previous usage are never read, so an array that
     * was used before can be reused as it is.
     */
    public CircularBooleanPrimitiveArrayQueue(final boolean[] array) {
        this(IBooleanPrimitiveArray.newInstance(array));
    }

    /**
     * Uses the given array as the backing store, so the capacity is the size of the array and it can be replaced by
     * another implementation (e.g. an off heap based one). Values of a previous usage are never read, so an array that
     * was used before can be reused as it is.
     */
    public CircularBooleanPrimitiveArrayQueue(final IBooleanPrimitiveArray array) {
        if (array.size() == 0) {
            throw new IllegalArgumentException("array should not be empty: " + array.size());
        }
        this.array = array;
        this.capacity = array.size();
        this.lastPossibleIndex = capacity - 1;
    }

    /**
     * The last value has index 0, the value before the last has index 1.
     */
    public boolean getReverse(final int index) {
        return array.get(arrayIndexReverse(index));
    }

    /**
     * The first value has index 0, the value after the first has index 1.
     */
    @Override
    public boolean get(final int index) {
        return array.get(arrayIndex(index));
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
            if (i > index) {
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
            if (i > index) {
                sb.append(", ");
            }
            sb.append(getReverse(i));
        }
        sb.append("]");
        return sb.toString();
    }

    public boolean[] toArray(final boolean[] a) {
        if (a.length < size) {
            // Make a new array of a's runtime type, but my contents:
            final boolean[] array = new boolean[size];
            for (int i = 0; i < array.length; i++) {
                array[i] = get(i);
            }
            return array;
        }
        for (int i = 0; i < size; i++) {
            a[i] = get(i);
        }
        if (a.length > size) {
            a[size] = MISSING_VALUE;
        }
        return a;
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced silently by the rollover (so
     * that it can be garbage collected).
     */
    public void circularAdd(final boolean value) {
        pretendAdd();
        array.set(endArrayIndex, value);
    }

    public void circularPrepend(final boolean value) {
        pretendPrepend();
        array.set(startArrayIndex, value);
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced by the rollover and returned
     * (e.g. to clean up the instance).
     */

    public boolean evictAdd(final boolean value) {
        //an untouched slot is never read, so only a real rollover reports an evicted element
        final boolean rolling = isFull();
        pretendAdd();
        final boolean evicted = rolling ? array.get(endArrayIndex) : MISSING_VALUE;
        array.set(endArrayIndex, value);
        return evicted;
    }

    public boolean evictPrepend(final boolean value) {
        //an untouched slot is never read, so only a real rollover reports an evicted element
        final boolean rolling = isFull();
        pretendPrepend();
        final boolean evicted = rolling ? array.get(startArrayIndex) : MISSING_VALUE;
        array.set(startArrayIndex, value);
        return evicted;
    }

    @Override
    public boolean offer(final boolean value) {
        if (isFull()) {
            return false;
        } else {
            circularAdd(value);
            return true;
        }
    }

    public boolean offerPrepend(final boolean value) {
        if (isFull()) {
            return false;
        } else {
            circularPrepend(value);
            return true;
        }
    }

    public boolean prepend(final boolean e) {
        if (offerPrepend(e)) {
            return true;
        } else {
            throw new IllegalStateException("offer returned false");
        }
    }

    @Override
    public boolean peek() {
        if (isEmpty()) {
            return MISSING_VALUE;
        }
        final boolean first = array.get(startArrayIndex);
        return first;
    }

    @Override
    public boolean poll() {
        if (isEmpty()) {
            return MISSING_VALUE;
        }
        final boolean first = array.get(startArrayIndex);
        array.set(startArrayIndex, MISSING_VALUE);
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
        //untouched slots are never read, so they are left at their default value
        pretendClear();
    }

    public void pretendClear() {
        this.size = 0;
        this.startArrayIndex = -1;
        this.endArrayIndex = -1;
    }

}
