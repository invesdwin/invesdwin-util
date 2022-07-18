package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.CharUtils;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.math.internal.ACharactersStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastCharacters;
import de.invesdwin.util.math.internal.CheckedCastCharactersObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ACharactersStaticFacade", targets = {
        CheckedCastCharacters.class, CheckedCastCharactersObj.class, com.google.common.primitives.Chars.class,
        CharUtils.class }, filterSeeMethodSignatures = { "com.google.common.primitives.Chars#compare(char, char)" })

@Immutable
public final class Characters extends ACharactersStaticFacade {

    public static final char DEFAULT_MISSING_VALUE = (char) 0;
    public static final Character DEFAULT_MISSING_VALUE_OBJ = DEFAULT_MISSING_VALUE;
    public static final IComparator<Character> COMPARATOR = IComparator.getDefaultInstance();

    private Characters() {
    }

    public static char[] toArray(final Collection<Character> vector) {
        if (vector == null) {
            return null;
        }
        return ACharactersStaticFacade.toArray(vector);
    }

    public static char[] toArrayVector(final Collection<Character> vector) {
        return toArray(vector);
    }

    public static char[][] toArrayMatrix(final List<? extends List<Character>> matrix) {
        if (matrix == null) {
            return null;
        }
        final char[][] arrayMatrix = new char[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Character> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Character> asList(final char... vector) {
        if (vector == null) {
            return null;
        } else {
            return ACharactersStaticFacade.asList(vector);
        }
    }

    public static List<Character> asListVector(final char[] vector) {
        return asList(vector);
    }

    public static List<List<Character>> asListMatrix(final char[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Character>> matrixAsList = new ArrayList<List<Character>>(matrix.length);
        for (final char[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static Character min(final Character... times) {
        Character minTime = null;
        for (final Character time : times) {
            minTime = min(minTime, time);
        }
        return minTime;
    }

    public static Character min(final Character time1, final Character time2) {
        if (time1 == null) {
            return time2;
        } else if (time2 == null) {
            return time1;
        }

        if (time1 < time2) {
            return time1;
        } else {
            return time2;
        }
    }

    public static Character max(final Character... times) {
        Character maxTime = null;
        for (final Character time : times) {
            maxTime = max(maxTime, time);
        }
        return maxTime;
    }

    public static Character max(final Character time1, final Character time2) {
        if (time1 == null) {
            return time2;
        } else if (time2 == null) {
            return time1;
        }

        if (time1 > time2) {
            return time1;
        } else {
            return time2;
        }
    }

    public static Character between(final Character value, final Character min, final Character max) {
        return max(min(value, max), min);
    }

    public static <T> char[][] fixInconsistentMatrixDimensions(final char[][] matrix) {
        return fixInconsistentMatrixDimensions(matrix, DEFAULT_MISSING_VALUE);
    }

    public static char[][] fixInconsistentMatrixDimensions(final char[][] matrix, final char missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> char[][] fixInconsistentMatrixDimensions(final char[][] matrix, final char missingValue,
            final boolean appendMissingValues) {
        final int rows = matrix.length;
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final char[] vector = matrix[i];
            if (cols != 0 && cols != vector.length) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.length);
        }
        if (!colsInconsistent) {
            return matrix;
        }
        final char[][] fixedMatrix = new char[rows][];
        for (int i = 0; i < matrix.length; i++) {
            final char[] vector = matrix[i];
            final char[] fixedVector;
            if (vector.length == cols) {
                fixedVector = vector.clone();
            } else {
                fixedVector = new char[cols];
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

    public static <T> Character[][] fixInconsistentMatrixDimensionsObj(final Character[][] matrix) {
        return fixInconsistentMatrixDimensionsObj(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static Character[][] fixInconsistentMatrixDimensionsObj(final Character[][] matrix,
            final Character missingValue) {
        return fixInconsistentMatrixDimensionsObj(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> Character[][] fixInconsistentMatrixDimensionsObj(final Character[][] matrix,
            final Character missingValue, final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue, appendMissingValues);
    }

    public static List<List<Character>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Character>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static List<List<Character>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Character>> matrix, final Character missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static List<List<Character>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Character>> matrix, final Character missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue, appendMissingValues);
    }

    public static int compare(final Character a, final Character b) {
        if (a == null && b == null) {
            return 0;
        } else if (a == null) {
            return -1;
        } else if (b == null) {
            return 1;
        }
        return compare(a.charValue(), b.charValue());
    }

    public static int compare(final Character a, final char b) {
        if (a == null) {
            return -1;
        }
        return compare(a.charValue(), b);
    }

    public static int compare(final char a, final Character b) {
        if (b == null) {
            return 1;
        }
        return compare(a, b.charValue());
    }

    public static int compare(final char a, final char b) {
        return Character.compare(a, b);
    }

}
