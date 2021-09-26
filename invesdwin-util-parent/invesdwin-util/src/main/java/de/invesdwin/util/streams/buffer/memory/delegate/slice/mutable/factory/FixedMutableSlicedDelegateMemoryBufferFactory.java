package de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.MutableSlicedDelegateMemoryBuffer;

@NotThreadSafe
public class FixedMutableSlicedDelegateMemoryBufferFactory implements IMutableSlicedDelegateMemoryBufferFactory {

    private final IMemoryBuffer delegate;
    private MutableSlicedDelegateMemoryBuffer slice;

    public FixedMutableSlicedDelegateMemoryBufferFactory(final IMemoryBuffer delegate) {
        this.delegate = delegate;
    }

    @Override
    public IMemoryBuffer sliceFrom(final long index) {
        return slice(index, delegate.remaining(index));
    }

    @Override
    public IMemoryBuffer slice(final long index, final long length) {
        if (index == 0 && length == delegate.capacity()) {
            return delegate;
        }
        if (slice == null) {
            slice = new MutableSlicedDelegateMemoryBuffer(delegate, index, length);
        } else {
            slice.setFrom(index);
            slice.setLength(length);
        }
        return slice;
    }

}
