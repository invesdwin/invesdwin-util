package de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory;

import java.nio.ByteOrder;

import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.ordered.OrderedExpandableMutableSlicedDelegateCloseableByteBufferFactory;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory.ordered.OrderedFixedMutableSlicedDelegateCloseableByteBufferFactory;

public interface IMutableSlicedDelegateCloseableByteBufferFactory extends IMutableSlicedDelegateByteBufferFactory {

    @Override
    ICloseableByteBuffer sliceFrom(int index);

    @Override
    ICloseableByteBuffer slice(int index, int length);

    static IMutableSlicedDelegateCloseableByteBufferFactory newInstance(final ICloseableByteBuffer delegate) {
        if (delegate.isExpandable()) {
            return new ExpandableMutableSlicedDelegateCloseableByteBufferFactory(delegate);
        } else {
            return new FixedMutableSlicedDelegateCloseableByteBufferFactory(delegate);
        }
    }

    static IMutableSlicedDelegateCloseableByteBufferFactory newInstance(final ICloseableByteBuffer delegate,
            final ByteOrder order) {
        if (delegate.getOrder() == order) {
            return newInstance(delegate);
        } else {
            if (delegate.isExpandable()) {
                return new OrderedExpandableMutableSlicedDelegateCloseableByteBufferFactory(delegate, order);
            } else {
                return new OrderedFixedMutableSlicedDelegateCloseableByteBufferFactory(delegate, order);
            }
        }
    }

}
