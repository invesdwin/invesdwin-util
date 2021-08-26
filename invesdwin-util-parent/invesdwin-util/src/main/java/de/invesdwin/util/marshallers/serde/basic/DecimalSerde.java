package de.invesdwin.util.marshallers.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.Decimal;

@Immutable
public final class DecimalSerde implements ISerde<Decimal> {

    public static final DecimalSerde GET = new DecimalSerde();
    public static final int FIXED_LENGTH = Decimal.BYTES;

    private DecimalSerde() {
    }

    @Override
    public Decimal fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Decimal obj) {
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public Decimal fromBuffer(final IByteBuffer buffer, final int length) {
        return extractDecimal(buffer, 0);
    }

    @Override
    public int toBuffer(final Decimal obj, final IByteBuffer buffer) {
        putDecimal(buffer, 0, obj);
        return FIXED_LENGTH;
    }

    public static void putDecimal(final IByteBuffer buffer, final int index, final Decimal value) {
        if (value == null) {
            buffer.putDouble(index, Double.NaN);
        } else {
            buffer.putDouble(index, value.doubleValue());
        }
    }

    public static Decimal extractDecimal(final IByteBuffer buffer, final int index) {
        final double value = buffer.getDouble(index);
        return extractDecimal(value);
    }

    public static Decimal extractDecimal(final double value) {
        if (Doubles.isNaN(value)) {
            return null;
        } else {
            return new Decimal(value);
        }
    }

}
