package de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory;

import java.nio.ByteOrder;

import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.ordered.OrderedExpandableMutableSlicedDelegateBytteBufferFactory;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.ordered.OrderedFixedMutableSlicedDelegateByteBufferFactory;

public interface IMutableSlicedDelegateByteBufferFactory {

    IByteBuffer sliceFrom(int index);

    IByteBuffer slice(int index, int length);

    static IMutableSlicedDelegateByteBufferFactory newInstance(final IByteBuffer delegate) {
        if (delegate.isExpandable()) {
            return new ExpandableMutableSlicedDelegateByteBufferFactory(delegate);
        } else {
            return new FixedMutableSlicedDelegateByteBufferFactory(delegate);
        }
    }

    static IMutableSlicedDelegateByteBufferFactory newInstance(final IByteBuffer delegate, final ByteOrder order) {
        if (delegate.getOrder() == order) {
            return newInstance(delegate);
        } else {
            if (delegate.isExpandable()) {
                return new OrderedExpandableMutableSlicedDelegateBytteBufferFactory(delegate, order);
            } else {
                return new OrderedFixedMutableSlicedDelegateByteBufferFactory(delegate, order);
            }
        }
    }

}
