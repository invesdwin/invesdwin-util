package de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.MutableSlicedDelegateCloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.MutableSlicedFromDelegateCloseableMemoryBuffer;

@NotThreadSafe
public class ExpandableMutableSlicedDelegateCloseableMemoryBufferFactory
        implements IMutableSlicedDelegateCloseableMemoryBufferFactory {

    private final ICloseableMemoryBuffer delegate;
    private MutableSlicedFromDelegateCloseableMemoryBuffer sliceFrom;
    private MutableSlicedDelegateCloseableMemoryBuffer slice;

    public ExpandableMutableSlicedDelegateCloseableMemoryBufferFactory(final ICloseableMemoryBuffer delegate) {
        this.delegate = delegate;
    }

    @Override
    public ICloseableMemoryBuffer sliceFrom(final long index) {
        if (index == 0) {
            return delegate;
        }
        if (sliceFrom == null) {
            sliceFrom = new MutableSlicedFromDelegateCloseableMemoryBuffer(delegate, index);
        } else {
            sliceFrom.setFrom(index);
        }
        return sliceFrom;
    }

    @Override
    public ICloseableMemoryBuffer slice(final long index, final long length) {
        if (index == 0 && length == delegate.capacity()) {
            return delegate;
        }
        if (slice == null) {
            slice = new MutableSlicedDelegateCloseableMemoryBuffer(delegate, index, length);
        } else {
            slice.setFrom(index);
            slice.setLength(length);
        }
        return slice;
    }

}
