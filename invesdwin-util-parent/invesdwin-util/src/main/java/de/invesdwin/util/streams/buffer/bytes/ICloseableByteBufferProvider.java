package de.invesdwin.util.streams.buffer.bytes;

import java.io.Closeable;

public interface ICloseableByteBufferProvider extends IByteBufferProvider, Closeable {

    @Override
    void close();

}
