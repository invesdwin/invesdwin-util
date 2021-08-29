package de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory;

import java.nio.ByteOrder;

import de.invesdwin.util.streams.buffer.IByteBuffer;

public interface IMutableSliceDelegateByteBufferFactory {

    IByteBuffer sliceFrom(int index);

    IByteBuffer slice(int index, int length);

    static IMutableSliceDelegateByteBufferFactory newInstance(final IByteBuffer delegate) {
        if (delegate.isExpandable()) {
            return new ExpandableMutableSliceDelegateByteBufferFactory(delegate);
        } else {
            return new FixedMutableSliceDelegateByteBufferFactory(delegate);
        }
    }

    static IMutableSliceDelegateByteBufferFactory newInstance(final IByteBuffer delegate, final ByteOrder order) {
        if (delegate.getOrder() == order) {
            return newInstance(delegate);
        } else {
            if (delegate.isExpandable()) {
                return new OrderedExpandableMutableSliceDelegateBytteBufferFactory(delegate, order);
            } else {
                return new OrderedFixedMutableSliceDelegateByteBufferFactory(delegate, order);
            }
        }
    }

}
