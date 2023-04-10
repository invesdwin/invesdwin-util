package de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.ordered;

import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.OrderedDelegateCloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.MutableSlicedDelegateCloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.MutableSlicedFromDelegateCloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateCloseableMemoryBufferFactory;

@NotThreadSafe
public class OrderedExpandableMutableSlicedDelegateCloseableMemoryBufferFactory
        implements IMutableSlicedDelegateCloseableMemoryBufferFactory {

    private final ICloseableMemoryBuffer delegate;
    private final ByteOrder order;
    private MutableSlicedFromDelegateCloseableMemoryBuffer sliceFrom;
    private ICloseableMemoryBuffer orderedSliceFrom;
    private MutableSlicedDelegateCloseableMemoryBuffer slice;
    private ICloseableMemoryBuffer orderedSlice;

    public OrderedExpandableMutableSlicedDelegateCloseableMemoryBufferFactory(final ICloseableMemoryBuffer delegate,
            final ByteOrder order) {
        this.delegate = delegate;
        this.order = order;
    }

    @Override
    public ICloseableMemoryBuffer sliceFrom(final long index) {
        if (index == 0) {
            return delegate;
        }
        if (sliceFrom == null) {
            sliceFrom = new MutableSlicedFromDelegateCloseableMemoryBuffer(delegate, index);
            orderedSliceFrom = OrderedDelegateCloseableMemoryBuffer.maybeWrap(sliceFrom, order);
        } else {
            sliceFrom.setFrom(index);
        }
        return orderedSliceFrom;
    }

    @Override
    public ICloseableMemoryBuffer slice(final long index, final long length) {
        if (index == 0 && length == delegate.capacity()) {
            return delegate;
        }
        if (slice == null) {
            slice = new MutableSlicedDelegateCloseableMemoryBuffer(delegate, index, length);
            orderedSlice = OrderedDelegateCloseableMemoryBuffer.maybeWrap(slice, order);
        } else {
            slice.setFrom(index);
            slice.setLength(length);
        }
        return orderedSlice;
    }

}
