package de.invesdwin.util.streams.buffer.bytes.internal.array;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;

@ThreadSafe
public final class ArrayExpandableByteBufferPool extends AAgronaObjectPool<ICloseableByteBuffer> {

    public static final ArrayExpandableByteBufferPool INSTANCE = new ArrayExpandableByteBufferPool();

    private ArrayExpandableByteBufferPool() {}

    @Override
    protected ICloseableByteBuffer newObject() {
        return new PooledArrayExpandableByteBuffer();
    }

}
