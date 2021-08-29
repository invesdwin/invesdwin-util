package de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.MutableSlicedDelegateByteBuffer;

@NotThreadSafe
public class FixedMutableSlicedDelegateByteBufferFactory implements IMutableSlicedDelegateByteBufferFactory {

    private final IByteBuffer delegate;
    private MutableSlicedDelegateByteBuffer slice;

    public FixedMutableSlicedDelegateByteBufferFactory(final IByteBuffer delegate) {
        this.delegate = delegate;
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        return slice(index, delegate.remaining(index));
    }

    @Override
    public IByteBuffer slice(final int index, final int length) {
        if (index == 0 && length == delegate.capacity()) {
            return delegate;
        }
        if (slice == null) {
            slice = new MutableSlicedDelegateByteBuffer(delegate, index, length);
        } else {
            slice.setFrom(index);
            slice.setLength(length);
        }
        return slice;
    }

}
