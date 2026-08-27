package de.invesdwin.util.streams.buffer.bytes.cleanable;

import javax.annotation.concurrent.Immutable;

@Immutable
public class SlicedCleanableDirectBuffer implements io.netty.util.internal.CleanableDirectBuffer {

    private final java.nio.ByteBuffer slice;
    private final io.netty.util.internal.CleanableDirectBuffer cleanable;

    public SlicedCleanableDirectBuffer(final java.nio.ByteBuffer slice,
            final io.netty.util.internal.CleanableDirectBuffer cleanable) {
        this.slice = slice;
        this.cleanable = cleanable;
    }

    @Override
    public java.nio.ByteBuffer buffer() {
        return slice;
    }

    @Override
    public void clean() {
        cleanable.clean();
    }

}
