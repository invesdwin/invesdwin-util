package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class IntegerSerde implements ISerde<Integer> {

    public static final IntegerSerde GET = new IntegerSerde();
    public static final int FIXED_LENGTH = Integer.BYTES;

    @Override
    public Integer fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Integer obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public Integer fromBuffer(final IByteBuffer buffer) {
        if (buffer.capacity() == 0) {
            return null;
        }
        return buffer.getInt(0);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final Integer obj) {
        if (obj == null) {
            return 0;
        }
        buffer.putInt(0, obj);
        return FIXED_LENGTH;
    }

    public static void putInteger(final IByteBuffer buffer, final int index, final Integer value) {
        if (value == null) {
            buffer.putInt(index, Integer.MIN_VALUE);
        } else {
            buffer.putInt(index, value);
        }
    }

    public static Integer extractInteger(final IByteBuffer buffer, final int index) {
        final int value = buffer.getInt(index);
        return extractInteger(value);
    }

    public static Integer extractInteger(final int value) {
        if (value == Integer.MIN_VALUE) {
            return null;
        } else {
            return value;
        }
    }

}
