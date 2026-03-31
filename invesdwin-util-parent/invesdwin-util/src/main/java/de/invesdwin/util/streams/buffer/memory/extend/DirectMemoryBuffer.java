package de.invesdwin.util.streams.buffer.memory.extend;

import java.io.Closeable;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.UnsafeApi;

import de.invesdwin.util.lang.finalizer.AFinalizer;

/**
 * Registers a cleaner to free memory
 *
 */
@NotThreadSafe
public class DirectMemoryBuffer extends UnsafeMemoryBuffer implements Closeable {

    private static final class MappedMemoryBufferFinalizer extends AFinalizer {

        private long address;

        private MappedMemoryBufferFinalizer(final long capacity) {
            this.address = UnsafeApi.allocateMemory(capacity);
        }

        @Override
        protected void clean() {
            UnsafeApi.freeMemory(address);
            address = 0;
        }

        @Override
        protected boolean isCleaned() {
            return address == 0;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }

    }

    private final MappedMemoryBufferFinalizer finalizer;

    public DirectMemoryBuffer(final long length) {
        this.finalizer = new MappedMemoryBufferFinalizer(length);
        this.finalizer.register(this);
        wrap(finalizer.address, length);
    }

    @Override
    public void close() {
        super.close();
        finalizer.close();
    }

}
