package de.invesdwin.util.streams.buffer.memory;

import java.io.IOException;

public interface IMemoryBufferProvider {

    long getBuffer(IMemoryBuffer dst) throws IOException;

    IMemoryBuffer asBuffer() throws IOException;

}
