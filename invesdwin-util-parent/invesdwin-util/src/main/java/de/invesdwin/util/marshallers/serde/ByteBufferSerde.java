package de.invesdwin.util.marshallers.serde;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class ByteBufferSerde implements ISerde<IByteBuffer> {

    public static final ByteBufferSerde GET = new ByteBufferSerde();

    @Override
    public IByteBuffer fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final IByteBuffer obj) {
        return obj.asByteArray();
    }

    @Override
    public IByteBuffer fromBuffer(final IByteBuffer buffer, final int length) {
        return buffer.sliceTo(length);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final IByteBuffer obj) {
        final int length = obj.capacity();
        if (buffer == obj) {
            return length;
        } else {
            buffer.putBytesTo(0, obj, length);
            return length;
        }
    }
}
