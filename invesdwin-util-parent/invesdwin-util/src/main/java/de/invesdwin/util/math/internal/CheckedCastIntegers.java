package de.invesdwin.util.math.internal;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.decimal.ADecimal;

@Immutable
public final class CheckedCastIntegers {

    private CheckedCastIntegers() {
    }

    public static int checkedCast(final Object value) {
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
            final int length = Array.getLength(value);
            if (length == 1) {
                final Object cValue = Array.get(value, 0);
                return checkedCast(cValue);
            }
        }
        throw UnknownArgumentException.newInstance(Object.class, value);
    }

    public static int checkedCastNoOverflow(final Object value) {
        if (value instanceof Number) {
            final Number cValue = (Number) value;
            return checkedCastNoOverflow(cValue);
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
            final int length = Array.getLength(value);
            if (length == 1) {
                final Object cValue = Array.get(value, 0);
                return checkedCastNoOverflow(cValue);
            }
        }
        throw UnknownArgumentException.newInstance(Object.class, value);
    }

    public static int checkedCast(final Number value) {
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

    public static int checkedCastNoOverflow(final Number value) {
        if (value instanceof Double) {
            final double cValue = value.doubleValue();
            return checkedCastNoOverflow(cValue);
        } else if (value instanceof Float) {
            final float cValue = value.floatValue();
            return checkedCastNoOverflow(cValue);
        } else if (value instanceof Long) {
            final long cValue = value.longValue();
            return checkedCastNoOverflow(cValue);
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
            return checkedCastNoOverflow(cValue);
        } else if (value instanceof BigDecimal) {
            final BigDecimal cValue = (BigDecimal) value;
            return checkedCastNoOverflow(cValue);
        } else if (value instanceof BigInteger) {
            final BigInteger cValue = (BigInteger) value;
            return checkedCastNoOverflow(cValue);
        } else {
            //fallback to double
            final double doubleValue = value.doubleValue();
            return checkedCastNoOverflow(doubleValue);
        }
    }

    public static int checkedCast(final CharSequence value) {
        if (value.length() != 1) {
            throw new IllegalArgumentException("Expecting exactly one character: " + value);
        }
        return checkedCast(value.charAt(0));
    }

    public static int checkedCast(final Boolean value) {
        if (value == Boolean.TRUE) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int checkedCast(final boolean value) {
        if (value) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int checkedCast(final Character value) {
        return checkedCast((char) value);
    }

    public static int checkedCast(final char value) {
        return value;
    }

    public static int checkedCast(final Byte value) {
        return checkedCast((byte) value);
    }

    public static int checkedCast(final byte value) {
        return value;
    }

    public static int checkedCast(final Short value) {
        return checkedCast((short) value);
    }

    public static int checkedCast(final short value) {
        return value;
    }

    public static int checkedCast(final Integer value) {
        return checkedCast((int) value);
    }

    public static int checkedCast(final int value) {
        return value;
    }

    public static int checkedCast(final Long value) {
        return checkedCast((long) value);
    }

    public static int checkedCastNoOverflow(final Long value) {
        return checkedCastNoOverflow((long) value);
    }

    public static int checkedCast(final long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("int overflow: " + value);
        }
        return (int) value;
    }

    public static int checkedCastNoOverflow(final long value) {
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) value;
    }

    public static int checkedCastNoOverflow(final Float value) {
        return checkedCastNoOverflow((float) value);
    }

    public static int checkedCast(final Float value) {
        return checkedCast((float) value);
    }

    public static int checkedCast(final float value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("int overflow: " + value);
        }
        return (int) value;
    }

    public static int checkedCastNoOverflow(final float value) {
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) value;
    }

    public static int checkedCast(final Double value) {
        return checkedCast((double) value);
    }

    public static int checkedCastNoOverflow(final Double value) {
        return checkedCastNoOverflow((double) value);
    }

    public static int checkedCast(final double value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("int overflow: " + value);
        }
        return (int) value;
    }

    public static int checkedCastNoOverflow(final double value) {
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) value;
    }

    public static int checkedCast(final ADecimal<?> value) {
        return checkedCast(value.getDefaultValue());
    }

    public static int checkedCastNoOverflow(final ADecimal<?> value) {
        return checkedCastNoOverflow(value.getDefaultValue());
    }

    public static int checkedCast(final BigDecimal value) {
        return checkedCast(value.doubleValue());
    }

    public static int checkedCastNoOverflow(final BigDecimal value) {
        return checkedCastNoOverflow(value.doubleValue());
    }

    public static int checkedCast(final BigInteger value) {
        return checkedCast(value.doubleValue());
    }

    public static int checkedCastNoOverflow(final BigInteger value) {
        return checkedCastNoOverflow(value.doubleValue());
    }

    //CHECKSTYLE:OFF
    public static int[] checkedCastVector(final Object value) {
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

    public static int[] checkedCastVector(final Object[] value) {
        if (value == null) {
            return null;
        }
        if (value.length == 1) {
            final Object firstValue = value[0];
            if (firstValue != null && firstValue.getClass().isArray()) {
                return checkedCastVector(firstValue);
            }
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final Boolean[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final BitSet value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.size()];
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCast(value.get(i));
        }
        return vector;
    }

    public static int[] checkedCastVector(final boolean[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastVector(list);
    }

    public static int[] checkedCastVector(final Iterable<?> value) {
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

    public static int[] checkedCastVector(final List<?> value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.size()];
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCast(value.get(i));
        }
        return vector;
    }

    public static int[] checkedCastVector(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastVector(cValue);
        }
        final int[] vector = new int[value.size()];
        final Iterator<?> iterator = value.iterator();
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCast(iterator.next());
        }
        return vector;
    }

    public static int[] checkedCastVector(final byte[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final Byte[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final Character[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final char[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final Short[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final short[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final int[] value) {
        return value;
    }

    public static int[] checkedCastVector(final Integer[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final Long[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final long[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final Float[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final float[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final Double[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final double[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final ADecimal<?>[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final BigDecimal[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static int[] checkedCastVector(final BigInteger[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    private static int[] checkedCastVector(final CharSequence value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length()];
        for (int i = 0; i < value.length(); i++) {
            vector[i] = checkedCast(value.charAt(i));
        }
        return vector;
    }

    private static int[] checkedCastVector(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final int[] vector = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    //CHECKSTYLE:OFF
    public static int[][] checkedCastMatrix(final Object value) {
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

    public static int[][] checkedCastMatrix(final Object[] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastMatrix(list);
    }

    public static int[][] checkedCastMatrix(final Iterable<?> value) {
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

    public static int[][] checkedCastMatrix(final List<?> value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.size()][];
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVector(value.get(row));
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastMatrix(cValue);
        }
        final int[][] matrix = new int[value.size()][];
        final Iterator<?> iterator = value.iterator();
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVector(iterator.next());
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final Byte[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final byte[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final Boolean[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final BitSet[] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final boolean[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final Character[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final char[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final Short[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final short[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final Integer[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final int[][] value) {
        return value;
    }

    public static int[][] checkedCastMatrix(final Long[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final long[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final Float[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final float[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final Double[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final double[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final ADecimal<?>[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final BigDecimal[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static int[][] checkedCastMatrix(final BigInteger[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    private static int[][] checkedCastMatrix(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    private static int[][] checkedCastMatrix(final CharSequence[][] value) {
        if (value == null) {
            return null;
        }
        final int[][] matrix = new int[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

}
