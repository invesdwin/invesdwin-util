package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.math.internal.ABytesStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastBytes;
import de.invesdwin.util.math.internal.CheckedCastBytesObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ABytesStaticFacade", targets = { CheckedCastBytes.class,
        CheckedCastBytesObj.class,
        com.google.common.primitives.Bytes.class }, filterMethodSignatureExpressions = { ".* toArray\\(.*" })
@Immutable
public final class Bytes extends ABytesStaticFacade {

    public static final byte[] EMPTY_ARRAY = new byte[0];

    public static final byte DEFAULT_MISSING_VALUE = (byte) 0;
    public static final Byte DEFAULT_MISSING_VALUE_OBJ = DEFAULT_MISSING_VALUE;
    public static final IComparator<Byte> COMPARATOR = IComparator.getDefaultInstance();

    public static final byte ONE = (byte) 1;
    public static final byte ZERO = (byte) 0;
    public static final byte MINUES_ONE = (byte) -1;

    private Bytes() {
    }

    public static byte[] toArray(final Collection<? extends Number> vector) {
        if (vector == null) {
            return null;
        }
        return com.google.common.primitives.Bytes.toArray(vector);
    }

    public static byte[] toArrayVector(final Collection<Byte> vector) {
        return toArray(vector);
    }

    public static byte[][] toArrayMatrix(final List<? extends List<Byte>> matrix) {
        if (matrix == null) {
            return null;
        }
        final byte[][] arrayMatrix = new byte[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Byte> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<java.lang.Byte> asList(final byte... vector) {
        if (vector == null) {
            return null;
        } else {
            return ABytesStaticFacade.asList(vector);
        }
    }

    public static List<Byte> asListVector(final byte[] vector) {
        return asList(vector);
    }

    public static List<List<Byte>> asListMatrix(final byte[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Byte>> matrixAsList = new ArrayList<List<Byte>>(matrix.length);
        for (final byte[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static Byte min(final Byte... times) {
        Byte minTime = null;
        for (final Byte time : times) {
            minTime = min(minTime, time);
        }
        return minTime;
    }

    public static Byte min(final Byte time1, final Byte time2) {
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

    public static Byte max(final Byte... times) {
        Byte maxTime = null;
        for (final Byte time : times) {
            maxTime = max(maxTime, time);
        }
        return maxTime;
    }

    public static Byte max(final Byte time1, final Byte time2) {
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

    public static Byte between(final Byte value, final Byte min, final Byte max) {
        return max(min(value, max), min);
    }

    public static <T> byte[][] fixInconsistentMatrixDimensions(final byte[][] matrix) {
        return fixInconsistentMatrixDimensions(matrix, DEFAULT_MISSING_VALUE);
    }

    public static byte[][] fixInconsistentMatrixDimensions(final byte[][] matrix, final byte missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> byte[][] fixInconsistentMatrixDimensions(final byte[][] matrix, final byte missingValue,
            final boolean appendMissingValues) {
        final int rows = matrix.length;
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final byte[] vector = matrix[i];
            if (cols != 0 && cols != vector.length) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.length);
        }
        if (!colsInconsistent) {
            return matrix;
        }
        final byte[][] fixedMatrix = new byte[rows][];
        for (int i = 0; i < matrix.length; i++) {
            final byte[] vector = matrix[i];
            final byte[] fixedVector;
            if (vector.length == cols) {
                fixedVector = vector.clone();
            } else {
                fixedVector = new byte[cols];
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

    public static <T> Byte[][] fixInconsistentMatrixDimensionsObj(final Byte[][] matrix) {
        return fixInconsistentMatrixDimensionsObj(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static Byte[][] fixInconsistentMatrixDimensionsObj(final Byte[][] matrix, final byte missingValue) {
        return fixInconsistentMatrixDimensionsObj(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> Byte[][] fixInconsistentMatrixDimensionsObj(final Byte[][] matrix, final Byte missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue, appendMissingValues);
    }

    public static List<List<Byte>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Byte>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, DEFAULT_MISSING_VALUE_OBJ);
    }

    public static List<List<Byte>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Byte>> matrix, final Byte missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static List<List<Byte>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends Byte>> matrix, final Byte missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue, appendMissingValues);
    }

    public static int compare(final Byte a, final Byte b) {
        if (a == null && b == null) {
            return 0;
        } else if (a == null) {
            return -1;
        } else if (b == null) {
            return 1;
        }
        return compare(a.byteValue(), b.byteValue());
    }

    public static int compare(final Byte a, final int b) {
        if (a == null) {
            return -1;
        }
        return compare(a.byteValue(), b);
    }

    public static int compare(final int a, final Byte b) {
        if (b == null) {
            return 1;
        }
        return compare(a, b.byteValue());
    }

    public static int compare(final byte a, final byte b) {
        return Byte.compare(a, b);
    }

    public static byte[] concatenate(final byte[] array1, final byte[] array2) {
        if (array1.length == 0) {
            return array2;
        } else if (array2.length == 0) {
            return array1;
        }

        final int length = array1.length + array2.length;
        final byte[] newArray = new byte[length];
        int destPos = 0;

        System.arraycopy(array1, 0, newArray, destPos, array1.length);
        destPos += array1.length;

        System.arraycopy(array2, 0, newArray, destPos, array2.length);
        destPos += array2.length;

        return newArray;
    }

    public static byte[] concatenate(final byte[]... arrays) {
        int sizedArrays = 0;
        byte[] lastSizedArray = null;

        int length = 0;
        for (int i = 0; i < arrays.length; i++) {
            final byte[] array = arrays[i];
            length += array.length;
            if (array.length > 0) {
                sizedArrays++;
                lastSizedArray = array;
            }
        }
        if (sizedArrays == 0) {
            return EMPTY_ARRAY;
        } else if (sizedArrays == 1) {
            return lastSizedArray;
        }

        final byte[] newArray = new byte[length];
        int destPos = 0;
        for (int i = 0; i < arrays.length; i++) {
            final byte[] array = arrays[i];
            System.arraycopy(array, 0, newArray, destPos, array.length);
            destPos += array.length;
        }
        return newArray;
    }

    public static byte[] subArray(final byte[] array, final int beginIndex, final int endIndex) {
        if (beginIndex == 0 && endIndex == array.length - 1) {
            return array;
        }
        final int length = endIndex - beginIndex;
        final byte[] subarray = new byte[length];
        System.arraycopy(array, beginIndex, subarray, 0, length);
        return subarray;
    }

}
