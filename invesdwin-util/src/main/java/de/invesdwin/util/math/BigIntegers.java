package de.invesdwin.util.math;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.decimal.ADecimal;

@Immutable
public final class BigIntegers {

    public static final ADelegateComparator<BigInteger> COMPARATOR = new ADelegateComparator<BigInteger>() {
        @Override
        protected Comparable<?> getCompareCriteria(final BigInteger e) {
            return e;
        }
    };

    private BigIntegers() {}

    public static BigInteger valueOf(final ADecimal<?> value) {
        if (value == null) {
            return null;
        } else {
            return value.bigIntegerValue();
        }
    }

    public static BigInteger valueOf(final Number number) {
        if (number == null) {
            return null;
        } else if (number instanceof BigInteger) {
            return (BigInteger) number;
        } else {
            try {
                return new BigInteger(number.toString());
            } catch (final NumberFormatException e) {
                return valueOf(number.longValue());
            }
        }
    }

    public static List<BigInteger> valueOf(final Number... numbers) {
        return valueOf(Arrays.asList(numbers));
    }

    public static List<BigInteger> valueOf(final List<? extends Number> numbers) {
        final List<BigInteger> bigDecimals = new ArrayList<BigInteger>();
        for (final Number n : numbers) {
            bigDecimals.add(valueOf(n));
        }
        return bigDecimals;
    }

}
