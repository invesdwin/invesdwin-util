package de.invesdwin.util.math;

import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import com.google.common.primitives.DoublesAccessor;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.internal.ADoublesStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastDoubles;
import de.invesdwin.util.math.internal.CheckedCastDoublesObj;
import io.netty.util.concurrent.FastThreadLocal;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ADoublesStaticFacade", targets = {
        CheckedCastDoubles.class, CheckedCastDoublesObj.class,
        com.google.common.primitives.Doubles.class }, filterMethodSignatureExpressions = { ".* toArray\\(.*" })
@Immutable
public final class Doubles extends ADoublesStaticFacade {

    public static final Double[] OBJ_EMPTY_ARRAY = new Double[0];
    public static final double[] EMPTY_ARRAY = new double[0];
    public static final double[][] EMPTY_MATRIX = new double[0][];

    //CHECKSTYLE:OFF
    public static final double NaN = Double.NaN;
    //CHECKSTYLE:ON
    public static final String NAN_STR = "NaN";
    //CHECKSTYLE:OFF
    public static final double MAX_VALUE = Double.MAX_VALUE;
    public static final double MIN_VALUE = -Double.MAX_VALUE;
    //CHECKSTYLE:ON
    public static final double ONE_THIRD = 1D / 3D;
    public static final double DEFAULT_MISSING_VALUE = 0d;
    public static final Double DEFAULT_MISSING_VALUE_OBJ = DEFAULT_MISSING_VALUE;
    public static final IComparator<Double> COMPARATOR = IComparator.getDefaultInstance();

    public static final double FIRST_ABOVE_ZERO = 0.000000001;
    public static final double FIRST_BELOW_ZERO = -0.000000001;
    public static final double FALSE = 0D;
    public static final double TRUE = 1D;
    private static final long RAW_BITS_NEGATIVE_ZERO = Double.doubleToRawLongBits(-0.0);

    private static final FastThreadLocal<NumberFormat> NUMBER_FORMAT = new FastThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() throws Exception {
            final NumberFormat format = NumberFormat.getNumberInstance(Locale.ENGLISH);
            format.setMaximumFractionDigits(MathContext.DECIMAL128.getPrecision());
            format.setRoundingMode(Decimal.DEFAULT_ROUNDING_MODE);
            format.setGroupingUsed(false);
            return format;
        }
    };

    private Doubles() {}

    public static double[] toArray(final Collection<? extends Number> vector) {
        if (vector == null) {
            return null;
        }
        return com.google.common.primitives.Doubles.toArray(vector);
    }

    public static double[] toArrayVector(final Collection<? extends Number> vector) {
        return toArray(vector);
    }

    public static double[][] toArrayMatrix(final List<? extends List<? extends Number>> matrix) {
        if (matrix == null) {
            return null;
        }
        final double[][] arrayMatrix = new double[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<? extends Number> vector = matrix.get(i);
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

    public static double max(final double first, final Double second) {
        if (second == null) {
            return first;
        } else {
            return max(first, second.doubleValue());
        }
    }

    public static double max(final Double first, final double second) {
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

    public static double min(final double first, final Double second) {
        if (second == null) {
            return first;
        } else {
            return min(first, second.doubleValue());
        }
    }

    public static double min(final Double first, final double second) {
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

    public static double between(final double value, final double minInclusive, final double maxInclusive) {
        return max(min(value, maxInclusive), minInclusive);
    }

    public static Double between(final Double value, final Double minInclusive, final Double maxInclusive) {
        return max(min(value, maxInclusive), minInclusive);
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

    public static Double round(final Double value) {
        if (value == null) {
            return null;
        } else {
            return round(value.doubleValue());
        }
    }

    public static double round(final double value) {
        return round(value, ADecimal.DEFAULT_ROUNDING_SCALE, ADecimal.DEFAULT_ROUNDING_MODE);
    }

    public static double round(final double value, final RoundingMode roundingMode) {
        return round(value, ADecimal.DEFAULT_ROUNDING_SCALE, roundingMode);
    }

    public static double round(final double value, final int scale) {
        return round(value, scale, ADecimal.DEFAULT_ROUNDING_MODE);
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

    public static double square(final double a) {
        return a * a;
    }

    public static double pow(final double a, final double b) {
        final double pow = Math.pow(a, b);
        if (Double.isNaN(pow) && a < 0D) {
            final double absA = Doubles.abs(a);
            return -Math.pow(absA, b);
        } else {
            return pow;
        }
    }

    /**
     * Has special handling for 0 and negative values to work around exception cases.
     * 
     * Negative values are treated as neutral.
     */
    public static double log(final double value) {
        if (value <= 0D) {
            return 0D;
        } else {
            return Math.log(value);
        }
    }

    public static double exp(final double value) {
        if (value == 0D) {
            if (Double.doubleToRawLongBits(value) == RAW_BITS_NEGATIVE_ZERO) {
                return -1D;
            } else {
                return 1D;
            }
        } else {
            return Math.exp(value);
        }
    }

    /**
     * Has special handling for 0 and negative values to work around exception cases.
     * 
     * Negative values are treated as neutral.
     */
    public static double log10(final double value) {
        if (value <= 0D) {
            return 0D;
        } else {
            return Math.log10(value);
        }
    }

    public static double exp10(final double value) {
        if (value == 0D) {
            if (Double.doubleToRawLongBits(value) == RAW_BITS_NEGATIVE_ZERO) {
                return -1D;
            } else {
                return 1D;
            }
        } else {
            return Math.pow(10D, value);
        }
    }

    public static double cos(final double value) {
        return Math.cos(value);
    }

    public static double sin(final double value) {
        return Math.sin(value);
    }

    public static double remainder(final double value, final double divisor) {
        return value % divisor;
    }

    /**
     * Has special handling for 0 and negative values to work around exception cases.
     */
    public static double sqrt(final double value) {
        if (value < 0D) {
            return -Math.sqrt(value);
        } else {
            return Math.sqrt(value);
        }
    }

    public static double root(final double value, final double n) {
        final double log = log(value);
        final double result = exp(divide(log, n));
        return result;
    }

    public static double nullToNan(final Double value) {
        if (value == null) {
            return Double.NaN;
        } else {
            return value.doubleValue();
        }
    }

    public static double nullToNan(final Number value) {
        if (value == null) {
            return Double.NaN;
        } else {
            return value.doubleValue();
        }
    }

    public static double nullToZero(final Double value) {
        if (value == null) {
            return 0D;
        } else {
            return nanToZero(value.doubleValue());
        }
    }

    public static double nullToZero(final Number value) {
        if (value == null) {
            return 0D;
        } else {
            return nanToZero(value.doubleValue());
        }
    }

    public static double nanToZero(final Number value) {
        if (value == null) {
            return 0D;
        } else {
            return nanToZero(value.doubleValue());
        }
    }

    public static double nanToZero(final Double value) {
        if (value == null) {
            return 0D;
        } else {
            return nanToZero(value.doubleValue());
        }
    }

    public static double nanToZero(final double value) {
        assert isNotInfinite(value) : newInfiniteErrorMessage(value);
        if (isNaN(value)) {
            return 0D;
        } else {
            return value;
        }
    }

    public static double nonFiniteToZero(final double value) {
        if (isNonFinite(value)) {
            return 0;
        } else {
            return value;
        }
    }

    public static double zeroToNan(final Number value) {
        if (value == null) {
            return Double.NaN;
        } else {
            return zeroToNan(value.doubleValue());
        }
    }

    public static double zeroToNan(final Double value) {
        if (value == null) {
            return Double.NaN;
        } else {
            return zeroToNan(value.doubleValue());
        }
    }

    public static double zeroToNan(final double value) {
        if (isZero(value)) {
            return Double.NaN;
        } else {
            return value;
        }
    }

    private static String newInfiniteErrorMessage(final double value) {
        return "Infinite: " + value;
    }

    public static boolean isNaN(final double value) {
        return Double.isNaN(value);
    }

    public static boolean isNaN(final Number number) {
        if (number == null) {
            return false;
        }
        return Doubles.isNaN(number.doubleValue());
    }

    public static boolean isNullOrNaN(final Double value) {
        return value == null || isNaN(value);
    }

    public static double scaleByPowerOfTen(final double value, final int n) {
        return value * pow(10, n);
    }

    public static boolean isInfinite(final double value) {
        return Double.isInfinite(value);
    }

    public static boolean isNotInfinite(final double value) {
        return !Double.isInfinite(value);
    }

    public static boolean isNonFinite(final double value) {
        return !Double.isFinite(value);
    }

    public static Double nanToNull(final Double value) {
        if (value == null) {
            return null;
        } else {
            return nanToNull(value.doubleValue());
        }
    }

    public static Double nanToNull(final double value) {
        assert !isInfinite(value) : newInfiniteErrorMessage(value);
        if (isNaN(value)) {
            return null;
        } else {
            return value;
        }
    }

    public static double max(final double a, final double b) {
        if (isNaN(a)) {
            return b;
        } else if (isNaN(b)) {
            return a;
        }
        return Math.max(a, b);
    }

    public static double min(final double a, final double b) {
        if (isNaN(a)) {
            return b;
        } else if (isNaN(b)) {
            return a;
        }
        return Math.min(a, b);
    }

    public static double cbrt(final double a) {
        return Math.cbrt(a);
    }

    public static double negate(final double value) {
        return value * -1D;
    }

    public static double growthRate(final double first, final double second) {
        final double rate = Doubles.divide(second - first, Doubles.abs(first));
        return rate;
    }

    public static double infinityToZero(final double value) {
        if (isInfinite(value)) {
            return 0D;
        } else {
            return value;
        }
    }

    public static double infinityToNan(final double value) {
        if (isInfinite(value)) {
            return Double.NaN;
        } else {
            return 0;
        }
    }

    /**
     * https://stats.stackexchange.com/questions/70801/how-to-normalize-data-to-0-1-range
     */
    public static double normalize(final double value, final double min, final double max) {
        return infinityToZero((value - min) / (max - min));
    }

    public static double denormalize(final double value, final double min, final double max) {
        return min + (max - min) * value;
    }

    /**
     * https://stackoverflow.com/questions/38403240/truncating-float-to-the-two-first-non-zero-decimal-digits
     */
    public static int getTrailingDecimalDigitsScale(final double number, final int trailingDecimalDigits,
            final int maxScale) {
        final double abs = abs(number);
        if (abs < FIRST_ABOVE_ZERO) {
            return trailingDecimalDigits;
        }
        final int integral = (int) abs;
        if (integral != 0) {
            return trailingDecimalDigits;
        }
        double decimal = abs - integral;
        int digits = 0;

        final double threshold = Doubles.scaleByPowerOfTen(0.1, trailingDecimalDigits);
        while (decimal < threshold && digits < maxScale) {
            decimal *= 10;
            digits++;
        }

        return digits;
    }

    public static double min(final double... values) {
        double min = values[0];
        for (int i = 1; i < values.length; i++) {
            min = min(min, values[i]);
        }
        return min;
    }

    public static Double minNullable(final Double... values) {
        Double min = null;
        for (int i = 0; i < values.length; i++) {
            min = min(min, values[i]);
        }
        return min;
    }

    public static double max(final double... values) {
        double max = values[0];
        for (int i = 1; i < values.length; i++) {
            max = max(max, values[i]);
        }
        return max;
    }

    public static Double maxNullable(final Double... values) {
        Double max = null;
        for (int i = 0; i < values.length; i++) {
            max = max(max, values[i]);
        }
        return max;
    }

    public static Boolean toBooleanNullable(final double value) {
        if (Doubles.isNaN(value)) {
            return null;
        } else if (value > 0D) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public static Boolean toBooleanNullable(final Double value) {
        if (value == null || Doubles.isNaN(value)) {
            return null;
        } else if (value > 0D) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public static boolean toBoolean(final Double value) {
        return value != null && value.doubleValue() > 0D;
    }

    public static boolean toBoolean(final double value) {
        return value > 0D;
    }

    public static double fromBoolean(final boolean value) {
        if (value) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    public static double fromBoolean(final Boolean value) {
        if (value == null) {
            return Double.NaN;
        } else if (value) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    /**
     * https://stackoverflow.com/questions/9898512/how-to-test-if-a-double-is-an-integer/9898528
     */
    public static boolean isInteger(final double value) {
        return value == Integers.checkedCastNoOverflow(value);
    }

    public static double multiply(final double value, final double otherValue) {
        return value * otherValue;
    }

    public static double subtract(final double value, final double otherValue) {
        return value - otherValue;
    }

    public static double add(final double value, final double otherValue) {
        return value + otherValue;
    }

    public static double addNaNable(final double value, final double otherValue) {
        return add(nanToZero(value), nanToZero(otherValue));
    }

    public static double subtractNaNable(final double value, final double otherValue) {
        return subtract(nanToZero(value), nanToZero(otherValue));
    }

    public static double modulo(final double value, final double otherValue) {
        return value % otherValue;
    }

    public static double sum(final double a, final double b) {
        if (isNaN(a)) {
            return b;
        } else if (isNaN(b)) {
            return a;
        } else {
            return add(a, b);
        }
    }

    public static boolean isZero(final double value) {
        return value == 0D;
    }

    public static boolean isNotZero(final double value) {
        return !isZero(value);
    }

    public static boolean isZeroRounded(final double value) {
        return compare(value, 0D) == 0;
    }

    public static boolean isNotZeroRounded(final double value) {
        return !isZeroRounded(value);
    }

    /**
     * Alias for isPositiveOrZero.
     */
    public static boolean isPositive(final double value) {
        return isPositiveOrZero(value);
    }

    public static boolean isPositiveOrZero(final double value) {
        return value >= 0;
    }

    public static boolean isPositiveNonZero(final double value) {
        return value > 0;
    }

    /**
     * Alias for isNegativeNonZero.
     */
    public static boolean isNegative(final double value) {
        return isNegativeNonZero(value);
    }

    public static boolean isNegativeNonZero(final double value) {
        return !isPositive(value);
    }

    public static boolean isNegativeOrZero(final double value) {
        return !isPositiveNonZero(value);
    }

    public static boolean isLessThan(final double value, final Double otherValue) {
        if (otherValue == null) {
            return false;
        }
        return isLessThan(value, otherValue.doubleValue());
    }

    public static boolean isLessThan(final double value, final Number otherValue) {
        if (otherValue == null) {
            return false;
        }
        return isLessThan(value, otherValue.doubleValue());
    }

    public static boolean isGreaterThan(final double value, final Double otherValue) {
        if (otherValue == null) {
            return false;
        }
        return isGreaterThan(value, otherValue.doubleValue());
    }

    public static boolean isGreaterThan(final double value, final Number otherValue) {
        if (otherValue == null) {
            return false;
        }
        return isGreaterThan(value, otherValue.doubleValue());
    }

    public static boolean isGreaterThanOrEqualTo(final double value, final Double otherValue) {
        if (otherValue == null) {
            return false;
        }
        return isGreaterThanOrEqualTo(value, otherValue.doubleValue());
    }

    public static boolean isGreaterThanOrEqualTo(final double value, final Number otherValue) {
        if (otherValue == null) {
            return false;
        }
        return isGreaterThanOrEqualTo(value, otherValue.doubleValue());
    }

    public static boolean isLessThanOrEqualTo(final double value, final Double otherValue) {
        if (otherValue == null) {
            return false;
        }
        return isLessThanOrEqualTo(value, otherValue.doubleValue());
    }

    public static boolean isLessThanOrEqualTo(final double value, final Number otherValue) {
        if (otherValue == null) {
            return false;
        }
        return isLessThanOrEqualTo(value, otherValue.doubleValue());
    }

    public static int compare(final Double a, final Double b) {
        if (a == null && b == null) {
            return 0;
        } else if (a == null) {
            return -1;
        } else if (b == null) {
            return 1;
        }
        return compare(a.intValue(), b.intValue());
    }

    public static int compare(final Double a, final double b) {
        if (a == null) {
            return -1;
        }
        return compare(a.intValue(), b);
    }

    public static int compare(final double a, final Double b) {
        if (b == null) {
            return 1;
        }
        return compare(a, b.intValue());
    }

    /**
     * NaN is treated as 0 for comparison purposes. This method is normally used for comparisons in the UI or arbitraty
     * lists.
     */
    public static int compare(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return 1;
        } else if (difference < FIRST_BELOW_ZERO) {
            return -1;
        } else if (difference == 0D) {
            return 0;
        } else if (isNaN(difference)) {
            final boolean valueNaN = isNaN(value);
            final boolean otherValueNaN = isNaN(otherValue);
            if (valueNaN && otherValueNaN) {
                return 0;
            } else if (valueNaN) {
                //treat NaN as 0
                return compare(0D, otherValue);
            } else {
                return compare(value, 0D);
            }
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

    public static boolean equals(final Double value, final Double otherValue) {
        return equals(nullToNan(value), nullToNan(otherValue));
    }

    public static boolean equals(final double value, final Double otherValue) {
        return equals(value, nullToNan(otherValue));
    }

    public static boolean equals(final Double value, final double otherValue) {
        return equals(nullToNan(value), otherValue);
    }

    /**
     * Here we use classical java logic where a comparison with NaN always results in false. Since in math you can not
     * compare an existing value with a missing one.
     */
    public static boolean equals(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return false;
        } else if (difference < FIRST_BELOW_ZERO) {
            return false;
        } else if (difference == 0D) {
            return true;
        } else if (isNaN(difference)) {
            //treat as missing
            return false;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue == roundedOther;
        }
    }

    public static boolean equalsMatrix(final double[][] a, final double[][] b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        final int length = a.length;
        if (b.length != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            final double[] vectorA = a[i];
            final double[] vectorB = b[i];
            if (!equalsVector(vectorA, vectorB)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsVector(final double[] a, final double[] b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }

        final int length = a.length;
        if (b.length != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            final double valueA = a[i];
            final double valueB = b[i];
            if (!equals(valueA, valueB)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsNaNable(final Double value, final Double otherValue) {
        return equalsNaNable(nullToNan(value), nullToNan(otherValue));
    }

    public static boolean equalsNaNable(final double value, final Double otherValue) {
        return equalsNaNable(value, nullToNan(otherValue));
    }

    public static boolean equalsNaNable(final Double value, final double otherValue) {
        return equalsNaNable(nullToNan(value), otherValue);
    }

    /**
     * NaN always loses, an existing value wins. Useful for comparison if a new takeProfit/stopLoss is better than
     * missing one.
     */
    public static boolean equalsNaNable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return false;
        } else if (difference < FIRST_BELOW_ZERO) {
            return false;
        } else if (difference == 0D) {
            return true;
        } else if (isNaN(difference)) {
            return isNaN(value) == isNaN(otherValue);
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue == roundedOther;
        }
    }

    public static Boolean equalsNullable(final Double value, final Double otherValue) {
        return equalsNullable(nullToNan(value), nullToNan(otherValue));
    }

    public static Boolean equalsNullable(final double value, final Double otherValue) {
        return equalsNullable(value, nullToNan(otherValue));
    }

    public static Boolean equalsNullable(final Double value, final double otherValue) {
        return equalsNullable(nullToNan(value), otherValue);
    }

    /**
     * Either value is NaN results in a null result. Thus comparison is propagated as MISSING. Useful in expression
     * language to deactivate missing calculations.
     */
    public static Boolean equalsNullable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return Boolean.FALSE;
        } else if (difference < FIRST_BELOW_ZERO) {
            return Boolean.FALSE;
        } else if (difference == 0D) {
            return Boolean.TRUE;
        } else if (isNaN(difference)) {
            //treat as missing
            return null;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue == roundedOther;
        }
    }

    public static boolean notEquals(final Double value, final Double otherValue) {
        return notEquals(nullToNan(value), nullToNan(otherValue));
    }

    public static boolean notEquals(final double value, final Double otherValue) {
        return notEquals(value, nullToNan(otherValue));
    }

    public static boolean notEquals(final Double value, final double otherValue) {
        return notEquals(nullToNan(value), otherValue);
    }

    /**
     * Here we use classical java logic than a comparison with NaN always results in false. Since in math you can not
     * compare an existing value with a missing one.
     */
    public static boolean notEquals(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return true;
        } else if (difference < FIRST_BELOW_ZERO) {
            return true;
        } else if (difference == 0D) {
            return false;
        } else if (isNaN(difference)) {
            //treat as missing
            return false;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue != roundedOther;
        }
    }

    public static boolean notEqualsNaNable(final Double value, final Double otherValue) {
        return notEqualsNaNable(nullToNan(value), nullToNan(otherValue));
    }

    public static boolean notEqualsNaNable(final double value, final Double otherValue) {
        return notEqualsNaNable(value, nullToNan(otherValue));
    }

    public static boolean notEqualsNaNable(final Double value, final double otherValue) {
        return notEqualsNaNable(nullToNan(value), otherValue);
    }

    /**
     * NaN always loses, an existing value wins. Useful for comparison if a new takeProfit/stopLoss is better than
     * missing one.
     */
    public static boolean notEqualsNaNable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return true;
        } else if (difference < FIRST_BELOW_ZERO) {
            return true;
        } else if (difference == 0D) {
            return false;
        } else if (isNaN(difference)) {
            return isNaN(value) != isNaN(otherValue);
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue != roundedOther;
        }
    }

    public static Boolean notEqualsNullable(final Double value, final Double otherValue) {
        return notEqualsNullable(nullToNan(value), nullToNan(otherValue));
    }

    public static Boolean notEqualsNullable(final double value, final Double otherValue) {
        return notEqualsNullable(value, nullToNan(otherValue));
    }

    public static Boolean notEqualsNullable(final Double value, final double otherValue) {
        return notEqualsNullable(nullToNan(value), otherValue);
    }

    /**
     * Either value is NaN results in a null result. Thus comparison is propagated as MISSING. Useful in expression
     * language to deactivate missing calculations.
     */
    public static Boolean notEqualsNullable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return Boolean.TRUE;
        } else if (difference < FIRST_BELOW_ZERO) {
            return Boolean.TRUE;
        } else if (difference == 0D) {
            return Boolean.FALSE;
        } else if (isNaN(difference)) {
            //treat as missing
            return null;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue != roundedOther;
        }
    }

    /**
     * Here we use classical java logic than a comparison with NaN always results in false. Since in math you can not
     * compare an existing value with a missing one.
     */
    public static boolean isGreaterThan(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return true;
        } else if (difference < FIRST_BELOW_ZERO) {
            return false;
        } else if (difference == 0D) {
            return false;
        } else if (isNaN(difference)) {
            //treat as missing
            return false;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue > roundedOther;
        }
    }

    /**
     * NaN always loses, an existing value wins. Useful for comparison if a new takeProfit/stopLoss is better than
     * missing one.
     */
    public static boolean isGreaterThanNaNable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return true;
        } else if (difference < FIRST_BELOW_ZERO) {
            return false;
        } else if (difference == 0D) {
            return false;
        } else if (isNaN(difference)) {
            final boolean valueNaN = isNaN(value);
            final boolean otherValueNaN = isNaN(otherValue);
            if (valueNaN && otherValueNaN) {
                return false;
            } else {
                return otherValueNaN;
            }
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue > roundedOther;
        }
    }

    /**
     * Either value is NaN results in a null result. Thus comparison is propagated as MISSING. Useful in expression
     * language to deactivate missing calculations.
     */
    public static Boolean isGreaterThanNullable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return Boolean.TRUE;
        } else if (difference < FIRST_BELOW_ZERO) {
            return Boolean.FALSE;
        } else if (difference == 0D) {
            return Boolean.FALSE;
        } else if (isNaN(difference)) {
            //treat as missing
            return null;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue > roundedOther;
        }
    }

    /**
     * Here we use classical java logic than a comparison with NaN always results in false. Since in math you can not
     * compare an existing value with a missing one.
     */
    public static boolean isGreaterThanOrEqualTo(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return true;
        } else if (difference < FIRST_BELOW_ZERO) {
            return false;
        } else if (difference == 0D) {
            return true;
        } else if (isNaN(difference)) {
            //treat as missing
            return false;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue >= roundedOther;
        }
    }

    /**
     * NaN always loses, an existing value wins. Useful for comparison if a new takeProfit/stopLoss is better than
     * missing one.
     */
    public static boolean isGreaterThanOrEqualToNaNable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return true;
        } else if (difference < FIRST_BELOW_ZERO) {
            return false;
        } else if (difference == 0D) {
            return true;
        } else if (isNaN(difference)) {
            final boolean valueNaN = isNaN(value);
            final boolean otherValueNaN = isNaN(otherValue);
            if (valueNaN && otherValueNaN) {
                return true;
            } else {
                return otherValueNaN;
            }
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue >= roundedOther;
        }
    }

    /**
     * Either value is NaN results in a null result. Thus comparison is propagated as MISSING. Useful in expression
     * language to deactivate missing calculations.
     */
    public static Boolean isGreaterThanOrEqualToNullable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return Boolean.TRUE;
        } else if (difference < FIRST_BELOW_ZERO) {
            return Boolean.FALSE;
        } else if (difference == 0D) {
            return Boolean.TRUE;
        } else if (isNaN(difference)) {
            //treat as missing
            return null;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue >= roundedOther;
        }
    }

    /**
     * Here we use classical java logic than a comparison with NaN always results in false. Since in math you can not
     * compare an existing value with a missing one.
     */
    public static boolean isLessThan(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return false;
        } else if (difference < FIRST_BELOW_ZERO) {
            return true;
        } else if (difference == 0D) {
            return false;
        } else if (isNaN(difference)) {
            //treat as missing
            return false;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue < roundedOther;
        }
    }

    /**
     * NaN always loses, an existing value wins. Useful for comparison if a new takeProfit/stopLoss is better than
     * missing one.
     */
    public static boolean isLessThanNaNable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return false;
        } else if (difference < FIRST_BELOW_ZERO) {
            return true;
        } else if (difference == 0D) {
            return false;
        } else if (isNaN(difference)) {
            final boolean valueNaN = isNaN(value);
            final boolean otherValueNaN = isNaN(otherValue);
            if (valueNaN && otherValueNaN) {
                return false;
            } else {
                return otherValueNaN;
            }
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue < roundedOther;
        }
    }

    /**
     * Either value is NaN results in a null result. Thus comparison is propagated as MISSING. Useful in expression
     * language to deactivate missing calculations.
     */
    public static Boolean isLessThanNullable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return Boolean.FALSE;
        } else if (difference < FIRST_BELOW_ZERO) {
            return Boolean.TRUE;
        } else if (difference == 0D) {
            return Boolean.FALSE;
        } else if (isNaN(difference)) {
            //treat as missing
            return null;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue < roundedOther;
        }
    }

    /**
     * Here we use classical java logic than a comparison with NaN always results in false. Since in math you can not
     * compare an existing value with a missing one.
     */
    public static boolean isLessThanOrEqualTo(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return false;
        } else if (difference < FIRST_BELOW_ZERO) {
            return true;
        } else if (difference == 0D) {
            return true;
        } else if (isNaN(difference)) {
            //treat as missing
            return false;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue <= roundedOther;
        }
    }

    /**
     * NaN always loses, an existing value wins. Useful for comparison if a new takeProfit/stopLoss is better than
     * missing one.
     */
    public static boolean isLessThanOrEqualToNaNable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return false;
        } else if (difference < FIRST_BELOW_ZERO) {
            return true;
        } else if (difference == 0D) {
            return true;
        } else if (isNaN(difference)) {
            final boolean valueNaN = isNaN(value);
            final boolean otherValueNaN = isNaN(otherValue);
            if (valueNaN && otherValueNaN) {
                return true;
            } else {
                return otherValueNaN;
            }
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue <= roundedOther;
        }
    }

    /**
     * Either value is NaN results in a null result. Thus comparison is propagated as MISSING. Useful in expression
     * language to deactivate missing calculations.
     */
    public static Boolean isLessThanOrEqualToNullable(final double value, final double otherValue) {
        final double difference = value - otherValue;
        if (difference > FIRST_ABOVE_ZERO) {
            return Boolean.FALSE;
        } else if (difference < FIRST_BELOW_ZERO) {
            return Boolean.TRUE;
        } else if (difference == 0D) {
            return Boolean.TRUE;
        } else if (isNaN(difference)) {
            //treat as missing
            return null;
        } else {
            final double defaultRoundedValue = round(value);
            final double roundedOther = round(otherValue);
            return defaultRoundedValue <= roundedOther;
        }
    }

    public static Double valueOfOrNull(final String s) {
        if (!Strings.isDecimal(s)) {
            return null;
        }
        try {
            return Double.valueOf(s);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    public static boolean isBetween(final double value, final double lowerBound, final double upperBound) {
        return isGreaterThanOrEqualTo(value, lowerBound) && isLessThanOrEqualTo(value, upperBound);
    }

    public static boolean isBetweenExclusive(final double value, final double lowerBound, final double upperBound) {
        return isGreaterThan(value, lowerBound) && isLessThan(value, upperBound);
    }

    public static String getSign(final double value) {
        return getSign(value, false);
    }

    public static String getSignInverted(final double value) {
        return getSign(value, true);
    }

    public static String getSign(final double value, final boolean inverted) {
        if (isPositive(value)) {
            if (!inverted) {
                return ADecimal.POSITIVE_SIGN;
            } else {
                return ADecimal.NEGATIVE_SIGN;
            }
        } else {
            if (!inverted) {
                return ADecimal.NEGATIVE_SIGN;
            } else {
                return ADecimal.POSITIVE_SIGN;
            }
        }
    }

    public static double distance(final double a, final double b) {
        return abs(a - b);
    }

    public static double[][] transposeMatrix(final double[][] matrix) {
        final int m = matrix.length;
        final int n = matrix[0].length;

        final double[][] transposedMatrix = new double[n][m];

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < m; y++) {
                transposedMatrix[x][y] = matrix[y][x];
            }
        }

        return transposedMatrix;
    }

    public static String getShortenedSuffix(final double absMax) {
        if (isNaN(absMax)) {
            return "";
        } else if (absMax > 10_000_000_000D || absMax < -10_000_000_000D) {
            return "g";
        } else if (absMax > 10_000_000D || absMax < -10_000_000D) {
            return "m";
        } else if (absMax > 10_000D || absMax < -10_000D) {
            return "k";
        } else {
            return "";
        }
    }

    public static double getShortenedValue(final double value, final double absMax) {
        if (Doubles.isNaN(absMax)) {
            return value;
        } else if (absMax > 10_000_000_000D || absMax < -10_000_000_000D) {
            return value / 10_000_000_000D;
        } else if (absMax > 10_000_000D || absMax < -10_000_000D) {
            return value / 1_000_000D;
        } else if (absMax > 10_0000D || absMax < -10_0000D) {
            return value / 1_000D;
        } else {
            return value;
        }
    }

    public static String toString(final double value) {
        return NUMBER_FORMAT.get().format(value);
    }

    public static double avg(final double min, final double max) {
        return (min + max) / 2;
    }

    public static double maxInclusiveToExclusive(final double maxInclusive) {
        return maxInclusive + Doubles.FIRST_ABOVE_ZERO;
    }

    public static double maxExclusiveToInclusive(final double maxExclusive) {
        return maxExclusive - Doubles.FIRST_ABOVE_ZERO;
    }

    public static double scaleByPowerOfTenNearReference(final double value, final double reference) {
        if (value == 0D || reference == 0D) {
            return value;
        }
        final boolean positive = isPositive(reference);
        final double r;
        if (positive) {
            r = reference;
        } else {
            r = -reference;
        }
        double v = applySignFromReference(value, r);
        double difference = abs(r - v);
        for (int tries = 0; tries < 20; tries++) {
            final double newV;
            if (r > v) {
                newV = v * 10;
            } else {
                newV = v / 10;
            }
            final double newDifference = abs(r - newV);
            if (newDifference > difference) {
                //minimum scale found
                break;
            }
            difference = newDifference;
            v = newV;
        }
        if (positive) {
            return v;
        } else {
            return -v;
        }
    }

    public static double applySignFromReference(final double value, final double reference) {
        if (Doubles.isPositive(value) != Doubles.isPositive(reference)) {
            return -value;
        } else {
            return value;
        }
    }

    public static double tryParseNaN(final String string) {
        if (!DoublesAccessor.FLOATING_POINT_PATTERN.matcher(string).matches()) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(string);
        } catch (final NumberFormatException e) {
            return Double.NaN;
        }
    }

}
