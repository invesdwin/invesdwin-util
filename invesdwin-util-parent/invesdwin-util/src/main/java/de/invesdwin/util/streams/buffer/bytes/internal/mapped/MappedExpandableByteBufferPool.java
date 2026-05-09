package de.invesdwin.util.streams.buffer.bytes.internal.mapped;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;

@ThreadSafe
public final class MappedExpandableByteBufferPool extends AAgronaObjectPool<ICloseableByteBuffer> {

    public static final MappedExpandableByteBufferPool INSTANCE = new MappedExpandableByteBufferPool();

    private MappedExpandableByteBufferPool() {}

    @Override
    protected ICloseableByteBuffer newObject() {
        return new PooledMappedExpandableByteBuffer();
    }

    @Override
    public void invalidateObject(final ICloseableByteBuffer element) {
        if (element instanceof PooledMappedExpandableByteBuffer) {
            final PooledMappedExpandableByteBuffer cElement = (PooledMappedExpandableByteBuffer) element;
            cElement.superClose();
        }
    }

}
