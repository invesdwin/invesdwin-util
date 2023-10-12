package de.invesdwin.util.streams.buffer.memory.delegate;

import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableMemoryBufferFactory;

/**
 * This wrapper can be used for remote communication where a fixed endianness should be used.
 */
@NotThreadSafe
public final class OrderedDelegateCloseableMemoryBuffer extends OrderedDelegateMemoryBuffer
        implements ICloseableMemoryBuffer {

    private OrderedDelegateCloseableMemoryBuffer(final ICloseableMemoryBuffer delegate, final ByteOrder order) {
        super(delegate, order);
    }

    private ICloseableMemoryBuffer getDelegate() {
        return (ICloseableMemoryBuffer) delegate;
    }

    @Override
    protected IMutableSlicedDelegateCloseableMemoryBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateCloseableMemoryBufferFactory.newInstance(this, order);
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

    public static ICloseableMemoryBuffer maybeWrap(final ICloseableMemoryBuffer buffer, final ByteOrder order) {
        if (order != buffer.getOrder()) {
            if (buffer instanceof OrderedDelegateCloseableMemoryBuffer) {
                final OrderedDelegateCloseableMemoryBuffer cBuffer = (OrderedDelegateCloseableMemoryBuffer) buffer;
                return cBuffer.getDelegate();
            } else {
                return new OrderedDelegateCloseableMemoryBuffer(buffer, order);
            }
        } else {
            //no conversion needed, already uses default
            return buffer;
        }
    }

    @Override
    public ICloseableMemoryBuffer ensureCapacity(final long desiredCapacity) {
        delegate.ensureCapacity(desiredCapacity);
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
            return maybeWrap(asImmutableSlice, order);
        }
    }

}
