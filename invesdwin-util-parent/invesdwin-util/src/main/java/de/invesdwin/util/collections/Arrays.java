package de.invesdwin.util.collections;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.AArraysStaticFacade;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.time.date.BisectDuplicateKeyHandling;
import de.invesdwin.util.time.date.FDates;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.AArraysStaticFacade", targets = {
        java.util.Arrays.class, java.lang.reflect.Array.class, org.apache.commons.lang3.ArrayUtils.class,
        org.apache.commons.math3.util.MathArrays.class }, filterSeeMethodSignatures = {
                "org.apache.commons.lang3.ArrayUtils#newInstance(java.lang.Class, int)" })
public class Arrays extends AArraysStaticFacade {

    private static final ALoadingCache<Class<?>, Object> TYPE_EMPTYARRAYS = new ALoadingCache<Class<?>, Object>() {
        @Override
        protected Object loadValue(final Class<?> key) {
            return newInstance(key, 0);
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> T[] getEmptyArray(final Class<T> type) {
        return (T[]) TYPE_EMPTYARRAYS.get(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] concat(final Class<T> type, final T[]... arrays) {
        if (arrays.length == 0) {
            return (T[]) newInstance(type, 0);
        }

        int finalLength = 0;
        for (int i = 0; i < arrays.length; i++) {
            //should be an empty array
            finalLength += arrays[i].length;
        }

        if (finalLength == 0) {
            return arrays[0];
        }

        final T[] dest = (T[]) newInstance(type, finalLength);
        int destPos = 0;

        for (int i = 0; i < arrays.length; i++) {
            final T[] array = arrays[i];
            System.arraycopy(array, 0, dest, destPos, array.length);
            destPos += array.length;
        }
        return dest;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] concat(final T[]... arrays) {
        if (arrays.length == 0) {
            final Class<?> arrayType = arrays.getClass().getComponentType().getComponentType();
            return (T[]) newInstance(arrayType, 0);
        }

        int finalLength = 0;
        for (int i = 0; i < arrays.length; i++) {
            finalLength += arrays[i].length;
        }

        if (finalLength == 0) {
            //should be an empty array
            return arrays[0];
        }

        final T[] dest = Arrays.copyOf(arrays[0], finalLength);
        int destPos = arrays[0].length;

        for (int i = 1; i < arrays.length; i++) {
            final T[] array = arrays[i];
            System.arraycopy(array, 0, dest, destPos, array.length);
            destPos += array.length;
        }
        return dest;
    }

    @SafeVarargs
    public static <T> List<T> asList(final T... a) {
        if (a == null) {
            return null;
        }
        if (a.length == 0) {
            return Collections.emptyList();
        }
        return java.util.Arrays.asList(a);
    }

    public static boolean[][] clone(final boolean[][] array) {
        if (array == null) {
            return null;
        }
        final boolean[][] cloned = new boolean[array.length][];
        for (int i = 0; i < cloned.length; i++) {
            cloned[i] = clone(array[i]);
        }
        return cloned;
    }

    public static byte[][] clone(final byte[][] array) {
        if (array == null) {
            return null;
        }
        final byte[][] cloned = new byte[array.length][];
        for (int i = 0; i < cloned.length; i++) {
            cloned[i] = clone(array[i]);
        }
        return cloned;
    }

    public static char[][] clone(final char[][] array) {
        if (array == null) {
            return null;
        }
        final char[][] cloned = new char[array.length][];
        for (int i = 0; i < cloned.length; i++) {
            cloned[i] = clone(array[i]);
        }
        return cloned;
    }

    /** @see org.apache.commons.lang3.ArrayUtils#clone(double[]) */
    public static double[][] clone(final double[][] array) {
        if (array == null) {
            return null;
        }
        final double[][] cloned = new double[array.length][];
        for (int i = 0; i < cloned.length; i++) {
            cloned[i] = clone(array[i]);
        }
        return cloned;
    }

    public static float[][] clone(final float[][] array) {
        if (array == null) {
            return null;
        }
        final float[][] cloned = new float[array.length][];
        for (int i = 0; i < cloned.length; i++) {
            cloned[i] = clone(array[i]);
        }
        return cloned;
    }

    public static int[][] clone(final int[][] array) {
        if (array == null) {
            return null;
        }
        final int[][] cloned = new int[array.length][];
        for (int i = 0; i < cloned.length; i++) {
            cloned[i] = clone(array[i]);
        }
        return cloned;
    }

    public static long[][] clone(final long[][] array) {
        if (array == null) {
            return null;
        }
        final long[][] cloned = new long[array.length][];
        for (int i = 0; i < cloned.length; i++) {
            cloned[i] = clone(array[i]);
        }
        return cloned;
    }

    public static short[][] clone(final short[][] array) {
        if (array == null) {
            return null;
        }
        final short[][] cloned = new short[array.length][];
        for (int i = 0; i < cloned.length; i++) {
            cloned[i] = clone(array[i]);
        }
        return cloned;
    }

    public static <T> T[][] clone(final T[][] array) {
        if (array == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        final T[][] cloned = (T[][]) Arrays.newInstance(array.getClass().getComponentType(), array.length);
        for (int i = 0; i < cloned.length; i++) {
            cloned[i] = clone(array[i]);
        }
        return cloned;
    }

    public static <T> int bisect(final T[] values, final IComparator<T> comparator, final T skippingKeysAbove,
            final BisectDuplicateKeyHandling duplicateKeyHandling) {
        if (values.length == 0) {
            return FDates.MISSING_INDEX;
        }
        int lo = 0;
        final T firstKey = values[lo];
        if (comparator.compareTyped(firstKey, skippingKeysAbove) >= 0) {
            return duplicateKeyHandling.apply(values, comparator, lo, firstKey);
        }
        int hi = values.length;
        final int lastIndex = hi - 1;
        final T lastKey = values[lastIndex];
        if (comparator.compareTyped(lastKey, skippingKeysAbove) <= 0) {
            return duplicateKeyHandling.apply(values, comparator, lastIndex, lastKey);
        }
        while (lo < hi) {
            // same as (low+high)/2
            final int mid = (lo + hi) >>> 1;
            //if (x < list.get(mid)) {
            final T midKey = values[mid];
            final int compareTo = comparator.compareTyped(midKey, skippingKeysAbove);
            switch (compareTo) {
            case FDates.MISSING_INDEX:
                lo = mid + 1;
                break;
            case 0:
                return duplicateKeyHandling.apply(values, comparator, mid, midKey);
            case 1:
                hi = mid;
                break;
            default:
                throw UnknownArgumentException.newInstance(Integer.class, compareTo);
            }
        }
        if (lo <= 0) {
            return 0;
        }
        if (lo >= values.length) {
            lo = lo - 1;
        }
        final T loKey = values[lo];
        if (comparator.compareTypedNotNullSafe(loKey, skippingKeysAbove) > 0) {
            //no duplicate key handling needed because this is the last value before the actual requested key
            final int index = lo - 1;
            return index;
        } else {
            return duplicateKeyHandling.apply(values, comparator, lo, loKey);
        }
    }

    public static String matrixToString(final byte[][] matrix) {
        final int lastIndex = matrix.length - 1;
        final StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < matrix.length; i++) {
            final byte[] vector = matrix[i];
            sb.append("  ");
            sb.append(Arrays.toString(vector));
            if (i < lastIndex) {
                sb.append(",");
            }
            sb.append('\n');
        }
        sb.append("\n]");
        return sb.toString();
    }

    public static String matrixToString(final short[][] matrix) {
        final int lastIndex = matrix.length - 1;
        final StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < matrix.length; i++) {
            final short[] vector = matrix[i];
            sb.append("  ");
            sb.append(Arrays.toString(vector));
            if (i < lastIndex) {
                sb.append(",");
            }
            sb.append('\n');
        }
        sb.append("\n]");
        return sb.toString();
    }

    public static String matrixToString(final char[][] matrix) {
        final int lastIndex = matrix.length - 1;
        final StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < matrix.length; i++) {
            final char[] vector = matrix[i];
            sb.append("  ");
            sb.append(Arrays.toString(vector));
            if (i < lastIndex) {
                sb.append(",");
            }
            sb.append('\n');
        }
        sb.append("\n]");
        return sb.toString();
    }

    public static String matrixToString(final int[][] matrix) {
        final int lastIndex = matrix.length - 1;
        final StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < matrix.length; i++) {
            final int[] vector = matrix[i];
            sb.append("  ");
            sb.append(Arrays.toString(vector));
            if (i < lastIndex) {
                sb.append(",");
            }
            sb.append('\n');
        }
        sb.append("\n]");
        return sb.toString();
    }

    public static String matrixToString(final long[][] matrix) {
        final int lastIndex = matrix.length - 1;
        final StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < matrix.length; i++) {
            final long[] vector = matrix[i];
            sb.append("  ");
            sb.append(Arrays.toString(vector));
            if (i < lastIndex) {
                sb.append(",");
            }
            sb.append('\n');
        }
        sb.append("\n]");
        return sb.toString();
    }

    public static String matrixToString(final float[][] matrix) {
        final int lastIndex = matrix.length - 1;
        final StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < matrix.length; i++) {
            final float[] vector = matrix[i];
            sb.append("  ");
            sb.append(Arrays.toString(vector));
            if (i < lastIndex) {
                sb.append(",");
            }
            sb.append('\n');
        }
        sb.append("\n]");
        return sb.toString();
    }

    public static String matrixToString(final double[][] matrix) {
        final int lastIndex = matrix.length - 1;
        final StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < matrix.length; i++) {
            final double[] vector = matrix[i];
            sb.append("  ");
            sb.append(Arrays.toString(vector));
            if (i < lastIndex) {
                sb.append(",");
            }
            sb.append('\n');
        }
        sb.append("]");
        return sb.toString();
    }

    public static String matrixToString(final Object[][] matrix) {
        final int lastIndex = matrix.length - 1;
        final StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < matrix.length; i++) {
            final Object[] vector = matrix[i];
            sb.append("  ");
            sb.append(Arrays.toString(vector));
            if (i < lastIndex) {
                sb.append(",");
            }
            sb.append('\n');
        }
        sb.append("]");
        return sb.toString();
    }

}
