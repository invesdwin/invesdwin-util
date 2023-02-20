package de.invesdwin.util.streams.buffer.file.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import de.invesdwin.util.streams.buffer.bytes.EmptyByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.ListByteBuffer;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
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

    public static final long WINDOWS_MAX_LENGTH_PER_SEGMENT = (long) ByteSizeScale.BYTES.convert(4,
            ByteSizeScale.GIGABYTES);

    private final List<IMemoryMappedFile> list = new ArrayList<>();

    private final long offset;
    private final long length;

    public SegmentedMemoryMappedFile(final String path, final long offset, final long length, final boolean readOnly)
            throws IOException {
        this.offset = offset;
        this.length = length;
        initList(path, readOnly);
    }

    private void initList(final String path, final boolean readOnly) throws IOException {
        long position = 0;
        for (int buf = 0; buf < list.size(); buf++) {
            IMemoryMappedFile buffer = list.get(buf);
            long capacity = buffer.capacity();
            if (offset >= position + capacity) {
                position += capacity;
                continue;
            } else {
                long bufferPosition = offset - position;
                if (capacity >= bufferPosition + length) {
                    list.add(new MemoryMappedFile(path, bufferPosition, length, readOnly));
                    return;
                } else {
                    final long limit = offset + length;
                    long remaining = length;
                    for (long i = offset; i < limit;) {
                        while (bufferPosition >= capacity) {
                            buf++;
                            buffer = list.get(buf);
                            capacity = buffer.capacity();
                            bufferPosition = 0;
                        }
                        final int toCopy = Integers.checkedCast(Longs.min(remaining, buffer.remaining(bufferPosition)));
                        list.add(new MemoryMappedFile(path, bufferPosition, toCopy, readOnly));
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                    }
                    return;
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
        return list.get(0).isClosed();
    }

    @Override
    public void close() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).close();
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
