package de.invesdwin.util.collections.array.large.queue.circular;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.queue.IDoubleLargeCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularDoublePrimitiveArrayQueue;
import de.invesdwin.util.math.Integers;

/**
 * Large variant of {@link CircularDoublePrimitiveArrayQueue} using long based indexes and sizes, thus not being limited
 * to an {@link Integer#MAX_VALUE} sized queue. Values are stored in a {@link IDoubleLargeArray} which switches to
 * segmented heap arrays for capacities exceeding the maximum array size of the JVM.
 */
@NotThreadSafe
public class CircularDoubleLargeArrayQueue extends ACircularLargeArrayQueue implements IDoubleLargeCircularQueue {

    private final IDoubleLargeArray array;

    public CircularDoubleLargeArrayQueue(final long capacity) {
        super(capacity);
        this.array = IDoubleLargeArray.newInstance(capacity);
    }

    /**
     * Uses the given array as the backing store, so the capacity is the size of the array and it can be replaced by
     * another implementation (e.g. an off heap based one). Values of a previous usage are never read, so an array that
     * was used before can be reused as it is.
     */
    public CircularDoubleLargeArrayQueue(final IDoubleLargeArray array) {
        super(array.size());
        this.array = array;
    }

    /**
     * The last value has index 0, the value before the last has index 1.
     */
    @Override
    public double getReverse(final long index) {
        return array.get(arrayIndexReverse(index));
    }

    /**
     * The first value has index 0, the value after the first has index 1.
     */
    @Override
    public double get(final long index) {
        return array.get(arrayIndex(index));
    }

    @Override
    public double getOrMissing(final long index) {
        if (isInRange(index)) {
            return array.get(arrayIndexUnchecked(index));
        }
        return MISSING_VALUE;
    }

    @Override
    public double getReverseOrMissing(final long index) {
        if (isInRangeReverse(index)) {
            return array.get(arrayIndexReverseUnchecked(index));
        }
        return MISSING_VALUE;
    }

    @Override
    public int getDoubles(final double[] dst, final int dstOffset, final long srcIndex, final int length) {
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
            final long idx = srcIndex + i;
            if (isInRange(idx)) {
                dst[dstOffset + i] = array.get(arrayIndexUnchecked(idx));
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
    public double[] toArray(final double[] a) {
        final int intSize = Integers.checkedCast(size);
        if (a.length < intSize) {
            // Make a new array of a's runtime type, but my contents:
            final double[] newArray = new double[intSize];
            for (int i = 0; i < newArray.length; i++) {
                newArray[i] = get(i);
            }
            return newArray;
        }
        for (int i = 0; i < intSize; i++) {
            a[i] = get(i);
        }
        if (a.length > intSize) {
            a[intSize] = MISSING_VALUE;
        }
        return a;
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced silently by the rollover (so
     * that it can be garbage collected).
     */
    @Override
    public void circularAdd(final double value) {
        pretendAdd();
        array.set(endArrayIndex, value);
    }

    @Override
    public void circularPrepend(final double value) {
        pretendPrepend();
        array.set(startArrayIndex, value);
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced by the rollover and returned
     * (e.g. to clean up the instance).
     */
    @Override
    public double evictAdd(final double value) {
        final long evictedSlot = evictAddSlot();
        final double evicted = evictedSlot >= 0 ? array.get(evictedSlot) : MISSING_VALUE;
        array.set(endArrayIndex, value);
        return evicted;
    }

    @Override
    public double evictPrepend(final double value) {
        final long evictedSlot = evictPrependSlot();
        final double evicted = evictedSlot >= 0 ? array.get(evictedSlot) : MISSING_VALUE;
        array.set(startArrayIndex, value);
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
            return MISSING_VALUE;
        }
        final double first = array.get(startArrayIndex);
        return first;
    }

    @Override
    public double poll() {
        if (isEmpty()) {
            return MISSING_VALUE;
        }
        final double first = array.get(startArrayIndex);
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
