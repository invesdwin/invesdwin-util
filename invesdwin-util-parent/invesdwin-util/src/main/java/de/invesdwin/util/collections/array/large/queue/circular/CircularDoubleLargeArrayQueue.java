package de.invesdwin.util.collections.array.large.queue.circular;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.accessor.IDoubleLargeArrayAccessor;
import de.invesdwin.util.collections.array.large.queue.IDoubleLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularDoublePrimitiveArrayQueue;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.math.Integers;

/**
 * Large variant of {@link CircularDoublePrimitiveArrayQueue} using long based indexes and sizes, thus not
 * being limited to an {@link Integer#MAX_VALUE} sized queue. Values are stored in a {@link IDoubleLargeArray}
 * which switches to segmented heap arrays for capacities exceeding the maximum array size of the JVM.
 */
@NotThreadSafe
public class CircularDoubleLargeArrayQueue implements IDoubleLargeQueue, IDoubleLargeArrayAccessor {

    private final IDoubleLargeArray array;
    private final long capacity;
    private final long lastPossibleIndex;
    private long size = 0;
    private long startArrayIndex = -1;
    private long endArrayIndex = -1;

    public CircularDoubleLargeArrayQueue(final long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity should not be negative or 0: " + capacity);
        }
        this.array = IDoubleLargeArray.newInstance(capacity);
        this.capacity = capacity;
        this.lastPossibleIndex = capacity - 1;
    }

    /**
     * Uses the given array as the backing store, so the capacity is the size of the array and it can be replaced by
     * another implementation (e.g. an off heap based one). Values of a previous usage are never read, so an array that
     * was used before can be reused as it is.
     */
    public CircularDoubleLargeArrayQueue(final IDoubleLargeArray array) {
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
    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    @Override
    public long size() {
        return size;
    }

    public long capacity() {
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

    private long arrayIndexReverse(final long index) {
        if (index >= size) {
            throw FastIndexOutOfBoundsException.getInstance("index[%s] should not be greater or equal to size[%s]",
                    index, size);
        }
        if (index < 0) {
            throw FastIndexOutOfBoundsException.getInstance("index[%s] should not be negative", index);
        }
        final long idx = (endArrayIndex - index);
        if (idx < 0) {
            final long adjIdx = (capacity + idx);
            return adjIdx;
        } else {
            return idx;
        }
    }

    private long arrayIndex(final long index) {
        if (index >= size) {
            throw FastIndexOutOfBoundsException.getInstance("index[%s] should not be greater or equal to size[%s]",
                    index, size);
        }
        if (index < 0) {
            throw FastIndexOutOfBoundsException.getInstance("index[%s] should not be negative", index);
        }
        final long idx = (startArrayIndex + index) % capacity;
        return idx;
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
    public void circularAdd(final double value) {
        pretendAdd();
        array.set(endArrayIndex, value);
    }

    public void circularPrepend(final double value) {
        pretendPrepend();
        array.set(startArrayIndex, value);
    }

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced by the rollover and returned
     * (e.g. to clean up the instance).
     */
    public double evictAdd(final double value) {
        //an untouched slot is never read, so only a real rollover reports an evicted element
        final boolean rolling = isFull();
        pretendAdd();
        final double evicted = rolling ? array.get(endArrayIndex) : MISSING_VALUE;
        array.set(endArrayIndex, value);
        return evicted;
    }

    public double evictPrepend(final double value) {
        //an untouched slot is never read, so only a real rollover reports an evicted element
        final boolean rolling = isFull();
        pretendPrepend();
        final double evicted = rolling ? array.get(startArrayIndex) : MISSING_VALUE;
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

    public void pretendClear() {
        this.size = 0;
        this.startArrayIndex = -1;
        this.endArrayIndex = -1;
    }

}
