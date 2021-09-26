package de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.ordered;

import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.OrderedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.MutableSlicedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.MutableSlicedFromDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateMemoryBufferFactory;

@NotThreadSafe
public class OrderedExpandableMutableSlicedDelegateMemoryBufferFactory
        implements IMutableSlicedDelegateMemoryBufferFactory {

    private final IMemoryBuffer delegate;
    private final ByteOrder order;
    private MutableSlicedFromDelegateMemoryBuffer sliceFrom;
    private IMemoryBuffer orderedSliceFrom;
    private MutableSlicedDelegateMemoryBuffer slice;
    private IMemoryBuffer orderedSlice;

    public OrderedExpandableMutableSlicedDelegateMemoryBufferFactory(final IMemoryBuffer delegate,
            final ByteOrder order) {
        this.delegate = delegate;
        this.order = order;
    }

    @Override
    public IMemoryBuffer sliceFrom(final long index) {
        if (index == 0) {
            return delegate;
        }
        if (sliceFrom == null) {
            sliceFrom = new MutableSlicedFromDelegateMemoryBuffer(delegate, index);
            orderedSliceFrom = OrderedDelegateMemoryBuffer.maybeWrap(sliceFrom, order);
        } else {
            sliceFrom.setFrom(index);
        }
        return orderedSliceFrom;
    }

    @Override
    public IMemoryBuffer slice(final long index, final long length) {
        if (index == 0 && length == delegate.capacity()) {
            return delegate;
        }
        if (slice == null) {
            slice = new MutableSlicedDelegateMemoryBuffer(delegate, index, length);
            orderedSlice = OrderedDelegateMemoryBuffer.maybeWrap(slice, order);
        } else {
            slice.setFrom(index);
            slice.setLength(length);
        }
        return orderedSlice;
    }

}
