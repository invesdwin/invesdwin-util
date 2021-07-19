package de.invesdwin.util.bean;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.builder.Builder;

@NotThreadSafe
public class ReusableHashCodeBuilder implements Builder<Integer> {
    public static final int INITIAL = 17;
    public static final int MULTIPLIER = 37;

    private int total = INITIAL;

    public void reset() {
        total = INITIAL;
    }

    public void reset(final int initialOddNumber) {
        total = initialOddNumber;
    }

    public ReusableHashCodeBuilder append(final boolean value) {
        total = total * MULTIPLIER + (value ? 0 : 1);
        return this;
    }

    public static int appendStatic(final int total, final boolean value) {
        return total * MULTIPLIER + (value ? 0 : 1);
    }

    public ReusableHashCodeBuilder append(final boolean[] array) {
        if (array == null) {
            total = total * MULTIPLIER;
        } else {
            for (int i = 0; i < array.length; i++) {
                append(array[i]);
            }
        }
        return this;
    }

    public static int appendStatic(final int total, final boolean[] array) {
        if (array == null) {
            return total * MULTIPLIER;
        } else {
            int newTotal = total;
            for (int i = 0; i < array.length; i++) {
                newTotal = appendStatic(newTotal, array[i]);
            }
            return newTotal;
        }
    }

    public ReusableHashCodeBuilder append(final byte value) {
        total = total * MULTIPLIER + value;
        return this;
    }

    public static int appendStatic(final int total, final byte value) {
        return total * MULTIPLIER + value;
    }

    public ReusableHashCodeBuilder append(final byte[] array) {
        if (array == null) {
            total = total * MULTIPLIER;
        } else {
            for (int i = 0; i < array.length; i++) {
                append(array[i]);
            }
        }
        return this;
    }

    public static int appendStatic(final int total, final byte[] array) {
        if (array == null) {
            return total * MULTIPLIER;
        } else {
            int newTotal = total;
            for (int i = 0; i < array.length; i++) {
                newTotal = appendStatic(newTotal, array[i]);
            }
            return newTotal;
        }
    }

    public ReusableHashCodeBuilder append(final char value) {
        total = total * MULTIPLIER + value;
        return this;
    }

    public static int appendStatic(final int total, final char value) {
        return total * MULTIPLIER + value;
    }

    public ReusableHashCodeBuilder append(final char[] array) {
        if (array == null) {
            total = total * MULTIPLIER;
        } else {
            for (int i = 0; i < array.length; i++) {
                append(array[i]);
            }
        }
        return this;
    }

    public static int appendStatic(final int total, final char[] array) {
        if (array == null) {
            return total * MULTIPLIER;
        } else {
            int newTotal = total;
            for (int i = 0; i < array.length; i++) {
                newTotal = appendStatic(newTotal, array[i]);
            }
            return newTotal;
        }
    }

    public ReusableHashCodeBuilder append(final double value) {
        return append(Double.doubleToLongBits(value));
    }

    public static int appendStatic(final int total, final double value) {
        return appendStatic(total, Double.doubleToLongBits(value));
    }

    public ReusableHashCodeBuilder append(final double[] array) {
        if (array == null) {
            total = total * MULTIPLIER;
        } else {
            for (int i = 0; i < array.length; i++) {
                append(array[i]);
            }
        }
        return this;
    }

    public static int appendStatic(final int total, final double[] array) {
        if (array == null) {
            return total * MULTIPLIER;
        } else {
            int newTotal = total;
            for (int i = 0; i < array.length; i++) {
                newTotal = appendStatic(newTotal, array[i]);
            }
            return newTotal;
        }
    }

    public ReusableHashCodeBuilder append(final float value) {
        total = total * MULTIPLIER + Float.floatToIntBits(value);
        return this;
    }

    public static int appendStatic(final int total, final float value) {
        return total * MULTIPLIER + Float.floatToIntBits(value);
    }

    public ReusableHashCodeBuilder append(final float[] array) {
        if (array == null) {
            total = total * MULTIPLIER;
        } else {
            for (int i = 0; i < array.length; i++) {
                append(array[i]);
            }
        }
        return this;
    }

    public static int appendStatic(final int total, final float[] array) {
        if (array == null) {
            return total * MULTIPLIER;
        } else {
            int newTotal = total;
            for (int i = 0; i < array.length; i++) {
                newTotal = appendStatic(newTotal, array[i]);
            }
            return newTotal;
        }
    }

    public ReusableHashCodeBuilder append(final int value) {
        total = total * MULTIPLIER + value;
        return this;
    }

    public static int appendStatic(final int total, final int value) {
        return total * MULTIPLIER + value;
    }

    public ReusableHashCodeBuilder append(final int[] array) {
        if (array == null) {
            total = total * MULTIPLIER;
        } else {
            for (int i = 0; i < array.length; i++) {
                append(array[i]);
            }
        }
        return this;
    }

    public static int appendStatic(final int total, final int[] array) {
        if (array == null) {
            return total * MULTIPLIER;
        } else {
            int newTotal = total;
            for (int i = 0; i < array.length; i++) {
                newTotal = appendStatic(newTotal, array[i]);
            }
            return newTotal;
        }
    }

    public ReusableHashCodeBuilder append(final long value) {
        total = total * MULTIPLIER + ((int) (value ^ (value >>> 32)));
        return this;
    }

    public static int appendStatic(final int total, final long value) {
        return total * MULTIPLIER + ((int) (value ^ (value >>> 32)));
    }

    public ReusableHashCodeBuilder append(final long[] array) {
        if (array == null) {
            total = total * MULTIPLIER;
        } else {
            for (int i = 0; i < array.length; i++) {
                append(array[i]);
            }
        }
        return this;
    }

    public static int appendStatic(final int total, final long[] array) {
        if (array == null) {
            return total * MULTIPLIER;
        } else {
            int newTotal = total;
            for (int i = 0; i < array.length; i++) {
                newTotal = appendStatic(newTotal, array[i]);
            }
            return newTotal;
        }
    }

    public ReusableHashCodeBuilder append(final Object object) {
        if (object == null) {
            total = total * MULTIPLIER;
        } else {
            if (object.getClass().isArray()) {
                // factor out array case in order to keep method small enough
                // to be inlined
                appendArray(object);
            } else {
                total = total * MULTIPLIER + object.hashCode();
            }
        }
        return this;
    }

    public static int appendStatic(final int total, final Object object) {
        if (object == null) {
            return total * MULTIPLIER;
        } else {
            if (object.getClass().isArray()) {
                // factor out array case in order to keep method small enough
                // to be inlined
                return appendArrayStatic(total, object);
            } else {
                return total * MULTIPLIER + object.hashCode();
            }
        }
    }

    private void appendArray(final Object object) {
        // 'Switch' on type of array, to dispatch to the correct handler
        // This handles multi dimensional arrays
        if (object instanceof long[]) {
            append((long[]) object);
        } else if (object instanceof int[]) {
            append((int[]) object);
        } else if (object instanceof short[]) {
            append((short[]) object);
        } else if (object instanceof char[]) {
            append((char[]) object);
        } else if (object instanceof byte[]) {
            append((byte[]) object);
        } else if (object instanceof double[]) {
            append((double[]) object);
        } else if (object instanceof float[]) {
            append((float[]) object);
        } else if (object instanceof boolean[]) {
            append((boolean[]) object);
        } else {
            // Not an array of primitives
            append((Object[]) object);
        }
    }

    private static int appendArrayStatic(final int total, final Object object) {
        // 'Switch' on type of array, to dispatch to the correct handler
        // This handles multi dimensional arrays
        if (object instanceof long[]) {
            return appendStatic(total, (long[]) object);
        } else if (object instanceof int[]) {
            return appendStatic(total, (int[]) object);
        } else if (object instanceof short[]) {
            return appendStatic(total, object);
        } else if (object instanceof char[]) {
            return appendStatic(total, (char[]) object);
        } else if (object instanceof byte[]) {
            return appendStatic(total, (byte[]) object);
        } else if (object instanceof double[]) {
            return appendStatic(total, (double[]) object);
        } else if (object instanceof float[]) {
            return appendStatic(total, (float[]) object);
        } else if (object instanceof boolean[]) {
            return appendStatic(total, (boolean[]) object);
        } else {
            // Not an array of primitives
            return appendStatic(total, (Object[]) object);
        }
    }

    public ReusableHashCodeBuilder append(final Object[] array) {
        if (array == null) {
            total = total * MULTIPLIER;
        } else {
            for (int i = 0; i < array.length; i++) {
                append(array[i]);
            }
        }
        return this;
    }

    public static int appendStatic(final int total, final Object[] array) {
        if (array == null) {
            return total * MULTIPLIER;
        } else {
            int newTotal = total;
            for (int i = 0; i < array.length; i++) {
                newTotal = appendStatic(newTotal, array[i]);
            }
            return newTotal;
        }
    }

    public static int appendStatic(final int total, final short value) {
        return total * MULTIPLIER + value;
    }

    public ReusableHashCodeBuilder append(final short[] array) {
        if (array == null) {
            total = total * MULTIPLIER;
        } else {
            for (int i = 0; i < array.length; i++) {
                append(array[i]);
            }
        }
        return this;
    }

    public static int appendStatic(final int total, final short[] array) {
        if (array == null) {
            return total * MULTIPLIER;
        } else {
            int newTotal = total;
            for (int i = 0; i < array.length; i++) {
                newTotal = appendStatic(newTotal, array[i]);
            }
            return newTotal;
        }
    }

    public ReusableHashCodeBuilder appendSuper(final int superHashCode) {
        total = total * MULTIPLIER + superHashCode;
        return this;
    }

    public static int appendSuperStatic(final int total, final int superHashCode) {
        return total * MULTIPLIER + superHashCode;
    }

    public int toHashCode() {
        return total;
    }

    @Override
    public Integer build() {
        return Integer.valueOf(toHashCode());
    }

    //CHECKSTYLE:OFF
    @Override
    public int hashCode() {
        //CHECKSTYLE:ON
        return toHashCode();
    }

}
