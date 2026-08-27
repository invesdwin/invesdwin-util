package de.invesdwin.util.streams.buffer.bytes.extend.internal;

import java.io.Closeable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;

/**
 * Registers a cleaner to free memory
 *
 */
@NotThreadSafe
public class UninitializedDirectByteBuffer extends UnsafeByteBuffer implements Closeable {

    private static final class UninitializedDirectByteBufferFinalizer extends AFinalizer {

        private io.netty.util.internal.CleanableDirectBuffer buffer;

        private UninitializedDirectByteBufferFinalizer(final io.netty.util.internal.CleanableDirectBuffer buffer) {
            Assertions.checkNotNull(buffer);
            this.buffer = buffer;
        }

        @Override
        protected void clean() {
            buffer.clean();
            buffer = null;
        }

        @Override
        protected boolean isCleaned() {
            return buffer == null;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }

    }

    private final UninitializedDirectByteBufferFinalizer finalizer;

    public UninitializedDirectByteBuffer(final int length) {
        final io.netty.util.internal.CleanableDirectBuffer buffer = UninitializedDirectByteBuffers
                .allocateDirectByteBufferNoCleaner(length);
        wrap(buffer.buffer(), 0, length);
        this.finalizer = new UninitializedDirectByteBufferFinalizer(buffer);
    }

    public UninitializedDirectByteBuffer(final int length, final int alignment) {
        final io.netty.util.internal.CleanableDirectBuffer buffer = UninitializedDirectByteBuffers
                .allocateDirectByteBufferNoCleanerAligned(length, alignment);
        wrap(buffer.buffer(), 0, length);
        this.finalizer = new UninitializedDirectByteBufferFinalizer(buffer);
    }

    public UninitializedDirectByteBuffer(final io.netty.util.internal.CleanableDirectBuffer buffer) {
        super(buffer.buffer());
        this.finalizer = new UninitializedDirectByteBufferFinalizer(buffer);
    }

    @Override
    public void close() {
        super.close();
        finalizer.close();
    }

}
