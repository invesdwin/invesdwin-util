package de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.ordered;

import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.OrderedDelegateCloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.MutableSlicedDelegateCloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.MutableSlicedFromDelegateCloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableByteBufferFactory;

@NotThreadSafe
public class OrderedExpandableMutableSlicedDelegateCloseableByteBufferFactory
        implements IMutableSlicedDelegateCloseableByteBufferFactory {

    private final ICloseableByteBuffer delegate;
    private final ByteOrder order;
    private MutableSlicedFromDelegateCloseableByteBuffer sliceFrom;
    private ICloseableByteBuffer orderedSliceFrom;
    private MutableSlicedDelegateCloseableByteBuffer slice;
    private ICloseableByteBuffer orderedSlice;

    public OrderedExpandableMutableSlicedDelegateCloseableByteBufferFactory(final ICloseableByteBuffer delegate,
            final ByteOrder order) {
        this.delegate = delegate;
        this.order = order;
    }

    @Override
    public ICloseableByteBuffer sliceFrom(final int index) {
        if (index == 0) {
            return delegate;
        }
        if (sliceFrom == null) {
            sliceFrom = new MutableSlicedFromDelegateCloseableByteBuffer(delegate, index);
            orderedSliceFrom = OrderedDelegateCloseableByteBuffer.maybeWrap(sliceFrom, order);
        } else {
            sliceFrom.setFrom(index);
        }
        return orderedSliceFrom;
    }

    @Override
    public ICloseableByteBuffer slice(final int index, final int length) {
        if (index == 0 && length == delegate.capacity()) {
            return delegate;
        }
        if (slice == null) {
            slice = new MutableSlicedDelegateCloseableByteBuffer(delegate, index, length);
            orderedSlice = OrderedDelegateCloseableByteBuffer.maybeWrap(slice, order);
        } else {
            slice.setFrom(index);
            slice.setLength(length);
        }
        return orderedSlice;
    }

}
