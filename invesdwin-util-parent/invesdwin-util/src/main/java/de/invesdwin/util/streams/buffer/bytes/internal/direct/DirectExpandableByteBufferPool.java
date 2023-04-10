package de.invesdwin.util.streams.buffer.bytes.internal.direct;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers;

@ThreadSafe
public final class DirectExpandableByteBufferPool extends AAgronaObjectPool<ICloseableByteBuffer> {

    public static final DirectExpandableByteBufferPool INSTANCE = new DirectExpandableByteBufferPool();

    private DirectExpandableByteBufferPool() {}

    @Override
    protected ICloseableByteBuffer newObject() {
        if (UninitializedDirectByteBuffers.isDirectByteBufferNoCleanerSupported()) {
            return new PooledUninitializedDirectExpandableByteBuffer();
        } else {
            return new PooledDirectExpandableByteBuffer();
        }
    }

}
