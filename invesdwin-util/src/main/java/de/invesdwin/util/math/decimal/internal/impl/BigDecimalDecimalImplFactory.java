package de.invesdwin.util.math.decimal.internal.impl;

import java.math.BigDecimal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.BigDecimals;

/**
 * More accurate alternative to Double, though about 12% slower...
 * 
 */
@Immutable
public class BigDecimalDecimalImplFactory implements IDecimalImplFactory<BigDecimalDecimalImpl> {

    @Override
    public BigDecimalDecimalImpl valueOf(final Number value) {
        final BigDecimal bigDecimal = toBigDecimal(value);
        return new BigDecimalDecimalImpl(bigDecimal, bigDecimal);
    }

    @Override
    public BigDecimalDecimalImpl valueOf(final Double value) {
        final BigDecimal bigDecimal = toBigDecimal(value);
        return new BigDecimalDecimalImpl(bigDecimal, bigDecimal);
    }

    @Override
    public BigDecimalDecimalImpl valueOf(final String value) {
        final BigDecimal bigDecimal = new BigDecimal(value);
        return new BigDecimalDecimalImpl(bigDecimal, bigDecimal);
    }

    public static BigDecimal toBigDecimal(final Number number) {
        if (number == null) {
            return BigDecimal.ZERO;
        } else if (number instanceof BigDecimal) {
            final BigDecimal cNumber = (BigDecimal) number;
            return cNumber;
        } else {
            try {
                //first use string to prevent inprecision of double conversion, which might break the scale
                return new BigDecimal(number.toString());
            } catch (final NumberFormatException e) {
                return new BigDecimal(number.doubleValue(), BigDecimals.DEFAULT_MATH_CONTEXT);
            }
        }
    }

}
