package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.AScaledDecimal;
import de.invesdwin.util.math.decimal.Decimal;

@SuppressWarnings("serial")
@NotThreadSafe
public class ByteSize extends AScaledDecimal<ByteSize, ByteSizeScale> {

    public static final ByteSizeScale DEFAULT_SCALE = ByteSizeScale.BYTES;
    public static final ByteSize ZERO = new ByteSize(Decimal.ZERO, DEFAULT_SCALE);

    public ByteSize(final Decimal value, final ByteSizeScale scale) {
        super(value, scale);
    }

    @Override
    public ByteSizeScale getDefaultScale() {
        return DEFAULT_SCALE;
    }

    @Override
    protected ByteSize getGenericThis() {
        return this;
    }

    @Override
    protected ByteSize newValueCopy(final Decimal value, final ByteSizeScale scale) {
        return new ByteSize(value, scale);
    }

    public static ByteSize nullToZero(final ByteSize value) {
        if (value == null) {
            return ZERO;
        } else {
            return value;
        }
    }

    @Override
    public ByteSize zero() {
        return ZERO;
    }

}
