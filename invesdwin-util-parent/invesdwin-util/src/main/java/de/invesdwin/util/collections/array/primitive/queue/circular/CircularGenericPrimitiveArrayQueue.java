package de.invesdwin.util.collections.array.primitive.queue.circular;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.primitive.IGenericPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.accessor.IGenericPrimitiveArrayAccessor;
import de.invesdwin.util.collections.array.primitive.queue.IGenericPrimitiveQueue;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.error.FastNoSuchElementException;

/**
 * Similar to org.apache.commons.collections4.queue.CircularFifoQueue<E> or
 * org.jctools.queues.ConcurrentCircularArrayQueue<E>
 * 
 * Though with classic queue semantics and special functions for using the circular features with specialities for
 * memory management.
 */
@NotThreadSafe
public class CircularGenericPrimitiveArrayQueue<E> implements IGenericPrimitiveQueue<E>, IGenericPrimitiveArrayAccessor<E> {

    private final IGenericPrimitiveArray<E> array;
    private final int capacity;
    private final int lastPossibleIndex;
    private int size = 0;
    private int startArrayIndex = -1;
    private int endArrayIndex = -1;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CircularGenericPrimitiveArrayQueue(final int capacity) {
        //CHECKSTYLE:OFF
        //Object based storage, use the type based constructor to get a typed array
        this((Class) Object.class, capacity);
        //CHECKSTYLE:ON
    }

    public CircularGenericPrimitiveArrayQueue(final Class<E> type, final int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity should not be negative or 0: " + capacity);
        }
        this.array = IGenericPrimitiveArray.newInstance(type, capacity);
        this.capacity = array.size();
        this.lastPossibleIndex = capacity - 1;
    }

    /**
     * Uses the given array as the backing store, so the capacity is the size of the array and it can be replaced by
     * another implementation (e.g. an off heap based one). Values of a previous usage are never read, so an array that
     * was used before can be reused as it is.
     */
    public CircularGenericPrimitiveArrayQueue(final E[] array) {
        this(IGenericPrimitiveArray.newInstance(array));
    }

    /**
     * Uses the given array as the backing store, so the capacity is the size of the array and it can be replaced by
     * another implementation (e.g. an off heap based one). Values of a previous usage are never read, so an array that
     * was used before can be reused as it is.
     */
    public CircularGenericPrimitiveArrayQueue(final IGenericPrimitiveArray<E> array) {
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
    @SuppressWarnings("unchecked")
    public E getReverse(final int index) {
        return array.get(arrayIndexReverse(index));
    }

    /**
     * The first value has index 0, the value after the first has index 1.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E get(final int index) {
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
        array.set(endArrayIndex, value);
    }

    public void circularPrepend(final E value) {
        pretendPrepend();
        array.set(startArrayIndex, value);
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced by the rollover and returned
     * (e.g. to clean up the instance).
     */
    @SuppressWarnings("unchecked")
    public E evictAdd(final E value) {
        pretendAdd();
        final E evicted = array.get(endArrayIndex);
        array.set(endArrayIndex, value);
        return evicted;
    }

    @SuppressWarnings("unchecked")
    public E evictPrepend(final E value) {
        pretendPrepend();
        final E evicted = array.get(startArrayIndex);
        array.set(startArrayIndex, value);
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
        final E first = array.get(startArrayIndex);
        return first;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E poll() {
        if (isEmpty()) {
            return null;
        }
        final E first = array.get(startArrayIndex);
        array.set(startArrayIndex, null);
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
        final E last = array.get(endArrayIndex);
        array.set(endArrayIndex, null);
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
        //drop all references so that they can be garbage collected
        for (int i = 0; i < capacity; i++) {
            array.set(i, null);
        }
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
