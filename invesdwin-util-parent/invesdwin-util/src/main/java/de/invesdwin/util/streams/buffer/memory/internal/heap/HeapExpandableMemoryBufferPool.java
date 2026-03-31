package de.invesdwin.util.streams.buffer.memory.internal.heap;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;

@ThreadSafe
public final class HeapExpandableMemoryBufferPool extends AAgronaObjectPool<ICloseableMemoryBuffer> {

    public static final HeapExpandableMemoryBufferPool INSTANCE = new HeapExpandableMemoryBufferPool();

    private HeapExpandableMemoryBufferPool() {}

    @Override
    protected ICloseableMemoryBuffer newObject() {
        return new PooledHeapExpandableMemoryBuffer();
    }

    @Override
    public void invalidateObject(final ICloseableMemoryBuffer element) {
        if (element instanceof PooledHeapExpandableMemoryBuffer) {
            final PooledHeapExpandableMemoryBuffer cElement = (PooledHeapExpandableMemoryBuffer) element;
            cElement.superClose();
        }
    }

}
