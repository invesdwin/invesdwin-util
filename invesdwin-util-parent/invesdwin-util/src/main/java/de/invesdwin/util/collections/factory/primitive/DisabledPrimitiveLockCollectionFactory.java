package de.invesdwin.util.collections.factory.primitive;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IGenericPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.JavaPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.RoaringPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.queue.IBooleanPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.IDoublePrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.IGenericPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.IIntegerPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.ILongPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularBooleanPrimitiveArrayQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularDoublePrimitiveArrayQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularGenericPrimitiveArrayQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularIntegerPrimitiveArrayQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularLongPrimitiveArrayQueue;
import de.invesdwin.util.collections.factory.DisabledLockCollectionFactory;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;

/**
 * Raw unlocked instances for {@link ILockCollectionFactory#primitive()}; headless backtests pay no locking at all.
 */
@Immutable
public final class DisabledPrimitiveLockCollectionFactory implements IPrimitiveLockCollectionFactory {

    public static final DisabledPrimitiveLockCollectionFactory INSTANCE = new DisabledPrimitiveLockCollectionFactory();

    private DisabledPrimitiveLockCollectionFactory() {}

    @Override
    public IPrimitiveBitSet newBitSet(final int initialSize) {
        /*
         * java bitsets are about twice as fast as roaring bitsets, though roaring might be interesting to use with
         * larger sizes to stay in memory limits
         */
        if (initialSize > DisabledLockCollectionFactory.ROARING_BITMAP_THRESHOLD) {
            return new RoaringPrimitiveBitSet(initialSize);
        } else {
            return new JavaPrimitiveBitSet(initialSize);
        }
    }

    @Override
    public IDoublePrimitiveCircularQueue newCircularDoubleQueue(final int capacity) {
        return new CircularDoublePrimitiveArrayQueue(capacity);
    }

    @Override
    public IDoublePrimitiveCircularQueue newCircularDoubleQueue(final IDoublePrimitiveArray backingStore) {
        return new CircularDoublePrimitiveArrayQueue(backingStore);
    }

    @Override
    public ILongPrimitiveCircularQueue newCircularLongQueue(final int capacity) {
        return new CircularLongPrimitiveArrayQueue(capacity);
    }

    @Override
    public ILongPrimitiveCircularQueue newCircularLongQueue(final ILongPrimitiveArray backingStore) {
        return new CircularLongPrimitiveArrayQueue(backingStore);
    }

    @Override
    public IIntegerPrimitiveCircularQueue newCircularIntegerQueue(final int capacity) {
        return new CircularIntegerPrimitiveArrayQueue(capacity);
    }

    @Override
    public IIntegerPrimitiveCircularQueue newCircularIntegerQueue(final IIntegerPrimitiveArray backingStore) {
        return new CircularIntegerPrimitiveArrayQueue(backingStore);
    }

    @Override
    public IBooleanPrimitiveCircularQueue newCircularBooleanQueue(final int capacity) {
        return new CircularBooleanPrimitiveArrayQueue(capacity);
    }

    @Override
    public IBooleanPrimitiveCircularQueue newCircularBooleanQueue(final IBooleanPrimitiveArray backingStore) {
        return new CircularBooleanPrimitiveArrayQueue(backingStore);
    }

    @Override
    public <E> IGenericPrimitiveCircularQueue<E> newCircularGenericQueue(final Class<E> type, final int capacity) {
        return new CircularGenericPrimitiveArrayQueue<E>(type, capacity);
    }

    @Override
    public <E> IGenericPrimitiveCircularQueue<E> newCircularGenericQueue(
            final IGenericPrimitiveArray<E> backingStore) {
        return new CircularGenericPrimitiveArrayQueue<E>(backingStore);
    }

}
