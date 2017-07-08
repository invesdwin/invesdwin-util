package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.internal.AShortsStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastShorts;
import de.invesdwin.util.math.internal.CheckedCastShortsObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.AShortsStaticFacade", targets = {
        CheckedCastShorts.class, CheckedCastShortsObj.class, com.google.common.primitives.Shorts.class })
@Immutable
public final class Shorts extends AShortsStaticFacade {

    public static final ADelegateComparator<Short> COMPARATOR = new ADelegateComparator<Short>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Short e) {
            return e;
        }
    };

    private Shorts() {}

    public static short[] toArray(final Collection<? extends Number> vector) {
        if (vector == null) {
            return null;
        }
        return AShortsStaticFacade.toArray(vector);
    }

    public static short[] toArrayVector(final Collection<Short> vector) {
        return toArray(vector);
    }

    public static short[][] toArrayMatrix(final List<? extends List<Short>> matrix) {
        if (matrix == null) {
            return null;
        }
        final short[][] arrayMatrix = new short[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Short> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Short> asList(final short... vector) {
        if (vector == null) {
            return null;
        } else {
            return AShortsStaticFacade.asList(vector);
        }
    }

    public static List<Short> asListVector(final short[] vector) {
        return asList(vector);
    }

    public static List<List<Short>> asListMatrix(final short[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Short>> matrixAsList = new ArrayList<List<Short>>(matrix.length);
        for (final short[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static Short min(final Short... values) {
        Short minValue = null;
        for (final Short value : values) {
            minValue = min(minValue, value);
        }
        return minValue;
    }

    public static Short min(final Short value1, final Short value2) {
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

    public static Short max(final Short... values) {
        Short maxValue = null;
        for (final Short value : values) {
            maxValue = max(maxValue, value);
        }
        return maxValue;
    }

    public static Short max(final Short value1, final Short value2) {
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

    public static Short between(final Short value, final Short min, final Short max) {
        return max(min(value, max), min);
    }

    public static <T> short[][] fixInconsistentMatrixDimensions(final short[][] matrix) {
        return fixInconsistentMatrixDimensions(matrix, (short) 0);
    }

    public static <T> short[][] fixInconsistentMatrixDimensions(final short[][] matrix, final short missingValue) {
        final int rows = matrix.length;
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final short[] vector = matrix[i];
            if (cols != 0 && cols != vector.length) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.length);
        }
        if (!colsInconsistent) {
            return matrix;
        }
        final short[][] fixedMatrix = new short[rows][];
        for (int i = 0; i < matrix.length; i++) {
            final short[] vector = matrix[i];
            final short[] fixedVector;
            if (vector.length == cols) {
                fixedVector = vector.clone();
            } else {
                fixedVector = new short[cols];
                System.arraycopy(vector, 0, fixedVector, 0, vector.length);
                if (missingValue != 0) {
                    for (int j = vector.length - 1; j < cols; j++) {
                        fixedVector[j] = missingValue;
                    }
                }
            }
            fixedMatrix[i] = fixedVector;
        }
        return fixedMatrix;
    }

    public static <T> Short[][] fixInconsistentMatrixDimensionsObj(final Short[][] matrix) {
        return fixInconsistentMatrixDimensionsObj(matrix, (short) 0);
    }

    public static <T> Short[][] fixInconsistentMatrixDimensionsObj(final Short[][] matrix, final Short missingValue) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue);
    }

    public static List<List<Short>> fixInconsistentMatrixDimensionsAsList(final List<? extends List<? extends Short>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, (short) 0);
    }

    public static List<List<Short>> fixInconsistentMatrixDimensionsAsList(final List<? extends List<? extends Short>> matrix,
            final Short missingValue) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue);
    }

}
