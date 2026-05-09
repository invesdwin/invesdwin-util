package de.invesdwin.util.streams.buffer.bytes.internal.heap;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;

@ThreadSafe
public final class HeapExpandableByteBufferPool extends AAgronaObjectPool<ICloseableByteBuffer> {

    public static final HeapExpandableByteBufferPool INSTANCE = new HeapExpandableByteBufferPool();

    private HeapExpandableByteBufferPool() {}

    @Override
    protected ICloseableByteBuffer newObject() {
        return new PooledHeapExpandableByteBuffer();
    }

}
