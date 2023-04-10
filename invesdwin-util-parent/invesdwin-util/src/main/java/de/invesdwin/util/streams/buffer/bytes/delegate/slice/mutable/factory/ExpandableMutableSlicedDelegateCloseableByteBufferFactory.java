package de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.MutableSlicedDelegateCloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.MutableSlicedFromDelegateCloseableByteBuffer;

@NotThreadSafe
public class ExpandableMutableSlicedDelegateCloseableByteBufferFactory
        implements IMutableSlicedDelegateCloseableByteBufferFactory {

    private final ICloseableByteBuffer delegate;
    private MutableSlicedFromDelegateCloseableByteBuffer sliceFrom;
    private MutableSlicedDelegateCloseableByteBuffer slice;

    public ExpandableMutableSlicedDelegateCloseableByteBufferFactory(final ICloseableByteBuffer delegate) {
        this.delegate = delegate;
    }

    @Override
    public ICloseableByteBuffer sliceFrom(final int index) {
        if (index == 0) {
            return delegate;
        }
        if (sliceFrom == null) {
            sliceFrom = new MutableSlicedFromDelegateCloseableByteBuffer(delegate, index);
        } else {
            sliceFrom.setFrom(index);
        }
        return sliceFrom;
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
