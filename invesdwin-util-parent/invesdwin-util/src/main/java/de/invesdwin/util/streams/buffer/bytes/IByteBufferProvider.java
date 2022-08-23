package de.invesdwin.util.streams.buffer.bytes;

public interface IByteBufferProvider {

    int getBuffer(IByteBuffer dst);

    IByteBuffer asBuffer();

}
