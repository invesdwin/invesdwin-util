package de.invesdwin.util.collections.factory.large;

import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.queue.IBooleanLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.IDoubleLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.IGenericLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.IIntegerLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.ILongLargeCircularQueue;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.factory.primitive.IPrimitiveLockCollectionFactory;

/**
 * Long-indexed view of {@link ILockCollectionFactory} for the large array world (bitsets and circular queues).
 * Always reached via {@code lockCollectionFactory.large()} - {@link ILockCollectionFactory} stays the single
 * starting point, so long-indexed allocations can never be mixed with the int-indexed {@link IPrimitiveLockCollectionFactory}
 * ones whose cursors cannot span them.
 */
public interface ILargeLockCollectionFactory {

    /**
     * A java bitset below the roaring threshold (about twice as fast), a roaring bitmap above it to stay within
     * memory limits, and a segmented roaring bitmap beyond what a single roaring bitmap can address.
     */
    ILargeBitSet newBitSet(long initialSize);

    /**
     * Circular queues with rollover and clamped positional reads; the thread-safe factory returns locked wrappers,
     * the disabled one raw unlocked instances (headless backtests pay no locking). The backing-store overloads let
     * the caller provide the storage (e.g. an off heap array) which becomes the queue's capacity.
     */
    IDoubleLargeCircularQueue newCircularDoubleQueue(long capacity);

    IDoubleLargeCircularQueue newCircularDoubleQueue(IDoubleLargeArray backingStore);

    ILongLargeCircularQueue newCircularLongQueue(long capacity);

    ILongLargeCircularQueue newCircularLongQueue(ILongLargeArray backingStore);

    IIntegerLargeCircularQueue newCircularIntegerQueue(long capacity);

    IIntegerLargeCircularQueue newCircularIntegerQueue(IIntegerLargeArray backingStore);

    IBooleanLargeCircularQueue newCircularBooleanQueue(long capacity);

    IBooleanLargeCircularQueue newCircularBooleanQueue(IBooleanLargeArray backingStore);

    <E> IGenericLargeCircularQueue<E> newCircularGenericQueue(Class<E> type, long capacity);

    <E> IGenericLargeCircularQueue<E> newCircularGenericQueue(IGenericLargeArray<E> backingStore);

}
