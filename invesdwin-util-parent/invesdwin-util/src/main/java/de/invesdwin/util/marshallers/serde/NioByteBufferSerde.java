package de.invesdwin.util.marshallers.serde;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;

@Immutable
public class NioByteBufferSerde implements ISerde<java.nio.ByteBuffer> {

    public static final NioByteBufferSerde GET = new NioByteBufferSerde();

    @Override
    public java.nio.ByteBuffer fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final java.nio.ByteBuffer obj) {
        return new UnsafeByteBuffer(obj).asByteArray();
    }

    @Override
    public java.nio.ByteBuffer fromBuffer(final IByteBuffer buffer) {
        return buffer.asNioByteBuffer();
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final java.nio.ByteBuffer obj) {
        final int length = obj.capacity();
        if (buffer.nioByteBuffer() == obj) {
            return length;
        } else {
            buffer.putBytesTo(0, obj, length);
            return length;
        }
    }
}
