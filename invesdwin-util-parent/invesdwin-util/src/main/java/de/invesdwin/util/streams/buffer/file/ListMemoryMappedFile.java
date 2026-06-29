package de.invesdwin.util.streams.buffer.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.error.FastIndexOutOfBoundsException;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
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
 * 
 * This implementation can handle files with varying segment sizes.
 */
@NotThreadSafe
public class ListMemoryMappedFile implements IMemoryMappedFile {

    private final long offset;
    private final long length;
    private final boolean closeAllowed;
    private boolean markedForClose;
    private final List<IMemoryMappedFile> list;

    public ListMemoryMappedFile(final long maxSegmentSize, final boolean closeAllowed, final File file,
            final long offset, final long length, final boolean readOnly, final boolean deleteOnClose,
            final boolean separateFiles) throws IOException {
        this.closeAllowed = closeAllowed;
        this.offset = offset;
        this.length = length;
        this.list = initList(maxSegmentSize, file, readOnly, deleteOnClose, separateFiles);
    }

    public ListMemoryMappedFile(final boolean closeAllowed, final boolean deleteOnClose,
            final IMemoryMappedFile... list) {
        this(closeAllowed, deleteOnClose, Arrays.asList(list));
    }

    public ListMemoryMappedFile(final boolean closeAllowed, final boolean deleteOnClose,
            final List<IMemoryMappedFile> list) {
        this.closeAllowed = closeAllowed;
        this.offset = 0;
        this.length = calculateLength(list);
        this.list = list;
    }

    @Override
    public File getFile() {
        return list.get(0).getFile();
    }

    private long calculateLength(final List<IMemoryMappedFile> list) {
        long length = 0;
        for (int i = 0; i < list.size(); i++) {
            length += list.get(i).capacity();
        }
        return length;
    }

    private List<IMemoryMappedFile> initList(final long maxSegmentSize, final File file, final boolean readOnly,
            final boolean deleteOnClose, final boolean separateFiles) throws IOException {
        final List<IMemoryMappedFile> list = new ArrayList<>();
        final long limit = offset + length;
        long position = 0;
        while (position < limit) {
            if (offset >= position + maxSegmentSize) {
                position += maxSegmentSize;
                continue;
            } else {
                long bufferPosition = offset - position;
                if (maxSegmentSize >= bufferPosition + length) {
                    list.add(new MemoryMappedFile(closeAllowed, newFile(file, position, separateFiles), bufferPosition,
                            length, readOnly, deleteOnClose));
                    return list;
                } else {
                    long remaining = length;
                    for (long i = offset; i < limit;) {
                        while (bufferPosition >= maxSegmentSize) {
                            bufferPosition = 0;
                        }
                        final long mappedPosition;
                        if (separateFiles) {
                            mappedPosition = bufferPosition;
                        } else {
                            mappedPosition = position;
                        }
                        final long toCopy = Longs.min(remaining, maxSegmentSize - bufferPosition);
                        list.add(new MemoryMappedFile(closeAllowed, newFile(file, position, separateFiles),
                                mappedPosition, toCopy, readOnly, false));
                        remaining -= toCopy;
                        i += toCopy;
                        bufferPosition += toCopy;
                        position += toCopy;
                    }
                    return list;
                }
            }
        }
        throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", offset, capacity());
    }

    private File newFile(final File file, final long bufferPosition, final boolean separateFiles) {
        if (separateFiles) {
            return Files.prefixExtension(file, "_" + bufferPosition);
        } else {
            return file;
        }
    }

    @Override
    public boolean isDeleteOnClose() {
        return list.get(0).isDeleteOnClose();
    }

    @Override
    public void setDeleteOnClose(final boolean deleteOnClose) {
        final IMemoryMappedFile first = list.get(0);
        first.setDeleteOnClose(deleteOnClose);
        for (int i = 1; i < list.size(); i++) {
            final IMemoryMappedFile other = list.get(i);
            if (first.getFile().equals(other.getFile())) {
                list.get(i).setDeleteOnClose(deleteOnClose);
            } else {
                break;
            }
        }
    }

    @Override
    public Object getRefCountLock() {
        return list;
    }

    @Override
    public int getRefCount() {
        if (list.isEmpty()) {
            return 0;
        }
        return list.get(0).getRefCount();
    }

    @Override
    public boolean incrementRefCount() {
        if (list.isEmpty()) {
            return false;
        }
        return list.get(0).incrementRefCount();
    }

    @Override
    public int decrementRefCount() {
        if (list.isEmpty()) {
            return 0;
        }
        final int decremented = list.get(0).decrementRefCount();
        if (decremented <= 0 && markedForClose) {
            close();
            markedForClose = false;
        }
        return decremented;
    }

    @Override
    public void markForClose() {
        markedForClose = true;
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
            for (int i = list.size() - 1; i >= 0; i--) {
                //first one might deleteOnClose
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
                            final int toCopy = ByteBuffers
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
            throw FastIndexOutOfBoundsException.getInstance("offset=%s capacity=%s", index, capacity());
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
                            final int toCopy = ByteBuffers
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
            throw FastIndexOutOfBoundsException.getInstance("index=%s capacity=%s", index, capacity());
        }
    }

}
