package de.invesdwin.util.marshallers.serde;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;
import io.netty.util.concurrent.FastThreadLocal;

@Immutable
public final class SerdeBaseMethods {

    private static final FastThreadLocal<IByteBuffer> EXPANDABLE_BUFFER_REF = new FastThreadLocal<IByteBuffer>() {
        @Override
        protected IByteBuffer initialValue() throws Exception {
            return ByteBuffers.allocateExpandable();
        }
    };

    private SerdeBaseMethods() {
    }

    public static <O> O fromBytes(final ISerde<O> serde, final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return serde.fromBuffer(ByteBuffers.wrap(bytes), bytes.length);
    }

    public static <O> byte[] toBytes(final ISerde<O> serde, final O obj) {
        if (obj == null) {
            return Bytes.EMPTY_ARRAY;
        }
        final IByteBuffer buffer = EXPANDABLE_BUFFER_REF.get();
        final int length = serde.toBuffer(buffer, obj);
        return buffer.asByteArrayCopyTo(length);
    }

    public static <O> O fromBuffer(final ISerde<O> serde, final IByteBuffer buffer, final int length) {
        if (length == 0) {
            return null;
        }
        final byte[] bytes = buffer.asByteArrayTo(length);
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
