package de.invesdwin.util.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@Immutable
public final class BigDecimals {

    public static final BigDecimal DEFAULT_MISSING_VALUE = BigDecimal.ZERO;
    public static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(MathContext.DECIMAL128.getPrecision(),
            Decimal.DEFAULT_ROUNDING_MODE);
    public static final IComparator<BigDecimal> COMPARATOR = IComparator.getDefaultInstance();

    private BigDecimals() {
    }

    public static BigDecimal valueOf(final ADecimal<?> value) {
        if (value == null) {
            return null;
        } else {
            return value.bigDecimalValue();
        }
    }

    public static BigDecimal valueOf(final Number number) {
        return new BigDecimal(number.doubleValue());
    }

    public static List<BigDecimal> valueOf(final Number... numbers) {
        return valueOf(Arrays.asList(numbers));
    }

    public static List<BigDecimal> valueOf(final List<? extends Number> numbers) {
        final List<BigDecimal> bigDecimals = new ArrayList<BigDecimal>(numbers.size());
        for (final Number n : numbers) {
            bigDecimals.add(valueOf(n));
        }
        return bigDecimals;
    }

    public static BigDecimal nullToZero(final BigDecimal value) {
        if (value == null) {
            return DEFAULT_MISSING_VALUE;
        } else {
            return value;
        }
    }

    public static BigDecimal[][] fixInconsistentMatrixDimensions(final BigDecimal[][] matrix) {
        return fixInconsistentMatrixDimensions(matrix, DEFAULT_MISSING_VALUE);
    }

    public static BigDecimal[][] fixInconsistentMatrixDimensions(final BigDecimal[][] matrix,
            final BigDecimal missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static BigDecimal[][] fixInconsistentMatrixDimensions(final BigDecimal[][] matrix,
            final BigDecimal missingValue, final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue, appendMissingValues);
    }

    public static List<List<BigDecimal>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends BigDecimal>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, DEFAULT_MISSING_VALUE);
    }

    public static List<List<BigDecimal>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends BigDecimal>> matrix, final BigDecimal missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static List<List<BigDecimal>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends BigDecimal>> matrix, final BigDecimal missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue, appendMissingValues);
    }

    public static int compare(final BigDecimal a, final BigDecimal b) {
        if (a == null && b == null) {
            return 0;
        } else if (a == null) {
            return -1;
        } else if (b == null) {
            return 1;
        }
        return a.compareTo(b);
    }

}
