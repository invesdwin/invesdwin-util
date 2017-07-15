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
public final class CheckedCastCharactersObj {

    private CheckedCastCharactersObj() {}

    public static Character checkedCastObj(final Object value) {
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

    public static Character checkedCastObj(final Number value) {
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

    public static Character checkedCastObj(final CharSequence value) {
        if (value == null) {
            return null;
        } else if (value.length() != 1) {
            throw new IllegalArgumentException("Expecting exactly one character: " + value);
        }
        return checkedCastObj(value.charAt(0));
    }

    public static Character checkedCastObj(final Boolean value) {
        if (value == null) {
            return null;
        } else if (value == Boolean.TRUE) {
            return 1;
        } else {
            return 0;
        }
    }

    public static Character checkedCastObj(final boolean value) {
        if (value) {
            return 1;
        } else {
            return 0;
        }
    }

    public static Character checkedCastObj(final Character value) {
        return value;
    }

    public static Character checkedCastObj(final char value) {
        return value;
    }

    public static Character checkedCastObj(final Byte value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((byte) value);
    }

    public static Character checkedCastObj(final byte value) {
        //char cannot be negative
        if (value < Character.MIN_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static Character checkedCastObj(final Short value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((short) value);
    }

    public static Character checkedCastObj(final short value) {
        if (value < Character.MIN_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static Character checkedCastObj(final Integer value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((int) value);
    }

    public static Character checkedCastObj(final int value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static Character checkedCastObj(final Long value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((long) value);
    }

    public static Character checkedCastObj(final long value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static Character checkedCastObj(final Float value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((float) value);
    }

    public static Character checkedCastObj(final float value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static Character checkedCastObj(final Double value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj((double) value);
    }

    public static Character checkedCastObj(final double value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static Character checkedCastObj(final ADecimal<?> value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj(value.getDefaultValue().doubleValueRaw());
    }

    public static Character checkedCastObj(final BigDecimal value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj(value.doubleValue());
    }

    public static Character checkedCastObj(final BigInteger value) {
        if (value == null) {
            return null;
        }
        return checkedCastObj(value.doubleValue());
    }

    //CHECKSTYLE:OFF
    public static Character[] checkedCastVectorObj(final Object value) {
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
        } else if (value instanceof Object[]) {
            final Object[] cValue = (Object[]) value;
            return checkedCastVectorObj(cValue);
        } else {
            throw UnknownArgumentException.newInstance(Object.class, value);
        }
    }

    public static Character[] checkedCastVectorObj(final Object[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final Boolean[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final boolean[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastVectorObj(list);
    }

    public static Character[] checkedCastVectorObj(final Iterable<?> value) {
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

    public static Character[] checkedCastVectorObj(final List<?> value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.size()];
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCastObj(value.get(i));
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastVectorObj(cValue);
        }
        final Character[] vector = new Character[value.size()];
        final Iterator<?> iterator = value.iterator();
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCastObj(iterator.next());
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final byte[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final Byte[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final Character[] value) {
        return value;
    }

    public static Character[] checkedCastVectorObj(final char[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final Short[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final short[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final int[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final Integer[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final Long[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final long[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final Float[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final float[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final Double[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final double[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final ADecimal<?>[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final BigDecimal[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    public static Character[] checkedCastVectorObj(final BigInteger[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    private static Character[] checkedCastVectorObj(final CharSequence value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length()];
        for (int i = 0; i < value.length(); i++) {
            vector[i] = checkedCastObj(value.charAt(i));
        }
        return vector;
    }

    private static Character[] checkedCastVectorObj(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final Character[] vector = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCastObj(value[i]);
        }
        return vector;
    }

    //CHECKSTYLE:OFF
    public static Character[][] checkedCastMatrixObj(final Object value) {
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
        } else if (value instanceof Object[]) {
            final Object[] cValue = (Object[]) value;
            return checkedCastMatrixObj(cValue);
        } else {
            throw UnknownArgumentException.newInstance(Object.class, value);
        }
    }

    public static Character[][] checkedCastMatrixObj(final Object[] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastMatrixObj(list);
    }

    public static Character[][] checkedCastMatrixObj(final Iterable<?> value) {
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

    public static Character[][] checkedCastMatrixObj(final List<?> value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.size()][];
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVectorObj(value.get(row));
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastMatrixObj(cValue);
        }
        final Character[][] matrix = new Character[value.size()][];
        final Iterator<?> iterator = value.iterator();
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVectorObj(iterator.next());
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final Byte[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final byte[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final Boolean[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final boolean[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final Character[][] value) {
        return value;
    }

    public static Character[][] checkedCastMatrixObj(final char[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final Short[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final short[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final Integer[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final int[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final Long[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final long[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final Float[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final float[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final Double[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final double[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final ADecimal<?>[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final BigDecimal[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    public static Character[][] checkedCastMatrixObj(final BigInteger[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    private static Character[][] checkedCastMatrixObj(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

    private static Character[][] checkedCastMatrixObj(final CharSequence[][] value) {
        if (value == null) {
            return null;
        }
        final Character[][] matrix = new Character[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVectorObj(value[row]);
        }
        return matrix;
    }

}
