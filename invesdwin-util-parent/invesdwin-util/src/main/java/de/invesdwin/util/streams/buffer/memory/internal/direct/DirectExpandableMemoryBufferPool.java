package de.invesdwin.util.streams.buffer.memory.internal.direct;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;

@ThreadSafe
public final class DirectExpandableMemoryBufferPool extends AAgronaObjectPool<ICloseableMemoryBuffer> {

    public static final DirectExpandableMemoryBufferPool INSTANCE = new DirectExpandableMemoryBufferPool();

    private DirectExpandableMemoryBufferPool() {}

    @Override
    protected ICloseableMemoryBuffer newObject() {
        return new PooledDirectExpandableMemoryBuffer();
    }

    @Override
    public void invalidateObject(final ICloseableMemoryBuffer element) {
        if (element instanceof PooledDirectExpandableMemoryBuffer) {
            final PooledDirectExpandableMemoryBuffer cElement = (PooledDirectExpandableMemoryBuffer) element;
            cElement.superClose();
        }
    }

}
