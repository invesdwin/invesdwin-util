package de.invesdwin.util.collections.array.large.queue.circular;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.collections.array.large.queue.IGenericLargeCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularGenericPrimitiveArrayQueue;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.math.Integers;

/**
 * Large variant of {@link CircularGenericPrimitiveArrayQueue} using long based indexes and sizes, thus not being
 * limited to an {@link Integer#MAX_VALUE} sized queue. Values are stored in a {@link IGenericLargeArray} which switches
 * to segmented heap arrays for capacities exceeding the maximum array size of the JVM.
 */
@NotThreadSafe
public class CircularGenericLargeArrayQueue<E> extends ACircularLargeArrayQueue
        implements IGenericLargeCircularQueue<E> {

    private final IGenericLargeArray<E> array;

    public CircularGenericLargeArrayQueue(final Class<E> type, final long capacity) {
        super(capacity);
        this.array = IGenericLargeArray.newInstance(type, capacity);
    }

    /**
     * Uses the given array as the backing store, so the capacity is the size of the array and it can be replaced by
     * another implementation (e.g. an off heap based one). Values of a previous usage are never read, so an array that
     * was used before can be reused as it is.
     */
    public CircularGenericLargeArrayQueue(final IGenericLargeArray<E> array) {
        super(array.size());
        this.array = array;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CircularGenericLargeArrayQueue(final E[] emptyArray, final long capacity) {
        //CHECKSTYLE:OFF
        this((Class) emptyArray.getClass().getComponentType(), capacity);
        //CHECKSTYLE:ON
    }

    /**
     * The last value has index 0, the value before the last has index 1.
     */
    @Override
    public E getReverse(final long index) {
        return array.get(arrayIndexReverse(index));
    }

    /**
     * The first value has index 0, the value after the first has index 1.
     */
    @Override
    public E get(final long index) {
        return array.get(arrayIndex(index));
    }

    @Override
    public E getOrMissing(final long index) {
        if (isInRange(index)) {
            return array.get(arrayIndexUnchecked(index));
        }
        return null;
    }

    @Override
    public E getReverseOrMissing(final long index) {
        if (isInRangeReverse(index)) {
            return array.get(arrayIndexReverseUnchecked(index));
        }
        return null;
    }

    @Override
    public String toString() {
        return toString(0, size);
    }

    public String toString(final long index, final long size) {
        final StringBuilder sb = new StringBuilder("[");
        for (long i = index; i < size; i++) {
            if (i > index) {
                sb.append(", ");
            }
            sb.append(get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    public String toStringReverse(final long index, final long size) {
        final StringBuilder sb = new StringBuilder("[");
        for (long i = index; i < size; i++) {
            if (i > index) {
                sb.append(", ");
            }
            sb.append(getReverse(i));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * WARNING: this operation will fail for queues that exceed the maximum array size of the JVM. Use with caution.
     */
    @SuppressWarnings("unchecked")
    public E[] toArray(final E[] a) {
        final int intSize = Integers.checkedCast(size);
        if (a.length < intSize) {
            // Make a new array of a's runtime type, but my contents:
            final E[] newArray = (E[]) Arrays.newInstance(a.getClass().getComponentType(), intSize);
            for (int i = 0; i < newArray.length; i++) {
                newArray[i] = get(i);
            }
            return newArray;
        }
        for (int i = 0; i < intSize; i++) {
            a[i] = get(i);
        }
        if (a.length > intSize) {
            a[intSize] = null;
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
        final long evictedSlot = evictAddSlot();
        final E evicted = evictedSlot >= 0 ? array.get(evictedSlot) : null;
        array.set(endArrayIndex, value);
        return evicted;
    }

    @Override
    public E evictPrepend(final E value) {
        final long evictedSlot = evictPrependSlot();
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
    public E peek() {
        if (isEmpty()) {
            return null;
        }
        final E first = array.get(startArrayIndex);
        return first;
    }

    @Override
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
        for (long i = 0; i < capacity; i++) {
            array.set(i, null);
        }
    }

}
