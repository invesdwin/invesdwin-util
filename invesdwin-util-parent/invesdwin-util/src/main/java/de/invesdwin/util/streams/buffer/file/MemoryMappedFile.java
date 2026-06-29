package de.invesdwin.util.streams.buffer.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.OperatingSystem;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.UnsafeMemoryBuffer;
import net.openhft.chronicle.core.OSAccessor;

/**
 * Class for direct access to a memory mapped file.
 * 
 * This class was inspired from an entry in Bryce Nyeggen's blog
 *
 * https://github.com/caplogic/Mappedbus/blob/master/src/main/io/mappedbus/MemoryMappedFile.java
 *
 */
@NotThreadSafe
public final class MemoryMappedFile implements IMemoryMappedFile {

    private final boolean closeAllowed;
    private final MemoryMappedFileFinalizer finalizer;
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
    private MemoryMappedFile(final boolean closeAllowed, final File file, final long offset, final long length,
            final boolean readOnly, final boolean deleteOnClose) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative: " + length);
        }
        this.closeAllowed = closeAllowed;
        this.finalizer = new MemoryMappedFileFinalizer(file, offset, length, readOnly, deleteOnClose);
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
        return new MappedMemoryBuffer(address, length, file, index);
    }

    private static final class MappedMemoryBuffer extends UnsafeMemoryBuffer {
        private final File file;
        private final long index;

        private MappedMemoryBuffer(final long address, final long length, final File file, final long index) {
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

    private static final class MemoryMappedFileFinalizer extends AFinalizer {
        private final File file;
        private final long offset;
        private final long length;
        private final RandomAccessFile raf;
        private final FileChannel channel;
        private final long address;
        private boolean deleteOnClose;
        private volatile boolean cleaned;

        private MemoryMappedFileFinalizer(final File file, final long offset, final long length, final boolean readOnly,
                final boolean deleteOnClose) throws IOException {
            this.file = file;
            this.offset = offset;
            this.length = length;
            if (readOnly) {
                this.raf = new RandomAccessFile(this.file, "r");
                this.channel = raf.getChannel();
                this.address = OSAccessor.mapUnaligned(channel, MapMode.READ_ONLY, this.offset, this.length);
            } else {
                this.raf = new RandomAccessFile(this.file, "rw");
                final long limit = this.offset + this.length;
                if (raf.length() < limit) {
                    raf.setLength(limit);
                }
                this.channel = raf.getChannel();
                this.address = OSAccessor.mapUnaligned(channel, MapMode.READ_WRITE, this.offset, this.length);
            }
            this.deleteOnClose = deleteOnClose;
        }

        @Override
        protected void clean() {
            cleaned = true;
            try {
                OSAccessor.unmapUnaligned(address, this.length);
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

    public static IMemoryMappedFile map(final boolean closeAllowed, final File file, final long offset,
            final long length, final boolean readOnly, final boolean deleteOnClose) throws IOException {
        if (OperatingSystem.isWindows()) {
            return new NioMemoryMappedFile(closeAllowed, file, offset, length, readOnly, deleteOnClose);
        } else {
            return new MemoryMappedFile(closeAllowed, file, offset, length, readOnly, deleteOnClose);
        }
    }

}