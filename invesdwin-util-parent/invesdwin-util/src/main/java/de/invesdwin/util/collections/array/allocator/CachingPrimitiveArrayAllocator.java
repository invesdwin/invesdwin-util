package de.invesdwin.util.collections.array.allocator;

import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.IIntegerArray;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.array.IPrimitiveArray;
import de.invesdwin.util.collections.attributes.AttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.pool.MemoryLimit;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@ThreadSafe
public class CachingPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

    private final IPrimitiveArrayAllocator delegate;
    private final Map<String, IPrimitiveArray> map = newMap();
    private final Runnable maybeClearCache;
    private AttributesMap attributes;

    public CachingPrimitiveArrayAllocator(final IPrimitiveArrayAllocator delegate) {
        this.delegate = delegate;
        if (delegate.unwrap(OnHeapPrimitiveArrayAllocator.class) != null) {
            this.maybeClearCache = () -> MemoryLimit.maybeClearCache(CachingPrimitiveArrayAllocator.class, "map", map);
        } else {
            this.maybeClearCache = () -> {
            };
        }
    }

    protected Map<String, IPrimitiveArray> newMap() {
        return ILockCollectionFactory.getInstance(true).newConcurrentMap();
    }

    @Override
    public IByteBuffer getByteBuffer(final String id) {
        maybeClearCache.run();
        return (IByteBuffer) map.get(id);
    }

    @Override
    public IDoubleArray getDoubleArray(final String id) {
        maybeClearCache.run();
        return (IDoubleArray) map.get(id);
    }

    @Override
    public IIntegerArray getIntegerArray(final String id) {
        maybeClearCache.run();
        return (IIntegerArray) map.get(id);
    }

    @Override
    public IBooleanArray getBooleanArray(final String id) {
        maybeClearCache.run();
        return (IBooleanArray) map.get(id);
    }

    @Override
    public IBitSet getBitSet(final String id) {
        maybeClearCache.run();
        return (IBitSet) map.get(id);
    }

    @Override
    public ILongArray getLongArray(final String id) {
        maybeClearCache.run();
        return (ILongArray) map.get(id);
    }

    @Override
    public IByteBuffer newByteBuffer(final String id, final int size) {
        maybeClearCache.run();
        return (IByteBuffer) map.computeIfAbsent(id, (t) -> delegate.newByteBuffer(id, size));
    }

    @Override
    public IDoubleArray newDoubleArray(final String id, final int size) {
        maybeClearCache.run();
        return (IDoubleArray) map.computeIfAbsent(id, (t) -> delegate.newDoubleArray(id, size));
    }

    @Override
    public IIntegerArray newIntegerArray(final String id, final int size) {
        maybeClearCache.run();
        return (IIntegerArray) map.computeIfAbsent(id, (t) -> delegate.newIntegerArray(id, size));
    }

    @Override
    public IBooleanArray newBooleanArray(final String id, final int size) {
        maybeClearCache.run();
        return (IBooleanArray) map.computeIfAbsent(id, (t) -> delegate.newBooleanArray(id, size));
    }

    @Override
    public IBitSet newBitSet(final String id, final int size) {
        maybeClearCache.run();
        return (IBitSet) map.computeIfAbsent(id, (t) -> delegate.newBitSet(id, size));
    }

    @Override
    public ILongArray newLongArray(final String id, final int size) {
        maybeClearCache.run();
        return (ILongArray) map.computeIfAbsent(id, (t) -> delegate.newLongArray(id, size));
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CachingPrimitiveArrayAllocator) {
            final CachingPrimitiveArrayAllocator cObj = (CachingPrimitiveArrayAllocator) obj;
            return Objects.equals(delegate, cObj.delegate);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(CachingPrimitiveArrayAllocator.class, delegate);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (type.isAssignableFrom(getClass())) {
            return (T) this;
        } else {
            return null;
        }
    }

    @Override
    public IAttributesMap getAttributes() {
        if (attributes == null) {
            synchronized (this) {
                if (attributes == null) {
                    attributes = new AttributesMap();
                }
            }
        }
        return attributes;
    }

}
