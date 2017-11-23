package de.invesdwin.util.math;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Objects;
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

    public static Double max(final Double first, final Double second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.max(first, second);
        }
    }

    public static Double min(final Double first, final Double second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.min(first, second);
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

    public static double divideIfNotZero(final Double dividend, final Double divisor) {
        if (dividend == null || divisor == null) {
            return 0D;
        } else {
            return divideHandlingZero(dividend.doubleValue(), divisor.doubleValue());
        }
    }

    public static double divideHandlingZero(final double dividend, final double divisor) {
        if (divisor == 0D) {
            return 0D;
        } else {
            return dividend / divisor;
        }
    }

}
