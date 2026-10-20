package de.invesdwin.util.collections.factory.large;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.JavaLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.roaring.RoaringLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.roaring.SegmentedRoaringLargeBitSet;
import de.invesdwin.util.collections.array.large.queue.IBooleanLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.IDoubleLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.IGenericLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.IIntegerLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.ILongLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularBooleanLargeArrayQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularDoubleLargeArrayQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularGenericLargeArrayQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularIntegerLargeArrayQueue;
import de.invesdwin.util.collections.array.large.queue.circular.CircularLongLargeArrayQueue;
import de.invesdwin.util.collections.factory.DisabledLockCollectionFactory;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

/**
 * Raw unlocked instances for {@link ILockCollectionFactory#large()}; headless backtests pay no locking at all.
 */
@Immutable
public final class DisabledLargeLockCollectionFactory implements ILargeLockCollectionFactory {

    public static final DisabledLargeLockCollectionFactory INSTANCE = new DisabledLargeLockCollectionFactory();

    private DisabledLargeLockCollectionFactory() {}

    @Override
    public ILargeBitSet newBitSet(final long initialSize) {
        if (initialSize > RoaringLargeBitSet.MAX_SIZE) {
            /*
             * TODO: test which one is actually faster. Roaring64 implementations do not support range-based
             * operations, which are used in some places. They might be slower than the segmented version, which does
             * support them.
             */
            //return new Roaring64LargeNavigableBitSet(initialSize);
            //return new Roaring64LargeBitSet(initialSize);
            return new SegmentedRoaringLargeBitSet(initialSize);
        } else if (initialSize > DisabledLockCollectionFactory.ROARING_BITMAP_THRESHOLD) {
            return new RoaringLargeBitSet(initialSize);
        } else {
            return new JavaLargeBitSet(ByteBuffers.checkedCast(initialSize));
        }
    }

    @Override
    public IDoubleLargeCircularQueue newCircularDoubleQueue(final long capacity) {
        return new CircularDoubleLargeArrayQueue(capacity);
    }

    @Override
    public IDoubleLargeCircularQueue newCircularDoubleQueue(final IDoubleLargeArray backingStore) {
        return new CircularDoubleLargeArrayQueue(backingStore);
    }

    @Override
    public ILongLargeCircularQueue newCircularLongQueue(final long capacity) {
        return new CircularLongLargeArrayQueue(capacity);
    }

    @Override
    public ILongLargeCircularQueue newCircularLongQueue(final ILongLargeArray backingStore) {
        return new CircularLongLargeArrayQueue(backingStore);
    }

    @Override
    public IIntegerLargeCircularQueue newCircularIntegerQueue(final long capacity) {
        return new CircularIntegerLargeArrayQueue(capacity);
    }

    @Override
    public IIntegerLargeCircularQueue newCircularIntegerQueue(final IIntegerLargeArray backingStore) {
        return new CircularIntegerLargeArrayQueue(backingStore);
    }

    @Override
    public IBooleanLargeCircularQueue newCircularBooleanQueue(final long capacity) {
        return new CircularBooleanLargeArrayQueue(capacity);
    }

    @Override
    public IBooleanLargeCircularQueue newCircularBooleanQueue(final IBooleanLargeArray backingStore) {
        return new CircularBooleanLargeArrayQueue(backingStore);
    }

    @Override
    public <E> IGenericLargeCircularQueue<E> newCircularGenericQueue(final Class<E> type, final long capacity) {
        return new CircularGenericLargeArrayQueue<E>(type, capacity);
    }

    @Override
    public <E> IGenericLargeCircularQueue<E> newCircularGenericQueue(final IGenericLargeArray<E> backingStore) {
        return new CircularGenericLargeArrayQueue<E>(backingStore);
    }

}
