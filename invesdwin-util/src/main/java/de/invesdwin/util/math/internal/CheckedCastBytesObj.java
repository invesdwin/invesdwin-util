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
public final class CheckedCastBytesObj {

    private CheckedCastBytesObj() {}

    public static Byte checkedCastObj(final Object value) {
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
        } else {
            throw UnknownArgumentException.newInstance(Object.class, value);
        }
    }

    public static Byte checkedCastObj(final Number value) {
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

    public static Byte checkedCastObj(final CharSequence value) {
        if (value == null) {
            return null;
        } else if (value.length() != 1) {
            throw new IllegalArgumentException("Expecting exactly one character: " + value);
        }
        return checkedCastObj(value.charAt(0));
    }

    public static Byte checkedCastObj(final Boolean value) {
        if (value == null) {
            return null;
        } else if (value == Boolean.TRUE) {
            return 1;
        } else {
            return 0;
        }
    }

    public static Byte checkedCastObj(final boolean value) {
        if (value) {
            return 1;
        } else {
            return 0;
        }
    }

    public static Byte checkedCastObj(final Character value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((char) value);
    }

    public static Byte checkedCastObj(final char value) {
        //char cannot be negative
        if (value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow: " + value);
        }
        return (byte) value;
    }

    public static Byte checkedCastObj(final Byte value) {
        return value;
    }

    public static Byte checkedCastObj(final byte value) {
        return value;
    }

    public static Byte checkedCastObj(final Short value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((short) value);
    }

    public static Byte checkedCastObj(final short value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow: " + value);
        }
        return (byte) value;
    }

    public static Byte checkedCastObj(final Integer value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((int) value);
    }

    public static Byte checkedCastObj(final int value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow: " + value);
        }
        return (byte) value;
    }

    public static Byte checkedCastObj(final Long value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((long) value);
    }

    public static Byte checkedCastObj(final long value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow: " + value);
        }
        return (byte) value;
    }

    public static Byte checkedCastObj(final Float value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((float) value);
    }

    public static Byte checkedCastObj(final float value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow: " + value);
        }
        return (byte) value;
    }

    public static Byte checkedCastObj(final Double value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((double) value);
    }

    public static Byte checkedCastObj(final double value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new ArithmeticException("byte overflow: " + value);
        }
        return (byte) value;
    }

    public static Byte checkedCastObj(final ADecimal<?> value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj(value.getDefaultValue().doubleValueRaw());
    }

    public static Byte checkedCastObj(final BigDecimal value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj(value.doubleValue());
    }

    public static Byte checkedCastObj(final BigInteger value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj(value.doubleValue());
    }

    //CHECKSTYLE:OFF
    public static Byte[] checkedCastVectorObj(final Object value) {
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

    public static Byte[] checkedCastVectorObj(final Boolean[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final boolean[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastVectorObj(list);
    }

    public static Byte[] checkedCastVectorObj(final Iterable<?> value) {
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

    public static Byte[] checkedCastVectorObj(final List<?> value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.size()];
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCastObj(value.get(i));
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastVectorObj(cValue);
        }
        final Byte[] vector = new Byte[value.size()];
        final Iterator<?> iterator = value.iterator();
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCastObj(iterator.next());
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final byte[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final Byte[] value) {
        return value;
    }

    public static Byte[] checkedCastVectorObj(final Character[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final char[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final Short[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final short[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final int[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final Integer[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final Long[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final long[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final Float[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final float[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final Double[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final double[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final ADecimal<?>[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final BigDecimal[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Byte[] checkedCastVectorObj(final BigInteger[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    private static Byte[] checkedCastVectorObj(final CharSequence value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length()];
        for (int i = 0; i < value.length(); i++) {
            vector[i] = checkedCastObj(value.charAt(i));
        }
        return vector;
    }

    private static Byte[] checkedCastVectorObj(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final Byte[] vector = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    //CHECKSTYLE:OFF
    public static Byte[][] checkedCastMatrixObj(final Object value) {
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

    public static Byte[][] checkedCastMatrixObj(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastMatrixObj(list);
    }

    public static Byte[][] checkedCastMatrixObj(final Iterable<?> value) {
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

    public static Byte[][] checkedCastMatrixObj(final List<?> value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.size()][];
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVectorObj(value.get(row));
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastMatrixObj(cValue);
        }
        final Byte[][] matrix = new Byte[value.size()][];
        final Iterator<?> iterator = value.iterator();
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVectorObj(iterator.next());
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final Byte[][] value) {
        return value;
    }

    public static Byte[][] checkedCastMatrixObj(final byte[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final Boolean[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final boolean[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final Character[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final char[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final Short[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final short[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final Integer[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final int[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final Long[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final long[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final Float[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final float[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final Double[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final double[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final ADecimal<?>[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final BigDecimal[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Byte[][] checkedCastMatrixObj(final BigInteger[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    private static Byte[][] checkedCastMatrixObj(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    private static Byte[][] checkedCastMatrixObj(final CharSequence[][] value) {
        if (value == null) {
            return null;
        }
        final Byte[][] matrix = new Byte[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

}
