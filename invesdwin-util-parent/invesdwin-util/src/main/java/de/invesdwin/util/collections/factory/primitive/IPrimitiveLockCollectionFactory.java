package de.invesdwin.util.collections.factory.primitive;

import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IGenericPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.queue.IBooleanPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.IDoublePrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.IGenericPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.IIntegerPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.ILongPrimitiveCircularQueue;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.factory.large.ILargeLockCollectionFactory;

/**
 * Int-indexed view of {@link ILockCollectionFactory} for the primitive array world (bitsets and circular queues).
 * Always reached via {@code lockCollectionFactory.primitive()} - {@link ILockCollectionFactory} stays the single
 * starting point, so int-indexed allocations can never be mixed with the long-indexed {@link ILargeLockCollectionFactory}
 * ones whose cursors do not fit into an int.
 */
public interface IPrimitiveLockCollectionFactory {

    /**
     * A java bitset below the roaring threshold (about twice as fast), a roaring bitmap above it to stay within
     * memory limits.
     */
    IPrimitiveBitSet newBitSet(int initialSize);

    /**
     * Int-indexed circular queues with rollover and clamped positional reads mirroring the large variants; the
     * thread-safe factory returns locked wrappers, the disabled one raw unlocked instances. The backing-store
     * overloads let the caller provide the storage (e.g. a wrapped primitive or off heap array) which becomes the
     * queue's capacity.
     */
    IDoublePrimitiveCircularQueue newCircularDoubleQueue(int capacity);

    IDoublePrimitiveCircularQueue newCircularDoubleQueue(IDoublePrimitiveArray backingStore);

    ILongPrimitiveCircularQueue newCircularLongQueue(int capacity);

    ILongPrimitiveCircularQueue newCircularLongQueue(ILongPrimitiveArray backingStore);

    IIntegerPrimitiveCircularQueue newCircularIntegerQueue(int capacity);

    IIntegerPrimitiveCircularQueue newCircularIntegerQueue(IIntegerPrimitiveArray backingStore);

    IBooleanPrimitiveCircularQueue newCircularBooleanQueue(int capacity);

    IBooleanPrimitiveCircularQueue newCircularBooleanQueue(IBooleanPrimitiveArray backingStore);

    <E> IGenericPrimitiveCircularQueue<E> newCircularGenericQueue(Class<E> type, int capacity);

    <E> IGenericPrimitiveCircularQueue<E> newCircularGenericQueue(IGenericPrimitiveArray<E> backingStore);

}
