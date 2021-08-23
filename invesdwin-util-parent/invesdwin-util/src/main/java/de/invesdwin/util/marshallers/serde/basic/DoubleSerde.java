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
    public Double fromBuffer(final IByteBuffer buffer) {
        return extractDouble(buffer, 0);
    }

    @Override
    public int toBuffer(final Double obj, final IByteBuffer buffer) {
        putDouble(buffer, 0, obj);
        return FIXED_LENGTH;
    }

    public static int putDouble(final IByteBuffer buffer, final int index, final Double value) {
        if (value == null) {
            return buffer.putDouble(index, Double.NaN);
        } else {
            return buffer.putDouble(index, value);
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
