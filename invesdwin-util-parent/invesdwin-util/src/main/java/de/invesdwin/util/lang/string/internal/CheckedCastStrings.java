// CHECKSTYLE:OFF file length
package de.invesdwin.util.lang.string.internal;
// CHECKSTYLE:ON

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Characters;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@Immutable
public final class CheckedCastStrings {

    private CheckedCastStrings() {}

    public static String checkedCast(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
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
        } else if (value instanceof String) {
            final String cValue = (String) value;
            return checkedCast(cValue);
        } else if (value instanceof CharSequence) {
            final CharSequence cValue = (CharSequence) value;
            return checkedCast(cValue);
        } else if (value instanceof char[]) {
            final char[] cValue = (char[]) value;
            return checkedCast(cValue);
        } else if (value instanceof Character[]) {
            final Character[] cValue = (Character[]) value;
            return checkedCast(cValue);
        } else if (value instanceof byte[]) {
            final byte[] cValue = (byte[]) value;
            return checkedCast(cValue);
        } else if (value instanceof Byte[]) {
            final Byte[] cValue = (Byte[]) value;
            return checkedCast(cValue);
        } else if (value instanceof Object[]) {
            final Object[] cValue = (Object[]) value;
            return checkedCast(cValue);
        } else if (value instanceof boolean[]) {
            final boolean[] cValue = (boolean[]) value;
            return checkedCast(cValue);
        } else if (value instanceof short[]) {
            final short[] cValue = (short[]) value;
            return checkedCast(cValue);
        } else if (value instanceof int[]) {
            final int[] cValue = (int[]) value;
            return checkedCast(cValue);
        } else if (value instanceof long[]) {
            final long[] cValue = (long[]) value;
            return checkedCast(cValue);
        } else if (value instanceof float[]) {
            final float[] cValue = (float[]) value;
            return checkedCast(cValue);
        } else if (value instanceof double[]) {
            final double[] cValue = (double[]) value;
            return checkedCast(cValue);
        } else {
            //fallback to toString()
            return String.valueOf(value);
        }
    }

    public static String checkedCast(final Object[] value) {
        if (value == null) {
            return null;
        } else if (value.length == 1) {
            return checkedCast(value[0]);
        } else {
            return Arrays.toString(value);
        }
    }

    public static String checkedCast(final boolean[] value) {
        if (value == null) {
            return null;
        } else if (value.length == 1) {
            return checkedCast(value[0]);
        } else {
            return Arrays.toString(value);
        }
    }

    public static String checkedCast(final short[] value) {
        if (value == null) {
            return null;
        } else if (value.length == 1) {
            return checkedCast(value[0]);
        } else {
            return Arrays.toString(value);
        }
    }

    public static String checkedCast(final int[] value) {
        if (value == null) {
            return null;
        } else if (value.length == 1) {
            return checkedCast(value[0]);
        } else {
            return Arrays.toString(value);
        }
    }

    public static String checkedCast(final long[] value) {
        if (value == null) {
            return null;
        } else if (value.length == 1) {
            return checkedCast(value[0]);
        } else {
            return Arrays.toString(value);
        }
    }

    public static String checkedCast(final float[] value) {
        if (value == null) {
            return null;
        } else if (value.length == 1) {
            return checkedCast(value[0]);
        } else {
            return Arrays.toString(value);
        }
    }

    public static String checkedCast(final double[] value) {
        if (value == null) {
            return null;
        } else if (value.length == 1) {
            return checkedCast(value[0]);
        } else {
            return Arrays.toString(value);
        }
    }

    public static String checkedCast(final Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Double) {
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

    public static String checkedCast(final Character[] value) {
        if (value == null) {
            return null;
        }
        return checkedCast(Characters.checkedCastVector(value));
    }

    public static String checkedCast(final char[] value) {
        if (value == null) {
            return null;
        }
        return new String(value);
    }

    public static String checkedCast(final byte[] value) {
        if (value == null) {
            return null;
        }
        return new String(value);
    }

    public static String checkedCast(final Byte[] value) {
        if (value == null) {
            return null;
        }
        return checkedCast(Bytes.checkedCastVectorObj(value));
    }

    public static String checkedCast(final String value) {
        return value;
    }

    public static String checkedCast(final CharSequence value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static String checkedCast(final Boolean value) {
        if (value == null) {
            return null;
        }
        return checkedCast((boolean) value);
    }

    public static String checkedCast(final boolean value) {
        return String.valueOf(value);
    }

    public static String checkedCast(final Character value) {
        if (value == null) {
            return null;
        }
        return checkedCast((char) value);
    }

    public static String checkedCast(final char value) {
        return String.valueOf(value);
    }

    public static String checkedCast(final Byte value) {
        if (value == null) {
            return null;
        }
        return checkedCast((byte) value);
    }

    public static String checkedCast(final byte value) {
        return String.valueOf(value);
    }

    public static String checkedCast(final Short value) {
        if (value == null) {
            return null;
        }
        return checkedCast((short) value);
    }

    public static String checkedCast(final short value) {
        return String.valueOf(value);
    }

    public static String checkedCast(final Integer value) {
        if (value == null) {
            return null;
        }
        return checkedCast((int) value);
    }

    public static String checkedCast(final int value) {
        return String.valueOf(value);
    }

    public static String checkedCast(final Long value) {
        if (value == null) {
            return null;
        }
        return checkedCast((long) value);
    }

    public static String checkedCast(final long value) {
        return String.valueOf(value);
    }

    public static String checkedCast(final Float value) {
        if (value == null) {
            return null;
        }
        return checkedCast((float) value);
    }

    public static String checkedCast(final float value) {
        return String.valueOf(value);
    }

    public static String checkedCast(final Double value) {
        if (value == null) {
            return null;
        }
        return checkedCast((double) value);
    }

    public static String checkedCast(final double value) {
        return String.valueOf(value);
    }

    public static String checkedCast(final ADecimal<?> value) {
        if (value == null) {
            return null;
        }
        return checkedCast(value.getDefaultValue());
    }

    public static String checkedCast(final BigDecimal value) {
        if (value == null) {
            return null;
        }
        return checkedCast(value.doubleValue());
    }

    public static String checkedCast(final BigInteger value) {
        if (value == null) {
            return null;
        }
        return checkedCast(value.doubleValue());
    }

    //CHECKSTYLE:OFF
    public static String[] checkedCastVector(final Object value) {
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
        } else if (value instanceof Decimal[]) {
            final Decimal[] cValue = (Decimal[]) value;
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
        } else if (value instanceof String[]) {
            final String[] cValue = (String[]) value;
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

    public static String[] checkedCastVector(final Object[] value) {
        if (value == null) {
            return null;
        }
        if (value.length == 1) {
            final Object firstValue = value[0];
            if (firstValue != null && firstValue.getClass().isArray()) {
                return checkedCastVector(firstValue);
            }
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final Boolean[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final boolean[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastVector(list);
    }

    public static String[] checkedCastVector(final Iterable<?> value) {
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

    public static String[] checkedCastVector(final List<?> value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.size()];
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCast(value.get(i));
        }
        return vector;
    }

    public static String[] checkedCastVector(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastVector(cValue);
        }
        final String[] vector = new String[value.size()];
        final Iterator<?> iterator = value.iterator();
        for (int i = 0; i < value.size(); i++) {
            vector[i] = checkedCast(iterator.next());
        }
        return vector;
    }

    public static String[] checkedCastVector(final byte[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final Byte[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final Character[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final char[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final Short[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final short[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final int[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final Integer[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final Long[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final long[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final Float[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final float[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final Double[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final double[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final Decimal[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final BigDecimal[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final BigInteger[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    public static String[] checkedCastVector(final CharSequence value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length()];
        for (int i = 0; i < value.length(); i++) {
            vector[i] = checkedCast(value.charAt(i));
        }
        return vector;
    }

    public static String[] checkedCastVector(final String[] value) {
        return value;
    }

    public static String[] checkedCastVector(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final String[] vector = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            vector[i] = checkedCast(value[i]);
        }
        return vector;
    }

    //CHECKSTYLE:OFF
    public static String[][] checkedCastMatrix(final Object value) {
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
        } else if (value instanceof Decimal[][]) {
            final Decimal[][] cValue = (Decimal[][]) value;
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
        } else if (value instanceof String[][]) {
            final String[][] cValue = (String[][]) value;
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

    public static String[][] checkedCastMatrix(final Object[] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final Iterator<?> value) {
        if (value == null) {
            return null;
        }
        final List<Object> list = new ArrayList<Object>();
        while (value.hasNext()) {
            list.add(value.next());
        }
        return checkedCastMatrix(list);
    }

    public static String[][] checkedCastMatrix(final Iterable<?> value) {
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

    public static String[][] checkedCastMatrix(final List<?> value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.size()][];
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVector(value.get(row));
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final Collection<?> value) {
        if (value == null) {
            return null;
        } else if (value instanceof List) {
            final List<?> cValue = (List<?>) value;
            return checkedCastMatrix(cValue);
        }
        final String[][] matrix = new String[value.size()][];
        final Iterator<?> iterator = value.iterator();
        for (int row = 0; row < value.size(); row++) {
            matrix[row] = checkedCastVector(iterator.next());
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final Byte[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final byte[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final Boolean[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final boolean[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final Character[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final char[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final Short[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final short[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final Integer[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final int[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final Long[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final long[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final Float[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final float[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final Double[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final double[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final Decimal[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final BigDecimal[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final BigInteger[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final CharSequence[] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

    public static String[][] checkedCastMatrix(final String[][] value) {
        return value;
    }

    public static String[][] checkedCastMatrix(final CharSequence[][] value) {
        if (value == null) {
            return null;
        }
        final String[][] matrix = new String[value.length][];
        for (int row = 0; row < value.length; row++) {
            matrix[row] = checkedCastVector(value[row]);
        }
        return matrix;
    }

}
