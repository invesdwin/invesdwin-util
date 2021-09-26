package de.invesdwin.util.streams.buffer.bytes;

public interface IByteBufferWriter {

    int write(IByteBuffer buffer);

    IByteBuffer asBuffer();

}
