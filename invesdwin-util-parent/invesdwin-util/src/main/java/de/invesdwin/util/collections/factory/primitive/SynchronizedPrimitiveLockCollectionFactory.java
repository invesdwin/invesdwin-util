package de.invesdwin.util.collections.factory.primitive;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IGenericPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.concurrent.SynchronizedPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.queue.IBooleanPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.IDoublePrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.IGenericPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.IIntegerPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.ILongPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.concurrent.SynchronizedBooleanPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.concurrent.SynchronizedDoublePrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.concurrent.SynchronizedGenericPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.concurrent.SynchronizedIntegerPrimitiveCircularQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.concurrent.SynchronizedLongPrimitiveCircularQueue;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.factory.SynchronizedLockCollectionFactory;

/**
 * Monitor-synchronized wrappers around {@link DisabledPrimitiveLockCollectionFactory} for
 * {@link SynchronizedLockCollectionFactory#primitive()}.
 */
@Immutable
public final class SynchronizedPrimitiveLockCollectionFactory implements IPrimitiveLockCollectionFactory {

    public static final SynchronizedPrimitiveLockCollectionFactory INSTANCE = new SynchronizedPrimitiveLockCollectionFactory();

    private SynchronizedPrimitiveLockCollectionFactory() {}

    @Override
    public IPrimitiveBitSet newBitSet(final int initialSize) {
        return new SynchronizedPrimitiveBitSet(DisabledPrimitiveLockCollectionFactory.INSTANCE.newBitSet(initialSize));
    }

    @Override
    public IDoublePrimitiveCircularQueue newCircularDoubleQueue(final int capacity) {
        return new SynchronizedDoublePrimitiveCircularQueue(DisabledPrimitiveLockCollectionFactory.INSTANCE.newCircularDoubleQueue(capacity));
    }

    @Override
    public IDoublePrimitiveCircularQueue newCircularDoubleQueue(final IDoublePrimitiveArray backingStore) {
        return new SynchronizedDoublePrimitiveCircularQueue(DisabledPrimitiveLockCollectionFactory.INSTANCE.newCircularDoubleQueue(backingStore));
    }

    @Override
    public ILongPrimitiveCircularQueue newCircularLongQueue(final int capacity) {
        return new SynchronizedLongPrimitiveCircularQueue(DisabledPrimitiveLockCollectionFactory.INSTANCE.newCircularLongQueue(capacity));
    }

    @Override
    public ILongPrimitiveCircularQueue newCircularLongQueue(final ILongPrimitiveArray backingStore) {
        return new SynchronizedLongPrimitiveCircularQueue(DisabledPrimitiveLockCollectionFactory.INSTANCE.newCircularLongQueue(backingStore));
    }

    @Override
    public IIntegerPrimitiveCircularQueue newCircularIntegerQueue(final int capacity) {
        return new SynchronizedIntegerPrimitiveCircularQueue(DisabledPrimitiveLockCollectionFactory.INSTANCE.newCircularIntegerQueue(capacity));
    }

    @Override
    public IIntegerPrimitiveCircularQueue newCircularIntegerQueue(final IIntegerPrimitiveArray backingStore) {
        return new SynchronizedIntegerPrimitiveCircularQueue(DisabledPrimitiveLockCollectionFactory.INSTANCE.newCircularIntegerQueue(backingStore));
    }

    @Override
    public IBooleanPrimitiveCircularQueue newCircularBooleanQueue(final int capacity) {
        return new SynchronizedBooleanPrimitiveCircularQueue(DisabledPrimitiveLockCollectionFactory.INSTANCE.newCircularBooleanQueue(capacity));
    }

    @Override
    public IBooleanPrimitiveCircularQueue newCircularBooleanQueue(final IBooleanPrimitiveArray backingStore) {
        return new SynchronizedBooleanPrimitiveCircularQueue(DisabledPrimitiveLockCollectionFactory.INSTANCE.newCircularBooleanQueue(backingStore));
    }

    @Override
    public <E> IGenericPrimitiveCircularQueue<E> newCircularGenericQueue(final Class<E> type, final int capacity) {
        return new SynchronizedGenericPrimitiveCircularQueue<E>(DisabledPrimitiveLockCollectionFactory.INSTANCE.newCircularGenericQueue(type, capacity));
    }

    @Override
    public <E> IGenericPrimitiveCircularQueue<E> newCircularGenericQueue(
            final IGenericPrimitiveArray<E> backingStore) {
        return new SynchronizedGenericPrimitiveCircularQueue<E>(DisabledPrimitiveLockCollectionFactory.INSTANCE.newCircularGenericQueue(backingStore));
    }

}
