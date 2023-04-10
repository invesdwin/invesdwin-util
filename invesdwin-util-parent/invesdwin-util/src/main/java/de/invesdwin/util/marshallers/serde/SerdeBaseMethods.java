package de.invesdwin.util.marshallers.serde;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;

@Immutable
public final class SerdeBaseMethods {

    private SerdeBaseMethods() {}

    public static <O> O fromBytes(final ISerde<O> serde, final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return serde.fromBuffer(ByteBuffers.wrap(bytes));
    }

    public static <O> byte[] toBytes(final ISerde<O> serde, final O obj) {
        if (obj == null) {
            return Bytes.EMPTY_ARRAY;
        }
        try (ICloseableByteBuffer buffer = ByteBuffers.EXPANDABLE_POOL.borrowObject()) {
            final int length = serde.toBuffer(buffer, obj);
            //we need this as a copy since byte arrays might be stored/cached before the next toBytes call might happen
            return buffer.asByteArrayCopyTo(length);
        }
    }

    public static <O> O fromBuffer(final ISerde<O> serde, final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        final byte[] bytes = buffer.asByteArray();
        return serde.fromBytes(bytes);
    }

    public static <O> int toBuffer(final ISerde<O> serde, final IByteBuffer buffer, final O obj) {
        if (obj == null) {
            return 0;
        }
        final byte[] bytes = serde.toBytes(obj);
        buffer.putBytes(0, bytes);
        return bytes.length;
    }

}
