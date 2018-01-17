package de.invesdwin.util.math.decimal.internal.impl;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.dfp.Dfp;
import org.apache.commons.math3.dfp.DfpField;
import org.apache.commons.math3.dfp.DfpField.RoundingMode;

import de.invesdwin.util.math.BigDecimals;

/**
 * Better not use this, Dfp is horribly slow (about 4x to 5x slower than BigDecimal and double to be precise) because of
 * slow rounding and toDouble! BigDecimal is a better alternative for precise calculations...
 * 
 */
@Immutable
public class DfpDecimalImplFactory implements IDecimalImplFactory<DfpDecimalImpl> {

    public static final Dfp ONE;
    public static final Dfp ZERO_POINT_FIVE;
    public static final Dfp ZERO;
    private static final DfpField DFP_FIELD;

    static {
        DFP_FIELD = new DfpField(BigDecimals.DEFAULT_MATH_CONTEXT.getPrecision());
        DFP_FIELD.setRoundingMode(RoundingMode.ROUND_HALF_UP);
        ZERO_POINT_FIVE = DFP_FIELD.newDfp("0.5");
        ZERO = DFP_FIELD.getZero();
        ONE = DFP_FIELD.getOne();
    }

    @Override
    public DfpDecimalImpl valueOf(final Number value) {
        final Dfp dfp = toDfp(value);
        return new DfpDecimalImpl(dfp);
    }

    @Override
    public DfpDecimalImpl valueOf(final Double value) {
        final Dfp dfp = toDfp(value);
        return new DfpDecimalImpl(dfp);
    }

    @Override
    public DfpDecimalImpl valueOf(final String value) {
        final Dfp newDfp = DFP_FIELD.newDfp(value);
        return new DfpDecimalImpl(newDfp);
    }

    public static Dfp toDfp(final Number number) {
        if (number == null) {
            return ZERO;
        } else {
            final Dfp dfp = DFP_FIELD.newDfp(number.toString());
            if (!dfp.isZero()) {
                return dfp;
            } else {
                //string conversion might have gone wrong here
                return DFP_FIELD.newDfp(number.doubleValue());
            }
        }
    }

}
