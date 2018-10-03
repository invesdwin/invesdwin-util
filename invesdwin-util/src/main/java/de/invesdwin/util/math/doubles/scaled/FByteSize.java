package de.invesdwin.util.math.doubles.scaled;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.doubles.AScaledFDouble;

@SuppressWarnings("serial")
@NotThreadSafe
public class FByteSize extends AScaledFDouble<FByteSize, FByteSizeScale> {

    public static final FByteSizeScale DEFAULT_SCALE = FByteSizeScale.BYTES;
    public static final FByteSize ZERO = new FByteSize(0D, DEFAULT_SCALE);

    public FByteSize(final double value, final FByteSizeScale scale) {
        super(value, scale);
    }

    @Override
    public FByteSizeScale getDefaultScale() {
        return DEFAULT_SCALE;
    }

    @Override
    protected FByteSize getGenericThis() {
        return this;
    }

    @Override
    protected FByteSize newValueCopy(final double value, final FByteSizeScale scale) {
        return new FByteSize(value, scale);
    }

    public static FByteSize nullToZero(final FByteSize value) {
        if (value == null) {
            return ZERO;
        } else {
            return value;
        }
    }

    @Override
    public FByteSize zero() {
        return ZERO;
    }

}
