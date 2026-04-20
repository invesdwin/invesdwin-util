package de.invesdwin.util.collections.array.large.buffer;

import java.io.IOException;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.bitset.BitSetBooleanLargeArray;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.LongArrayLargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.LongArrayLargeBitSetBase;
import de.invesdwin.util.collections.array.large.slice.SliceDelegateBooleanLargeArray;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class BufferBooleanLargeArray implements IBooleanLargeArray {

    public static final int LENGTH_INDEX = 0;
    public static final int LENGTH_SIZE = Long.BYTES;

    public static final int ARRAY_INDEX = LENGTH_INDEX + LENGTH_SIZE;

    private final IMemoryBuffer buffer;
    private final BitSetBooleanLargeArray delegate;

    public BufferBooleanLargeArray(final Function<LongArrayLargeBitSet, ILargeBitSet> delegateCopyFactory,
            final IMemoryBuffer buffer) {
        this.buffer = buffer;
        final long expectedSize = buffer.getLong(LENGTH_INDEX);
        final LongArrayLargeBitSetBase bitSet = new LongArrayLargeBitSetBase(
                new BufferLongLargeArray(buffer.sliceFrom(ARRAY_INDEX)), expectedSize);
        this.delegate = new BitSetBooleanLargeArray(new LongArrayLargeBitSet(delegateCopyFactory, bitSet, expectedSize));
        assert buffer.getId() == delegate.getId();
    }

    public BufferBooleanLargeArray(final Function<LongArrayLargeBitSet, ILargeBitSet> delegateCopyFactory,
            final IMemoryBuffer buffer, final long expectedSize) {
        this.buffer = buffer;
        final LongArrayLargeBitSetBase bitSet = new LongArrayLargeBitSetBase(new BufferLongLargeArray(buffer),
                expectedSize);
        this.delegate = new BitSetBooleanLargeArray(new LongArrayLargeBitSet(delegateCopyFactory, bitSet, expectedSize));
    }

    @Override
    public int getId() {
        return buffer.getId();
    }

    public BitSetBooleanLargeArray getDelegate() {
        return delegate;
    }

    @Override
    public void set(final long index, final boolean value) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        delegate.set(index, value);
    }

    @Override
    public boolean get(final long index) {
        if (index < 0 || index >= size()) {
            throw FastIndexOutOfBoundsException.getInstance("Index: %s, Size: %s", index + size());
        }
        return delegate.get(index);
    }

    @Override
    public long size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public IBooleanLargeArray slice(final long fromIndex, final long length) {
        return new SliceDelegateBooleanLargeArray(this, fromIndex, length);
    }

    @Override
    public boolean[] asArray(final long fromIndex, final int length) {
        return delegate.asArray(fromIndex, length);
    }

    @Override
    public boolean[] asArrayCopy(final long fromIndex, final int length) {
        return delegate.asArrayCopy(fromIndex, length);
    }

    @Override
    public void getBooleans(final long srcPos, final IBooleanLargeArray dest, final long destPos, final long length) {
        delegate.getBooleans(srcPos, dest, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, ByteBuffers.checkedCast(Longs.min(ByteBuffers.MAX_TO_STRING_COUNT, size()))));
    }

    @Override
    public long getBuffer(final IMemoryBuffer dst) throws IOException {
        dst.putLong(LENGTH_INDEX, delegate.getBitSet().size());
        buffer.getBytes(0, dst, ARRAY_INDEX, buffer.capacity());
        return ARRAY_INDEX + buffer.capacity();
    }

    @Override
    public long getBufferLength() {
        return ARRAY_INDEX + buffer.capacity();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

}
