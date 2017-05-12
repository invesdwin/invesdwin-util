package de.invesdwin.util.math.internal;

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
public final class CheckedCastDoubles {

    private CheckedCastDoubles() {}

    public static double checkedCast(final Object value) {
        if (value instanceof Number) {
            final Number cValue = (Number) value;
            return checkedCast(cValue);
        } else if (value instanceof Boolean) {
            final boolean cValue = (Boolean) value;
            return checkedCast(cValue);
        } else if (value instanceof Character) {
            final char cValue = (Character) value;
            return checkedCast(cValue);
        } else if (value instanceof CharSequence) {
            final CharSequence cValue = (CharSequence) value;
            return checkedCast(cValue);
        } else {
            throw UnknownArgumentException.newInstance(Object.class, value);
        }
    }

    public static double checkedCast(final Number value) {
        if (value instanceof Double) {
            final double cValue = value.doubleValue();
            return checkedCast(cValue);
        } else if (value instanceof Float) {
            final float cValue = value.floatValue();
            return checkedCast(cValue);
        } else if (value instanceof Long) {
            final long cValue = value.longValue();
            return checkedCast(cValue);
        } else if (value instanceof Integer) {
            final int cValue = value.intValue();
            return checkedCast(cValue);
        } else if (value instanceof Short) {
            final short cValue = value.shortValue();
            return checkedCast(cValue);
        } else if (value instanceof Byte) {
            final byte cValue = value.byteValue();
            return checkedCast(cValue);
        } else if (value instanceof ADecimal<?>) {
            final ADecimal<?> cValue = (ADecimal<?>) value;
            return checkedCast(cValue);
        } else if (value instanceof BigDecimal) {
            final BigDecimal cValue = (BigDecimal) value;
            return checkedCast(cValue);
        } else if (value instanceof BigInteger) {
            final BigInteger cValue = (BigInteger) value;
            return checkedCast(cValue);
        } else {
            //fallback to double
            final double doubleValue = value.doubleValue();
            return checkedCast(doubleValue);
        }
    }

    public static double checkedCast(final CharSequence value) {
        if (value.length() != 1) {
            throw new IllegalArgumentException("Expecting exactly one character: " + value);
        }
        return checkedCast(value.charAt(0));
    }

    public static double checkedCast(final Boolean value) {
        if (value == Boolean.TRUE) {
            return 1D;
        } else {
            return 0D;
        }
    }

    public static double checkedCast(final boolean value) {
        if (value) {
            return 1D;
        } else {
            return 0D;
        }
    }

    public static double checkedCast(final Character value) {
        return checkedCast((char) value);
    }

    public static double checkedCast(final char value) {
        return value;
    }

    public static double checkedCast(final Byte value) {
        return checkedCast((byte) value);
    }

    public static double checkedCast(final byte value) {
        return value;
    }

    public static double checkedCast(final Short value) {
        return checkedCast((short) value);
    }

    public static double checkedCast(final short value) {
        return value;
    }

    public static double checkedCast(final Integer value) {
        return checkedCast((int) value);
    }

    public static double checkedCast(final int value) {
        return value;
    }

    public static double checkedCast(final Long value) {
        return checkedCast((long) value);
    }

    public static double checkedCast(final long value) {
        return value;
    }

    public static double checkedCast(final Float value) {
        return checkedCast((float) value);
    }

    public static double checkedCast(final float value) {
        return value;
    }

    public static double checkedCast(final Double value) {
        return checkedCast((double) value);
    }

    public static double checkedCast(final double value) {
        return value;
    }

    public static double checkedCast(final ADecimal<?> value) {
        return checkedCast(value.getDefaultValue().doubleValueRaw());
    }

    public static double checkedCast(final BigDecimal value) {
        return checkedCast(value.doubleValue());
    }

    public static double checkedCast(final BigInteger value) {
        return checkedCast(value.doubleValue());
    }

    //CHECKSTYLE:OFF
    public static double[] checkedCastVector(final Object value) {
        //CHECKSTYLE:ON
        if (value == null) {
            return null;
        } else if (value instanceof byte[]) {
            final byte[] cValue = (byte[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof Byte[]) {
            final Byte[] cValue = (Byte[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof boolean[]) {
            final boolean[] cValue = (boolean[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof Boolean[]) {
            final Boolean[] cValue = (Boolean[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof double[]) {
            final double[] cValue = (double[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof Double[]) {
            final Double[] cValue = (Double[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof float[]) {
            final float[] cValue = (float[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof Float[]) {
            final Float[] cValue = (Float[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof long[]) {
            final long[] cValue = (long[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof Long[]) {
            final Long[] cValue = (Long[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof int[]) {
            final int[] cValue = (int[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof Integer[]) {
            final Integer[] cValue = (Integer[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof short[]) {
            final short[] cValue = (short[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof Short[]) {
            final Short[] cValue = (Short[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof ADecimal[]) {
            final ADecimal<?>[] cValue = (ADecimal<?>[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof BigDecimal[]) {
            final BigDecimal[] cValue = (BigDecimal[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof BigInteger[]) {
            final BigInteger[] cValue = (BigInteger[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof Iterable) {
            final Iterable<?> cValue = (Iterable<?>) value;
            return checkedCastVector(cValue);
        } else if (value instanceof Iterator) {
            final Iterator<?> cValue = (Iterator<?>) value;
            return checkedCastVector(cValue);
        } else if (value instanceof char[]) {
            final char[] cValue = (char[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof Character[]) {
            final Character[] cValue = (Character[]) value;
            return checkedCastVector(cValue);
        } else if (value instanceof CharSequence) {
            final CharSequence cValue = (CharSequence) value;
            return checkedCastVector(cValue);
        } else if (value instanceof CharSequence[]) {
            final CharSequence[] cValue = (CharSequence[]) value;
            return checkedCastVector(cValue);
        } else {
            throw UnknownArgumentException.newInstance(Class.class, value.getClass());
        }
    }

    public static double[] checkedCastVector(final Boolean[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final boolean[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastVector(list);
    }

    public static double[] checkedCastVector(final Iterable<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastVector(cValue);
        } else if (value instanceof Collection) {
            final Collection<?> cValue = (Collection<?>) value;
            return checkedCastVector(cValue);
        }
        return checkedCastVector(value.iterator());
    }

    public static double[] checkedCastVector(final List<?> value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.size()];
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCast(value.get(i));
        }
        return vector;
    }

    public static double[] checkedCastVector(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastVector(cValue);
        }
        final double[] vector = new double[value.size()];
        final Iterator<?> iterator = value.iterator();
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCast(iterator.next());
        }
        return vector;
    }

    public static double[] checkedCastVector(final byte[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final Byte[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final Character[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final char[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final Short[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final short[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final int[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final Integer[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final Long[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final long[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final Float[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final float[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final Double[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final double[] value) {
        return value;
    }

    public static double[] checkedCastVector(final ADecimal<?>[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final BigDecimal[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static double[] checkedCastVector(final BigInteger[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    private static double[] checkedCastVector(final CharSequence value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length()];
        for (int i = 0; i < value.length(); i++) {
            vector[i] = checkedCast(value.charAt(i));
        }
        return vector;
    }

    private static double[] checkedCastVector(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final double[] vector = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    //CHECKSTYLE:OFF
    public static double[][] checkedCastMatrix(final Object value) {
        //CHECKSTYLE:ON
        if (value == null) {
            return null;
        } else if (value instanceof byte[][]) {
            final byte[][] cValue = (byte[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof Byte[][]) {
            final Byte[][] cValue = (Byte[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof boolean[][]) {
            final boolean[][] cValue = (boolean[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof Boolean[][]) {
            final Boolean[][] cValue = (Boolean[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof double[][]) {
            final double[][] cValue = (double[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof Double[][]) {
            final Double[][] cValue = (Double[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof float[][]) {
            final float[][] cValue = (float[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof Float[][]) {
            final Float[][] cValue = (Float[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof long[][]) {
            final long[][] cValue = (long[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof Long[][]) {
            final Long[][] cValue = (Long[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof int[][]) {
            final int[][] cValue = (int[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof Integer[][]) {
            final Integer[][] cValue = (Integer[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof short[][]) {
            final short[][] cValue = (short[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof Short[][]) {
            final Short[][] cValue = (Short[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof char[][]) {
            final char[][] cValue = (char[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof Character[][]) {
            final Character[][] cValue = (Character[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof ADecimal[][]) {
            final ADecimal<?>[][] cValue = (ADecimal<?>[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof BigDecimal[][]) {
            final BigDecimal[][] cValue = (BigDecimal[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof BigInteger[][]) {
            final BigInteger[][] cValue = (BigInteger[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof CharSequence[]) {
            final CharSequence[] cValue = (CharSequence[]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof CharSequence[][]) {
            final CharSequence[][] cValue = (CharSequence[][]) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof Iterable) {
            final Iterable<?> cValue = (Iterable<?>) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof Iterator) {
            final Iterator<?> cValue = (Iterator<?>) value;
            return checkedCastMatrix(cValue);
        } else {
            throw UnknownArgumentException.newInstance(Class.class, value.getClass());
        }
    }

    public static double[][] checkedCastMatrix(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastMatrix(list);
    }

    public static double[][] checkedCastMatrix(final Iterable<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastMatrix(cValue);
        } else if (value instanceof Collection) {
            final Collection<?> cValue = (Collection<?>) value;
            return checkedCastMatrix(cValue);
        }
        return checkedCastMatrix(value.iterator());
    }

    public static double[][] checkedCastMatrix(final List<?> value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.size()][];
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVector(value.get(row));
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastMatrix(cValue);
        }
        final double[][] matrix = new double[value.size()][];
        final Iterator<?> iterator = value.iterator();
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVector(iterator.next());
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final Byte[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final byte[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final Boolean[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final boolean[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final Character[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final char[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final Short[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final short[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final Integer[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final int[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final Long[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final long[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final Float[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final float[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final Double[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final double[][] value) {
        return value;
    }

    public static double[][] checkedCastMatrix(final ADecimal<?>[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final BigDecimal[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static double[][] checkedCastMatrix(final BigInteger[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    private static double[][] checkedCastMatrix(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    private static double[][] checkedCastMatrix(final CharSequence[][] value) {
        if (value == null) {
            return null;
        }
        final double[][] matrix = new double[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

}
