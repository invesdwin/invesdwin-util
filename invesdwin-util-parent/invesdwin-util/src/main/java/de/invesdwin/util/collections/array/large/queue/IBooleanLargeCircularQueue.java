package de.invesdwin.util.collections.array.large.queue;

import de.invesdwin.util.collections.array.large.accessor.IBooleanLargeArrayAccessor;

/**
 * Circular window semantics on top of {@link IBooleanLargeQueue}: bounded capacity with rollover, positional reads from
 * both ends and clamped variants. Unlike the plain queue, reading is a total function with
 * {@link #getOrMissing(long)}/{@link #getReverseOrMissing(long)}: any out-of-range index returns {@link #MISSING_VALUE}
 * instead of throwing, which lets window based consumers probe past the ring edges cheaply. The clamped reads cover
 * positional staleness only; deciding whether reading beyond a watermark is allowed at all stays the consumer's policy.
 *
 * <p>
 * The queues stay capacity-agnostic (any capacity &gt; 0, no power-of-two rule): absolute-slot/ring protocols are
 * layered on top by their consumers.
 *
 * <p>
 * Class only (not part of this interface, present on the concrete implementations): offerPrepend, prepend, toArray,
 * toString ranges, pretendClear.
 */
public interface IBooleanLargeCircularQueue extends IBooleanLargeQueue, IBooleanLargeArrayAccessor {

    long capacity();

    boolean isFull();

    /**
     * The first value has index 0, the value after the first has index 1.
     *
     * @throws IndexOutOfBoundsException
     *             if the index is not in {@code [0, size)}
     */
    @Override
    boolean get(long index);

    /**
     * The last value has index 0, the value before the last has index 1.
     *
     * @throws IndexOutOfBoundsException
     *             if the index is not in {@code [0, size)}
     */
    boolean getReverse(long index);

    /**
     * Clamped forward read: never throws, returns {@link #MISSING_VALUE} for any index outside {@code [0, size)}. Note:
     * {@code MISSING_VALUE} is {@code false} and indistinguishable from a stored {@code false} (best-effort signal, see
     * {@link IBooleanLargeQueue#MISSING_VALUE}) -- prefer {@link #isInRange(long)}.
     */
    boolean getOrMissing(long index);

    /**
     * Clamped reverse read: never throws, returns {@link #MISSING_VALUE} (best-effort, see
     * {@link #getOrMissing(long)}).
     */
    boolean getReverseOrMissing(long index);

    /**
     * True if the given forward index is currently readable.
     */
    boolean isInRange(long index);

    /**
     * True if the given reverse index is currently readable.
     */
    boolean isInRangeReverse(long index);

    /**
     * Copies up to {@code length} values forward starting at {@code srcIndex} into {@code dst} at {@code dstOffset};
     * positions outside {@code [srcIndex, srcIndex+length)} that are out of range are filled with
     * {@link #MISSING_VALUE} (clamped, never throwing for range). Forward direction only.
     *
     * @return the number of real values copied (the rest are {@link #MISSING_VALUE} fills)
     * @throws IllegalArgumentException
     *             if {@code length} is negative or the destination window does not fit into {@code dst}
     */
    int getBooleans(boolean[] dst, int dstOffset, long srcIndex, int length);

    /**
     * Rolls over one element higher without adding or removing anything. Useful to circle the indexes around the
     * existing values (e.g. when reusing the values in the circular array).
     */
    void pretendAdd();

    void pretendPrepend();

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced silently by the rollover.
     */
    void circularAdd(boolean value);

    void circularPrepend(boolean value);

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced by the rollover and returned
     * (e.g. to clean up the instance). Returns {@link #MISSING_VALUE} if nothing was evicted.
     */
    boolean evictAdd(boolean value);

    /**
     * Same as {@link #evictAdd(boolean)} but rolling over the other end.
     */
    boolean evictPrepend(boolean value);

}
