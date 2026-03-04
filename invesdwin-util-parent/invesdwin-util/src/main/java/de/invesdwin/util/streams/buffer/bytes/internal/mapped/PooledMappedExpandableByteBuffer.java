package de.invesdwin.util.streams.buffer.bytes.internal.mapped;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.ExpandableMutableSlicedDelegateCloseableByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableByteBufferFactory;

@NotThreadSafe
class PooledMappedExpandableByteBuffer
        extends de.invesdwin.util.streams.buffer.bytes.extend.MappedExpandableByteBuffer
        implements ICloseableByteBuffer {

    PooledMappedExpandableByteBuffer() {}

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
        MappedExpandableByteBufferPool.INSTANCE.returnObject(this);
    }

    @Override
    public ICloseableByteBuffer asImmutableSlice() {
        return this;
    }

}
