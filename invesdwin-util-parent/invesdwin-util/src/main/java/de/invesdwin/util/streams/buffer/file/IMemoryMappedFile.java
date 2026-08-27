package de.invesdwin.util.streams.buffer.file;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import de.invesdwin.util.lang.OperatingSystem;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

public interface IMemoryMappedFile extends Closeable {

    /**
     * Unsafe calls can map 4gb (see OSAccessor.mapUnaligned) with long addressing.
     */
    long MAX_SEGMENT_SIZE_WINDOWS_UNSAFE = (long) ByteSizeScale.BYTES.convert(4, ByteSizeScale.GIGABYTES);
    /**
     * NioMemoryMappedFile can only allocate Integer.MAX_VALUE because MappedByteBuffer uses int addressing.
     */
    long MAX_SEGMENT_SIZE_WINDOWS_SAFE = NioMemoryMappedFile.MAX_SIZE;
    long MAX_SEGMENT_SIZE_WINDOWS = Longs.min(MAX_SEGMENT_SIZE_WINDOWS_UNSAFE, MAX_SEGMENT_SIZE_WINDOWS_SAFE);
    long MAX_SEGMENT_SIZE = OperatingSystem.isWindows() ? MAX_SEGMENT_SIZE_WINDOWS : Long.MAX_VALUE;

    File getFile();

    Object getRefCountLock();

    int getRefCount();

    boolean incrementRefCount();

    int decrementRefCount();

    void markForClose();

    boolean isDeleteOnClose();

    void setDeleteOnClose(boolean deleteOnClose);

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

    static IMemoryMappedFile map(final boolean closeAllowed, final File file, final long index, final long length,
            final boolean readOnly, final boolean deleteOnClose) throws IOException {
        if (isSegmentSizeExceeded(length)) {
            return new SegmentedMemoryMappedFile(MAX_SEGMENT_SIZE, closeAllowed, file, index, length, readOnly,
                    deleteOnClose, false);
        } else {
            return MemoryMappedFile.map(closeAllowed, file, index, length, readOnly, deleteOnClose);
        }
    }

    static boolean isSegmentSizeExceeded(final long length) {
        return length >= MAX_SEGMENT_SIZE;
    }

    static long roundToBlockSize(final long length, final boolean readOnly) {
        if (readOnly) {
            return length;
        } else {
            return roundToBlockSize(length);
        }
    }

    /**
     * IoUtil.BLOCK_SIZE is 4096, so we round to the next multiple of 4096
     */
    static long roundToBlockSize(final long length) {
        return (length + 0xfff) & ~0xfff;
    }

}
