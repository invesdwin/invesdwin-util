package de.invesdwin.util.marshallers.serde;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.IByteBufferProvider;

@Immutable
public class ByteBufferProviderSerde implements ISerde<IByteBufferProvider> {

    public static final ByteBufferProviderSerde GET = new ByteBufferProviderSerde();

    @Override
    public IByteBufferProvider fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final IByteBufferProvider obj) {
        try {
            return obj.asBuffer().asByteArray();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IByteBufferProvider fromBuffer(final IByteBuffer buffer) {
        return buffer;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final IByteBufferProvider obj) {
        if (buffer == obj) {
            return buffer.capacity();
        } else {
            try {
                return obj.getBuffer(buffer);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
