package de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.SlicedFromDelegateCloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.ExpandableMutableSlicedDelegateCloseableByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableByteBufferFactory;

@NotThreadSafe
public class MutableSlicedFromDelegateCloseableByteBuffer extends MutableSlicedFromDelegateByteBuffer
        implements ICloseableByteBuffer {

    public MutableSlicedFromDelegateCloseableByteBuffer(final ICloseableByteBuffer delegate, final int from) {
        super(delegate, from);
    }

    private ICloseableByteBuffer getDelegate() {
        return (ICloseableByteBuffer) delegate;
    }

    @Override
    protected IMutableSlicedDelegateCloseableByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = new ExpandableMutableSlicedDelegateCloseableByteBufferFactory(this);
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
        return new SlicedFromDelegateCloseableByteBuffer(asImmutableSlice, from);
    }

}
