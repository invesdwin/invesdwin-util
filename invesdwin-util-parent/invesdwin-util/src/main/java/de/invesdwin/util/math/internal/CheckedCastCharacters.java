package de.invesdwin.util.math.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.decimal.ADecimal;

@Immutable
public final class CheckedCastCharacters {

    private CheckedCastCharacters() {
    }

    public static char checkedCast(final Object value) {
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
        } else if (value.getClass().isArray()) {
            final int length = Arrays.getLength(value);
            if (length == 1) {
                final Object cValue = Arrays.get(value, 0);
                return checkedCast(cValue);
            }
        }
        throw UnknownArgumentException.newInstance(Object.class, value);
    }

    public static char checkedCast(final Number value) {
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

    public static char checkedCast(final CharSequence value) {
        if (value.length() != 1) {
            throw new IllegalArgumentException("Expecting exactly one character: " + value);
        }
        return checkedCast(value.charAt(0));
    }

    public static char checkedCast(final Boolean value) {
        if (value == Boolean.TRUE) {
            return 1;
        } else {
            return 0;
        }
    }

    public static char checkedCast(final boolean value) {
        if (value) {
            return 1;
        } else {
            return 0;
        }
    }

    public static char checkedCast(final Character value) {
        return checkedCast((char) value);
    }

    public static char checkedCast(final char value) {
        return value;
    }

    public static char checkedCast(final Byte value) {
        return checkedCast((byte) value);
    }

    public static char checkedCast(final byte value) {
        if (value < Character.MIN_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static char checkedCast(final Short value) {
        return checkedCast((short) value);
    }

    public static char checkedCast(final short value) {
        if (value < Character.MIN_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static char checkedCast(final Integer value) {
        return checkedCast((int) value);
    }

    public static char checkedCast(final int value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static char checkedCast(final Long value) {
        return checkedCast((long) value);
    }

    public static char checkedCast(final long value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static char checkedCast(final Float value) {
        return checkedCast((float) value);
    }

    public static char checkedCast(final float value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static char checkedCast(final Double value) {
        return checkedCast((double) value);
    }

    public static char checkedCast(final double value) {
        if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
            throw new ArithmeticException("char overflow: " + value);
        }
        return (char) value;
    }

    public static char checkedCast(final ADecimal<?> value) {
        return checkedCast(value.getDefaultValue());
    }

    public static char checkedCast(final BigDecimal value) {
        return checkedCast(value.doubleValue());
    }

    public static char checkedCast(final BigInteger value) {
        return checkedCast(value.doubleValue());
    }

    //CHECKSTYLE:OFF
    public static char[] checkedCastVector(final Object value) {
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
        } else if (value instanceof BitSet) {
            final BitSet cValue = (BitSet) value;
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
        } else if (value instanceof Object[]) {
            final Object[] cValue = (Object[]) value;
            return checkedCastVector(cValue);
        } else {
            throw UnknownArgumentException.newInstance(Object.class, value);
        }
    }

    public static char[] checkedCastVector(final Object[] value) {
        if (value == null) {
            return null;
        }
        if (value.length == 1) {
            final Object firstValue = value[0];
            if (firstValue != null && firstValue.getClass().isArray()) {
                return checkedCastVector(firstValue);
            }
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final Boolean[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final BitSet value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.size()];
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCast(value.get(i));
        }
        return vector;
    }

    public static char[] checkedCastVector(final boolean[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastVector(list);
    }

    public static char[] checkedCastVector(final Iterable<?> value) {
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

    public static char[] checkedCastVector(final List<?> value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.size()];
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCast(value.get(i));
        }
        return vector;
    }

    public static char[] checkedCastVector(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastVector(cValue);
        }
        final char[] vector = new char[value.size()];
        final Iterator<?> iterator = value.iterator();
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCast(iterator.next());
        }
        return vector;
    }

    public static char[] checkedCastVector(final byte[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final Byte[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final Character[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final char[] value) {
        return value;
    }

    public static char[] checkedCastVector(final Short[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final short[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final int[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final Integer[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final Long[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final long[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final Float[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final float[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final Double[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final double[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final ADecimal<?>[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final BigDecimal[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static char[] checkedCastVector(final BigInteger[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    private static char[] checkedCastVector(final CharSequence value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length()];
        for (int i = 0; i < value.length(); i++) {
            vector[i] = checkedCast(value.charAt(i));
        }
        return vector;
    }

    private static char[] checkedCastVector(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final char[] vector = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    //CHECKSTYLE:OFF
    public static char[][] checkedCastMatrix(final Object value) {
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
        } else if (value instanceof BitSet[]) {
            final BitSet[] cValue = (BitSet[]) value;
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
        } else if (value instanceof Object[]) {
            final Object[] cValue = (Object[]) value;
            return checkedCastMatrix(cValue);
        } else {
            throw UnknownArgumentException.newInstance(Object.class, value);
        }
    }

    public static char[][] checkedCastMatrix(final Object[] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastMatrix(list);
    }

    public static char[][] checkedCastMatrix(final Iterable<?> value) {
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

    public static char[][] checkedCastMatrix(final List<?> value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.size()][];
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVector(value.get(row));
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastMatrix(cValue);
        }
        final char[][] matrix = new char[value.size()][];
        final Iterator<?> iterator = value.iterator();
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVector(iterator.next());
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final Byte[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final byte[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final Boolean[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final BitSet[] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final boolean[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final Character[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final char[][] value) {
        return value;
    }

    public static char[][] checkedCastMatrix(final Short[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final short[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final Integer[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final int[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final Long[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final long[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final Float[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final float[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final Double[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final double[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final ADecimal<?>[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final BigDecimal[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static char[][] checkedCastMatrix(final BigInteger[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    private static char[][] checkedCastMatrix(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    private static char[][] checkedCastMatrix(final CharSequence[][] value) {
        if (value == null) {
            return null;
        }
        final char[][] matrix = new char[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

}
