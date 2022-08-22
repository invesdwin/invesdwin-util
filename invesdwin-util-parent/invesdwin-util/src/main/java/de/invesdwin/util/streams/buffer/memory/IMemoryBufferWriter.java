package de.invesdwin.util.streams.buffer.memory;

public interface IMemoryBufferWriter {

    long writeBuffer(IMemoryBuffer dst);

    IMemoryBuffer asBuffer();

}
