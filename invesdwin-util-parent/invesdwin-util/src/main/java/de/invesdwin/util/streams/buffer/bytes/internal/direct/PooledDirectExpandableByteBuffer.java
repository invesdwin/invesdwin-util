package de.invesdwin.util.streams.buffer.bytes.internal.direct;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;

@NotThreadSafe
class PooledDirectExpandableByteBuffer
        extends de.invesdwin.util.streams.buffer.bytes.extend.internal.DirectExpandableByteBuffer
        implements ICloseableByteBuffer {

    PooledDirectExpandableByteBuffer() {}

    @Override
    public void close() {
        DirectExpandableByteBufferPool.INSTANCE.returnObject(this);
    }

    public void superClose() {
        super.close();
    }

}
