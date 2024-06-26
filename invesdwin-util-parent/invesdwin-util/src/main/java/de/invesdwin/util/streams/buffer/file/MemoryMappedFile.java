package de.invesdwin.util.streams.buffer.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.IoUtil;

import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.UnsafeMemoryBuffer;

/**
 * Class for direct access to a memory mapped file.
 * 
 * This class was inspired from an entry in Bryce Nyeggen's blog
 *
 * https://github.com/caplogic/Mappedbus/blob/master/src/main/io/mappedbus/MemoryMappedFile.java
 *
 */
@NotThreadSafe
public class MemoryMappedFile implements IMemoryMappedFile {

    private final boolean closeAllowed;
    private final MemoryMappedFileFinalizer finalizer;
    private final AtomicInteger refCount = new AtomicInteger();

    /**
     * Constructs a new memory mapped file.
     * 
     * @param loc
     *            the file name
     * @param len
     *            the file length
     * @throws Exception
     *             in case there was an error creating the memory mapped file
     */
    public MemoryMappedFile(final String path, final long offset, final long length, final boolean readOnly,
            final boolean closeAllowed) throws IOException {
        this.closeAllowed = closeAllowed;
        this.finalizer = new MemoryMappedFileFinalizer(path, offset, length, readOnly);
        this.finalizer.register(this);
    }

    @Override
    public int getRefCount() {
        return refCount.get();
    }

    @Override
    public boolean incrementRefCount() {
        if (finalizer.isClosed()) {
            return false;
        }
        refCount.incrementAndGet();
        return true;
    }

    @Override
    public void decrementRefCount() {
        refCount.decrementAndGet();
    }

    @Override
    public long addressOffset() {
        return finalizer.address;
    }

    @Override
    public long wrapAdjustment() {
        return finalizer.offset;
    }

    @Override
    public long capacity() {
        return finalizer.length;
    }

    @Override
    public boolean isClosed() {
        return finalizer.isClosed();
    }

    @Override
    public void close() {
        if (closeAllowed) {
            finalizer.close();
        }
    }

    @Override
    public IByteBuffer newByteBuffer(final long index, final int length) {
        final long address = addressOffset() + index;
        return new UnsafeByteBuffer(address, length);
    }

    @Override
    public IMemoryBuffer newMemoryBuffer(final long index, final long length) {
        final long address = addressOffset() + index;
        return new UnsafeMemoryBuffer(address, length);
    }

    private static final class MemoryMappedFileFinalizer extends AFinalizer {
        private final String path;
        private final long offset;
        private final long length;
        private final RandomAccessFile raf;
        private final FileChannel channel;
        private final long address;
        private volatile boolean cleaned;

        private MemoryMappedFileFinalizer(final String path, final long offset, final long length,
                final boolean readOnly) throws IOException {
            this.path = path;
            this.offset = offset;
            if (readOnly) {
                this.length = length;
                this.raf = new RandomAccessFile(this.path, "r");
                this.channel = raf.getChannel();
                this.address = IoUtil.map(channel, MapMode.READ_ONLY, this.offset, this.length);
            } else {
                this.length = roundTo4096(length);
                this.raf = new RandomAccessFile(this.path, "rw");
                raf.setLength(this.length);
                this.channel = raf.getChannel();
                this.address = IoUtil.map(channel, MapMode.READ_WRITE, this.offset, this.length);
            }
        }

        @Override
        protected void clean() {
            cleaned = true;
            IoUtil.unmap(channel, address, this.length);
            try {
                channel.close();
                raf.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected boolean isCleaned() {
            return cleaned;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }

        private static long roundTo4096(final long i) {
            return (i + 0xfff) & ~0xfff;
        }

    }

}