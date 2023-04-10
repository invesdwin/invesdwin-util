package de.invesdwin.util.streams.buffer.bytes.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableByteBufferFactory;
import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;

/**
 * This wrapper can be used for remote communication where a fixed endianness should be used.
 */
@NotThreadSafe
public final class MemoryDelegateCloseableByteBuffer extends MemoryDelegateByteBuffer implements ICloseableByteBuffer {

    public MemoryDelegateCloseableByteBuffer(final ICloseableMemoryBuffer delegate) {
        super(delegate);
    }

    private ICloseableMemoryBuffer getDelegate() {
        return (ICloseableMemoryBuffer) delegate;
    }

    @Override
    protected IMutableSlicedDelegateCloseableByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateCloseableByteBufferFactory.newInstance(this);
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
        delegate.ensureCapacity(desiredCapacity);
        return this;
    }

    @Override
    public void close() {
        getDelegate().close();
    }

}
