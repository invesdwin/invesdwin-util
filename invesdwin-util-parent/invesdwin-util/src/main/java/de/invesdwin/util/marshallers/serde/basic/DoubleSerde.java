package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.math.Doubles;

@Immutable
public final class DoubleSerde implements ISerde<Double> {

    public static final DoubleSerde GET = new DoubleSerde();
    public static final int FIXED_LENGTH = Double.BYTES;

    private DoubleSerde() {
    }

    @Override
    public Double fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Double obj) {
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public Double fromBuffer(final IByteBuffer buffer, final int length) {
        if (length == 0) {
            return null;
        }
        return buffer.getDouble(0);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final Double obj) {
        if (obj == null) {
            return 0;
        }
        buffer.putDouble(0, obj);
        return FIXED_LENGTH;
    }

    public static void putDouble(final IByteBuffer buffer, final int index, final Double value) {
        if (value == null) {
            buffer.putDouble(index, Double.NaN);
        } else {
            buffer.putDouble(index, value);
        }
    }

    public static Double extractDouble(final IByteBuffer buffer, final int index) {
        final double value = buffer.getDouble(index);
        return extractDouble(value);
    }

    public static Double extractDouble(final double value) {
        if (Doubles.isNaN(value)) {
            return null;
        } else {
            return value;
        }
    }

}
