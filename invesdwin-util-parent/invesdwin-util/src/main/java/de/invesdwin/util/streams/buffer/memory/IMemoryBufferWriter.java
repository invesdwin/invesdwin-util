package de.invesdwin.util.streams.buffer.memory;

public interface IMemoryBufferWriter {

    long write(IMemoryBuffer buffer);

    IMemoryBuffer asBuffer();

}
