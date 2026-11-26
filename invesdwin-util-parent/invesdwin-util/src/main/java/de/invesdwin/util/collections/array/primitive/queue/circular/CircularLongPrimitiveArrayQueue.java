package de.invesdwin.util.collections.array.primitive.queue.circular;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.queue.ILongPrimitiveCircularQueue;

/**
 * Int-indexed counterpart of {@code CircularLongLargeArrayQueue} - same circular semantics, bounded by the int range
 * instead of the long range. Values are stored in an {@link ILongPrimitiveArray}.
 */
@NotThreadSafe
public class CircularLongPrimitiveArrayQueue extends ACircularPrimitiveArrayQueue
        implements ILongPrimitiveCircularQueue {

    private final ILongPrimitiveArray array;

    public CircularLongPrimitiveArrayQueue(final int capacity) {
        super(capacity);
        this.array = ILongPrimitiveArray.newInstance(capacity);
    }

    /**
     * Uses the given array as the backing store, so the capacity is the size of the array and it can be replaced by
     * another implementation (e.g. an off heap based one). Values of a previous usage are never read, so an array that
     * was used before can be reused as it is.
     */
    public CircularLongPrimitiveArrayQueue(final long[] array) {
        this(ILongPrimitiveArray.newInstance(array));
    }

    /**
     * Uses the given array as the backing store, so the capacity is the size of the array and it can be replaced by
     * another implementation (e.g. an off heap based one). Values of a previous usage are never read, so an array that
     * was used before can be reused as it is.
     */
    public CircularLongPrimitiveArrayQueue(final ILongPrimitiveArray array) {
        super(array.size());
        this.array = array;
    }

    /**
     * The last value has index 0, the value before the last has index 1.
     */
    @Override
    public long getReverse(final int index) {
        return array.get(arrayIndexReverse(index));
    }

    /**
     * The first value has index 0, the value after the first has index 1.
     */
    @Override
    public long get(final int index) {
        return array.get(arrayIndex(index));
    }

    @Override
    public long getOrMissing(final int index) {
        if (isInRange(index)) {
            return array.get(arrayIndexUnchecked(index));
        }
        return MISSING_VALUE;
    }

    @Override
    public long getReverseOrMissing(final int index) {
        if (isInRangeReverse(index)) {
            return array.get(arrayIndexReverseUnchecked(index));
        }
        return MISSING_VALUE;
    }

    @Override
    public int getLongs(final long[] dst, final int dstOffset, final int srcIndex, final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length should not be negative: " + length);
        }
        //overflow-safe bounds check in subtraction form: dstOffset + length > dst.length
        if (dstOffset < 0 || length > dst.length - dstOffset) {
            throw new IllegalArgumentException("destination window [" + dstOffset + ", " + (dstOffset + length)
                    + ") does not fit into dst of length " + dst.length);
        }
        int copied = 0;
        for (int i = 0; i < length; i++) {
            //long math: srcIndex + i can overflow int
            final long idx = (long) srcIndex + i;
            if (idx >= 0 && idx < size) {
                dst[dstOffset + i] = array.get(arrayIndexUnchecked((int) idx));
                copied++;
            } else {
                dst[dstOffset + i] = MISSING_VALUE;
            }
        }
        return copied;
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

    public long[] toArray(final long[] a) {
        if (a.length < size) {
            // Make a new array of a's runtime type, but my contents:
            final long[] array = new long[size];
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
    @Override
    public void circularAdd(final long value) {
        pretendAdd();
        array.set(endArrayIndex, value);
    }

    @Override
    public void circularPrepend(final long value) {
        pretendPrepend();
        array.set(startArrayIndex, value);
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced by the rollover and returned
     * (e.g. to clean up the instance).
     */
    @Override
    public long evictAdd(final long value) {
        final int evictedSlot = evictAddSlot();
        final long evicted = evictedSlot >= 0 ? array.get(evictedSlot) : MISSING_VALUE;
        array.set(endArrayIndex, value);
        return evicted;
    }

    @Override
    public long evictPrepend(final long value) {
        final int evictedSlot = evictPrependSlot();
        final long evicted = evictedSlot >= 0 ? array.get(evictedSlot) : MISSING_VALUE;
        array.set(startArrayIndex, value);
        return evicted;
    }

    @Override
    public boolean offer(final long value) {
        if (isFull()) {
            return false;
        } else {
            circularAdd(value);
            return true;
        }
    }

    public boolean offerPrepend(final long value) {
        if (isFull()) {
            return false;
        } else {
            circularPrepend(value);
            return true;
        }
    }

    public boolean prepend(final long e) {
        if (offerPrepend(e)) {
            return true;
        } else {
            throw new IllegalStateException("offer returned false");
        }
    }

    @Override
    public long peek() {
        if (isEmpty()) {
            return MISSING_VALUE;
        }
        final long first = array.get(startArrayIndex);
        return first;
    }

    @Override
    public long poll() {
        if (isEmpty()) {
            return MISSING_VALUE;
        }
        final long first = array.get(startArrayIndex);
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

}
