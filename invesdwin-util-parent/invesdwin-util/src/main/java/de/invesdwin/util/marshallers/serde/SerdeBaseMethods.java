package de.invesdwin.util.marshallers.serde;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.buffer.ByteBuffers;
import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.math.Bytes;

@Immutable
public final class SerdeBaseMethods {

    private SerdeBaseMethods() {
    }

    public static <O> O fromBytes(final ISerde<O> serde, final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return serde.fromBuffer(ByteBuffers.wrap(bytes));
    }

    public static <O> byte[] toBytes(final ISerde<O> serde, final O obj, final int fixedLength) {
        if (obj == null) {
            return Bytes.EMPTY_ARRAY;
        }
        final IByteBuffer buffer = ByteBuffers.allocate(fixedLength);
        final int length = serde.toBuffer(obj, buffer);
        return buffer.asByteArrayCopyTo(length);
    }

    public static <O> O fromBuffer(final ISerde<O> serde, final IByteBuffer buffer) {
        final byte[] bytes = buffer.asByteArrayCopy();
        return serde.fromBytes(bytes);
    }

    public static <O> int toBuffer(final ISerde<O> serde, final O obj, final IByteBuffer buffer) {
        final byte[] bytes = serde.toBytes(obj);
        buffer.putBytes(0, bytes);
        return bytes.length;
    }

}
