package de.invesdwin.util.collections.array.allocator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.IIntegerArray;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class OnHeapPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

    public static final OnHeapPrimitiveArrayAllocator INSTANCE = new OnHeapPrimitiveArrayAllocator();

    private OnHeapPrimitiveArrayAllocator() {}

    @Override
    public IByteBuffer getByteBuffer(final String id) {
        return null;
    }

    @Override
    public IDoubleArray getDoubleArray(final String id) {
        return null;
    }

    @Override
    public IIntegerArray getIntegerArray(final String id) {
        return null;
    }

    @Override
    public IBooleanArray getBooleanArray(final String id) {
        return null;
    }

    @Override
    public IBitSet getBitSet(final String id) {
        return null;
    }

    @Override
    public ILongArray getLongArray(final String id) {
        return null;
    }

    @Override
    public IByteBuffer newByteBuffer(final String id, final int size) {
        return ByteBuffers.allocate(size);
    }

    @Override
    public IDoubleArray newDoubleArray(final String id, final int size) {
        return IDoubleArray.newInstance(size);
    }

    @Override
    public IIntegerArray newIntegerArray(final String id, final int size) {
        return IIntegerArray.newInstance(size);
    }

    @Override
    public IBooleanArray newBooleanArray(final String id, final int size) {
        return IBooleanArray.newInstance(ILockCollectionFactory.getInstance(false).newBitSet(size));
    }

    @Override
    public IBitSet newBitSet(final String id, final int size) {
        return ILockCollectionFactory.getInstance(false).newBitSet(size);
    }

    @Override
    public ILongArray newLongArray(final String id, final int size) {
        return ILongArray.newInstance(size);
    }

    @Override
    public String toString() {
        return OnHeapPrimitiveArrayAllocator.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(OnHeapPrimitiveArrayAllocator.class);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OnHeapPrimitiveArrayAllocator;
    }

}
