package de.invesdwin.util.collections.array.primitive.queue.circular;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastIndexOutOfBoundsException;

/**
 * Type-free cursor parts shared by all {@code Circular*PrimitiveArrayQueue} implementations - the int-indexed
 * counterpart of {@code ACircularLargeArrayQueue} (see its javadoc for the rationale of the conditional subtract and
 * of keeping queues capacity-agnostic).
 *
 * <p>
 * The forward sum {@code startArrayIndex + index} is computed as a {@code long} before the conditional subtract:
 * with int-backed indexes near {@link Integer#MAX_VALUE} the raw int sum can overflow, which would break the
 * "sum &lt; 2 * capacity" proof the large (long-indexed) variant relies on.
 */
@NotThreadSafe
public abstract class ACircularPrimitiveArrayQueue {

    protected final int capacity;
    protected final int lastPossibleIndex;
    protected int size = 0;
    protected int startArrayIndex = -1;
    protected int endArrayIndex = -1;

    protected ACircularPrimitiveArrayQueue(final int capacity) {
        this.capacity = checkCapacity(capacity);
        this.lastPossibleIndex = this.capacity - 1;
    }

    /**
     * Validates that the given capacity is usable; invoked by the base constructor itself
     * (identity or throw - subclasses may also call it BEFORE their own array allocation,
     * but must never NORMALIZE here: they size their backing array from their own parameter).
     */
    protected static int checkCapacity(final int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity should not be negative or 0: " + capacity);
        }
        return capacity;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int capacity() {
        return capacity;
    }

    /**
     * Rolls over one element higher without adding or removing anything. Useful to circle the indexes around the
     * existing values (e.g. when reusing the values in the circular array).
     */
    public final void pretendAdd() {
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

    public final void pretendPrepend() {
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

    /**
     * Resets size and window indexes without touching the stored values; untouched slots are never read.
     */
    public final void pretendClear() {
        this.size = 0;
        this.startArrayIndex = -1;
        this.endArrayIndex = -1;
    }

    protected final void incrementStartArrayIndex() {
        startArrayIndex++;
        if (startArrayIndex >= capacity) {
            startArrayIndex = 0;
        }
    }

    protected final void decrementEndArrayIndex() {
        endArrayIndex--;
        if (endArrayIndex < 0) {
            endArrayIndex = lastPossibleIndex;
        }
    }

    /**
     * The first value has index 0, the value after the first has index 1.
     *
     * @throws IndexOutOfBoundsException
     *             if the index is not in {@code [0, size)}
     */
    protected final int arrayIndex(final int index) {
        checkForwardIndex(index);
        return arrayIndexUnchecked(index);
    }

    /**
     * Same as {@link #arrayIndex(int)} but without the bounds check, to be used after an own range test (e.g. clamped
     * reads) or with a provably in-range index.
     */
    protected final int arrayIndexUnchecked(final int index) {
        //long math: startArrayIndex + index can overflow int near Integer#MAX_VALUE
        final long idx = (long) startArrayIndex + index;
        //sum is below 2*capacity, so one conditional subtract is equivalent to (startArrayIndex + index) % capacity
        if (idx >= capacity) {
            return (int) (idx - capacity);
        }
        return (int) idx;
    }

    /**
     * The last value has index 0, the value before the last has index 1.
     *
     * @throws IndexOutOfBoundsException
     *             if the index is not in {@code [0, size)}
     */
    protected final int arrayIndexReverse(final int index) {
        checkForwardIndex(index);
        return arrayIndexReverseUnchecked(index);
    }

    /**
     * Same as {@link #arrayIndexReverse(int)} but without the bounds check. No overflow possible: the difference of
     * two values in {@code [0, capacity)} cannot exceed the int range, and adding capacity back stays below it.
     */
    protected final int arrayIndexReverseUnchecked(final int index) {
        final int idx = endArrayIndex - index;
        if (idx < 0) {
            return capacity + idx;
        }
        return idx;
    }

    /**
     * True if the given index can currently be read (in {@code [0, size)}).
     */
    public final boolean isInRange(final int index) {
        return index >= 0 && index < size;
    }

    /**
     * True if the given reverse index can currently be read (in {@code [0, size)}).
     */
    public final boolean isInRangeReverse(final int index) {
        return index >= 0 && index < size;
    }

    private void checkForwardIndex(final int index) {
        if (index >= size) {
            throw FastIndexOutOfBoundsException.getInstance("index[%s] should not be greater or equal to size[%s]",
                    index, size);
        }
        if (index < 0) {
            throw FastIndexOutOfBoundsException.getInstance("index[%s] should not be negative", index);
        }
    }

    /**
     * Type-free part of an evicting add: rolls over and reports which slot currently holds the value that gets
     * overwritten by the caller's write.
     *
     * @return {@link #endArrayIndex} (the slot holding the evicted value) if a real eviction happened, otherwise -1;
     *         an untouched slot is never reported as evicted
     */
    protected final int evictAddSlot() {
        //an untouched slot is never read, so only a real rollover reports an evicted element
        final boolean rolling = isFull();
        pretendAdd();
        if (rolling) {
            return endArrayIndex;
        } else {
            return -1;
        }
    }

    /**
     * Type-free part of an evicting prepend; same contract as {@link #evictAddSlot()} but using
     * {@link #startArrayIndex}.
     */
    protected final int evictPrependSlot() {
        //an untouched slot is never read, so only a real rollover reports an evicted element
        final boolean rolling = isFull();
        pretendPrepend();
        if (rolling) {
            return startArrayIndex;
        } else {
            return -1;
        }
    }

}
