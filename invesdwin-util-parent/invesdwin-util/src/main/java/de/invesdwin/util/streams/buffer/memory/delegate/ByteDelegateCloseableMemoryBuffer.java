package de.invesdwin.util.streams.buffer.memory.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.FixedMutableSlicedDelegateCloseableMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableMemoryBufferFactory;

@NotThreadSafe
public class ByteDelegateCloseableMemoryBuffer extends ByteDelegateMemoryBuffer implements ICloseableMemoryBuffer {

    public ByteDelegateCloseableMemoryBuffer(final ICloseableByteBuffer buffer) {
        super(buffer);
    }

    @Override
    public ICloseableByteBuffer getDelegate() {
        return (ICloseableByteBuffer) delegate;
    }

    @Deprecated
    @Override
    public void setDelegate(final IByteBuffer delegate) {
        setDelegate((ICloseableByteBuffer) delegate);
    }

    public void setDelegate(final ICloseableByteBuffer delegate) {
        this.delegate = delegate;
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
        delegate.ensureCapacity(Integers.checkedCast(desiredCapacity));
        return this;
    }

    @Override
    public void close() {
        getDelegate().close();
    }

}
