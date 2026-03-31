package de.invesdwin.util.streams.buffer.bytes.extend;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.string.UniqueNameGenerator;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.MemoryMappedFile;

/**
 * Registers a cleaner to free memory
 *
 */
@NotThreadSafe
public class MappedByteBuffer extends UnsafeByteBuffer implements Closeable {

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator() {
        @Override
        protected long getInitialValue() {
            return 1;
        }
    };

    private static final class MappedBufferFinalizer extends AFinalizer {

        private final File file;
        private final boolean deleteOnClose;
        private MemoryMappedFile mappedFile;

        private MappedBufferFinalizer(final File file, final int length, final boolean deleteOnClose) {
            this.file = file;
            this.deleteOnClose = deleteOnClose;
            try {
                Files.forceMkdirParent(file);
                mappedFile = new MemoryMappedFile(file, 0, length, false, true);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        protected void clean() {
            final IMemoryMappedFile mappedFileCopy = mappedFile;
            if (mappedFileCopy != null) {
                mappedFileCopy.close();
                mappedFile = null;
                if (deleteOnClose) {
                    Files.deleteQuietly(file);
                }
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

    private final MappedBufferFinalizer finalizer;

    public MappedByteBuffer(final int length) {
        this(length, MappedByteBuffer.class.getSimpleName());
    }

    public MappedByteBuffer(final int length, final String name) {
        this(length, new File(new File(Files.getTempDirectory(), MappedByteBuffer.class.getSimpleName()),
                Files.normalizeFilename(UNIQUE_NAME_GENERATOR.get(Strings.putSuffix(name, ".bin")))));
    }

    public MappedByteBuffer(final int length, final File file) {
        this(length, file, true);
    }

    public MappedByteBuffer(final int length, final File file, final boolean deleteOnClose) {
        this.finalizer = new MappedBufferFinalizer(file, length, deleteOnClose);
        this.finalizer.register(this);
        wrap(finalizer.mappedFile.addressOffset(), length);
    }

    @Override
    public void close() {
        super.close();
        finalizer.close();
    }

}
