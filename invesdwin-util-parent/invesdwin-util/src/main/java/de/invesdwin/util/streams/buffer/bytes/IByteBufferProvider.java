package de.invesdwin.util.streams.buffer.bytes;

import java.io.IOException;

public interface IByteBufferProvider {

    int getBuffer(IByteBuffer dst) throws IOException;

    IByteBuffer asBuffer() throws IOException;

}
