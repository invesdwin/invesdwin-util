package de.invesdwin.util.streams.buffer.memory.internal.mapped;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.buffer.memory.extend.MappedExpandableMemoryBuffer;

@NotThreadSafe
class PooledMappedExpandableMemoryBuffer extends MappedExpandableMemoryBuffer {

    PooledMappedExpandableMemoryBuffer() {}

    @Override
    public void close() {
        MappedExpandableMemoryBufferPool.INSTANCE.returnObject(this);
    }

    public void superClose() {
        super.close();
    }
}
