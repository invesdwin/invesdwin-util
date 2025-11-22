package de.invesdwin.util.streams.buffer.file;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import de.invesdwin.util.lang.OperatingSystem;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

public interface IMemoryMappedFile extends Closeable {

    File getFile();

    int getRefCount();

    boolean incrementRefCount();

    void decrementRefCount();

    long addressOffset();

    long wrapAdjustment();

    long capacity();

    default long remaining(final long index) {
        return capacity() - index;
    }

    boolean isClosed();

    @Override
    void close();

    IByteBuffer newByteBuffer(long index, int length);

    IMemoryBuffer newMemoryBuffer(long index, long length);

    static IMemoryMappedFile map(final File file, final long index, final long length, final boolean readOnly,
            final boolean closeAllowed) throws IOException {
        if (isSegmentSizeExceeded(length)) {
            return new SegmentedMemoryMappedFile(closeAllowed, file, index, length, readOnly,
                    SegmentedMemoryMappedFile.WINDOWS_MAX_LENGTH_PER_SEGMENT_MAPPED);
        } else {
            return new MemoryMappedFile(file, index, length, readOnly, closeAllowed);
        }
    }

    static boolean isSegmentSizeExceeded(final long length) {
        return OperatingSystem.isWindows() && length >= SegmentedMemoryMappedFile.WINDOWS_MAX_LENGTH_PER_SEGMENT_MAPPED;
    }

}
