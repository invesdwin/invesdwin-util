package de.invesdwin.util.math;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.internal.AIntegersStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastIntegers;
import de.invesdwin.util.math.internal.CheckedCastIntegersObj;
import de.invesdwin.util.math.statistics.RunningMedian;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.AIntegersStaticFacade", targets = {
        CheckedCastIntegers.class, CheckedCastIntegersObj.class,
        com.google.common.primitives.Ints.class }, filterMethodSignatureExpressions = { ".* toArray\\(.*" })
@Immutable
public final class Integers extends AIntegersStaticFacade {

    public static final int[] EMPTY_ARRAY = new int[0];
    public static final Integer[] EMPTY_ARRAY_OBJ = new Integer[0];
    public static final int DEFAULT_MISSING_VALUE = 0;
    public static final Integer DEFAULT_MISSING_VALUE_OBJ = DEFAULT_MISSING_VALUE;
    public static final IComparator<Integer> COMPARATOR = IComparator.getDefaultInstance();

    private Integers() {}

    public static int[] toArray(final Collection<? extends Number> vector) {
        if (vector == null) {
            return null;
        }
        return com.google.common.primitives.Ints.toArray(vector);
    }

    public static int[] toArrayVector(final Collection<Integer> vector) {
        return toArray(vector);
    }

    public static int[][] toArrayMatrix(final List<? extends List<Integer>> matrix) {
        if (matrix == null) {
            return null;
        }
        final int[][] arrayMatrix = new int[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Integer> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Integer> asList(final int... vector) {
        if (vector == null) {
            return null;
        } else {
            return AIntegersStaticFacade.asList(vector);
        }
    }

    public static List<Integer> asListVector(final int[] vector) {
        return asList(vector);
    }

    public static List<List<Integer>> asListMatrix(final int[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Integer>> matrixAsList = new ArrayList<List<Integer>>(matrix.length);
        for (final int[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static int max(final int first, final Integer second) {
        if (second == null) {
            return first;
        } else {
            return max(first, second.intValue());
        }
    }

    public static int max(final Integer first, final int second) {
        if (first == null) {
            return second;
        } else {
            return max(first.intValue(), second);
        }
    }

    public static Integer max(final Integer first, final Integer second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.max(first, second);
        }
    }

    public static int max(final int first, final int second) {
        return Math.max(first, second);
    }

    public static int min(final int first, final int second) {
        return Math.min(first, second);
    }

    public static int min(final int first, final Integer second) {
        if (second == null) {
            return first;
        } else {
            return min(first, second.intValue());
        }
    }

    public static int min(final Integer first, final int second) {
        if (first == null) {
            return second;
        } else {
            return min(first.intValue(), second);
        }
    }

    public static Integer min(final Integer first, final Integer second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.min(first, second);
        }
    }

    public static int avg(final int first, final int second) {
        final long sum = (long) first + (long) second;
        return (int) sum / 2;
    }

    public static int avg(final Integer... values) {
        long sum = 0;
        for (int i = 0; i < values.length; i++) {
            final Integer value = values[i];
            sum += value;
        }
        return (int) (sum / values.length);
    }

    public static int avg(final int... values) {
        long sum = 0;
        for (int i = 0; i < values.length; i++) {
            final int value = values[i];
            sum += value;
        }
        return (int) (sum / values.length);
    }

    public static int avg(final Collection<Integer> values) {
        if (values instanceof List) {
            return avg((List<Integer>) values);
        }
        long sum = 0;
        for (final Integer value : values) {
            sum += value;
        }
        return (int) (sum / values.size());
    }

    public static int avg(final List<Integer> values) {
        long sum = 0;
        for (int i = 0; i < values.size(); i++) {
            final Integer value = values.get(i);
            sum += value;
        }
        return (int) (sum / values.size());
    }

    public static int sum(final Iterable<Integer> values) {
        if (values instanceof List) {
            return sum((List<Integer>) values);
        }
        int sum = 0;
        for (final Integer value : values) {
            sum += value;
        }
        return sum;
    }

    public static int sum(final List<Integer> values) {
        int sum = 0;
        for (int i = 0; i < values.size(); i++) {
            final Integer value = values.get(i);
            sum += value;
        }
        return sum;
    }

    public static Integer median(final Collection<Integer> values) {
        if (values instanceof List) {
            return median((List<Integer>) values);
        }
        final RunningMedian median = new RunningMedian(values.size());
        for (final Integer value : values) {
            median.add(Doubles.checkedCastObj(value));
        }
        return checkedCastObj(median.getMedian());
    }

    public static Integer median(final List<Integer> values) {
        final RunningMedian median = new RunningMedian(values.size());
        for (int i = 0; i < values.size(); i++) {
            final Integer value = values.get(i);
            median.add(Doubles.checkedCastObj(value));
        }
        return checkedCastObj(median.getMedian());
    }

    public static Integer maxNullable(final Integer... values) {
        Integer maxValue = null;
        for (int i = 0; i < values.length; i++) {
            final Integer value = values[i];
            maxValue = max(maxValue, value);
        }
        return maxValue;
    }

    public static int max(final int... values) {
        int maxValue = values[0];
        for (int i = 1; i < values.length; i++) {
            final int value = values[i];
            maxValue = max(maxValue, value);
        }
        return maxValue;
    }

    public static Integer max(final Iterable<Integer> values) {
        if (values instanceof List) {
            return max((List<Integer>) values);
        }
        Integer max = null;
        for (final Integer value : values) {
            max = max(max, value);
        }
        return max;
    }

    public static Integer minNullable(final Integer... values) {
        Integer minValue = null;
        for (int i = 0; i < values.length; i++) {
            final Integer value = values[i];
            minValue = min(minValue, value);
        }
        return minValue;
    }

    public static int min(final int... values) {
        int minValue = values[0];
        for (int i = 1; i < values.length; i++) {
            final int value = values[i];
            minValue = min(minValue, value);
        }
        return minValue;
    }

    public static Integer min(final Iterable<Integer> values) {
        if (values instanceof List) {
            return min((List<Integer>) values);
        }
        Integer min = null;
        for (final Integer value : values) {
            min = min(min, value);
        }
        return min;
    }

    public static Integer max(final List<Integer> values) {
        Integer max = null;
        for (int i = 0; i < values.size(); i++) {
            final Integer value = values.get(i);
            max = max(max, value);
        }
        return max;
    }

    public static Integer min(final List<Integer> values) {
        Integer min = null;
        for (int i = 0; i < values.size(); i++) {
            final Integer value = values.get(i);
            min = min(min, value);
        }
        return min;
    }

    public static Integer between(final Integer value, final Integer minInclusive, final Integer maxInclusive) {
        return max(min(value, maxInclusive), minInclusive);
    }

    public static Integer between(final Integer value, final int minInclusive, final int maxInclusive) {
        return max(min(value, maxInclusive), minInclusive);
    }

    public static int between(final int value, final int minInclusive, final int maxInclusive) {
        return max(min(value, maxInclusive), minInclusive);
    }

    public static <T> int[][] fixInconsistentMatrixDimensions(final int[][] matrix) {
        return fixInconsistentMatrixDimensions(matrix, DEFAULT_MISSING_VALUE);
    }

    public static int[][] fixInconsistentMatrixDimensions(final int[][] matrix, final int missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> int[][] fixInconsistentMatrixDimensions(final int[][] matrix, final int missingValue,
            final boolean appendMissingValues) {
        final int rows = matrix.length;
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final int[] vector = matrix[i];
            if (cols != 0 && cols != vector.length) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.length);
        }
        if (!colsInconsistent) {
            return matrix;
        }
        final int[][] fixedMatrix = new int[rows][];
        for (int i = 0; i < matrix.length; i++) {
            final int[] vector = matrix[i];
            final int[] fixedVector;
            if (vector.length == cols) {
                fixedVector = vector.clone();
            } else {
                fixedVector = new int[cols];
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

    public static <T> Integer[][] fixInconsistentMatrixDimensionsObj(final Integer[][] matrix) {
        return fixInconsistentMatrixDimensionsObj(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static Integer[][] fixInconsistentMatrixDimensionsObj(final Integer[][] matrix, final Integer missingValue) {
        return fixInconsistentMatrixDimensionsObj(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> Integer[][] fixInconsistentMatrixDimensionsObj(final Integer[][] matrix,
            final Integer missingValue, final boolean appendMissingValue) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue, appendMissingValue);
    }

    public static List<List<Integer>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Integer>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static List<List<Integer>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Integer>> matrix, final Integer missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static List<List<Integer>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Integer>> matrix, final Integer missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue, appendMissingValues);
    }

    public static int abs(final int value) {
        return Math.abs(value);
    }

    public static Boolean toBooleanNullable(final int value) {
        if (value > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public static boolean toBoolean(final int value) {
        return value > 0;
    }

    public static int fromBoolean(final boolean value) {
        if (value) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int fromBoolean(final Boolean value) {
        if (Booleans.isTrue(value)) {
            return 1;
        } else {
            return 0;
        }
    }

    public static boolean notEquals(final int a, final int b) {
        return a != b;
    }

    public static boolean isGreaterThan(final int a, final int b) {
        return a > b;
    }

    public static boolean isGreaterThanOrEqualTo(final int a, final int b) {
        return a >= b;
    }

    public static boolean equals(final int a, final int b) {
        return a == b;
    }

    public static boolean isLessThanOrEqualTo(final int a, final int b) {
        return a <= b;
    }

    public static boolean isLessThan(final int a, final int b) {
        return a < b;
    }

    public static int add(final double value, final double otherValue) {
        return checkedCastNoOverflow(Doubles.add(value, otherValue));
    }

    public static int add(final int value, final int otherValue) {
        return checkedCastNoOverflow(Doubles.add(value, otherValue));
    }

    public static int subtract(final int value, final int otherValue) {
        return value - otherValue;
    }

    public static int subtract(final double value, final double otherValue) {
        return checkedCastNoOverflow(Doubles.subtract(value, otherValue));
    }

    public static int multiply(final double value, final double otherValue) {
        return checkedCastNoOverflow(Doubles.multiply(value, otherValue));
    }

    public static int multiply(final int value, final int otherValue) {
        return checkedCastNoOverflow(Doubles.multiply(value, otherValue));
    }

    public static int divide(final int a, final int b) {
        if (b == 0) {
            return 0;
        } else {
            return a / b;
        }
    }

    public static int divide(final double value, final double otherValue) {
        return checkedCastNoOverflow(Doubles.divide(value, otherValue));
    }

    public static int modulo(final int a, final int b) {
        return a % b;
    }

    public static int modulo(final double value, final double otherValue) {
        return checkedCastNoOverflow(Doubles.modulo(value, otherValue));
    }

    public static int pow(final double value, final double otherValue) {
        return checkedCastNoOverflow(Doubles.pow(value, otherValue));
    }

    public static int pow(final int value, final int otherValue) {
        return checkedCastNoOverflow(Doubles.pow(value, otherValue));
    }

    public static int nullToZero(final Integer value) {
        if (value == null) {
            return 0;
        } else {
            return value.intValue();
        }
    }

    public static int compare(final Integer a, final Integer b) {
        if (a == null && b == null) {
            return 0;
        } else if (a == null) {
            return -1;
        } else if (b == null) {
            return 1;
        }
        return compare(a.intValue(), b.intValue());
    }

    public static int compare(final Integer a, final int b) {
        if (a == null) {
            return -1;
        }
        return compare(a.intValue(), b);
    }

    public static int compare(final int a, final Integer b) {
        if (b == null) {
            return 1;
        }
        return compare(a, b.intValue());
    }

    public static int compare(final int a, final int b) {
        return Integer.compare(a, b);
    }

    public static Integer valueOfOrNull(final String s) {
        if (!Strings.isInteger(s)) {
            return null;
        }
        try {
            return Integer.valueOf(s);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    public static int roundToStep(final int value, final int step) {
        return (int) Doubles.roundToStep(value, step);
    }

    public static int roundToStep(final int value, final int step, final RoundingMode roundingMode) {
        return (int) Doubles.roundToStep(value, step, roundingMode);
    }

    public static int maxInclusiveToExclusive(final int maxInclusive) {
        return maxInclusive + 1;
    }

    public static int maxExclusiveToInclusive(final int maxExclusive) {
        return maxExclusive - 1;
    }

}
