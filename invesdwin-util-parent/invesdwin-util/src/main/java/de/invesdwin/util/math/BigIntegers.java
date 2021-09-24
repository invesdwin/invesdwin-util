package de.invesdwin.util.math;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.math.decimal.ADecimal;

@Immutable
public final class BigIntegers {

    public static final BigInteger DEFAULT_MISSING_VALUE = BigInteger.ZERO;
    public static final IComparator<BigInteger> COMPARATOR = IComparator.getDefaultInstance();

    private BigIntegers() {
    }

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
        final List<BigInteger> bigDecimals = new ArrayList<BigInteger>(numbers.size());
        for (final Number n : numbers) {
            bigDecimals.add(valueOf(n));
        }
        return bigDecimals;
    }

    public static BigInteger[][] fixInconsistentMatrixDimensions(final BigInteger[][] matrix) {
        return fixInconsistentMatrixDimensions(matrix, DEFAULT_MISSING_VALUE);
    }

    public static BigInteger[][] fixInconsistentMatrixDimensions(final BigInteger[][] matrix,
            final BigInteger missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static BigInteger[][] fixInconsistentMatrixDimensions(final BigInteger[][] matrix,
            final BigInteger missingValue, final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue, appendMissingValues);
    }

    public static List<List<BigInteger>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends BigInteger>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, DEFAULT_MISSING_VALUE);
    }

    public static List<List<BigInteger>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends BigInteger>> matrix, final BigInteger missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static List<List<BigInteger>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends BigInteger>> matrix, final BigInteger missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue, appendMissingValues);
    }

}
