package de.invesdwin.util.collections.array;

import java.io.IOException;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface IPrimitiveArray {

    int size();

    int getBuffer(IByteBuffer buffer) throws IOException;

}
