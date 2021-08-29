package de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.MutableSliceDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.MutableSliceFromDelegateByteBuffer;

@NotThreadSafe
public class ExpandableMutableSliceDelegateByteBufferFactory implements IMutableSliceDelegateByteBufferFactory {

    private final IByteBuffer delegate;
    private MutableSliceFromDelegateByteBuffer sliceFrom;
    private MutableSliceDelegateByteBuffer slice;

    public ExpandableMutableSliceDelegateByteBufferFactory(final IByteBuffer delegate) {
        this.delegate = delegate;
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        if (index == 0) {
            return delegate;
        }
        if (sliceFrom == null) {
            sliceFrom = new MutableSliceFromDelegateByteBuffer(delegate, index);
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
            slice = new MutableSliceDelegateByteBuffer(delegate, index, length);
        } else {
            slice.setFrom(index);
            slice.setLength(length);
        }
        return slice;
    }

}
