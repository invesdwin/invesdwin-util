package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public class SliceDelegateBooleanArray implements IBooleanArray {

    private final IBooleanArray delegate;
    private final int from;
    private final int length;

    public SliceDelegateBooleanArray(final IBooleanArray delegate, final int from, final int length) {
        this.delegate = delegate;
        this.from = from;
        this.length = length;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public void set(final int index, final boolean value) {
        delegate.set(index + from, value);
    }

    @Override
    public boolean get(final int index) {
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
    public IBooleanArray slice(final int fromIndex, final int length) {
        return delegate.slice(fromIndex + from, length);
    }

    @Override
    public boolean[] asArray() {
        return delegate.asArray(from, length);
    }

    @Override
    public boolean[] asArray(final int fromIndex, final int length) {
        return delegate.asArray(fromIndex + from, length);
    }

    @Override
    public boolean[] asArrayCopy() {
        return delegate.asArrayCopy(from, length);
    }

    @Override
    public boolean[] asArrayCopy(final int fromIndex, final int length) {
        return delegate.asArrayCopy(fromIndex + from, length);
    }

    @Override
    public void getBooleans(final int srcPos, final IBooleanArray dest, final int destPos, final int length) {
        delegate.getBooleans(srcPos + from, dest, destPos, length);
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
