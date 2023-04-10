package de.invesdwin.util.streams.buffer.memory;

import java.io.Closeable;

public interface ICloseableMemoryBufferProvider extends IMemoryBufferProvider, Closeable {

    @Override
    void close();

}
