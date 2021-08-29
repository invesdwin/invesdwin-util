package de.invesdwin.util.streams.buffer;

public interface IByteBufferWriter {

    int write(IByteBuffer buffer);

    IByteBuffer asByteBuffer();

}
