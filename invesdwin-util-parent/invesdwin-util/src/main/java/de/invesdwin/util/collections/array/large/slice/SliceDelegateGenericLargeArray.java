package de.invesdwin.util.collections.array.large.slice;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@NotThreadSafe
public class SliceDelegateGenericLargeArray<E> implements IGenericLargeArray<E> {

    private final IGenericLargeArray<E> delegate;
    private final long from;
    private final long length;

    public SliceDelegateGenericLargeArray(final IGenericLargeArray<E> delegate, final long from, final long length) {
        this.delegate = delegate;
        this.from = from;
        this.length = length;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public void set(final long index, final E value) {
        delegate.set(index + from, value);
    }

    @Override
    public E get(final long index) {
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
    public IGenericLargeArray<E> slice(final long fromIndex, final long length) {
        return delegate.slice(fromIndex + from, length);
    }

    @Override
    public E[] asArray(final long fromIndex, final int length) {
        return delegate.asArray(fromIndex + from, length);
    }

    @Override
    public E[] asArrayCopy(final long fromIndex, final int length) {
        return delegate.asArrayCopy(fromIndex + from, length);
    }

    @Override
    public void getGenerics(final long srcPos, final IGenericLargeArray<E> dest, final long destPos,
            final long length) {
        delegate.getGenerics(srcPos + from, dest, destPos, length);
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
