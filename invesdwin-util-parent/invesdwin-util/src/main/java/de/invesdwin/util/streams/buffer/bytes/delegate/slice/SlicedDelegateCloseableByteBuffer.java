package de.invesdwin.util.streams.buffer.bytes.delegate.slice;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.FixedMutableSlicedDelegateCloseableByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableByteBufferFactory;

@NotThreadSafe
public class SlicedDelegateCloseableByteBuffer extends SlicedDelegateByteBuffer implements ICloseableByteBuffer {

    public SlicedDelegateCloseableByteBuffer(final ICloseableByteBuffer delegate, final int from, final int length) {
        super(delegate, from, length);
    }

    private ICloseableByteBuffer getDelegate() {
        return (ICloseableByteBuffer) delegate;
    }

    @Override
    protected IMutableSlicedDelegateCloseableByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = new FixedMutableSlicedDelegateCloseableByteBufferFactory(this);
        }
        return (IMutableSlicedDelegateCloseableByteBufferFactory) mutableSliceFactory;
    }

    @Override
    public ICloseableByteBuffer sliceFrom(final int index) {
        return getMutableSliceFactory().sliceFrom(index);
    }

    @Override
    public ICloseableByteBuffer slice(final int index, final int length) {
        return getMutableSliceFactory().slice(index, length);
    }

    @Override
    public ICloseableByteBuffer ensureCapacity(final int desiredCapacity) {
        super.ensureCapacity(desiredCapacity);
        return this;
    }

    @Override
    public void close() {
        getDelegate().close();
    }

    @Override
    public ICloseableByteBuffer asImmutableSlice() {
        final ICloseableByteBuffer asImmutableSlice = getDelegate().asImmutableSlice();
        if (asImmutableSlice == delegate) {
            return this;
        } else {
            return new SlicedDelegateCloseableByteBuffer(asImmutableSlice, from, length);
        }
    }

}
