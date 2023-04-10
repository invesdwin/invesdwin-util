package de.invesdwin.util.streams.buffer.memory;

public interface ICloseableMemoryBuffer extends IMemoryBuffer, ICloseableMemoryBufferProvider {

    @Override
    ICloseableMemoryBuffer slice(long index, long length);

    @Override
    ICloseableMemoryBuffer sliceFrom(long index);

    @Override
    default ICloseableMemoryBuffer sliceTo(final long length) {
        return slice(0, length);
    }

    @Override
    ICloseableMemoryBuffer ensureCapacity(long capacity);

}
