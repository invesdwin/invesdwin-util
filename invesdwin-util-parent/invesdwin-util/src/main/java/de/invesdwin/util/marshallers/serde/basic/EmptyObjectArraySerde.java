package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class EmptyObjectArraySerde implements ISerde<Object[]> {

    public static final EmptyObjectArraySerde GET = new EmptyObjectArraySerde();
    public static final int FIXED_LENGTH = 0;

    @Override
    public Object[] fromBytes(final byte[] bytes) {
        return Objects.EMPTY_ARRAY;
    }

    @Override
    public Object[] fromBuffer(final IByteBuffer buffer) {
        return Objects.EMPTY_ARRAY;
    }

    @Override
    public byte[] toBytes(final Object[] obj) {
        return Bytes.EMPTY_ARRAY;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final Object[] obj) {
        return 0;
    }

}
