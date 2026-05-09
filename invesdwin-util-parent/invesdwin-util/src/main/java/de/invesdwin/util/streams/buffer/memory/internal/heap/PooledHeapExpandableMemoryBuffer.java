package de.invesdwin.util.streams.buffer.memory.internal.heap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.delegate.ChronicleDelegateMemoryBuffer;

@NotThreadSafe
class PooledHeapExpandableMemoryBuffer extends ChronicleDelegateMemoryBuffer {

    PooledHeapExpandableMemoryBuffer() {
        super(net.openhft.chronicle.bytes.Bytes.allocateElasticOnHeap(), true);
    }

    @Override
    public void close() {
        HeapExpandableMemoryBufferPool.INSTANCE.returnObject(this);
    }

    public void superClose() {
        super.close();
    }

}
