package de.invesdwin.util.streams.buffer.bytes.cleanable;

import javax.annotation.concurrent.Immutable;

import org.agrona.BufferUtil;

@Immutable
public class WrappedCleanableDirectBuffer implements io.netty.util.internal.CleanableDirectBuffer {

    private final java.nio.ByteBuffer buffer;

    public WrappedCleanableDirectBuffer(final java.nio.ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public java.nio.ByteBuffer buffer() {
        return buffer;
    }

    @Override
    public void clean() {
        BufferUtil.free(buffer);
    }

}
