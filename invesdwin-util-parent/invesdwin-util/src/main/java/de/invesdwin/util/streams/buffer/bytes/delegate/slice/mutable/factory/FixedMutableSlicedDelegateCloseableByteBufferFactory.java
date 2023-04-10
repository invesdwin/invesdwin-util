package de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.MutableSlicedDelegateCloseableByteBuffer;

@NotThreadSafe
public class FixedMutableSlicedDelegateCloseableByteBufferFactory
        implements IMutableSlicedDelegateCloseableByteBufferFactory {

    private final ICloseableByteBuffer delegate;
    private MutableSlicedDelegateCloseableByteBuffer slice;

    public FixedMutableSlicedDelegateCloseableByteBufferFactory(final ICloseableByteBuffer delegate) {
        this.delegate = delegate;
    }

    @Override
    public ICloseableByteBuffer sliceFrom(final int index) {
        return slice(index, delegate.remaining(index));
    }

    @Override
    public ICloseableByteBuffer slice(final int index, final int length) {
        if (index == 0 && length == delegate.capacity()) {
            return delegate;
        }
        if (slice == null) {
            slice = new MutableSlicedDelegateCloseableByteBuffer(delegate, index, length);
        } else {
            slice.setFrom(index);
            slice.setLength(length);
        }
        return slice;
    }

}
