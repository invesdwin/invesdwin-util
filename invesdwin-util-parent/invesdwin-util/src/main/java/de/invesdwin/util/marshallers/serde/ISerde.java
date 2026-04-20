package de.invesdwin.util.marshallers.serde;

import java.io.IOException;

import de.invesdwin.util.marshallers.serde.large.ILargeSerde;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.IByteBufferProvider;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

public interface ISerde<O> extends ILargeSerde<O> {

    default O fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    default byte[] toBytes(final O obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

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

    @Override
    default O fromBuffer(final IMemoryBuffer buffer) {
        return SerdeBaseMethods.fromBuffer(this, buffer);
    }

    @Override
    default long toBuffer(final IMemoryBuffer buffer, final O obj) {
        return SerdeBaseMethods.toBuffer(this, buffer, obj);
    }

}