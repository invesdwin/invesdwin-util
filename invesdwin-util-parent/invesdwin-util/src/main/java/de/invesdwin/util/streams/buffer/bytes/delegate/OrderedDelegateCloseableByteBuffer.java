package de.invesdwin.util.streams.buffer.bytes.delegate;

import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableByteBufferFactory;

/**
 * This wrapper can be used for remote communication where a fixed endianness should be used.
 */
@NotThreadSafe
public final class OrderedDelegateCloseableByteBuffer extends OrderedDelegateByteBuffer
        implements ICloseableByteBuffer {

    private OrderedDelegateCloseableByteBuffer(final ICloseableByteBuffer delegate, final ByteOrder order) {
        super(delegate, order);
    }

    private ICloseableByteBuffer getDelegate() {
        return (ICloseableByteBuffer) delegate;
    }

    @Override
    protected IMutableSlicedDelegateCloseableByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = IMutableSlicedDelegateCloseableByteBufferFactory.newInstance(this, order);
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
            return maybeWrap(asImmutableSlice, order);
        }
    }

    public static ICloseableByteBuffer maybeWrap(final ICloseableByteBuffer buffer, final ByteOrder order) {
        if (order != buffer.getOrder()) {
            if (buffer instanceof OrderedDelegateCloseableByteBuffer) {
                final OrderedDelegateCloseableByteBuffer cBuffer = (OrderedDelegateCloseableByteBuffer) buffer;
                return cBuffer.getDelegate();
            } else {
                return new OrderedDelegateCloseableByteBuffer(buffer, order);
            }
        } else {
            //no conversion needed, already uses default
            return buffer;
        }
    }

}
