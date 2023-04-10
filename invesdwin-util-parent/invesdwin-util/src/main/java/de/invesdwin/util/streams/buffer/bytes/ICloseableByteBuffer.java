package de.invesdwin.util.streams.buffer.bytes;

public interface ICloseableByteBuffer extends IByteBuffer, ICloseableByteBufferProvider {

    @Override
    ICloseableByteBuffer slice(int index, int length);

    @Override
    ICloseableByteBuffer sliceFrom(int index);

    @Override
    default ICloseableByteBuffer sliceTo(final int length) {
        return slice(0, length);
    }

    @Override
    ICloseableByteBuffer ensureCapacity(int capacity);

}
