package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.internal.ABooleansStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastBooleans;
import de.invesdwin.util.math.internal.CheckedCastBooleansObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ABooleansStaticFacade", targets = {
        CheckedCastBooleans.class, CheckedCastBooleansObj.class, com.google.common.primitives.Booleans.class })
@Immutable
public final class Booleans extends ABooleansStaticFacade {

    public static final ADelegateComparator<Boolean> COMPARATOR = new ADelegateComparator<Boolean>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Boolean e) {
            return e;
        }
    };

    private Booleans() {}

    public static boolean[] toArray(final Collection<Boolean> vector) {
        if (vector == null) {
            return null;
        }
        return ABooleansStaticFacade.toArray(vector);
    }

    public static boolean[] toArrayVector(final Collection<Boolean> vector) {
        return toArray(vector);
    }

    public static boolean[][] toArrayMatrix(final List<? extends List<Boolean>> matrix) {
        if (matrix == null) {
            return null;
        }
        final boolean[][] arrayMatrix = new boolean[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Boolean> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Boolean> asList(final boolean... vector) {
        if (vector == null) {
            return null;
        } else {
            return ABooleansStaticFacade.asList(vector);
        }
    }

    public static List<Boolean> asListVector(final boolean[] vector) {
        return asList(vector);
    }

    public static List<List<Boolean>> asListMatrix(final boolean[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Boolean>> matrixAsList = new ArrayList<List<Boolean>>(matrix.length);
        for (final boolean[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static <T> boolean[][] fixInconsistentMatrixDimensions(final boolean[][] matrix) {
        return fixInconsistentMatrixDimensions(matrix, false);
    }

    public static <T> boolean[][] fixInconsistentMatrixDimensions(final boolean[][] matrix, final boolean missingValue) {
        final int rows = matrix.length;
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final boolean[] vector = matrix[i];
            if (cols != 0 && cols != vector.length) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.length);
        }
        if (!colsInconsistent) {
            return matrix;
        }
        final boolean[][] fixedMatrix = new boolean[rows][];
        for (int i = 0; i < matrix.length; i++) {
            final boolean[] vector = matrix[i];
            final boolean[] fixedVector;
            if (vector.length == cols) {
                fixedVector = vector.clone();
            } else {
                fixedVector = new boolean[cols];
                System.arraycopy(vector, 0, fixedVector, 0, vector.length);
                if (missingValue) {
                    for (int j = vector.length - 1; j < cols; j++) {
                        fixedVector[j] = missingValue;
                    }
                }
            }
            fixedMatrix[i] = fixedVector;
        }
        return fixedMatrix;
    }

    public static <T> Boolean[][] fixInconsistentMatrixDimensionsObj(final Boolean[][] matrix) {
        return fixInconsistentMatrixDimensionsObj(matrix, Boolean.FALSE);
    }

    public static <T> Boolean[][] fixInconsistentMatrixDimensionsObj(final Boolean[][] matrix, final Boolean missingValue) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue);
    }

    public static List<List<Boolean>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Boolean>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, Boolean.FALSE);
    }

    public static List<List<Boolean>> fixInconsistentMatrixDimensionsAsList(final List<? extends List<? extends Boolean>> matrix,
            final Boolean missingValue) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue);
    }

}
