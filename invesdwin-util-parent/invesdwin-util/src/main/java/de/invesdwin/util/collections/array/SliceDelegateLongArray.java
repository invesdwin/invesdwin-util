package de.invesdwin.util.collections.array;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@NotThreadSafe
public class SliceDelegateLongArray implements ILongArray {

    private final ILongArray delegate;
    private final int from;
    private final int length;

    public SliceDelegateLongArray(final ILongArray delegate, final int from, final int length) {
        this.delegate = delegate;
        this.from = from;
        this.length = length;
    }

    @Override
    public void set(final int index, final long value) {
        delegate.set(index + from, value);
    }

    @Override
    public long get(final int index) {
        return delegate.get(index + from);
    }

    @Override
    public int size() {
        return length;
    }

    @Override
    public ILongArray slice(final int fromIndex, final int length) {
        return delegate.slice(fromIndex + from, length);
    }

    @Override
    public long[] asArray() {
        return delegate.asArray(from, length);
    }

    @Override
    public long[] asArray(final int fromIndex, final int length) {
        return delegate.asArray(fromIndex + from, length);
    }

    @Override
    public void arrayCopy(final int srcPos, final ILongArray dest, final int destPos, final int length) {
        delegate.arrayCopy(srcPos + from, dest, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, Integers.min(ByteBuffers.MAX_TO_STRING_COUNT, size())));
    }

}
