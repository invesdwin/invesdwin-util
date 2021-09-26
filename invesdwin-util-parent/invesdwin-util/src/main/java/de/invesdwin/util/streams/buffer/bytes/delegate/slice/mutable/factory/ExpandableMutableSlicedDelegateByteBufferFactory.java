package de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.factory;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.MutableSlicedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.slice.mutable.MutableSlicedFromDelegateByteBuffer;

@NotThreadSafe
public class ExpandableMutableSlicedDelegateByteBufferFactory implements IMutableSlicedDelegateByteBufferFactory {

    private final IByteBuffer delegate;
    private MutableSlicedFromDelegateByteBuffer sliceFrom;
    private MutableSlicedDelegateByteBuffer slice;

    public ExpandableMutableSlicedDelegateByteBufferFactory(final IByteBuffer delegate) {
        this.delegate = delegate;
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        if (index == 0) {
            return delegate;
        }
        if (sliceFrom == null) {
            sliceFrom = new MutableSlicedFromDelegateByteBuffer(delegate, index);
        } else {
            sliceFrom.setFrom(index);
        }
        return sliceFrom;
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
