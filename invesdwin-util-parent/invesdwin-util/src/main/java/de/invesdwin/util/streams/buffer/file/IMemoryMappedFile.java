package de.invesdwin.util.streams.buffer.file;

import java.io.Closeable;
import java.io.IOException;

import de.invesdwin.util.lang.OperatingSystem;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.file.internal.MemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.internal.SegmentedMemoryMappedFile;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

public interface IMemoryMappedFile extends Closeable {

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

    static IMemoryMappedFile map(final String path, final long index, final long length, final boolean readOnly)
            throws IOException {
        if (OperatingSystem.isWindows() && length > SegmentedMemoryMappedFile.WINDOWS_MAX_LENGTH_PER_SEGMENT && false) {
            return new SegmentedMemoryMappedFile(path, index, length, readOnly);
        } else {
            return new MemoryMappedFile(path, index, length, readOnly);
        }
    }

}