package de.invesdwin.util.streams.buffer.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import de.invesdwin.util.streams.buffer.bytes.EmptyByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.ListByteBuffer;
import de.invesdwin.util.streams.buffer.memory.EmptyMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ListMemoryBuffer;

/**
 * Workaround for accessing memory mapped files larger than 4gb on windows:
 * https://stackoverflow.com/questions/19679777/how-windows-maps-a-file-which-size-is-more-4gb
 * 
 * Though also be aware that mapped memory files can not be deleted on windows even if they are unmapped:
 * https://mapdb.org/blog/mmap_files_alloc_and_jvm_crash/
 */
@NotThreadSafe
public class SegmentedMemoryMappedFile implements IMemoryMappedFile {

    /**
     * This is the maximum size we can memory-map per segment on windows on files that are larger than 4 gb. Though this
     * does not work correctly, so files should be limited to around 3gb on disk as defined below.
     */
    public static final long WINDOWS_MAX_LENGTH_PER_SEGMENT_MAPPED = (long) ByteSizeScale.BYTES.convert(4,
            ByteSizeScale.GIGABYTES);
    /**
     * 1 gb of buffer should be more than enough so that we can write a bit more than the limit, then switch to another
     * file once the limit has been exceeded
     */
    public static final long WINDOWS_MAX_LENGTH_PER_SEGMENT_DISK = (long) ByteSizeScale.BYTES.convert(3,
            ByteSizeScale.GIGABYTES);

    private final List<IMemoryMappedFile> list;

    private final long offset;
    private final long length;
    private final boolean closeAllowed;

    public SegmentedMemoryMappedFile(final String path, final long offset, final long length, final boolean readOnly,
            final boolean closeAllowed, final long segmentLength) throws IOException {
        this.offset = offset;
        this.length = length;
        this.closeAllowed = closeAllowed;
        list = initList(path, readOnly, segmentLength);
    }

    public SegmentedMemoryMappedFile(final boolean closeAllowed, final IMemoryMappedFile... list) {
        this(closeAllowed, Arrays.asList(list));
    }

    public SegmentedMemoryMappedFile(final boolean closeAllowed, final List<IMemoryMappedFile> list) {
        this.closeAllowed = closeAllowed;
        this.offset = 0;
        this.length = calculateLength(list);
        this.list = list;
    }

    private long calculateLength(final List<IMemoryMappedFile> list) {
        long length = 0;
        for (int i = 0; i < list.size(); i++) {
            length += list.get(i).capacity();
        }
        return length;
    }

    private List<IMemoryMappedFile> initList(final String path, final boolean readOnly, final long segmentLength)
            throws IOException {
        final List<IMemoryMappedFile> list = new ArrayList<>();
        final long limit = offset + length;
        long position = 0;
        final long capacity = segmentLength;
        while (position < limit) {
            if (offset >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = offset - position;
                if (capacity >= bufferPosition + length) {
                    list.add(new MemoryMappedFile(path, bufferPosition, length, readOnly, closeAllowed));
                    return list;
                } else {
                    long remaining = length;
                    for (long i = offset; i < limit;) {
                        while (bufferPosition >= capacity) {
                            bufferPosition = 0;
                        }
                        final long toCopy = Longs.min(remaining, segmentLength - bufferPosition);
                        list.add(new MemoryMappedFile(path, bufferPosition, toCopy, readOnly, closeAllowed));
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                    }
                    return list;
                }
            }
        }
        throw new IndexOutOfBoundsException("index=" + offset + " capacity=" + capacity());
    }

    @Override
    public int getRefCount() {
        return list.get(0).getRefCount();
    }

    @Override
    public boolean incrementRefCount() {
        return list.get(0).incrementRefCount();
    }

    @Override
    public void decrementRefCount() {
        list.get(0).decrementRefCount();
    }

    @Deprecated
    @Override
    public long addressOffset() {
        throw new UnsupportedOperationException("invalid on segmented mapped memory files");
    }

    @Override
    public long wrapAdjustment() {
        return offset;
    }

    @Override
    public long capacity() {
        return length;
    }

    @Override
    public boolean isClosed() {
        return list.isEmpty() || list.get(0).isClosed();
    }

    @Override
    public void close() {
        if (closeAllowed) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).close();
            }
        }
        list.clear();
    }

    @Override
    public IByteBuffer newByteBuffer(final long index, final int length) {
        if (list.isEmpty()) {
            if (index == 0 && length == 0) {
                return EmptyByteBuffer.INSTANCE;
            } else {
                throw EmptyByteBuffer.newEmptyException();
            }
        } else if (list.size() == 1) {
            return list.get(0).newByteBuffer(index, length);
        } else {
            long position = 0;
            for (int buf = 0; buf < list.size(); buf++) {
                IMemoryMappedFile buffer = list.get(buf);
                long capacity = buffer.capacity();
                if (index >= position + capacity) {
                    position += capacity;
                    continue;
                } else {
                    long bufferPosition = index - position;
                    if (capacity >= bufferPosition + length) {
                        return buffer.newByteBuffer(bufferPosition, length);
                    } else {
                        final ListByteBuffer wrapper = new ListByteBuffer();
                        final long limit = index + length;
                        long remaining = length;
                        for (long i = index; i < limit;) {
                            while (bufferPosition >= capacity) {
                                buf++;
                                buffer = list.get(buf);
                                capacity = buffer.capacity();
                                bufferPosition = 0;
                            }
                            final int toCopy = Integers
                                    .checkedCast(Longs.min(remaining, buffer.remaining(bufferPosition)));
                            wrapper.getList().add(buffer.newByteBuffer(bufferPosition, toCopy));
                            remaining -= toCopy;
                            i += toCopy;
                            bufferPosition += toCopy;
                        }
                        return wrapper;
                    }
                }
            }
            throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
        }
    }

    @Override
    public IMemoryBuffer newMemoryBuffer(final long index, final long length) {
        if (list.isEmpty()) {
            if (index == 0 && length == 0) {
                return EmptyMemoryBuffer.INSTANCE;
            } else {
                throw EmptyMemoryBuffer.newEmptyException();
            }
        } else if (list.size() == 1) {
            return list.get(0).newMemoryBuffer(index, length);
        } else {
            long position = 0;
            for (int buf = 0; buf < list.size(); buf++) {
                IMemoryMappedFile buffer = list.get(buf);
                long capacity = buffer.capacity();
                if (index >= position + capacity) {
                    position += capacity;
                    continue;
                } else {
                    long bufferPosition = index - position;
                    if (capacity >= bufferPosition + length) {
                        return buffer.newMemoryBuffer(bufferPosition, length);
                    } else {
                        final ListMemoryBuffer wrapper = new ListMemoryBuffer();
                        final long limit = index + length;
                        long remaining = length;
                        for (long i = index; i < limit;) {
                            while (bufferPosition >= capacity) {
                                buf++;
                                buffer = list.get(buf);
                                capacity = buffer.capacity();
                                bufferPosition = 0;
                            }
                            final int toCopy = Integers
                                    .checkedCast(Longs.min(remaining, buffer.remaining(bufferPosition)));
                            wrapper.getList().add(buffer.newMemoryBuffer(bufferPosition, toCopy));
                            remaining -= toCopy;
                            i += toCopy;
                            bufferPosition += toCopy;
                        }
                        return wrapper;
                    }
                }
            }
            throw new IndexOutOfBoundsException("index=" + index + " capacity=" + capacity());
        }
    }

}
