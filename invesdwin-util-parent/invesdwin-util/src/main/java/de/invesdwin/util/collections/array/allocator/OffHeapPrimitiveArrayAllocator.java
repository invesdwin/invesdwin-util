package de.invesdwin.util.collections.array.allocator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.IIntegerArray;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.array.buffer.BufferBooleanArray;
import de.invesdwin.util.collections.array.buffer.BufferDoubleArray;
import de.invesdwin.util.collections.array.buffer.BufferIntegerArray;
import de.invesdwin.util.collections.array.buffer.BufferLongArray;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.bitset.LongArrayBitSet;
import de.invesdwin.util.collections.bitset.LongArrayBitSetBase;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class OffHeapPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

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
        return new BufferDoubleArray(ByteBuffers.allocateDirect(size * Double.BYTES));
    }

    @Override
    public IIntegerArray newIntegerArray(final String id, final int size) {
        return new BufferIntegerArray(ByteBuffers.allocateDirect(size * Integer.BYTES));
    }

    @Override
    public IBooleanArray newBooleanArray(final String id, final int size) {
        return new BufferBooleanArray(ByteBuffers.allocateDirect(BitSets.wordIndex(size)));
    }

    @Override
    public IBitSet newBitSet(final String id, final int size) {
        return new LongArrayBitSet(new LongArrayBitSetBase(newLongArray(id, BitSets.wordIndex(size - 1) + 1), size),
                size);
    }

    @Override
    public ILongArray newLongArray(final String id, final int size) {
        return new BufferLongArray(ByteBuffers.allocateDirect(size * Long.BYTES));
    }

}
