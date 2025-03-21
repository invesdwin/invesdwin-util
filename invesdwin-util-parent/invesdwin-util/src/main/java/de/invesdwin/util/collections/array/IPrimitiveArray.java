package de.invesdwin.util.collections.array;

import java.io.IOException;

import de.invesdwin.util.collections.array.accessor.IArrayAccessor;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface IPrimitiveArray extends IArrayAccessor {

    int getBuffer(IByteBuffer buffer) throws IOException;

}
