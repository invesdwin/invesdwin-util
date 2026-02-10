package de.invesdwin.util.math;

import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import javax.annotation.concurrent.Immutable;

import org.jspecify.annotations.Nullable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.primitive.util.BucketHashUtil;
import de.invesdwin.util.collections.primitive.util.DirectByteArrayAccess;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.math.internal.ABytesStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastBytes;
import de.invesdwin.util.math.internal.CheckedCastBytesObj;
import jakarta.validation.constraints.PositiveOrZero;

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

    private Bytes() {}

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

    public static Byte minNullable(final Byte... values) {
        Byte minValue = null;
        for (int i = 0; i < values.length; i++) {
            final Byte value = values[i];
            minValue = min(minValue, value);
        }
        return minValue;
    }

    public static byte min(final byte... values) {
        byte minValue = values[0];
        for (int i = 1; i < values.length; i++) {
            final byte value = values[i];
            minValue = min(minValue, value);
        }
        return minValue;
    }

    public static Byte min(final Byte value1, final Byte value2) {
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

    public static byte min(final byte value1, final Byte value2) {
        if (value2 == null) {
            return value1;
        }

        if (value1 < value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static byte min(final Byte value1, final byte value2) {
        if (value1 == null) {
            return value2;
        }

        if (value1 < value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static byte min(final byte value1, final byte value2) {
        if (value1 < value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static Byte maxNullable(final Byte... values) {
        Byte maxValue = null;
        for (int i = 0; i < values.length; i++) {
            final Byte value = values[i];
            maxValue = max(maxValue, value);
        }
        return maxValue;
    }

    public static byte max(final byte... values) {
        byte maxValue = values[0];
        for (int i = 1; i < values.length; i++) {
            final byte value = values[i];
            maxValue = max(maxValue, value);
        }
        return maxValue;
    }

    public static Byte max(final Byte value1, final Byte value2) {
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

    public static byte max(final byte value1, final Byte value2) {
        if (value2 == null) {
            return value1;
        }

        if (value1 > value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static byte max(final Byte value1, final byte value2) {
        if (value1 == null) {
            return value2;
        }

        if (value1 > value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static byte max(final byte value1, final byte value2) {
        if (value1 > value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static Byte between(final Byte value, final Byte minInclusive, final Byte maxInclusive) {
        return max(min(value, maxInclusive), minInclusive);
    }

    public static byte between(final byte value, final byte minInclusive, final byte maxInclusive) {
        return max(min(value, maxInclusive), minInclusive);
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

    public static byte[] concat(final byte[] array1, final byte[] array2) {
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

    public static byte[] concat(final byte[]... arrays) {
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
        final int length = endIndex - beginIndex;
        return subArrayLength(array, beginIndex, length);
    }

    public static byte[] subArrayLength(final byte[] array, final int index, final int length) {
        if (index == 0 && length == array.length) {
            return array;
        }
        final byte[] subarray = new byte[length];
        System.arraycopy(array, index, subarray, 0, length);
        return subarray;
    }

    /**
     * Taken from com.password4j.Utils
     */
    public static byte[] fromCharSequenceToBytes(final CharSequence charSequence) {
        if (charSequence == null) {
            return new byte[0];
        }
        final CharsetEncoder encoder = Charsets.DEFAULT.newEncoder();
        final int length = charSequence.length();
        final int arraySize = scale(length, encoder.maxBytesPerChar());
        final byte[] result = new byte[arraySize];
        if (length == 0) {
            return result;
        } else {
            char[] charArray;
            if (charSequence instanceof String) {
                charArray = ((String) charSequence).toCharArray();
            } else {
                charArray = fromCharSequenceToChars(charSequence);
            }

            charArray = Arrays.copyOfRange(charArray, 0, length);

            encoder.onMalformedInput(CodingErrorAction.REPLACE)
                    .onUnmappableCharacter(CodingErrorAction.REPLACE)
                    .reset();

            final java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.wrap(result);
            final CharBuffer charBuffer = CharBuffer.wrap(charArray, 0, length);

            encoder.encode(charBuffer, byteBuffer, true);
            encoder.flush(byteBuffer);

            return Arrays.copyOf(result, byteBuffer.position());
        }
    }

    private static int scale(final int initialLength, final float bytesPerChar) {
        return (int) ((double) initialLength * (double) bytesPerChar);
    }

    private static char[] fromCharSequenceToChars(final CharSequence charSequence) {
        if (charSequence == null || charSequence.length() == 0) {
            return new char[0];
        }
        final char[] result = new char[charSequence.length()];
        for (int i = 0; i < charSequence.length(); i++) {
            result[i] = charSequence.charAt(i);
        }

        return result;
    }

    public static byte[] safe(final byte @Nullable [] b) {
        return b != null ? b : EMPTY_ARRAY;
    }

    public static boolean isEmpty(final byte @Nullable [] b) {
        return b == null || b.length <= 0;
    }

    public static boolean nonEmpty(final byte @Nullable [] b) {
        return b != null && b.length > 0;
    }

    public static int len(final byte @Nullable [] b) {
        return b != null ? b.length : 0;
    }

    /// Simple bytes (from ints)
    public static byte[] bytes(final int... elements) {
        final int len = elements != null ? elements.length : 0;
        final byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[i] = (byte) elements[i];
        }
        return b;
    }

    /**
     * A common case of copying arrays from start to [start .. min(len1,len2)) Used when increasing or decreasing the
     * array length.
     * 
     * @see System#arraycopy(Object, int, Object, int, int)
     * @see java.util.Arrays#copyOf
     * @see java.util.Arrays#copyOfRange
     * @see java.util.Arrays#copyOfRangeInt
     * @see org.apache.commons.lang3.ArrayUtils#addAll
     * @see com.google.common.collect.ObjectArrays#concat
     * @see it.unimi.dsi.fastutil.objects.ObjectArrays#grow
     * @see it.unimi.dsi.fastutil.objects.ObjectArrays#trim
     */
    public static <A> A arraycopy(@Nullable final A src, final A dst, @PositiveOrZero final int len) {
        if (src != null) {
            System.arraycopy(src, 0, dst, 0, len);
        }
        return dst;
    }

    /**
     * Similar to JDK and FastUtil, but fixes mistakes and doesn't clone.
     * 
     * @see Arrays#copyOfRange(byte[], int, int)
     * @see it.unimi.dsi.fastutil.bytes.ByteArrays#copy(byte[], int, int)
     */
    public static byte[] copyOrSame(final byte @Nullable [] src, @PositiveOrZero final int from,
            @PositiveOrZero final int toNewLen) {
        if (src == null || (from <= 0 && toNewLen >= src.length)) {
            return src;
        }

        int tail = src.length - from;
        if (tail > toNewLen) {
            tail = toNewLen;
        }
        final byte[] b = new byte[tail];
        System.arraycopy(src, from, b, 0, tail);
        return b;
    }

    /**
     * Similar to {@link java.util.Arrays#setAll(int[], IntUnaryOperator)} but for byte and returns argument-array (e.g.
     * newly created, no extra line with assignment) One argument version with index.
     * 
     * @see #setAll(byte[], IntBinaryOperator)
     */
    public static byte[] setAll(final byte @Nullable [] array, final IntUnaryOperator generator) {
        if (array == null) {
            return EMPTY_ARRAY;
        }

        for (int i = 0, len = array.length; i < len; i++) {
            final int b = generator.applyAsInt(i);
            if (b < Short.MIN_VALUE) {
                break;
            } // pseudo command `break`
            if (b > Short.MAX_VALUE) {
                continue;
            } // skip
            array[i] = (byte) b;
        }
        return array;
    }

    /**
     * Similar to {@link java.util.Arrays#setAll(int[], IntUnaryOperator)} but for byte and returns argument-array (e.g.
     * newly created, no extra line with assignment) Two argument version with index and current byte.
     * 
     * @see #setAll(byte[], IntUnaryOperator)
     */
    public static byte[] setAll(final byte @Nullable [] array, final IntBinaryOperator modifier) {
        if (array == null) {
            return EMPTY_ARRAY;
        }

        for (int i = 0, len = array.length; i < len; i++) {
            final int b = modifier.applyAsInt(i, array[i]);
            if (b < Short.MIN_VALUE) {
                break;
            } // pseudo command `break`
            if (b > Short.MAX_VALUE) {
                continue;
            } // skip
            array[i] = (byte) b;
        }
        return array;
    }

    /**
     * Для работы с bytes лучше ByteBuf (netty, vert.x, oIo) или Input/OutputStream
     * 
     * @see Arrays#copyOf
     * @see Arrays#copyOfRange
     * @see Arrays#copyOfRangeByte
     * @see JSystem#arraycopy(Object, Object, int)
     * @see #concatClone(byte[], byte[])
     * @see JBuffer
     * @see it.unimi.dsi.fastutil.bytes.ByteArrays#swap
     * @see it.unimi.dsi.fastutil.bytes.ByteArrays#trim
     * @see it.unimi.dsi.fastutil.bytes.ByteArrays#grow
     * @see it.unimi.dsi.fastutil.bytes.ByteArrays#setLength
     * 
     * @see org.apache.commons.lang3.ArrayUtils#addAll
     * 
     * @see com.google.common.collect.ObjectArrays#concat
     * @see it.unimi.dsi.fastutil.objects.ObjectArrays#grow
     */
    public static byte[] concatOrSame(final byte @Nullable [] a, final byte @Nullable [] b) {
        final int len1;
        //CHECKSTYLE:OFF
        if (a == null || (len1 = a.length) <= 0) {
            //CHECKSTYLE:ON
            return b == null || b.length <= 0 ? EMPTY_ARRAY : b;
        } //×a

        final int len2;
        //CHECKSTYLE:OFF
        if (b == null || (len2 = b.length) <= 0) {
            //CHECKSTYLE:ON
            return a;
        } //×b

        final byte[] c = new byte[len1 + len2];
        System.arraycopy(a, 0, c, 0, len1);
        System.arraycopy(b, 0, c, len1, len2);
        return c;
    }

    /** Similar to {@link #concatOrSame}, but if only one array is not empty → returns cloned array */
    public static byte[] concatClone(final byte @Nullable [] a, final byte @Nullable [] b) {
        final int len1;
        //CHECKSTYLE:OFF
        if (a == null || (len1 = a.length) <= 0) {
            //CHECKSTYLE:ON
            return b == null || b.length <= 0 ? EMPTY_ARRAY : b.clone();
        } //×a

        final int len2;
        //CHECKSTYLE:OFF
        if (b == null || (len2 = b.length) <= 0) {
            //CHECKSTYLE:ON
            return a.clone();
        } //×b

        final byte[] c = new byte[len1 + len2];
        System.arraycopy(a, 0, c, 0, len1);
        System.arraycopy(b, 0, c, len1, len2);
        return c;
    }

    /** Concat multiple byte arrays */
    public static byte[] concatMulti(final byte @Nullable [] @Nullable... byteArrays) {
        if (BucketHashUtil.blankVarargs(byteArrays)) {
            return EMPTY_ARRAY;
        }

        int total = 0;// total length = all bytes in all arrays
        for (final byte[] src : byteArrays) {
            if (src != null) {
                total += src.length;
            }
        }

        final byte[] result = new byte[total];
        int j = 0;

        for (final byte[] src : byteArrays) {
            if (src != null && src.length > 0) {
                System.arraycopy(src, 0, result, j, src.length);
                j += src.length;
            }
        }
        return result;
    }

    public static byte[] longToBytes(final long longValue) {
        final byte[] b = new byte[8];
        DirectByteArrayAccess.setLong(b, 0, longValue);
        return b;
    }

    public static long bytesToLong(final byte[] bytes) {
        return DirectByteArrayAccess.getLong(bytes, 0);
    }

}
