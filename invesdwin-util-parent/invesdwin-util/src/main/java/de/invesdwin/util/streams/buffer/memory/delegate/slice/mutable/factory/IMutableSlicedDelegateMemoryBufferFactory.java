package de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory;

import java.nio.ByteOrder;

import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.ordered.OrderedExpandableMutableSlicedDelegateMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.ordered.OrderedFixedMutableSlicedDelegateMemoryBufferFactory;

public interface IMutableSlicedDelegateMemoryBufferFactory {

    IMemoryBuffer sliceFrom(long index);

    IMemoryBuffer slice(long index, long length);

    static IMutableSlicedDelegateMemoryBufferFactory newInstance(final IMemoryBuffer delegate) {
        if (delegate.isExpandable()) {
            return new ExpandableMutableSlicedDelegateMemoryBufferFactory(delegate);
        } else {
            return new FixedMutableSlicedDelegateMemoryBufferFactory(delegate);
        }
    }

    static IMutableSlicedDelegateMemoryBufferFactory newInstance(final IMemoryBuffer delegate, final ByteOrder order) {
        if (delegate.getOrder() == order) {
            return newInstance(delegate);
        } else {
            if (delegate.isExpandable()) {
                return new OrderedExpandableMutableSlicedDelegateMemoryBufferFactory(delegate, order);
            } else {
                return new OrderedFixedMutableSlicedDelegateMemoryBufferFactory(delegate, order);
            }
        }
    }

}
