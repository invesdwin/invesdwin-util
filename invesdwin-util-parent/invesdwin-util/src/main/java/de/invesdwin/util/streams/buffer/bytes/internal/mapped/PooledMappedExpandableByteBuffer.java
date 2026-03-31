package de.invesdwin.util.streams.buffer.bytes.internal.mapped;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;

@NotThreadSafe
class PooledMappedExpandableByteBuffer extends de.invesdwin.util.streams.buffer.bytes.extend.MappedExpandableByteBuffer
        implements ICloseableByteBuffer {

    PooledMappedExpandableByteBuffer() {}

    @Override
    public void close() {
        MappedExpandableByteBufferPool.INSTANCE.returnObject(this);
    }

    public void superClose() {
        super.close();
    }

}
