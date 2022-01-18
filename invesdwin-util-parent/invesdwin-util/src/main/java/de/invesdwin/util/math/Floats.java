package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.math.internal.AFloatsStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastFloats;
import de.invesdwin.util.math.internal.CheckedCastFloatsObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.AFloatsStaticFacade", targets = {
        CheckedCastFloats.class, CheckedCastFloatsObj.class,
        com.google.common.primitives.Floats.class }, filterMethodSignatureExpressions = { ".* toArray\\(.*" })
@Immutable
public final class Floats extends AFloatsStaticFacade {

    public static final float DEFAULT_MISSING_VALUE = 0f;
    public static final Float DEFAULT_MISSING_VALUE_OBJ = DEFAULT_MISSING_VALUE;
    public static final IComparator<Float> COMPARATOR = IComparator.getDefaultInstance();

    //CHECKSTYLE:OFF
    public static final float MAX_VALUE = Float.MAX_VALUE;
    public static final float MIN_VALUE = -Float.MAX_VALUE;
    //CHECKSTYLE:ON

    private Floats() {
    }

    public static float[] toArray(final Collection<? extends Number> vector) {
        if (vector == null) {
            return null;
        }
        return com.google.common.primitives.Floats.toArray(vector);
    }

    public static float[] toArrayVector(final Collection<Float> vector) {
        return toArray(vector);
    }

    public static float[][] toArrayMatrix(final List<? extends List<Float>> matrix) {
        if (matrix == null) {
            return null;
        }
        final float[][] arrayMatrix = new float[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Float> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Float> asList(final float... vector) {
        if (vector == null) {
            return null;
        } else {
            return AFloatsStaticFacade.asList(vector);
        }
    }

    public static List<Float> asListVector(final float[] vector) {
        return asList(vector);
    }

    public static List<List<Float>> asListMatrix(final float[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Float>> matrixAsList = new ArrayList<List<Float>>(matrix.length);
        for (final float[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static Float max(final Float first, final Float second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.max(first, second);
        }
    }

    public static Float min(final Float first, final Float second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.min(first, second);
        }
    }

    public static Float between(final Float value, final Float min, final Float max) {
        return max(min(value, max), min);
    }

    public static <T> float[][] fixInconsistentMatrixDimensions(final float[][] matrix) {
        return fixInconsistentMatrixDimensions(matrix, DEFAULT_MISSING_VALUE);
    }

    public static float[][] fixInconsistentMatrixDimensions(final float[][] matrix, final float missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> float[][] fixInconsistentMatrixDimensions(final float[][] matrix, final float missingValue,
            final boolean appendMissingValues) {
        final int rows = matrix.length;
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final float[] vector = matrix[i];
            if (cols != 0 && cols != vector.length) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.length);
        }
        if (!colsInconsistent) {
            return matrix;
        }
        final float[][] fixedMatrix = new float[rows][];
        for (int i = 0; i < matrix.length; i++) {
            final float[] vector = matrix[i];
            final float[] fixedVector;
            if (vector.length == cols) {
                fixedVector = vector.clone();
            } else {
                fixedVector = new float[cols];
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

    public static <T> Float[][] fixInconsistentMatrixDimensionsObj(final Float[][] matrix) {
        return fixInconsistentMatrixDimensionsObj(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static Float[][] fixInconsistentMatrixDimensionsObj(final Float[][] matrix, final Float missingValue) {
        return fixInconsistentMatrixDimensionsObj(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> Float[][] fixInconsistentMatrixDimensionsObj(final Float[][] matrix, final Float missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue, appendMissingValues);
    }

    public static List<List<Float>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Float>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static List<List<Float>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Float>> matrix, final Float missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static List<List<Float>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Float>> matrix, final Float missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue, appendMissingValues);
    }

    public static float abs(final float value) {
        return Math.abs(value);
    }
}
