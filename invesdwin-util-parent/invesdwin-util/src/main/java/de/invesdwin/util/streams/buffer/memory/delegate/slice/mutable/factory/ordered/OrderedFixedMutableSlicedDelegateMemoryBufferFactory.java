package de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.ordered;

import java.nio.ByteOrder;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.OrderedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.MutableSlicedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.IMutableSlicedDelegateMemoryBufferFactory;

@NotThreadSafe
public class OrderedFixedMutableSlicedDelegateMemoryBufferFactory implements IMutableSlicedDelegateMemoryBufferFactory {

    private final IMemoryBuffer delegate;
    private final ByteOrder order;
    private MutableSlicedDelegateMemoryBuffer slice;
    private IMemoryBuffer orderedSlice;

    public OrderedFixedMutableSlicedDelegateMemoryBufferFactory(final IMemoryBuffer delegate, final ByteOrder order) {
        this.delegate = delegate;
        this.order = order;
    }

    @Override
    public IMemoryBuffer sliceFrom(final long index) {
        return slice(index, delegate.remaining(index));
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
