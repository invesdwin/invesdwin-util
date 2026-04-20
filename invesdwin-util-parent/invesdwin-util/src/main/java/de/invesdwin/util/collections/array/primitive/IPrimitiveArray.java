package de.invesdwin.util.collections.array.primitive;

import java.io.IOException;

import de.invesdwin.util.collections.array.primitive.accessor.IPrimitiveArrayAccessor;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

public interface IPrimitiveArray extends IPrimitiveArrayAccessor, IPrimitiveArrayId {

    int getBuffer(IByteBuffer buffer) throws IOException;

    int getBufferLength();

}
