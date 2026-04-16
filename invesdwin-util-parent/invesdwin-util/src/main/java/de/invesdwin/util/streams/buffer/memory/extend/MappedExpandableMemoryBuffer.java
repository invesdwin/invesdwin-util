package de.invesdwin.util.streams.buffer.memory.extend;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.string.UniqueNameGenerator;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.memory.delegate.ChronicleDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.internal.CachedChunkedMappedFile;
import net.openhft.chronicle.bytes.MappedBytesStore;
import net.openhft.chronicle.core.OS;

/**
 * Registers a cleaner to free memory
 *
 */
@NotThreadSafe
public class MappedExpandableMemoryBuffer extends ChronicleDelegateMemoryBuffer {

    /**
     * 64 MB
     */
    public static final long DEFAULT_CHUNK_SIZE = 64 * 1024 * 1024;

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator() {
        @Override
        protected long getInitialValue() {
            return 1;
        }
    };

    private static final class MappedExpandableMemoryBufferFinalizer extends AFinalizer {

        private final File file;
        private final boolean deleteOnClose;
        private final long chunkSize;
        private net.openhft.chronicle.bytes.MappedBytes mappedBytes;

        private MappedExpandableMemoryBufferFinalizer(final long chunkSize, final File file,
                final boolean deleteOnClose, final long overlapSize, final boolean readOnly) {
            this.file = file;
            this.deleteOnClose = deleteOnClose;
            this.chunkSize = chunkSize;
            try {
                Files.forceMkdirParent(file);
                mappedBytes = CachedChunkedMappedFile.mappedBytes(file, chunkSize, overlapSize, readOnly);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        protected void clean() {
            final net.openhft.chronicle.bytes.MappedBytes mappedBytesCopy = mappedBytes;
            if (mappedBytesCopy != null) {
                mappedBytesCopy.close();
                mappedBytes = null;
                if (deleteOnClose) {
                    Files.deleteQuietly(file);
                }
            }
        }

        @Override
        protected boolean isCleaned() {
            return mappedBytes == null;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }

    }

    private final MappedExpandableMemoryBufferFinalizer finalizer;

    private final boolean closeAllowed;

    public MappedExpandableMemoryBuffer() {
        this(DEFAULT_CHUNK_SIZE, MappedExpandableMemoryBuffer.class.getSimpleName());
    }

    public MappedExpandableMemoryBuffer(final long chunkSize) {
        this(chunkSize, MappedExpandableMemoryBuffer.class.getSimpleName());
    }

    public MappedExpandableMemoryBuffer(final long chunkSize, final String name) {
        this(chunkSize, new File(new File(Files.getTempDirectory(), MappedExpandableMemoryBuffer.class.getSimpleName()),
                Files.normalizeFilename(UNIQUE_NAME_GENERATOR.get(Strings.putSuffix(name, ".bin")))));
    }

    public MappedExpandableMemoryBuffer(final long chunkSize, final File file) {
        this(chunkSize, file, false);
    }

    public MappedExpandableMemoryBuffer(final long chunkSize, final File file, final boolean deleteOnClose) {
        this(chunkSize, file, deleteOnClose, OS.pageSize(), false, true);
    }

    public MappedExpandableMemoryBuffer(final long chunkSize, final File file, final boolean deleteOnClose,
            final long overlapSize, final boolean readOnly, final boolean closeAllowed) {
        super(null, false);
        this.finalizer = new MappedExpandableMemoryBufferFinalizer(chunkSize, file, deleteOnClose, overlapSize,
                readOnly);
        this.finalizer.register(this);
        this.closeAllowed = closeAllowed;
    }

    @Override
    public net.openhft.chronicle.bytes.MappedBytes getDelegate() {
        return finalizer.mappedBytes;
    }

    @Override
    public void close() {
        if (closeAllowed) {
            setDelegate(null);
            finalizer.close();
        }
    }

    @Override
    public void clear(final byte value, final long index, final long length) {
        final net.openhft.chronicle.bytes.MappedBytes bytes = getDelegate();

        long currentPos = index;
        long remaining = length;
        final long endPos = currentPos + remaining;

        final int startChunk = (int) (currentPos / finalizer.chunkSize);
        final int endChunk = (int) (endPos / finalizer.chunkSize);

        try {
            for (int i = startChunk; i <= endChunk; i++) {

                final MappedBytesStore store = bytes.mappedFile().acquireByteStore(bytes, currentPos);
                final long endInStore = store.capacity(); // Usually the chunk size
                final long canWrite = Longs.min(remaining, endInStore - currentPos);

                final long address = store.addressForWrite(currentPos);
                OS.memory().setMemory(address, canWrite, value);

                remaining -= canWrite;
                currentPos += canWrite;
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        if (remaining != 0) {
            throw new IllegalStateException("Remaining should be 0 but is " + remaining);
        }
    }

}
