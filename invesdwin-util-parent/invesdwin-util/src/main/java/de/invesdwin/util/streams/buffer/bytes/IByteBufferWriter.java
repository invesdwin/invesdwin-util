package de.invesdwin.util.streams.buffer.bytes;

public interface IByteBufferWriter {

    int writeBuffer(IByteBuffer dst);

    IByteBuffer asBuffer();

}
