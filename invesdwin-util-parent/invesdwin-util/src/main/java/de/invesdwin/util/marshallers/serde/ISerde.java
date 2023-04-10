package de.invesdwin.util.marshallers.serde;

import java.io.IOException;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.IByteBufferProvider;

public interface ISerde<O> {

    O fromBytes(byte[] bytes);

    byte[] toBytes(O obj);

    O fromBuffer(IByteBuffer buffer);

    int toBuffer(IByteBuffer buffer, O obj);

    default O fromBuffer(final IByteBufferProvider buffer) {
        try {
            return fromBuffer(buffer.asBuffer());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    default int toBuffer(final IByteBufferProvider buffer, final O obj) {
        try {
            return toBuffer(buffer.asBuffer(), obj);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}