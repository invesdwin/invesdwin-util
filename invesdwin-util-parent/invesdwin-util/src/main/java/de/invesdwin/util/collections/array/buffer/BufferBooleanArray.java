package de.invesdwin.util.collections.array.buffer;

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

    private final IByteBuffer buffer;
    private final BitSetBooleanArray delegate;

    public BufferBooleanArray(final IByteBuffer buffer) {
        this.buffer = buffer;
        final LongArrayBitSetBase bitSet = new LongArrayBitSetBase(new BufferLongArray(buffer));
        this.delegate = new BitSetBooleanArray(new LongArrayBitSet(bitSet, bitSet.cardinality()));
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
    public int toBuffer(final IByteBuffer buffer) {
        buffer.putBytes(0, this.buffer);
        return this.buffer.capacity();
    }

}
