package de.invesdwin.util.streams.buffer.bytes;

public interface IByteBufferWriter {

    int writeBuffer(IByteBuffer buffer);

    IByteBuffer asBuffer();

}
