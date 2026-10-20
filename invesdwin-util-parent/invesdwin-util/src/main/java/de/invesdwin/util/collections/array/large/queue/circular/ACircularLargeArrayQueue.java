package de.invesdwin.util.collections.array.large.queue.circular;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastIndexOutOfBoundsException;

/**
 * Type-free cursor parts shared by all {@code Circular*LargeArrayQueue} implementations: capacity, size and the
 * rolling window indexes (start/end) plus their index arithmetic. Element type specific code (reads, writes, eviction
 * values) stays in the typed subclasses, so no slot access ever goes through an Object-returning method (boxing on
 * every primitive read would defeat the purpose of the large primitive queues).
 *
 * <p>
 * The forward index arithmetic uses a conditional subtract instead of a modulo: with {@code 0 <= startArrayIndex <
 * capacity} and {@code 0 <= index < size <= capacity} the sum {@code startArrayIndex + index} is always below
 * {@code 2 * capacity}, thus at most one subtraction is needed to wrap it - provably equivalent to {@code % capacity}
 * but without a hardware division. Same argument for the reverse direction with a single addition.
 *
 * <p>
 * The queues themselves stay capacity-agnostic on purpose (any capacity &gt; 0, no power-of-two rule, no commit
 * position): ring protocols with absolute slot ranges are built on top of them by their consumers.
 */
@NotThreadSafe
public abstract class ACircularLargeArrayQueue {

    protected final long capacity;
    protected final long lastPossibleIndex;
    protected long size = 0;
    protected long startArrayIndex = -1;
    protected long endArrayIndex = -1;

    protected ACircularLargeArrayQueue(final long capacity) {
        this.capacity = checkCapacity(capacity);
        this.lastPossibleIndex = this.capacity - 1;
    }

    /**
     * Validates that the given capacity is usable; invoked by the base constructor itself
     * (identity or throw - subclasses may also call it BEFORE their own array allocation,
     * but must never NORMALIZE here: they size their backing array from their own parameter).
     */
    protected static long checkCapacity(final long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity should not be negative or 0: " + capacity);
        }
        return capacity;
    }

    public long size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public long capacity() {
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
    protected final long arrayIndex(final long index) {
        checkForwardIndex(index);
        return arrayIndexUnchecked(index);
    }

    /**
     * Same as {@link #arrayIndex(long)} but without the bounds check, to be used after an own range test (e.g. clamped
     * reads) or with a provably in-range index.
     */
    protected final long arrayIndexUnchecked(final long index) {
        final long idx = startArrayIndex + index;
        //sum is below 2*capacity, so one conditional subtract is equivalent to (startArrayIndex + index) % capacity
        if (idx >= capacity) {
            return idx - capacity;
        }
        return idx;
    }

    /**
     * The last value has index 0, the value before the last has index 1.
     *
     * @throws IndexOutOfBoundsException
     *             if the index is not in {@code [0, size)}
     */
    protected final long arrayIndexReverse(final long index) {
        checkForwardIndex(index);
        return arrayIndexReverseUnchecked(index);
    }

    /**
     * Same as {@link #arrayIndexReverse(long)} but without the bounds check.
     */
    protected final long arrayIndexReverseUnchecked(final long index) {
        final long idx = endArrayIndex - index;
        if (idx < 0) {
            return capacity + idx;
        }
        return idx;
    }

    /**
     * True if the given index can currently be read (in {@code [0, size)}).
     */
    public final boolean isInRange(final long index) {
        return index >= 0 && index < size;
    }

    /**
     * True if the given reverse index can currently be read (in {@code [0, size)}).
     */
    public final boolean isInRangeReverse(final long index) {
        return index >= 0 && index < size;
    }

    private void checkForwardIndex(final long index) {
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
    protected final long evictAddSlot() {
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
    protected final long evictPrependSlot() {
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
