package de.invesdwin.util.collections.array.primitive.queue.circular;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.primitive.IGenericPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.queue.IGenericPrimitiveCircularQueue;
import de.invesdwin.util.error.FastNoSuchElementException;

/**
 * Int-indexed counterpart of {@code CircularGenericLargeArrayQueue} - same circular semantics, bounded by the int range
 * instead of the long range. Values are stored in an {@link IGenericPrimitiveArray}.
 */
@NotThreadSafe
public class CircularGenericPrimitiveArrayQueue<E> extends ACircularPrimitiveArrayQueue
        implements IGenericPrimitiveCircularQueue<E> {

    private final IGenericPrimitiveArray<E> array;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CircularGenericPrimitiveArrayQueue(final int capacity) {
        //CHECKSTYLE:OFF
        //Object based storage, use the type based constructor to get a typed array
        this((Class) Object.class, capacity);
        //CHECKSTYLE:ON
    }

    public CircularGenericPrimitiveArrayQueue(final Class<E> type, final int capacity) {
        super(capacity);
        this.array = IGenericPrimitiveArray.newInstance(type, capacity);
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
        super(array.size());
        this.array = array;
    }

    /**
     * The last value has index 0, the value before the last has index 1.
     */
    @SuppressWarnings("unchecked")
    @Override
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
    @SuppressWarnings("unchecked")
    public E getOrMissing(final int index) {
        if (isInRange(index)) {
            return array.get(arrayIndexUnchecked(index));
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E getReverseOrMissing(final int index) {
        if (isInRangeReverse(index)) {
            return array.get(arrayIndexReverseUnchecked(index));
        }
        return null;
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
    @Override
    public void circularAdd(final E value) {
        pretendAdd();
        array.set(endArrayIndex, value);
    }

    @Override
    public void circularPrepend(final E value) {
        pretendPrepend();
        array.set(startArrayIndex, value);
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced by the rollover and returned
     * (e.g. to clean up the instance). Returns null if nothing was evicted; an untouched slot is never reported as
     * evicted (a stale reference after pretendClear must not leak out as an eviction).
     */
    @Override
    public E evictAdd(final E value) {
        final int evictedSlot = evictAddSlot();
        final E evicted = evictedSlot >= 0 ? array.get(evictedSlot) : null;
        array.set(endArrayIndex, value);
        return evicted;
    }

    @Override
    public E evictPrepend(final E value) {
        final int evictedSlot = evictPrependSlot();
        final E evicted = evictedSlot >= 0 ? array.get(evictedSlot) : null;
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

    @Override
    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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

    @Override
    public void clear() {
        pretendClear();
        //drop all references so that they can be garbage collected
        for (int i = 0; i < capacity; i++) {
            array.set(i, null);
        }
    }

}
