package de.invesdwin.util.collections.array.buffer;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.BitSetBooleanArray;
import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.SliceDelegateBooleanArray;
import de.invesdwin.util.collections.bitset.LongArrayBitSet;
import de.invesdwin.util.collections.bitset.LongArrayBitSetBase;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class BufferBooleanArray implements IBooleanArray {

    public static final int LENGTH_INDEX = 0;
    public static final int LENGTH_SIZE = Integer.BYTES;

    public static final int ARRAY_INDEX = LENGTH_INDEX + LENGTH_SIZE;

    private final IByteBuffer buffer;
    private final BitSetBooleanArray delegate;

    public BufferBooleanArray(final IByteBuffer buffer) {
        this.buffer = buffer;
        final int expectedSize = buffer.getInt(LENGTH_INDEX);
        final LongArrayBitSetBase bitSet = new LongArrayBitSetBase(new BufferLongArray(buffer.sliceFrom(ARRAY_INDEX)),
                expectedSize);
        this.delegate = new BitSetBooleanArray(new LongArrayBitSet(bitSet, expectedSize));
    }

    public BufferBooleanArray(final IByteBuffer buffer, final int expectedSize) {
        this.buffer = buffer;
        final LongArrayBitSetBase bitSet = new LongArrayBitSetBase(new BufferLongArray(buffer), expectedSize);
        this.delegate = new BitSetBooleanArray(new LongArrayBitSet(bitSet, expectedSize));
    }

    public BitSetBooleanArray getDelegate() {
        return delegate;
    }

    @Override
    public void set(final int index, final boolean value) {
        delegate.set(index, value);
    }

    @Override
    public boolean get(final int index) {
        return delegate.get(index);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public IBooleanArray slice(final int fromIndex, final int length) {
        return new SliceDelegateBooleanArray(this, fromIndex, length);
    }

    @Override
    public boolean[] asArray() {
        return delegate.asArray();
    }

    @Override
    public boolean[] asArray(final int fromIndex, final int length) {
        return delegate.asArray(fromIndex, length);
    }

    @Override
    public boolean[] asArrayCopy() {
        return delegate.asArrayCopy();
    }

    @Override
    public boolean[] asArrayCopy(final int fromIndex, final int length) {
        return delegate.asArrayCopy(fromIndex, length);
    }

    @Override
    public void getBooleans(final int srcPos, final IBooleanArray dest, final int destPos, final int length) {
        delegate.getBooleans(srcPos, dest, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int getBuffer(final IByteBuffer dst) throws IOException {
        dst.putInt(LENGTH_INDEX, delegate.getBitSet().size());
        buffer.getBytes(0, dst, ARRAY_INDEX, buffer.capacity());
        return ARRAY_INDEX + buffer.capacity();
    }

}
