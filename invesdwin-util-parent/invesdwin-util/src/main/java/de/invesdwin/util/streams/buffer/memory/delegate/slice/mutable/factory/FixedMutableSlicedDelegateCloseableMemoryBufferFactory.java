package de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.MutableSlicedDelegateCloseableMemoryBuffer;

@NotThreadSafe
public class FixedMutableSlicedDelegateCloseableMemoryBufferFactory
        implements IMutableSlicedDelegateCloseableMemoryBufferFactory {

    private final ICloseableMemoryBuffer delegate;
    private MutableSlicedDelegateCloseableMemoryBuffer slice;

    public FixedMutableSlicedDelegateCloseableMemoryBufferFactory(final ICloseableMemoryBuffer delegate) {
        this.delegate = delegate;
    }

    @Override
    public ICloseableMemoryBuffer sliceFrom(final long index) {
        return slice(index, delegate.remaining(index));
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
