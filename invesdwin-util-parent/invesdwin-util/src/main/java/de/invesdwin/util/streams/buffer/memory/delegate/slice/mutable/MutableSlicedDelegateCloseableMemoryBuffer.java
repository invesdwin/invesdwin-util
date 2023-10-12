package de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedDelegateCloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.FixedMutableSlicedDelegateCloseableMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableMemoryBufferFactory;

@NotThreadSafe
public class MutableSlicedDelegateCloseableMemoryBuffer extends MutableSlicedDelegateMemoryBuffer
        implements ICloseableMemoryBuffer {

    public MutableSlicedDelegateCloseableMemoryBuffer(final ICloseableMemoryBuffer delegate, final long from,
            final long length) {
        super(delegate, from, length);
    }

    private ICloseableMemoryBuffer getDelegate() {
        return (ICloseableMemoryBuffer) delegate;
    }

    @Override
    protected IMutableSlicedDelegateCloseableMemoryBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = new FixedMutableSlicedDelegateCloseableMemoryBufferFactory(this);
        }
        return (IMutableSlicedDelegateCloseableMemoryBufferFactory) mutableSliceFactory;
    }

    @Override
    public ICloseableMemoryBuffer sliceFrom(final long index) {
        return getMutableSliceFactory().sliceFrom(index);
    }

    @Override
    public ICloseableMemoryBuffer slice(final long index, final long length) {
        return getMutableSliceFactory().slice(index, length);
    }

    @Override
    public ICloseableMemoryBuffer ensureCapacity(final long desiredCapacity) {
        super.ensureCapacity(desiredCapacity);
        return this;
    }

    @Override
    public void close() {
        getDelegate().close();
    }

    @Override
    public ICloseableMemoryBuffer asImmutableSlice() {
        final ICloseableMemoryBuffer asImmutableSlice = getDelegate().asImmutableSlice();
        return new SlicedDelegateCloseableMemoryBuffer(asImmutableSlice, from, length);
    }

}
