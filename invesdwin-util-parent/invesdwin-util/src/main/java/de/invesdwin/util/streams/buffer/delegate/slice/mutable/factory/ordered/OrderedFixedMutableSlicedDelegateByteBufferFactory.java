package de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.ordered;

import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.OrderedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.MutableSlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;

@NotThreadSafe
public class OrderedFixedMutableSlicedDelegateByteBufferFactory implements IMutableSlicedDelegateByteBufferFactory {

    private final IByteBuffer delegate;
    private final ByteOrder order;
    private MutableSlicedDelegateByteBuffer slice;
    private IByteBuffer orderedSlice;

    public OrderedFixedMutableSlicedDelegateByteBufferFactory(final IByteBuffer delegate, final ByteOrder order) {
        this.delegate = delegate;
        this.order = order;
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        return slice(index, delegate.remaining(index));
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
