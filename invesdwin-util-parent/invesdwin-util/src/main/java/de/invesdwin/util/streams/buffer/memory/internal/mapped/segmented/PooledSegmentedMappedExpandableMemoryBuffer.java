package de.invesdwin.util.streams.buffer.memory.internal.mapped.segmented;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.extend.SegmentedMappedExpandableMemoryBuffer;

@NotThreadSafe
class PooledSegmentedMappedExpandableMemoryBuffer extends SegmentedMappedExpandableMemoryBuffer {

    PooledSegmentedMappedExpandableMemoryBuffer() {}

    @Override
    public void close() {
        SegmentedMappedExpandableMemoryBufferPool.INSTANCE.returnObject(this);
    }

    public void superClose() {
        super.close();
    }
}
