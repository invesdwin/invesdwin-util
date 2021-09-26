package de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.factory;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.MutableSlicedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.mutable.MutableSlicedFromDelegateMemoryBuffer;

@NotThreadSafe
public class ExpandableMutableSlicedDelegateMemoryBufferFactory implements IMutableSlicedDelegateMemoryBufferFactory {

    private final IMemoryBuffer delegate;
    private MutableSlicedFromDelegateMemoryBuffer sliceFrom;
    private MutableSlicedDelegateMemoryBuffer slice;

    public ExpandableMutableSlicedDelegateMemoryBufferFactory(final IMemoryBuffer delegate) {
        this.delegate = delegate;
    }

    @Override
    public IMemoryBuffer sliceFrom(final long index) {
        if (index == 0) {
            return delegate;
        }
        if (sliceFrom == null) {
            sliceFrom = new MutableSlicedFromDelegateMemoryBuffer(delegate, index);
        } else {
            sliceFrom.setFrom(index);
        }
        return sliceFrom;
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
