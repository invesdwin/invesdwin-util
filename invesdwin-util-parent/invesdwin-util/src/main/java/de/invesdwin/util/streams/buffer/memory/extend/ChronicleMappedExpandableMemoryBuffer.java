package de.invesdwin.util.streams.buffer.memory.extend;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.reference.WeakThreadLocalReference;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.string.UniqueNameGenerator;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ChronicleDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.slice.SlicedFromDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.internal.CachedChunkedMappedFile;
import net.openhft.chronicle.bytes.MappedBytesStore;
import net.openhft.chronicle.core.OS;

/**
 * Registers a cleaner to free memory
 * 
 * MappedBytes is normally not thread safe due to the chronicle buffer switching the storage chunk internally during
 * reads, though we use a threadLocal to avoid these issues (e.g. in ChronicleLargeMappedFileChunkStorage).
 *
 */
@NotThreadSafe
public class ChronicleMappedExpandableMemoryBuffer extends ChronicleDelegateMemoryBuffer {

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
        private CachedChunkedMappedFile mappedFile;
        private final MappedBytesThreadLocalReference mappedBytesHolder;

        private MappedExpandableMemoryBufferFinalizer(final long chunkSize, final File file,
                final boolean deleteOnClose, final long overlapSize, final boolean readOnly) {
            this.file = file;
            this.deleteOnClose = deleteOnClose;
            this.chunkSize = chunkSize;
            try {
                Files.forceMkdirParent(file);
                mappedFile = CachedChunkedMappedFile.of(file, chunkSize, overlapSize, readOnly);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
            this.mappedBytesHolder = new MappedBytesThreadLocalReference(mappedFile);
        }

        @Override
        protected void clean() {
            final CachedChunkedMappedFile mappedBytesCopy = mappedFile;
            if (mappedBytesCopy != null) {
                mappedBytesCopy.close();
                mappedFile = null;
                if (deleteOnClose) {
                    Files.deleteQuietly(file);
                }
                mappedBytesHolder.remove();
            }
        }

        @Override
        protected boolean isCleaned() {
            return mappedFile == null;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }

    }

    /**
     * MappedBytes keeps a local field for the current chunk storage which would cause issues when being shared across
     * threads for parallel reads.
     */
    private static final class MappedBytesThreadLocalReference
            extends WeakThreadLocalReference<net.openhft.chronicle.bytes.MappedBytes> {
        private final CachedChunkedMappedFile mappedFile;

        private MappedBytesThreadLocalReference(final CachedChunkedMappedFile mappedFile) {
            this.mappedFile = mappedFile;
        }

        @Override
        protected net.openhft.chronicle.bytes.MappedBytes initialValue() {
            return mappedFile.createBytesFor();
        }
    }

    private final MappedExpandableMemoryBufferFinalizer finalizer;

    private final boolean closeAllowed;

    public ChronicleMappedExpandableMemoryBuffer() {
        this(DEFAULT_CHUNK_SIZE, ChronicleMappedExpandableMemoryBuffer.class.getSimpleName());
    }

    public ChronicleMappedExpandableMemoryBuffer(final long chunkSize) {
        this(chunkSize, ChronicleMappedExpandableMemoryBuffer.class.getSimpleName());
    }

    public ChronicleMappedExpandableMemoryBuffer(final long chunkSize, final String name) {
        this(chunkSize, new File(new File(Files.getTempDirectory(), ChronicleMappedExpandableMemoryBuffer.class.getSimpleName()),
                Files.normalizeFilename(UNIQUE_NAME_GENERATOR.get(Strings.putSuffix(name, ".bin")))));
    }

    public ChronicleMappedExpandableMemoryBuffer(final long chunkSize, final File file) {
        this(chunkSize, file, false);
    }

    public ChronicleMappedExpandableMemoryBuffer(final long chunkSize, final File file, final boolean deleteOnClose) {
        this(chunkSize, file, deleteOnClose, OS.pageSize(), false, true);
    }

    public ChronicleMappedExpandableMemoryBuffer(final long chunkSize, final File file, final boolean deleteOnClose,
            final long overlapSize, final boolean readOnly, final boolean closeAllowed) {
        super(null, false);
        this.finalizer = new MappedExpandableMemoryBufferFinalizer(chunkSize, file, deleteOnClose, overlapSize,
                readOnly);
        this.finalizer.register(this);
        this.closeAllowed = closeAllowed;
    }

    @Override
    public net.openhft.chronicle.bytes.MappedBytes getDelegate() {
        return finalizer.mappedBytesHolder.get();
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

    @Override
    public IMemoryBuffer newSliceFrom(final long index) {
        if (index == 0) {
            return this;
        } else {
            return new SlicedFromDelegateMemoryBuffer(newSliceInstance(), index);
        }
    }

    @Override
    public IMemoryBuffer newSlice(final long index, final long length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            return new SlicedDelegateMemoryBuffer(newSliceInstance(), index, length);
        }
    }

    private IMemoryBuffer newSliceInstance() {
        return new IsolatedMappedExpandableMemoryBuffer(finalizer.mappedFile);
    }

    /**
     * Make sure anywhere the buffer can be shared across other threads as a new slice, a separate threadLocal is used
     * so that the store does not have to be switched as often internally.
     */
    private final class IsolatedMappedExpandableMemoryBuffer extends ChronicleDelegateMemoryBuffer {

        private final MappedBytesThreadLocalReference mappedBytesHolder;

        private IsolatedMappedExpandableMemoryBuffer(final CachedChunkedMappedFile mappedFile) {
            super(null, false);
            this.mappedBytesHolder = new MappedBytesThreadLocalReference(mappedFile);
        }

        @Override
        public net.openhft.chronicle.bytes.Bytes<?> getDelegate() {
            return mappedBytesHolder.get();
        }
    }

}
