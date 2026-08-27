package de.invesdwin.util.streams.buffer.file;

import java.io.File;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.streams.buffer.bytes.ClosedByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.memory.ClosedMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public final class ClosedMemoryMappedFile implements IMemoryMappedFile {

    public static final ClosedMemoryMappedFile INSTANCE = new ClosedMemoryMappedFile();

    private ClosedMemoryMappedFile() {}

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public Object getRefCountLock() {
        return this;
    }

    @Override
    public int getRefCount() {
        return 0;
    }

    @Override
    public boolean incrementRefCount() {
        return false;
    }

    @Override
    public int decrementRefCount() {
        return 0;
    }

    @Override
    public void markForClose() {}

    @Override
    public boolean isDeleteOnClose() {
        return false;
    }

    @Override
    public void setDeleteOnClose(final boolean deleteOnClose) {}

    @Override
    public long addressOffset() {
        return 0;
    }

    @Override
    public long wrapAdjustment() {
        return 0;
    }

    @Override
    public long capacity() {
        return 0;
    }

    @Override
    public boolean isClosed() {
        return true;
    }

    @Override
    public void close() {}

    @Override
    public IByteBuffer newByteBuffer(final long index, final int length) {
        return ClosedByteBuffer.INSTANCE;
    }

    @Override
    public IMemoryBuffer newMemoryBuffer(final long index, final long length) {
        return ClosedMemoryBuffer.INSTANCE;
    }

}
