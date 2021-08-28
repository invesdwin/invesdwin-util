package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Bytes;

@Immutable
public class BooleanSerde implements ISerde<Boolean> {

    public static final BooleanSerde GET = new BooleanSerde();
    public static final int FIXED_LENGTH = Booleans.BYTES;

    @Override
    public Boolean fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Boolean obj) {
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public Boolean fromBuffer(final IByteBuffer buffer, final int length) {
        if (length == 0) {
            return null;
        }
        return buffer.getBoolean(0);
    }

    @Override
    public int toBuffer(final Boolean obj, final IByteBuffer buffer) {
        if (obj == null) {
            return 0;
        }
        buffer.putBoolean(0, obj);
        return FIXED_LENGTH;
    }

    public static void putBoolean(final IByteBuffer buffer, final int index, final Boolean value) {
        if (value == null) {
            buffer.putByte(index, Byte.MIN_VALUE);
        } else {
            buffer.putByte(index, Bytes.checkedCast(value));
        }
    }

    public static Boolean getBoolean(final IByteBuffer buffer, final int index) {
        final byte value = buffer.getByte(index);
        return getBoolean(value);
    }

    public static Boolean getBoolean(final byte value) {
        if (value == Byte.MIN_VALUE) {
            return null;
        } else {
            return Booleans.checkedCast(value);
        }
    }

}
