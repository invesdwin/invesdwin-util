package de.invesdwin.util.marshallers.serde.large;

import java.io.IOException;

import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBufferProvider;

public interface ILargeSerde<O> {

    O fromBuffer(IMemoryBuffer buffer);

    long toBuffer(IMemoryBuffer buffer, O obj);

    default O fromBuffer(final IMemoryBufferProvider buffer) {
        try {
            return fromBuffer(buffer.asBuffer());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    default long toBuffer(final IMemoryBufferProvider buffer, final O obj) {
        try {
            return toBuffer(buffer.asBuffer(), obj);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}