package de.invesdwin.util.streams.buffer.memory.extend;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.channels.ReadableByteChannel;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.IoUtil;

import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.string.UniqueNameGenerator;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.SegmentedMemoryMappedFile;
import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;
import de.invesdwin.util.streams.buffer.memory.delegate.ADelegateCloseableMemoryBuffer;

@NotThreadSafe
public class SegmentedMappedExpandableMemoryBuffer extends ADelegateCloseableMemoryBuffer
        implements ICloseableMemoryBuffer {

    /**
     * Maximum length to which the underlying buffer can grow.
     */
    public static final long MAX_BUFFER_LENGTH = Long.MAX_VALUE;
    public static final long MAX_SEGMENT_SIZE = IMemoryMappedFile.MAX_SEGMENT_SIZE;
    /**
     * Initial capacity of the buffer from which it will expand.
     */
    public static final int INITIAL_CAPACITY = IoUtil.BLOCK_SIZE;
    private static final boolean SEPARATE_FILES = true;

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator() {

        @Override
        protected long getInitialValue() {
            return 1;
        }
    };

    private static final class ChunkedMappedExpandableBufferFinalizer extends AFinalizer {

        private final File file;
        private final boolean deleteOnClose;
        private SegmentedMemoryMappedFile mappedFile;
        private IMemoryBuffer delegate;

        private ChunkedMappedExpandableBufferFinalizer(final File file, final long initialCapacity,
                final boolean deleteOnClose) {
            this.file = file;
            this.deleteOnClose = deleteOnClose;
            try {
                Files.forceMkdirParent(file);
                mappedFile = new SegmentedMemoryMappedFile(MAX_SEGMENT_SIZE, true, file, 0,
                        IMemoryMappedFile.roundToBlockSize(initialCapacity), false, deleteOnClose, SEPARATE_FILES);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
            this.delegate = mappedFile.newMemoryBuffer(0, mappedFile.capacity());
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

    private final ChunkedMappedExpandableBufferFinalizer finalizer;

    public SegmentedMappedExpandableMemoryBuffer() {
        this(INITIAL_CAPACITY);
    }

    public SegmentedMappedExpandableMemoryBuffer(final long initialCapacity) {
        this(initialCapacity, SegmentedMappedExpandableMemoryBuffer.class.getSimpleName());
    }

    public SegmentedMappedExpandableMemoryBuffer(final long initialCapacity, final String name) {
        this(initialCapacity,
                new File(
                        new File(Files.getTempDirectory(), SegmentedMappedExpandableMemoryBuffer.class.getSimpleName()),
                        Files.normalizeFilename(UNIQUE_NAME_GENERATOR.get(Strings.putSuffix(name, ".bin")))));
    }

    public SegmentedMappedExpandableMemoryBuffer(final long initialCapacity, final File file) {
        this(initialCapacity, file, true);
    }

    public SegmentedMappedExpandableMemoryBuffer(final long initialCapacity, final File file,
            final boolean deleteOnClose) {
        this.finalizer = new ChunkedMappedExpandableBufferFinalizer(file, initialCapacity, deleteOnClose);
        this.finalizer.register(this);
    }

    @Override
    protected IMemoryBuffer getDelegate() {
        return finalizer.delegate;
    }

    @Override
    public ICloseableMemoryBuffer ensureCapacity(final long desiredCapacity) {
        ensureCapacity(desiredCapacity, 0);
        return this;
    }

    private void ensureCapacity(final long index, final long length) {
        if (index < 0 || length < 0) {
            throw FastIndexOutOfBoundsException.getInstance("negative value: index=%s length=%s", index, length);
        }

        final long resultingPosition = index + length;
        final long currentCapacity = finalizer.mappedFile.capacity();
        if (resultingPosition > currentCapacity) {
            if (resultingPosition > MAX_BUFFER_LENGTH) {
                throw FastIndexOutOfBoundsException.getInstance("index=%s length=%s maxCapacity=%s", index, length,
                        MAX_BUFFER_LENGTH);
            }

            final long newCapacity = calculateExpansion(currentCapacity, resultingPosition);
            finalizer.mappedFile.setDeleteOnClose(false);
            finalizer.mappedFile.close();
            try {
                finalizer.mappedFile = new SegmentedMemoryMappedFile(MAX_SEGMENT_SIZE, true, finalizer.file, 0,
                        IMemoryMappedFile.roundToBlockSize(newCapacity), false, finalizer.deleteOnClose,
                        SEPARATE_FILES);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

            //copy not needed
            //            getBytes(0, newBuffer, 0, finalizer.capacity);

            finalizer.delegate = finalizer.mappedFile.newMemoryBuffer(0, finalizer.mappedFile.capacity());
        }

    }

    @Override
    public InputStream asInputStream(final long index, final long length) {
        ensureCapacity(index, length);
        return getDelegate().asInputStream(index, length);
    }

    @Override
    public void putBytes(final long index, final byte[] src, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        super.putBytes(index, src, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        super.putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        super.putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        ensureCapacity(index, length);
        super.putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final long index, final IMemoryBuffer srcBuffer, final long srcIndex, final long length) {
        ensureCapacity(index, length);
        super.putBytes(index, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytesTo(final long index, final DataInput src, final long length) throws IOException {
        ensureCapacity(index, length);
        super.putBytesTo(index, src, length);
    }

    @Override
    public void putBytesTo(final long index, final InputStream src, final long length) throws IOException {
        ensureCapacity(index, length);
        super.putBytesTo(index, src, length);
    }

    @Override
    public void putBytesTo(final long index, final ReadableByteChannel src, final long length) throws IOException {
        ensureCapacity(index, length);
        super.putBytesTo(index, src, length);
    }

    protected long calculateExpansion(final long currentLength, final long requiredLength) {
        final long value = MemoryBuffers.calculateExpansion(requiredLength);
        if (value > MAX_BUFFER_LENGTH) {
            return MAX_BUFFER_LENGTH;
        } else {
            return value;
        }
    }

    @Override
    public void close() {
        finalizer.close();
    }

}
