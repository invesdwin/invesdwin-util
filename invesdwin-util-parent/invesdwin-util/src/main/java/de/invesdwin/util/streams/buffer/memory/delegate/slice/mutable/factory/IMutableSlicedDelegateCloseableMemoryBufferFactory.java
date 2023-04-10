package de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory;

import java.nio.ByteOrder;

import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.ordered.OrderedExpandableMutableSlicedDelegateCloseableMemoryBufferFactory;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory.ordered.OrderedFixedMutableSlicedDelegateCloseableMemoryBufferFactory;

public interface IMutableSlicedDelegateCloseableMemoryBufferFactory extends IMutableSlicedDelegateMemoryBufferFactory {

    @Override
    ICloseableMemoryBuffer sliceFrom(long index);

    @Override
    ICloseableMemoryBuffer slice(long index, long length);

    static IMutableSlicedDelegateCloseableMemoryBufferFactory newInstance(final ICloseableMemoryBuffer delegate) {
        if (delegate.isExpandable()) {
            return new ExpandableMutableSlicedDelegateCloseableMemoryBufferFactory(delegate);
        } else {
            return new FixedMutableSlicedDelegateCloseableMemoryBufferFactory(delegate);
        }
    }

    static IMutableSlicedDelegateCloseableMemoryBufferFactory newInstance(final ICloseableMemoryBuffer delegate,
            final ByteOrder order) {
        if (delegate.getOrder() == order) {
            return newInstance(delegate);
        } else {
            if (delegate.isExpandable()) {
                return new OrderedExpandableMutableSlicedDelegateCloseableMemoryBufferFactory(delegate, order);
            } else {
                return new OrderedFixedMutableSlicedDelegateCloseableMemoryBufferFactory(delegate, order);
            }
        }
    }

}
