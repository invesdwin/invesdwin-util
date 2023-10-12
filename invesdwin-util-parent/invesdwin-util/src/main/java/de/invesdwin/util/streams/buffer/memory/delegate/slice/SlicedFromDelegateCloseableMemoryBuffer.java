package de.invesdwin.util.streams.buffer.memory.delegate.slice;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.ExpandableMutableSlicedDelegateCloseableMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableMemoryBufferFactory;

@NotThreadSafe
public class SlicedFromDelegateCloseableMemoryBuffer extends SlicedFromDelegateMemoryBuffer
        implements ICloseableMemoryBuffer {

    public SlicedFromDelegateCloseableMemoryBuffer(final IMemoryBuffer delegate, final long from) {
        super(delegate, from);
    }

    private ICloseableMemoryBuffer getDelegate() {
        return (ICloseableMemoryBuffer) delegate;
    }

    @Override
    protected IMutableSlicedDelegateCloseableMemoryBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = new ExpandableMutableSlicedDelegateCloseableMemoryBufferFactory(this);
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
        if (asImmutableSlice == delegate) {
            return this;
        } else {
            return new SlicedFromDelegateCloseableMemoryBuffer(asImmutableSlice, from);
        }
    }

}
