package de.invesdwin.util.streams.buffer.memory.internal.mapped.segmented;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;

@ThreadSafe
public final class SegmentedMappedExpandableMemoryBufferPool extends AAgronaObjectPool<ICloseableMemoryBuffer> {

    public static final SegmentedMappedExpandableMemoryBufferPool INSTANCE = new SegmentedMappedExpandableMemoryBufferPool();

    private SegmentedMappedExpandableMemoryBufferPool() {}

    @Override
    protected ICloseableMemoryBuffer newObject() {
        return new PooledSegmentedMappedExpandableMemoryBuffer();
    }

    @Override
    public void invalidateObject(final ICloseableMemoryBuffer element) {
        if (element instanceof PooledSegmentedMappedExpandableMemoryBuffer) {
            final PooledSegmentedMappedExpandableMemoryBuffer cElement = (PooledSegmentedMappedExpandableMemoryBuffer) element;
            cElement.superClose();
        }
    }

}
