package de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.ordered;

import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.OrderedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.MutableSlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.MutableSlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;

@NotThreadSafe
public class OrderedExpandableMutableSlicedDelegateByteBufferFactory implements IMutableSlicedDelegateByteBufferFactory {

    private final IByteBuffer delegate;
    private final ByteOrder order;
    private MutableSlicedFromDelegateByteBuffer sliceFrom;
    private IByteBuffer orderedSliceFrom;
    private MutableSlicedDelegateByteBuffer slice;
    private IByteBuffer orderedSlice;

    public OrderedExpandableMutableSlicedDelegateByteBufferFactory(final IByteBuffer delegate, final ByteOrder order) {
        this.delegate = delegate;
        this.order = order;
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        if (index == 0) {
            return delegate;
        }
        if (sliceFrom == null) {
            sliceFrom = new MutableSlicedFromDelegateByteBuffer(delegate, index);
            orderedSliceFrom = OrderedDelegateByteBuffer.maybeWrap(sliceFrom, order);
        } else {
            sliceFrom.setFrom(index);
        }
        return orderedSliceFrom;
    }

    @Override
    public IByteBuffer slice(final int index, final int length) {
        if (index == 0 && length == delegate.capacity()) {
            return delegate;
        }
        if (slice == null) {
            slice = new MutableSlicedDelegateByteBuffer(delegate, index, length);
            orderedSlice = OrderedDelegateByteBuffer.maybeWrap(slice, order);
        } else {
            slice.setFrom(index);
            slice.setLength(length);
        }
        return orderedSlice;
    }

}
