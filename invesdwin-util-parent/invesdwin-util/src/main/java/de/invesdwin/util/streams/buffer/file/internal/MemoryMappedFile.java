package de.invesdwin.util.streams.buffer.file.internal;

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
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
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
    public MemoryMappedFile(final String path, final long offset, final long length, final boolean readOnly)
            throws IOException {
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
        finalizer.close();
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
        private final long address;
        private final long length;
        private boolean cleaned;

        private MemoryMappedFileFinalizer(final String path, final long offset, final long length,
                final boolean readOnly) throws IOException {
            this.path = path;
            this.offset = offset;
            if (readOnly) {
                this.length = length;
            } else {
                this.length = roundTo4096(length);
            }
            if (readOnly) {
                this.address = mapAndSetOffsetReadOnly();
            } else {
                this.address = mapAndSetOffsetReadWrite();
            }
        }

        @Override
        protected void clean() {
            IoUtil.unmap(null, address, this.length);
            cleaned = true;
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

        private long mapAndSetOffsetReadOnly() throws IOException {
            final RandomAccessFile backingFile = new RandomAccessFile(this.path, "r");
            final FileChannel ch = backingFile.getChannel();
            final long address = IoUtil.map(ch, MapMode.READ_ONLY, offset, length);
            ch.close();
            backingFile.close();
            return address;
        }

        private long mapAndSetOffsetReadWrite() throws IOException {
            final RandomAccessFile backingFile = new RandomAccessFile(this.path, "rw");
            backingFile.setLength(this.length);
            final FileChannel ch = backingFile.getChannel();
            final long address = IoUtil.map(ch, MapMode.READ_WRITE, offset, length);
            ch.close();
            backingFile.close();
            return address;
        }
    }

}