package de.invesdwin.util.collections.array.large;

import java.io.IOException;

import de.invesdwin.util.collections.array.large.accessor.ILargeArrayAccessor;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

public interface ILargeArray extends ILargeArrayAccessor, ILargeArrayId {

    long getBuffer(IMemoryBuffer buffer) throws IOException;

    long getBufferLength();

}
