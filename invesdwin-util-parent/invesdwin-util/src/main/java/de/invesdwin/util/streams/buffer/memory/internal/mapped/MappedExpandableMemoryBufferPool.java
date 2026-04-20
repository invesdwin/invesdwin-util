package de.invesdwin.util.streams.buffer.memory.internal.mapped;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;

@ThreadSafe
public final class MappedExpandableMemoryBufferPool extends AAgronaObjectPool<ICloseableMemoryBuffer> {

    public static final MappedExpandableMemoryBufferPool INSTANCE = new MappedExpandableMemoryBufferPool();

    private MappedExpandableMemoryBufferPool() {}

    @Override
    protected ICloseableMemoryBuffer newObject() {
        return new PooledMappedExpandableMemoryBuffer();
    }

    @Override
    public void invalidateObject(final ICloseableMemoryBuffer element) {
        if (element instanceof PooledMappedExpandableMemoryBuffer) {
            final PooledMappedExpandableMemoryBuffer cElement = (PooledMappedExpandableMemoryBuffer) element;
            cElement.superClose();
        }
    }

}
