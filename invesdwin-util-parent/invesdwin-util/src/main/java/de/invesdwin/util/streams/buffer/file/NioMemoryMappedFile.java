package de.invesdwin.util.streams.buffer.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.BufferUtil;
import org.agrona.IoUtil;

import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.UnsafeMemoryBuffer;

/**
 * By using MappedByteBuffer we avoid anonymous mappings on windows that first allocate to the pagefile to then switch
 * to the actual memory mapped file afterwards. This avoids errors like "The paging file is too small for this operation
 * to be completed" or "Die Auslagerungsdatei ist zu klein, um diesen Vorgang durchzuführen" even though the allocation
 * should be backed directly by a file. Though be aware that a recent version of JDK is required for this to work
 * correctly.
 */
@NotThreadSafe
public class NioMemoryMappedFile implements IMemoryMappedFile {

    /**
     * Limit of sun.nio.ch.FileChannelImpl.map(MapMode, long, long)
     */
    public static final long MAX_SIZE = Integer.MAX_VALUE;

    private final boolean closeAllowed;
    private final NioMemoryMappedFileFinalizer finalizer;
    private final AtomicInteger refCount = new AtomicInteger();
    private boolean markedForClose;

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
    public NioMemoryMappedFile(final boolean closeAllowed, final File file, final long offset, final long length,
            final boolean readOnly, final boolean deleteOnClose) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative: " + length);
        }
        this.closeAllowed = closeAllowed;
        this.finalizer = new NioMemoryMappedFileFinalizer(file, offset, length, readOnly, deleteOnClose);
        this.finalizer.register(this);
    }

    @Override
    public boolean isDeleteOnClose() {
        return finalizer.deleteOnClose;
    }

    @Override
    public void setDeleteOnClose(final boolean deleteOnClose) {
        finalizer.deleteOnClose = deleteOnClose;
    }

    @Override
    public File getFile() {
        return finalizer.file;
    }

    public java.nio.MappedByteBuffer getMappedByteBuffer() {
        return finalizer.mappedByteBuffer;
    }

    @Override
    public Object getRefCountLock() {
        return refCount;
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
    public int decrementRefCount() {
        final int decremented = refCount.decrementAndGet();
        if (decremented <= 0 && markedForClose) {
            close();
            markedForClose = false;
        }
        return decremented;
    }

    @Override
    public void markForClose() {
        markedForClose = true;
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
        final File file = finalizer.file;
        final long address = addressOffset() + index;
        return new MappedByteBuffer(address, length, file, index);
    }

    @Override
    public IMemoryBuffer newMemoryBuffer(final long index, final long length) {
        final File file = finalizer.file;
        final long address = addressOffset() + index;
        return new NioMappedMemoryBuffer(address, length, file, index);
    }

    private static final class NioMappedMemoryBuffer extends UnsafeMemoryBuffer {
        private final File file;
        private final long index;

        private NioMappedMemoryBuffer(final long address, final long length, final File file, final long index) {
            super(address, length);
            this.file = file;
            this.index = index;
        }

        @Override
        public int getId() {
            return Objects.hashCode(file, index, capacity());
        }
    }

    private static final class MappedByteBuffer extends UnsafeByteBuffer {
        private final File file;
        private final long index;

        private MappedByteBuffer(final long address, final int length, final File file, final long index) {
            super(address, length);
            this.file = file;
            this.index = index;
        }

        @Override
        public int getId() {
            return Objects.hashCode(file, index, capacity());
        }
    }

    private static final class NioMemoryMappedFileFinalizer extends AFinalizer {
        private final File file;
        private final long offset;
        private final long length;
        private final RandomAccessFile raf;
        private final FileChannel channel;
        private final long address;
        private boolean deleteOnClose;
        private volatile boolean cleaned;
        private java.nio.MappedByteBuffer mappedByteBuffer;

        private NioMemoryMappedFileFinalizer(final File file, final long offset, final long length,
                final boolean readOnly, final boolean deleteOnClose) throws IOException {
            this.file = file;
            this.offset = offset;
            this.length = length;
            if (readOnly) {
                this.raf = new RandomAccessFile(this.file, "r");
                this.channel = raf.getChannel();
                this.mappedByteBuffer = channel.map(MapMode.READ_ONLY, this.offset, this.length);
                this.address = BufferUtil.address(mappedByteBuffer);
            } else {
                this.raf = new RandomAccessFile(this.file, "rw");
                final long limit = this.offset + this.length;
                if (raf.length() < limit) {
                    raf.setLength(limit);
                }
                this.channel = raf.getChannel();
                this.mappedByteBuffer = channel.map(MapMode.READ_WRITE, this.offset, this.length);
                this.address = BufferUtil.address(mappedByteBuffer);
            }
            this.deleteOnClose = deleteOnClose;
        }

        @Override
        protected void clean() {
            cleaned = true;
            final java.nio.MappedByteBuffer mappedByteBufferCopy = mappedByteBuffer;
            if (mappedByteBufferCopy != null) {
                IoUtil.unmap(mappedByteBufferCopy);
                mappedByteBuffer = null;
            }
            try {
                channel.close();
                raf.close();
                if (deleteOnClose) {
                    Files.delete(file);
                }
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

    }

}