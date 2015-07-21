package de.invesdwin.util.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.internal.impl.BigDecimalDecimalImplFactory;

@Immutable
public final class BigDecimals {

    public static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(MathContext.DECIMAL128.getPrecision(),
            Decimal.DEFAULT_ROUNDING_MODE);
    public static final ADelegateComparator<BigDecimal> COMPARATOR = new ADelegateComparator<BigDecimal>() {
        @Override
        protected Comparable<?> getCompareCriteria(final BigDecimal e) {
            return e;
        }
    };

    private BigDecimals() {}

    public static BigDecimal valueOf(final ADecimal<?> value) {
        if (value == null) {
            return null;
        } else {
            return value.bigDecimalValue();
        }
    }

    public static BigDecimal valueOf(final Number number) {
        return BigDecimalDecimalImplFactory.toBigDecimal(number);
    }

    public static List<BigDecimal> valueOf(final Number... numbers) {
        return valueOf(Arrays.asList(numbers));
    }

    public static List<BigDecimal> valueOf(final List<? extends Number> numbers) {
        final List<BigDecimal> bigDecimals = new ArrayList<BigDecimal>();
        for (final Number n : numbers) {
            bigDecimals.add(valueOf(n));
        }
        return bigDecimals;
    }

    public static BigDecimal nullToZero(final BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        } else {
            return value;
        }
    }

}
