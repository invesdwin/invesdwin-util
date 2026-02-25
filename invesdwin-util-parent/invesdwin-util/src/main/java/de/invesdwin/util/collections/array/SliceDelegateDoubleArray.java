package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class SliceDelegateDoubleArray implements IDoubleArray {

    private final IDoubleArray delegate;
    private final int from;
    private final int length;

    public SliceDelegateDoubleArray(final IDoubleArray delegate, final int from, final int length) {
        this.delegate = delegate;
        this.from = from;
        this.length = length;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public void set(final int index, final double value) {
        delegate.set(index + from, value);
    }

    @Override
    public double get(final int index) {
        return delegate.get(index + from);
    }

    @Override
    public int size() {
        return length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public IDoubleArray slice(final int fromIndex, final int length) {
        return delegate.slice(fromIndex + from, length);
    }

    @Override
    public double[] asArray() {
        return delegate.asArray(from, length);
    }

    @Override
    public double[] asArray(final int fromIndex, final int length) {
        return delegate.asArray(fromIndex + from, length);
    }

    @Override
    public double[] asArrayCopy() {
        return delegate.asArrayCopy(from, length);
    }

    @Override
    public double[] asArrayCopy(final int fromIndex, final int length) {
        return delegate.asArrayCopy(fromIndex + from, length);
    }

    @Override
    public void getDoubles(final int srcPos, final IDoubleArray dest, final int destPos, final int length) {
        delegate.getDoubles(srcPos + from, dest, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

}
