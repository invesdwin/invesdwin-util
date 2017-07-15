package de.invesdwin.util.math.internal;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.decimal.ADecimal;

@Immutable
public final class CheckedCastDoublesObj {

    private CheckedCastDoublesObj() {}

    public static Double checkedCastObj(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            final Number cValue = (Number) value;
            return checkedCastObj(cValue);
        } else if (value instanceof Boolean) {
            final boolean cValue = (Boolean) value;
            return checkedCastObj(cValue);
        } else if (value instanceof Character) {
            final char cValue = (Character) value;
            return checkedCastObj(cValue);
        } else if (value instanceof CharSequence) {
            final CharSequence cValue = (CharSequence) value;
            return checkedCastObj(cValue);
        } else if (value.getClass().isArray()) {
            final int length = Array.getLength(value);
            if (length == 1) {
                final Object cValue = Array.get(value, 0);
                return checkedCastObj(cValue);
            }
        }
        throw UnknownArgumentException.newInstance(Object.class, value);
    }

    public static Double checkedCastObj(final Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Double) {
            final double cValue = value.doubleValue();
            return checkedCastObj(cValue);
        } else if (value instanceof Float) {
            final float cValue = value.floatValue();
            return checkedCastObj(cValue);
        } else if (value instanceof Long) {
            final long cValue = value.longValue();
            return checkedCastObj(cValue);
        } else if (value instanceof Integer) {
            final int cValue = value.intValue();
            return checkedCastObj(cValue);
        } else if (value instanceof Short) {
            final short cValue = value.shortValue();
            return checkedCastObj(cValue);
        } else if (value instanceof Byte) {
            final byte cValue = value.byteValue();
            return checkedCastObj(cValue);
        } else if (value instanceof ADecimal<?>) {
            final ADecimal<?> cValue = (ADecimal<?>) value;
            return checkedCastObj(cValue);
        } else if (value instanceof BigDecimal) {
            final BigDecimal cValue = (BigDecimal) value;
            return checkedCastObj(cValue);
        } else if (value instanceof BigInteger) {
            final BigInteger cValue = (BigInteger) value;
            return checkedCastObj(cValue);
        } else {
            //fallback to double
            final double doubleValue = value.doubleValue();
            return checkedCastObj(doubleValue);
        }
    }

    public static Double checkedCastObj(final CharSequence value) {
        if (value == null) {
            return null;
        } else if (value.length() != 1) {
            throw new IllegalArgumentException("Expecting exactly one character: " + value);
        }
        return checkedCastObj(value.charAt(0));
    }

    public static Double checkedCastObj(final Boolean value) {
        if (value == null) {
            return null;
        } else if (value == Boolean.TRUE) {
            return 1D;
        } else {
            return 0D;
        }
    }

    public static Double checkedCastObj(final boolean value) {
        if (value) {
            return 1D;
        } else {
            return 0D;
        }
    }

    public static Double checkedCastObj(final Character value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((char) value);
    }

    public static Double checkedCastObj(final char value) {
        return (double) value;
    }

    public static Double checkedCastObj(final Byte value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((byte) value);
    }

    public static Double checkedCastObj(final byte value) {
        return (double) value;
    }

    public static Double checkedCastObj(final Short value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((int) value);
    }

    public static Double checkedCastObj(final short value) {
        return (double) value;
    }

    public static Double checkedCastObj(final Integer value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((int) value);
    }

    public static Double checkedCastObj(final int value) {
        return (double) value;
    }

    public static Double checkedCastObj(final Long value) {
        if (value == null) {
            return null;
        } else {
            return checkedCastObj((long) value);
        }
    }

    public static Double checkedCastObj(final long value) {
        return (double) value;
    }

    public static Double checkedCastObj(final Float value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((float) value);
    }

    public static Double checkedCastObj(final float value) {
        return (double) value;
    }

    public static Double checkedCastObj(final Double value) {
        return value;
    }

    public static Double checkedCastObj(final double value) {
        return value;
    }

    public static Double checkedCastObj(final ADecimal<?> value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj(value.getDefaultValue().doubleValueRaw());
    }

    public static Double checkedCastObj(final BigDecimal value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj(value.doubleValue());
    }

    public static Double checkedCastObj(final BigInteger value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj(value.doubleValue());
    }

    //CHECKSTYLE:OFF
    public static Double[] checkedCastVectorObj(final Object value) {
        //CHECKSTYLE:ON
        if (value == null) {
            return null;
        } else if (value instanceof byte[]) {
            final byte[] cValue = (byte[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof Byte[]) {
            final Byte[] cValue = (Byte[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof boolean[]) {
            final boolean[] cValue = (boolean[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof Boolean[]) {
            final Boolean[] cValue = (Boolean[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof double[]) {
            final double[] cValue = (double[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof Double[]) {
            final Double[] cValue = (Double[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof float[]) {
            final float[] cValue = (float[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof Float[]) {
            final Float[] cValue = (Float[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof long[]) {
            final long[] cValue = (long[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof Long[]) {
            final Long[] cValue = (Long[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof int[]) {
            final int[] cValue = (int[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof Integer[]) {
            final Integer[] cValue = (Integer[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof short[]) {
            final short[] cValue = (short[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof Short[]) {
            final Short[] cValue = (Short[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof ADecimal[]) {
            final ADecimal<?>[] cValue = (ADecimal<?>[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof BigDecimal[]) {
            final BigDecimal[] cValue = (BigDecimal[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof BigInteger[]) {
            final BigInteger[] cValue = (BigInteger[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof Iterable) {
            final Iterable<?> cValue = (Iterable<?>) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof Iterator) {
            final Iterator<?> cValue = (Iterator<?>) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof char[]) {
            final char[] cValue = (char[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof Character[]) {
            final Character[] cValue = (Character[]) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof CharSequence) {
            final CharSequence cValue = (CharSequence) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof CharSequence[]) {
            final CharSequence[] cValue = (CharSequence[]) value;
            return checkedCastVectorObj(cValue);
        } else {
            throw UnknownArgumentException.newInstance(Class.class, value.getClass());
        }
    }

    public static Double[] checkedCastVectorObj(final Boolean[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final boolean[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastVectorObj(list);
    }

    public static Double[] checkedCastVectorObj(final Iterable<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastVectorObj(cValue);
        } else if (value instanceof Collection) {
            final Collection<?> cValue = (Collection<?>) value;
            return checkedCastVectorObj(cValue);
        }
        return checkedCastVectorObj(value.iterator());
    }

    public static Double[] checkedCastVectorObj(final List<?> value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.size()];
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCastObj(value.get(i));
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastVectorObj(cValue);
        }
        final Double[] vector = new Double[value.size()];
        final Iterator<?> iterator = value.iterator();
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCastObj(iterator.next());
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final byte[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final Byte[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final Character[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final char[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final Short[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final short[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final int[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final Integer[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final Long[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final long[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final Float[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final float[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final Double[] value) {
        return value;
    }

    public static Double[] checkedCastVectorObj(final double[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final ADecimal<?>[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final BigDecimal[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Double[] checkedCastVectorObj(final BigInteger[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    private static Double[] checkedCastVectorObj(final CharSequence value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length()];
        for (int i = 0; i < value.length(); i++) {
            vector[i] = checkedCastObj(value.charAt(i));
        }
        return vector;
    }

    private static Double[] checkedCastVectorObj(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final Double[] vector = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    //CHECKSTYLE:OFF
    public static Double[][] checkedCastMatrixObj(final Object value) {
        //CHECKSTYLE:ON
        if (value == null) {
            return null;
        } else if (value instanceof byte[][]) {
            final byte[][] cValue = (byte[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof Byte[][]) {
            final Byte[][] cValue = (Byte[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof boolean[][]) {
            final boolean[][] cValue = (boolean[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof Boolean[][]) {
            final Boolean[][] cValue = (Boolean[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof double[][]) {
            final double[][] cValue = (double[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof Double[][]) {
            final Double[][] cValue = (Double[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof float[][]) {
            final float[][] cValue = (float[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof Float[][]) {
            final Float[][] cValue = (Float[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof long[][]) {
            final long[][] cValue = (long[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof Long[][]) {
            final Long[][] cValue = (Long[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof int[][]) {
            final int[][] cValue = (int[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof Integer[][]) {
            final Integer[][] cValue = (Integer[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof short[][]) {
            final short[][] cValue = (short[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof Short[][]) {
            final Short[][] cValue = (Short[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof char[][]) {
            final char[][] cValue = (char[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof Character[][]) {
            final Character[][] cValue = (Character[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof ADecimal[][]) {
            final ADecimal<?>[][] cValue = (ADecimal<?>[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof BigDecimal[][]) {
            final BigDecimal[][] cValue = (BigDecimal[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof BigInteger[][]) {
            final BigInteger[][] cValue = (BigInteger[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof CharSequence[]) {
            final CharSequence[] cValue = (CharSequence[]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof CharSequence[][]) {
            final CharSequence[][] cValue = (CharSequence[][]) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof Iterable) {
            final Iterable<?> cValue = (Iterable<?>) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof Iterator) {
            final Iterator<?> cValue = (Iterator<?>) value;
            return checkedCastMatrixObj(cValue);
        } else {
            throw UnknownArgumentException.newInstance(Class.class, value.getClass());
        }
    }

    public static Double[][] checkedCastMatrixObj(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastMatrixObj(list);
    }

    public static Double[][] checkedCastMatrixObj(final Iterable<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastMatrixObj(cValue);
        } else if (value instanceof Collection) {
            final Collection<?> cValue = (Collection<?>) value;
            return checkedCastMatrixObj(cValue);
        }
        return checkedCastMatrixObj(value.iterator());
    }

    public static Double[][] checkedCastMatrixObj(final List<?> value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.size()][];
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVectorObj(value.get(row));
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastMatrixObj(cValue);
        }
        final Double[][] matrix = new Double[value.size()][];
        final Iterator<?> iterator = value.iterator();
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVectorObj(iterator.next());
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final Byte[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final byte[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final Boolean[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final boolean[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final Character[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final char[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final Short[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final short[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final Integer[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final int[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final Long[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final long[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final Float[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final float[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final Double[][] value) {
        return value;
    }

    public static Double[][] checkedCastMatrixObj(final double[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final ADecimal<?>[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final BigDecimal[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Double[][] checkedCastMatrixObj(final BigInteger[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    private static Double[][] checkedCastMatrixObj(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    private static Double[][] checkedCastMatrixObj(final CharSequence[][] value) {
        if (value == null) {
            return null;
        }
        final Double[][] matrix = new Double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

}
