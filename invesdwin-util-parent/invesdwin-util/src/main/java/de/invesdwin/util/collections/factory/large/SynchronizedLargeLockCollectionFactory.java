package de.invesdwin.util.collections.factory.large;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.concurrent.SynchronizedLargeBitSet;
import de.invesdwin.util.collections.array.large.queue.IBooleanLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.IDoubleLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.IGenericLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.IIntegerLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.ILongLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.circular.concurrent.SynchronizedBooleanLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.circular.concurrent.SynchronizedDoubleLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.circular.concurrent.SynchronizedGenericLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.circular.concurrent.SynchronizedIntegerLargeCircularQueue;
import de.invesdwin.util.collections.array.large.queue.circular.concurrent.SynchronizedLongLargeCircularQueue;
import de.invesdwin.util.collections.factory.SynchronizedLockCollectionFactory;

/**
 * Monitor-synchronized wrappers around {@link DisabledLargeLockCollectionFactory} for
 * {@link SynchronizedLockCollectionFactory#large()}.
 */
@Immutable
public final class SynchronizedLargeLockCollectionFactory implements ILargeLockCollectionFactory {

    public static final SynchronizedLargeLockCollectionFactory INSTANCE = new SynchronizedLargeLockCollectionFactory();

    private SynchronizedLargeLockCollectionFactory() {}

    @Override
    public ILargeBitSet newBitSet(final long initialSize) {
        return new SynchronizedLargeBitSet(DisabledLargeLockCollectionFactory.INSTANCE.newBitSet(initialSize));
    }

    @Override
    public IDoubleLargeCircularQueue newCircularDoubleQueue(final long capacity) {
        return new SynchronizedDoubleLargeCircularQueue(DisabledLargeLockCollectionFactory.INSTANCE.newCircularDoubleQueue(capacity));
    }

    @Override
    public IDoubleLargeCircularQueue newCircularDoubleQueue(final IDoubleLargeArray backingStore) {
        return new SynchronizedDoubleLargeCircularQueue(DisabledLargeLockCollectionFactory.INSTANCE.newCircularDoubleQueue(backingStore));
    }

    @Override
    public ILongLargeCircularQueue newCircularLongQueue(final long capacity) {
        return new SynchronizedLongLargeCircularQueue(DisabledLargeLockCollectionFactory.INSTANCE.newCircularLongQueue(capacity));
    }

    @Override
    public ILongLargeCircularQueue newCircularLongQueue(final ILongLargeArray backingStore) {
        return new SynchronizedLongLargeCircularQueue(DisabledLargeLockCollectionFactory.INSTANCE.newCircularLongQueue(backingStore));
    }

    @Override
    public IIntegerLargeCircularQueue newCircularIntegerQueue(final long capacity) {
        return new SynchronizedIntegerLargeCircularQueue(DisabledLargeLockCollectionFactory.INSTANCE.newCircularIntegerQueue(capacity));
    }

    @Override
    public IIntegerLargeCircularQueue newCircularIntegerQueue(final IIntegerLargeArray backingStore) {
        return new SynchronizedIntegerLargeCircularQueue(DisabledLargeLockCollectionFactory.INSTANCE.newCircularIntegerQueue(backingStore));
    }

    @Override
    public IBooleanLargeCircularQueue newCircularBooleanQueue(final long capacity) {
        return new SynchronizedBooleanLargeCircularQueue(DisabledLargeLockCollectionFactory.INSTANCE.newCircularBooleanQueue(capacity));
    }

    @Override
    public IBooleanLargeCircularQueue newCircularBooleanQueue(final IBooleanLargeArray backingStore) {
        return new SynchronizedBooleanLargeCircularQueue(DisabledLargeLockCollectionFactory.INSTANCE.newCircularBooleanQueue(backingStore));
    }

    @Override
    public <E> IGenericLargeCircularQueue<E> newCircularGenericQueue(final Class<E> type, final long capacity) {
        return new SynchronizedGenericLargeCircularQueue<E>(DisabledLargeLockCollectionFactory.INSTANCE.newCircularGenericQueue(type, capacity));
    }

    @Override
    public <E> IGenericLargeCircularQueue<E> newCircularGenericQueue(final IGenericLargeArray<E> backingStore) {
        return new SynchronizedGenericLargeCircularQueue<E>(DisabledLargeLockCollectionFactory.INSTANCE.newCircularGenericQueue(backingStore));
    }

}
