package de.invesdwin.util.collections.array.large.slice;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class SliceDelegateIntegerLargeArray implements IIntegerLargeArray {

    private final IIntegerLargeArray delegate;
    private final long from;
    private final long length;

    public SliceDelegateIntegerLargeArray(final IIntegerLargeArray delegate, final long from, final long length) {
        this.delegate = delegate;
        this.from = from;
        this.length = length;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public void set(final long index, final int value) {
        delegate.set(index + from, value);
    }

    @Override
    public int get(final long index) {
        return delegate.get(index + from);
    }

    @Override
    public long size() {
        return length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public IIntegerLargeArray slice(final long fromIndex, final long length) {
        return delegate.slice(fromIndex + from, length);
    }

    @Override
    public int[] asArray(final long fromIndex, final int length) {
        return delegate.asArray(fromIndex + from, length);
    }

    @Override
    public int[] asArrayCopy(final long fromIndex, final int length) {
        return delegate.asArrayCopy(fromIndex + from, length);
    }

    @Override
    public void getIntegers(final long srcPos, final IIntegerLargeArray dest, final long destPos, final long length) {
        delegate.getIntegers(srcPos + from, dest, destPos, length);
    }

    @Override
    public String toString() {
        return Arrays.toString(asArray(0, ByteBuffers.checkedCast(Longs.min(ByteBuffers.MAX_TO_STRING_COUNT, size()))));
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBufferLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

}
