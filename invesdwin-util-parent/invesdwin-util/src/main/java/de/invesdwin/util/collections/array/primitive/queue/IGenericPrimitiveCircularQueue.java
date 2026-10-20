package de.invesdwin.util.collections.array.primitive.queue;

import de.invesdwin.util.collections.array.primitive.accessor.IGenericPrimitiveArrayAccessor;

/**
 * Circular window semantics on top of {@link IGenericPrimitiveQueue} (the int-indexed mirror of
 * {@code IGenericLargeCircularQueue}): bounded capacity with rollover, positional reads from both ends and clamped
 * variants. Unlike the plain queue, reading is a total function with {@link #getOrMissing(int)}/
 * {@link #getReverseOrMissing(int)}: any out-of-range index returns null instead of throwing, which lets window
 * based consumers probe past the ring edges cheaply. The clamped reads cover positional staleness only; deciding
 * whether reading beyond a watermark is allowed at all stays the consumer's policy.
 *
 * <p>
 * The queues stay capacity-agnostic (any capacity &gt; 0, no power-of-two rule): absolute-slot/ring protocols are
 * layered on top by their consumers.
 *
 * <p>
 * Class only (not part of this interface, present on the concrete implementations): offerPrepend, prepend,
 * toArray, toString ranges, pretendClear, pollLast, removeLast, getHead, getTail.
 */
public interface IGenericPrimitiveCircularQueue<E> extends IGenericPrimitiveQueue<E>, IGenericPrimitiveArrayAccessor<E> {

    int capacity();

    boolean isFull();

    /**
     * The first value has index 0, the value after the first has index 1.
     *
     * @throws IndexOutOfBoundsException
     *             if the index is not in {@code [0, size)}
     */
    @Override
    E get(int index);

    /**
     * The last value has index 0, the value before the last has index 1.
     *
     * @throws IndexOutOfBoundsException
     *             if the index is not in {@code [0, size)}
     */
    E getReverse(int index);

    /**
     * Clamped forward read: never throws, returns null for any index outside {@code [0, size)}.
     */
    E getOrMissing(int index);

    /**
     * Clamped reverse read: never throws, returns null for any index outside {@code [0, size)}.
     */
    E getReverseOrMissing(int index);

    /**
     * True if the given forward index is currently readable.
     */
    boolean isInRange(int index);

    /**
     * True if the given reverse index is currently readable.
     */
    boolean isInRangeReverse(int index);

    /**
     * Rolls over one element higher without adding or removing anything. Useful to circle the indexes around the
     * existing values (e.g. when reusing the values in the circular array).
     */
    void pretendAdd();

    void pretendPrepend();

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced silently by the rollover (so
     * that it can be garbage collected).
     */
    void circularAdd(E value);

    void circularPrepend(E value);

    /**
     * Adds an element to the array, if capacity is exceeded, the first element is replaced by the rollover and
     * returned (e.g. to clean up the instance). Returns null if nothing was evicted - a slot that was never rolled
     * over is never reported as evicted.
     */
    E evictAdd(E value);

    /**
     * Same as {@link #evictAdd(Object)} but rolling over the other end.
     */
    E evictPrepend(E value);

}
