package de.invesdwin.util.collections.array;

import java.util.Arrays;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.ByteBuffers;

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
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

}
