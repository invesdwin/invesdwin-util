package de.invesdwin.util.collections.array.allocator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.IIntegerArray;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class PrefixedPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

    private final IPrimitiveArrayAllocator delegate;
    private final String prefix;

    public PrefixedPrimitiveArrayAllocator(final IPrimitiveArrayAllocator delegate, final String prefix) {
        this.delegate = delegate;
        this.prefix = prefix;
    }

    @Override
    public IByteBuffer getByteBuffer(final String id) {
        return delegate.getByteBuffer(prefix + id);
    }

    @Override
    public IDoubleArray getDoubleArray(final String id) {
        return delegate.getDoubleArray(prefix + id);
    }

    @Override
    public IIntegerArray getIntegerArray(final String id) {
        return delegate.getIntegerArray(prefix + id);
    }

    @Override
    public IBooleanArray getBooleanArray(final String id) {
        return delegate.getBooleanArray(prefix + id);
    }

    @Override
    public IBitSet getBitSet(final String id) {
        return delegate.getBitSet(prefix + id);
    }

    @Override
    public ILongArray getLongArray(final String id) {
        return delegate.getLongArray(prefix + id);
    }

    @Override
    public IByteBuffer newByteBuffer(final String id, final int size) {
        return delegate.newByteBuffer(prefix + id, size);
    }

    @Override
    public IDoubleArray newDoubleArray(final String id, final int size) {
        return delegate.newDoubleArray(prefix + id, size);
    }

    @Override
    public IIntegerArray newIntegerArray(final String id, final int size) {
        return delegate.newIntegerArray(prefix + id, size);
    }

    @Override
    public IBooleanArray newBooleanArray(final String id, final int size) {
        return delegate.newBooleanArray(prefix + id, size);
    }

    @Override
    public IBitSet newBitSet(final String id, final int size) {
        return delegate.newBitSet(prefix + id, size);
    }

    @Override
    public ILongArray newLongArray(final String id, final int size) {
        return delegate.newLongArray(prefix + id, size);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (type.isAssignableFrom(getClass())) {
            return (T) this;
        } else {
            return delegate.unwrap(type);
        }
    }

}
