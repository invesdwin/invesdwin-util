package de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory;

import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.OrderedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.MutableSliceDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.MutableSliceFromDelegateByteBuffer;

@NotThreadSafe
public class OrderedExpandableMutableSliceDelegateBytteBufferFactory implements IMutableSliceDelegateByteBufferFactory {

    private final IByteBuffer delegate;
    private final ByteOrder order;
    private MutableSliceFromDelegateByteBuffer sliceFrom;
    private IByteBuffer orderedSliceFrom;
    private MutableSliceDelegateByteBuffer slice;
    private IByteBuffer orderedSlice;

    public OrderedExpandableMutableSliceDelegateBytteBufferFactory(final IByteBuffer delegate, final ByteOrder order) {
        this.delegate = delegate;
        this.order = order;
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        if (index == 0) {
            return delegate;
        }
        if (sliceFrom == null) {
            sliceFrom = new MutableSliceFromDelegateByteBuffer(delegate, index);
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
            slice = new MutableSliceDelegateByteBuffer(delegate, index, length);
            orderedSlice = OrderedDelegateByteBuffer.maybeWrap(slice, order);
        } else {
            slice.setFrom(index);
            slice.setLength(length);
        }
        return orderedSlice;
    }

}
