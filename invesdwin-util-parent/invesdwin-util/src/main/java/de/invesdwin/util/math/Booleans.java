package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.internal.ABooleansStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastBooleans;
import de.invesdwin.util.math.internal.CheckedCastBooleansObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ABooleansStaticFacade", targets = {
        CheckedCastBooleans.class, CheckedCastBooleansObj.class, com.google.common.primitives.Booleans.class,
        org.apache.commons.lang3.BooleanUtils.class }, filterSeeMethodSignatures = {
                "org.apache.commons.lang3.BooleanUtils#compare(boolean, boolean)" })
@Immutable
public final class Booleans extends ABooleansStaticFacade {

    public static final boolean DEFAULT_MISSING_VALUE = false;
    public static final Boolean DEFAULT_MISSING_VALUE_OBJ = DEFAULT_MISSING_VALUE;
    public static final IComparator<Boolean> COMPARATOR = IComparator.getDefaultInstance();
    public static final int BYTES = 1;
    public static final boolean[] EMPTY_ARRAY = new boolean[0];
    public static final Boolean[] EMPTY_ARRAY_OBJ = new Boolean[0];

    public static final String TRUE_STR = "true";
    public static final String FALSE_STR = "false";

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
        return fixInconsistentMatrixDimensions(matrix, DEFAULT_MISSING_VALUE);
    }

    public static boolean[][] fixInconsistentMatrixDimensions(final boolean[][] matrix, final boolean missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> boolean[][] fixInconsistentMatrixDimensions(final boolean[][] matrix, final boolean missingValue,
            final boolean appendMissingValues) {
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

    public static <T> Boolean[][] fixInconsistentMatrixDimensionsObj(final Boolean[][] matrix) {
        return fixInconsistentMatrixDimensionsObj(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static Boolean[][] fixInconsistentMatrixDimensionsObj(final Boolean[][] matrix, final Boolean missingValue) {
        return fixInconsistentMatrixDimensionsObj(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> Boolean[][] fixInconsistentMatrixDimensionsObj(final Boolean[][] matrix,
            final Boolean missingValue, final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue, appendMissingValues);
    }

    public static List<List<Boolean>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Boolean>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static List<List<Boolean>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Boolean>> matrix, final Boolean missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static List<List<Boolean>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Boolean>> matrix, final Boolean missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue, appendMissingValues);
    }

    public static boolean nullToFalse(final Boolean value) {
        return isTrue(value);
    }

    public static boolean nullToFalse(final Double value) {
        return nullToFalse(checkedCastObj(value));
    }

    public static boolean nullToFalse(final Decimal value) {
        return nullToFalse(checkedCastObj(value));
    }

    public static Boolean xor(final Boolean value1, final Boolean value2) {
        if (value1 == null) {
            return value2;
        } else if (value2 == null) {
            return value1;
        }
        if (xor(value1.booleanValue(), value2.booleanValue())) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public static boolean xor(final boolean value1, final boolean value2) {
        return value1 ^ value2;
    }

    public static boolean isTrue(final Boolean value) {
        return value != null && value.booleanValue();
    }

    public static boolean isFalse(final Boolean value) {
        return value != null && !value.booleanValue();
    }

    public static boolean isNotTrue(final Boolean value) {
        return value == null || !value.booleanValue();
    }

    public static boolean isNotFalse(final Boolean value) {
        return value == null || value.booleanValue();
    }

    public static boolean isTrue(final boolean value) {
        return value;
    }

    public static boolean isFalse(final boolean value) {
        return !value;
    }

    public static boolean isNotTrue(final boolean value) {
        return !value;
    }

    public static boolean isNotFalse(final boolean value) {
        return value;
    }

    public static int compare(final Boolean a, final Boolean b) {
        if (a == null && b == null) {
            return 0;
        } else if (a == null) {
            return -1;
        } else if (b == null) {
            return 1;
        }
        return compare(a.booleanValue(), b.booleanValue());
    }

    public static int compare(final Boolean a, final boolean b) {
        if (a == null) {
            return -1;
        }
        return compare(a.booleanValue(), b);
    }

    public static int compare(final boolean a, final Boolean b) {
        if (b == null) {
            return 1;
        }
        return compare(a, b.booleanValue());
    }

    public static int compare(final boolean a, final boolean b) {
        return Boolean.compare(a, b);
    }

}
