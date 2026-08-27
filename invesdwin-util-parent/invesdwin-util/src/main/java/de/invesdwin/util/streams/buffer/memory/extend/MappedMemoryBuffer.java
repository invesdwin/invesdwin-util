package de.invesdwin.util.streams.buffer.memory.extend;

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
public class MappedMemoryBuffer extends UnsafeMemoryBuffer implements Closeable {

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator() {
        @Override
        protected long getInitialValue() {
            return 1;
        }
    };

    private static final class MappedMemoryBufferFinalizer extends AFinalizer {

        private IMemoryMappedFile mappedFile;

        private MappedMemoryBufferFinalizer(final File file, final long length, final boolean deleteOnClose) {
            try {
                Files.forceMkdirParent(file);
                mappedFile = MemoryMappedFile.map(true, file, 0, length, false, deleteOnClose);
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

    private final MappedMemoryBufferFinalizer finalizer;

    public MappedMemoryBuffer(final long length) {
        this(length, MappedMemoryBuffer.class.getSimpleName());
    }

    public MappedMemoryBuffer(final long length, final String name) {
        this(length, new File(new File(Files.getTempDirectory(), MappedMemoryBuffer.class.getSimpleName()),
                Files.normalizeFilename(UNIQUE_NAME_GENERATOR.get(Strings.putSuffix(name, ".bin")))));
    }

    public MappedMemoryBuffer(final long length, final File file) {
        this(length, file, true);
    }

    public MappedMemoryBuffer(final long length, final File file, final boolean deleteOnClose) {
        this.finalizer = new MappedMemoryBufferFinalizer(file, length, deleteOnClose);
        this.finalizer.register(this);
        wrap(finalizer.mappedFile.addressOffset(), length);
    }

    @Override
    public void close() {
        super.close();
        finalizer.close();
    }

}
