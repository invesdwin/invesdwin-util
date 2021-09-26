package de.invesdwin.util.streams.buffer.bytes.extend.internal;

import java.io.Closeable;
import java.io.IOException;

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

        private java.nio.ByteBuffer buffer;

        private UninitializedDirectByteBufferFinalizer(final java.nio.ByteBuffer buffer) {
            Assertions.checkNotNull(buffer);
            this.buffer = buffer;
        }

        @Override
        protected void clean() {
            UninitializedDirectByteBuffers.freeDirectByteBufferNoCleaner(buffer);
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
        super(UninitializedDirectByteBuffers.allocateDirectByteBufferNoCleaner(length), 0, length);
        this.finalizer = new UninitializedDirectByteBufferFinalizer(nioByteBuffer());
    }

    @Override
    public void close() throws IOException {
        finalizer.close();
    }

}
