package de.invesdwin.util.math;

import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.internal.ADoublesStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastDoubles;
import de.invesdwin.util.math.internal.CheckedCastDoublesObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ADoublesStaticFacade", targets = {
        CheckedCastDoubles.class, CheckedCastDoublesObj.class,
        com.google.common.primitives.Doubles.class }, filterMethodSignatureExpressions = { ".* toArray\\(.*" })
@Immutable
public final class Doubles extends ADoublesStaticFacade {

    public static final double DEFAULT_MISSING_VALUE = 0d;
    public static final Double DEFAULT_MISSING_VALUE_OBJ = DEFAULT_MISSING_VALUE;
    public static final ADelegateComparator<Double> COMPARATOR = new ADelegateComparator<Double>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Double e) {
            return e;
        }
    };

    private static final double FIRST_ABOVE_ZERO = 0.000000001;
    private static final double FIRST_BELOW_ZERO = -0.000000001;

    private Doubles() {}

    public static double[] toArray(final Collection<? extends Number> vector) {
        if (vector == null) {
            return null;
        }
        return com.google.common.primitives.Doubles.toArray(vector);
    }

    public static double[] toArrayVector(final Collection<Double> vector) {
        return toArray(vector);
    }

    public static double[][] toArrayMatrix(final List<? extends List<Double>> matrix) {
        if (matrix == null) {
            return null;
        }
        final double[][] arrayMatrix = new double[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Double> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Double> asList(final double... vector) {
        if (vector == null) {
            return null;
        } else {
            return ADoublesStaticFacade.asList(vector);
        }
    }

    public static List<Double> asListVector(final double[] vector) {
        return asList(vector);
    }

    public static List<List<Double>> asListMatrix(final double[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Double>> matrixAsList = new ArrayList<List<Double>>(matrix.length);
        for (final double[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static List<Double> asList(final Double... vector) {
        if (vector == null) {
            return null;
        } else {
            return Arrays.asList(vector);
        }
    }

    public static List<Double> asListVector(final Double[] vector) {
        return asList(vector);
    }

    public static List<List<Double>> asListMatrix(final Double[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Double>> matrixAsList = new ArrayList<List<Double>>(matrix.length);
        for (final Double[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static Double max(final double first, final Double second) {
        if (second == null) {
            return first;
        } else {
            return max(first, second.doubleValue());
        }
    }

    public static Double max(final Double first, final double second) {
        if (first == null) {
            return second;
        } else {
            return max(first.doubleValue(), second);
        }
    }

    public static Double max(final Double first, final Double second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return max(first.doubleValue(), second.doubleValue());
        }
    }

    public static Double min(final double first, final Double second) {
        if (second == null) {
            return first;
        } else {
            return min(first, second.doubleValue());
        }
    }

    public static Double min(final Double first, final double second) {
        if (first == null) {
            return second;
        } else {
            return min(first.doubleValue(), second);
        }
    }

    public static Double min(final Double first, final Double second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return min(first.doubleValue(), second.doubleValue());
        }
    }

    public static Double between(final Double value, final Double min, final Double max) {
        return max(min(value, max), min);
    }

    public static <T> double[][] fixInconsistentMatrixDimensions(final double[][] matrix) {
        return fixInconsistentMatrixDimensions(matrix, DEFAULT_MISSING_VALUE);
    }

    public static double[][] fixInconsistentMatrixDimensions(final double[][] matrix, final double missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> double[][] fixInconsistentMatrixDimensions(final double[][] matrix, final double missingValue,
            final boolean appendMissingValues) {
        final int rows = matrix.length;
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final double[] vector = matrix[i];
            if (cols != 0 && cols != vector.length) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.length);
        }
        if (!colsInconsistent) {
            return matrix;
        }
        final double[][] fixedMatrix = new double[rows][];
        for (int i = 0; i < matrix.length; i++) {
            final double[] vector = matrix[i];
            final double[] fixedVector;
            if (vector.length == cols) {
                fixedVector = vector.clone();
            } else {
                fixedVector = new double[cols];
                if (appendMissingValues) {
                    System.arraycopy(vector, 0, fixedVector, 0, vector.length);
                    if (missingValue != DEFAULT_MISSING_VALUE) {
                        for (int j = vector.length - 1; j < fixedVector.length; j++) {
                            fixedVector[j] = missingValue;
                        }
                    }
                } else {
                    //prepend
                    final int missingValues = fixedVector.length - vector.length;
                    if (missingValue != DEFAULT_MISSING_VALUE) {
                        for (int j = 0; j < missingValues; j++) {
                            fixedVector[j] = missingValue;
                        }
                    }
                    System.arraycopy(vector, 0, fixedVector, missingValues, vector.length);
                }
            }
            fixedMatrix[i] = fixedVector;
        }
        return fixedMatrix;
    }

    public static <T> Double[][] fixInconsistentMatrixDimensionsObj(final Double[][] matrix) {
        return fixInconsistentMatrixDimensionsObj(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static Double[][] fixInconsistentMatrixDimensionsObj(final Double[][] matrix, final Double missingValue) {
        return fixInconsistentMatrixDimensionsObj(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> Double[][] fixInconsistentMatrixDimensionsObj(final Double[][] matrix, final Double missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue, appendMissingValues);
    }

    public static List<List<Double>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Double>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static List<List<Double>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Double>> matrix, final Double missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static List<List<Double>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Double>> matrix, final Double missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue, appendMissingValues);
    }

    public static void putDouble(final ByteBuffer buffer, final Double value) {
        if (value == null) {
            buffer.putDouble(Double.MIN_VALUE);
        } else {
            buffer.putDouble(value);
        }
    }

    public static Double extractDouble(final ByteBuffer buffer, final int index) {
        final double value = buffer.getDouble(index);
        return extractDouble(value);
    }

    public static Double extractDouble(final ByteBuffer buffer) {
        final double value = buffer.getDouble();
        return extractDouble(value);
    }

    public static Double extractDouble(final double value) {
        if (value == Double.MIN_VALUE) {
            return null;
        } else {
            return value;
        }
    }

    public static double divide(final Double dividend, final Double divisor) {
        if (dividend == null || divisor == null) {
            return 0D;
        } else {
            return divide(dividend.doubleValue(), divisor.doubleValue());
        }
    }

    public static double divide(final double dividend, final double divisor) {
        if (divisor == 0D) {
            return 0D;
        } else {
            return dividend / divisor;
        }
    }

    public static double round(final double value) {
        return round(value, ADecimal.DEFAULT_ROUNDING_SCALE, ADecimal.DEFAULT_ROUNDING_MODE);
    }

    public static double round(final double value, final RoundingMode roundingMode) {
        return round(ADecimal.DEFAULT_ROUNDING_SCALE, roundingMode);
    }

    public static double round(final double value, final int scale) {
        return round(scale, ADecimal.DEFAULT_ROUNDING_MODE);
    }

    public static double round(final double value, final int scale, final RoundingMode roundingMode) {
        if (value % 1 == 0 || roundingMode == RoundingMode.UNNECESSARY) {
            //nothing to round
            return value;
        }
        final long factor = (long) Math.pow(10, scale);
        final double toBeRoundedValue;
        if (scale < Decimal.DEFAULT_ROUNDING_SCALE && roundingMode != Decimal.DEFAULT_ROUNDING_MODE) {
            //fix 1 represented as 0.9999999 becoming 0 here instead of correctly being 1; for instance in FLOOR rounding mode
            toBeRoundedValue = round(value, scale + Decimal.DEFAULT_ROUNDING_SCALE, Decimal.DEFAULT_ROUNDING_MODE)
                    * factor;
        } else {
            toBeRoundedValue = value * factor;
        }

        final double roundedValue;
        switch (roundingMode) {
        case CEILING:
            roundedValue = Math.ceil(toBeRoundedValue);
            break;
        case UP:
            if (toBeRoundedValue >= 0) {
                roundedValue = (long) (toBeRoundedValue + 1d);
            } else {
                roundedValue = (long) (toBeRoundedValue - 1d);
            }
            break;
        case FLOOR:
            roundedValue = Math.floor(toBeRoundedValue);
            break;
        case DOWN:
            roundedValue = (long) toBeRoundedValue;
            break;
        case HALF_DOWN:
            if (toBeRoundedValue >= 0) {
                roundedValue = Math.ceil(toBeRoundedValue - 0.5d);
            } else {
                roundedValue = Math.floor(toBeRoundedValue + 0.5d);
            }
            break;
        case HALF_EVEN:
            //if the value is even and the fraction is 0.5, we need to round to the even number
            final long longValue = (long) toBeRoundedValue;
            if (longValue % 2 == 0) {
                //need to rounded here, since 0.5 can not be represented properly for doubles
                final long firstFractionalDigit = Longs.abs(Math.round(toBeRoundedValue % 1 * 10));
                if (firstFractionalDigit == 5) {
                    roundedValue = longValue;
                    break;
                }
            }

            //otherwise round to the nearest number
            roundedValue = Math.rint(toBeRoundedValue);
            break;
        case HALF_UP:
            if (toBeRoundedValue >= 0) {
                roundedValue = Math.floor(toBeRoundedValue + 0.5d);
            } else {
                roundedValue = Math.ceil(toBeRoundedValue - 0.5);
            }
            break;
        default:
            throw UnknownArgumentException.newInstance(RoundingMode.class, roundingMode);
        }
        return roundedValue / factor;
    }

    /**
     * With a step of 0.5: (Math.ceil(x * 2) / 2)
     */
    public static double roundToStep(final double value, final double step) {
        return roundToStep(value, step, ADecimal.DEFAULT_ROUNDING_MODE);
    }

    public static double roundToStep(final double value, final double step, final RoundingMode roundingMode) {
        final double stepReciprocal = reciprocal(step);
        final double multiplied = value * stepReciprocal;
        final double rounded = round(multiplied, 0, roundingMode);
        final double divided = divide(rounded, stepReciprocal);
        return divided;
    }

    public static double reciprocal(final double value) {
        return 1D / value;
    }

    public static double abs(final double value) {
        return Math.abs(value);
    }

    public static double pow(final double a, final double b) {
        double pow = Math.pow(a, b);
        if (Double.isNaN(pow) && a < 0D) {
            final double absA = Doubles.abs(a);
            pow = -Math.pow(absA, b);
        }
        return pow;
    }

    public static double log(final double value) {
        return Math.log(value);
    }

    public static double exp(final double value) {
        return Math.exp(value);
    }

    public static double log10(final double value) {
        return Math.log10(value);
    }

    public static double cos(final double value) {
        return Math.cos(value);
    }

    public static double sin(final double value) {
        return Math.sin(value);
    }

    public static double exp10(final double value) {
        return Math.pow(10D, value);
    }

    public static double remainder(final double value, final double divisor) {
        return value % divisor;
    }

    public static double sqrt(final double value) {
        return Math.sqrt(value);
    }

    public static double root(final double value, final double n) {
        final double log = Math.log(value);
        final double result = Math.exp(log / n);
        return result;
    }

    public static double nanToZero(final Double value) {
        if (value == null) {
            return 0D;
        } else {
            return nanToZero(value.doubleValue());
        }
    }

    public static double nanToZero(final double value) {
        assert !isInfinite(value) : "Infinite: " + value;
        if (isNaN(value)) {
            return 0D;
        } else {
            return value;
        }
    }

    public static boolean isNaN(final double value) {
        return Double.isNaN(value);
    }

    public static double nullToZero(final Double value) {
        if (value == null) {
            return 0D;
        } else {
            return value.doubleValue();
        }
    }

    public static double nullToZero(final Number value) {
        if (value == null) {
            return 0D;
        } else {
            return value.doubleValue();
        }
    }

    public static double scaleByPowerOfTen(final double value, final int n) {
        return value * pow(10, n);
    }

    public static boolean isInfinite(final double value) {
        return Double.isInfinite(value);
    }

    public static Double nanToNull(final Double value) {
        if (value == null) {
            return null;
        } else {
            return nanToNull(value.doubleValue());
        }
    }

    public static Double nanToNull(final double value) {
        assert !isInfinite(value) : "Infinite: " + value;
        if (isNaN(value)) {
            return null;
        } else {
            return value;
        }
    }

    public static double max(final double a, final double b) {
        return Math.max(a, b);
    }

    public static double min(final double a, final double b) {
        return Math.min(a, b);
    }

    public static int compare(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return 1;
        } else if (difference < FIRST_BELOW_ZERO) {
            return -1;
        } else if (difference == 0D) {
            return 0;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            if (defaultRoundedValue < roundedOther) {
                return -1;
            } else if (defaultRoundedValue > roundedOther) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static double negate(final double value) {
        return value * -1D;
    }

    public static double growthRate(final double first, final double second) {
        final double rate = Doubles.divide(second - first, Doubles.abs(first));
        return rate;
    }

    public static double nullToNan(final Double value) {
        if (value == null) {
            return Double.NaN;
        } else {
            return value.doubleValue();
        }
    }

}
