package de.invesdwin.util.collections.array;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface IPrimitiveArray {

    int size();

    int toBuffer(IByteBuffer buffer);

}
