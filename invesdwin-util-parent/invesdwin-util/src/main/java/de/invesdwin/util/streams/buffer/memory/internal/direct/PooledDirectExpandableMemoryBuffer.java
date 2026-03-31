package de.invesdwin.util.streams.buffer.memory.internal.direct;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.delegate.ChronicleDelegateMemoryBuffer;

@NotThreadSafe
class PooledDirectExpandableMemoryBuffer extends ChronicleDelegateMemoryBuffer {

    PooledDirectExpandableMemoryBuffer() {
        super(net.openhft.chronicle.bytes.Bytes.allocateElasticDirect(), true);
    }

    @Override
    public void close() {
        DirectExpandableMemoryBufferPool.INSTANCE.returnObject(this);
    }

    public void superClose() {
        super.close();
    }

}
