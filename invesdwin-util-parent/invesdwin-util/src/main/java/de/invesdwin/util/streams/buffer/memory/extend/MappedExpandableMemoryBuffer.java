package de.invesdwin.util.streams.buffer.memory.extend;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.string.UniqueNameGenerator;
import de.invesdwin.util.streams.buffer.memory.delegate.ChronicleDelegateMemoryBuffer;
import net.openhft.chronicle.core.OS;

/**
 * Registers a cleaner to free memory
 *
 */
@NotThreadSafe
public class MappedExpandableMemoryBuffer extends ChronicleDelegateMemoryBuffer {

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
        private net.openhft.chronicle.bytes.MappedBytes mappedBytes;

        private MappedExpandableMemoryBufferFinalizer(final long chunkSize, final File file,
                final boolean deleteOnClose, final long overlapSize, final boolean readOnly) {
            this.file = file;
            this.deleteOnClose = deleteOnClose;
            try {
                Files.forceMkdirParent(file);
                mappedBytes = net.openhft.chronicle.bytes.MappedBytes.mappedBytes(file, chunkSize, overlapSize,
                        readOnly);
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
    public net.openhft.chronicle.bytes.Bytes<?> getDelegate() {
        return finalizer.mappedBytes;
    }

    @Override
    public void close() {
        if (closeAllowed) {
            setDelegate(null);
            finalizer.close();
        }
    }

}
