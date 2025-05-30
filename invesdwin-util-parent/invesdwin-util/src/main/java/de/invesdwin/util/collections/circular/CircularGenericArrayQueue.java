package de.invesdwin.util.collections.circular;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.accessor.IGenericArrayAccessor;
import de.invesdwin.util.collections.queue.IGenericQueue;
import de.invesdwin.util.error.FastNoSuchElementException;

/**
 * Similar to org.apache.commons.collections4.queue.CircularFifoQueue<E> or
 * org.jctools.queues.ConcurrentCircularArrayQueue<E>
 * 
 * Though with classic queue semantics and special functions for using the circular features with specialities for
 * memory management.
 */
@NotThreadSafe
public class CircularGenericArrayQueue<E> implements IGenericQueue<E>, IGenericArrayAccessor<E> {

    private final Object[] array;
    private final int capacity;
    private final int lastPossibleIndex;
    private int size = 0;
    private int startArrayIndex = -1;
    private int endArrayIndex = -1;

    public CircularGenericArrayQueue(final int capacity) {
        this.array = new Object[capacity];
        this.capacity = array.length;
        this.lastPossibleIndex = capacity - 1;
        if (capacity == 0) {
            throw new IllegalArgumentException("size should not be 0");
        }
    }

    /**
     * The last value has index 0, the value before the last has index 1.
     */
    @SuppressWarnings("unchecked")
    public E getReverse(final int index) {
        return (E) array[arrayIndexReverse(index)];
    }

    /**
     * The first value has index 0, the value after the first has index 1.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E get(final int index) {
        return (E) array[arrayIndex(index)];
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
            throw new IndexOutOfBoundsException(
                    "index[" + index + "] should not be greater or equal to size[" + size + "]");
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("index[" + index + "] should not be negative");
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
            throw new IndexOutOfBoundsException(
                    "index[" + index + "] should not be greater or equal to size[" + size + "]");
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("index[" + index + "] should not be negative");
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

    @SuppressWarnings("unchecked")
    public E[] toArray(final E[] a) {
        if (a.length < size) {
            // Make a new array of a's runtime type, but my contents:
            final E[] array = (E[]) Arrays.newInstance(a.getClass().getComponentType(), size);
            for (int i = 0; i < array.length; i++) {
                array[i] = get(i);
            }
            return array;
        }
        for (int i = 0; i < size; i++) {
            a[i] = get(i);
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced silently by the rollover (so
     * that it can be garbage collected).
     */
    public void circularAdd(final E value) {
        pretendAdd();
        array[endArrayIndex] = value;
    }

    public void circularPrepend(final E value) {
        pretendPrepend();
        array[startArrayIndex] = value;
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced by the rollover and returned
     * (e.g. to clean up the instance).
     */
    @SuppressWarnings("unchecked")
    public E evictAdd(final E value) {
        pretendAdd();
        final E evicted = (E) array[endArrayIndex];
        array[endArrayIndex] = value;
        return evicted;
    }

    @SuppressWarnings("unchecked")
    public E evictPrepend(final E value) {
        pretendPrepend();
        final E evicted = (E) array[startArrayIndex];
        array[startArrayIndex] = value;
        return evicted;
    }

    @Override
    public boolean offer(final E value) {
        if (isFull()) {
            return false;
        } else {
            circularAdd(value);
            return true;
        }
    }

    public boolean offerPrepend(final E value) {
        if (isFull()) {
            return false;
        } else {
            circularPrepend(value);
            return true;
        }
    }

    public boolean prepend(final E e) {
        if (offerPrepend(e)) {
            return true;
        } else {
            throw new IllegalStateException("offer returned false");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public E peek() {
        if (isEmpty()) {
            return null;
        }
        final E first = (E) array[startArrayIndex];
        return first;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E poll() {
        if (isEmpty()) {
            return null;
        }
        final E first = (E) array[startArrayIndex];
        array[startArrayIndex] = null;
        size--;
        if (isEmpty()) {
            startArrayIndex = -1;
            endArrayIndex = -1;
        } else {
            incrementStartArrayIndex();
        }
        return first;
    }

    public E pollLast() {
        if (isEmpty()) {
            return null;
        }
        final E last = (E) array[endArrayIndex];
        array[endArrayIndex] = null;
        size--;
        if (isEmpty()) {
            startArrayIndex = -1;
            endArrayIndex = -1;
        } else {
            decrementEndArrayIndex();
        }
        return last;
    }

    public E removeLast() {
        final E pollLast = pollLast();
        if (pollLast != null) {
            return pollLast;
        } else {
            throw FastNoSuchElementException.getInstance("pollLast returned null");
        }
    }

    @Override
    public void clear() {
        pretendClear();
        Arrays.fill(array, null);
    }

    public void pretendClear() {
        this.size = 0;
        this.startArrayIndex = -1;
        this.endArrayIndex = -1;
    }

    public E getTail() {
        if (isEmpty()) {
            return null;
        } else {
            return getReverse(0);
        }
    }

    public E getHead() {
        if (isEmpty()) {
            return null;
        } else {
            return get(0);
        }
    }

}
