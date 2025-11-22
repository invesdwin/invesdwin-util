package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.internal.ALongsStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastLongs;
import de.invesdwin.util.math.internal.CheckedCastLongsObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ALongsStaticFacade", targets = { CheckedCastLongs.class,
        CheckedCastLongsObj.class,
        com.google.common.primitives.Longs.class }, filterMethodSignatureExpressions = { ".* toArray\\(.*" })
@Immutable
public final class Longs extends ALongsStaticFacade {

    public static final long[] EMPTY_ARRAY = new long[0];
    public static final long DEFAULT_MISSING_VALUE = 0;
    public static final Long DEFAULT_MISSING_VALUE_OBJ = DEFAULT_MISSING_VALUE;
    public static final IComparator<Long> COMPARATOR = IComparator.getDefaultInstance();

    private Longs() {}

    public static long[] toArray(final Collection<? extends Number> vector) {
        if (vector == null) {
            return null;
        }
        return com.google.common.primitives.Longs.toArray(vector);
    }

    public static long[] toArrayVector(final Collection<Long> vector) {
        return toArray(vector);
    }

    public static long[][] toArrayMatrix(final List<? extends List<Long>> matrix) {
        if (matrix == null) {
            return null;
        }
        final long[][] arrayMatrix = new long[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Long> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Long> asList(final long... vector) {
        if (vector == null) {
            return null;
        } else {
            return ALongsStaticFacade.asList(vector);
        }
    }

    public static List<Long> asListVector(final long[] vector) {
        return asList(vector);
    }

    public static List<List<Long>> asListMatrix(final long[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Long>> matrixAsList = new ArrayList<List<Long>>(matrix.length);
        for (final long[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static long min(final long... values) {
        long minValue = values[0];
        for (int i = 1; i < values.length; i++) {
            minValue = min(minValue, values[i]);
        }
        return minValue;
    }

    public static Long minNullable(final Long... values) {
        Long minValue = null;
        for (int i = 0; i < values.length; i++) {
            minValue = min(minValue, values[i]);
        }
        return minValue;
    }

    public static Long min(final Long value1, final Long value2) {
        if (value1 == null) {
            return value2;
        } else if (value2 == null) {
            return value1;
        }

        if (value1 < value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static long min(final long value1, final Long value2) {
        if (value2 == null) {
            return value1;
        }

        if (value1 < value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static long min(final Long value1, final long value2) {
        if (value1 == null) {
            return value2;
        }

        if (value1 < value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static long min(final long value1, final long value2) {
        if (value1 < value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static long max(final long... values) {
        long maxValue = values[0];
        for (int i = 1; i < values.length; i++) {
            maxValue = max(maxValue, values[i]);
        }
        return maxValue;
    }

    public static Long maxNullable(final Long... values) {
        Long maxValue = null;
        for (int i = 0; i < values.length; i++) {
            maxValue = max(maxValue, values[i]);
        }
        return maxValue;
    }

    public static Long max(final Long value1, final Long value2) {
        if (value1 == null) {
            return value2;
        } else if (value2 == null) {
            return value1;
        }

        if (value1 > value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static long max(final long value1, final Long value2) {
        if (value2 == null) {
            return value1;
        }

        if (value1 > value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static long max(final Long value1, final long value2) {
        if (value1 == null) {
            return value2;
        }

        if (value1 > value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static long orElse(final Long value, final long defaultValue) {
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public static long max(final long value1, final long value2) {
        if (value1 > value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static Long between(final Long value, final Long minInclusive, final Long maxInclusive) {
        return max(min(value, maxInclusive), minInclusive);
    }

    public static long between(final long value, final long minInclusive, final long maxInclusive) {
        return max(min(value, maxInclusive), minInclusive);
    }

    public static <T> long[][] fixInconsistentMatrixDimensions(final long[][] matrix) {
        return fixInconsistentMatrixDimensions(matrix, DEFAULT_MISSING_VALUE);
    }

    public static long[][] fixInconsistentMatrixDimensions(final long[][] matrix, final long missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> long[][] fixInconsistentMatrixDimensions(final long[][] matrix, final long missingValue,
            final boolean appendMissingValues) {
        final int rows = matrix.length;
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final long[] vector = matrix[i];
            if (cols != 0 && cols != vector.length) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.length);
        }
        if (!colsInconsistent) {
            return matrix;
        }
        final long[][] fixedMatrix = new long[rows][];
        for (int i = 0; i < matrix.length; i++) {
            final long[] vector = matrix[i];
            final long[] fixedVector;
            if (vector.length == cols) {
                fixedVector = vector.clone();
            } else {
                fixedVector = new long[cols];
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

    public static <T> Long[][] fixInconsistentMatrixDimensionsObj(final Long[][] matrix) {
        return fixInconsistentMatrixDimensionsObj(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static Long[][] fixInconsistentMatrixDimensionsObj(final Long[][] matrix, final Long missingValue) {
        return fixInconsistentMatrixDimensionsObj(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> Long[][] fixInconsistentMatrixDimensionsObj(final Long[][] matrix, final Long missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue, appendMissingValues);
    }

    public static List<List<Long>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Long>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static List<List<Long>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Long>> matrix, final Long missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static List<List<Long>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Long>> matrix, final Long missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue, appendMissingValues);
    }

    public static long abs(final long value) {
        return Math.abs(value);
    }

    public static boolean isBetween(final long value, final long min, final long max) {
        return min <= value && value <= max;
    }

    public static int compare(final Long a, final Long b) {
        if (a == null && b == null) {
            return 0;
        } else if (a == null) {
            return -1;
        } else if (b == null) {
            return 1;
        }
        return compare(a.longValue(), b.longValue());
    }

    public static int compare(final Long a, final long b) {
        if (a == null) {
            return -1;
        }
        return compare(a.longValue(), b);
    }

    public static int compare(final long a, final Long b) {
        if (b == null) {
            return 1;
        }
        return compare(a, b.longValue());
    }

    public static int compare(final long a, final long b) {
        return Long.compare(a, b);
    }

    public static Long valueOfOrNull(final String s) {
        if (!Strings.isInteger(s)) {
            return null;
        }
        try {
            return Long.valueOf(s);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    public static long combine(final int first, final int second) {
        return (((long) first) << 32) | (second & 0xffffffffL);
    }

    public static int combinedExtractFirst(final long combined) {
        return (int) (combined >> 32);
    }

    public static int combinedExtractSecond(final long combined) {
        return (int) combined;
    }

}
