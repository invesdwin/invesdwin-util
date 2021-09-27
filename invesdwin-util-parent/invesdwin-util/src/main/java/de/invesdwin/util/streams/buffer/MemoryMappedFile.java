package de.invesdwin.util.streams.buffer;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.IoUtil;

import de.invesdwin.util.lang.finalizer.AFinalizer;

/**
 * Class for direct access to a memory mapped file.
 * 
 * This class was inspired from an entry in Bryce Nyeggen's blog
 *
 * https://github.com/caplogic/Mappedbus/blob/master/src/main/io/mappedbus/MemoryMappedFile.java
 *
 */
@NotThreadSafe
public class MemoryMappedFile implements Closeable {

    private final MemoryMappedFileFinalizer finalizer;

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
    public MemoryMappedFile(final String path, final int length, final boolean readOnly) throws IOException {
        this.finalizer = new MemoryMappedFileFinalizer(path, length, readOnly);
        this.finalizer.register(this);
    }

    public long getAddress() {
        return finalizer.address;
    }

    public int getLength() {
        return finalizer.length;
    }

    public boolean isClosed() {
        return finalizer.isClosed();
    }

    @Override
    public void close() {
        finalizer.close();
    }

    private static final class MemoryMappedFileFinalizer extends AFinalizer {
        private final String path;
        private final long address;
        private final int length;
        private boolean cleaned;

        private MemoryMappedFileFinalizer(final String path, final int length, final boolean readOnly)
                throws IOException {
            this.path = path;
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

        private static int roundTo4096(final int i) {
            return (i + 0xfff) & ~0xfff;
        }

        private long mapAndSetOffsetReadOnly() throws IOException {
            final RandomAccessFile backingFile = new RandomAccessFile(this.path, "r");
            backingFile.setLength(this.length);
            final FileChannel ch = backingFile.getChannel();
            final long address = IoUtil.map(ch, MapMode.READ_ONLY, 0L, length);
            ch.close();
            backingFile.close();
            return address;
        }

        private long mapAndSetOffsetReadWrite() throws IOException {
            final RandomAccessFile backingFile = new RandomAccessFile(this.path, "rw");
            backingFile.setLength(this.length);
            final FileChannel ch = backingFile.getChannel();
            final long address = IoUtil.map(ch, MapMode.READ_WRITE, 0L, length);
            ch.close();
            backingFile.close();
            return address;
        }
    }

}