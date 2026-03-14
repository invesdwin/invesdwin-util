package de.invesdwin.util.collections.primitive.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;

/**
 * Utility methods for packing/unpacking primitive values in/out of byte arrays using {@linkplain ByteOrder#BIG_ENDIAN
 * big endian order} (aka. "network order").
 * <p>
 * All methods in this class will throw an {@linkplain NullPointerException} if {@code null} is passed in as a method
 * parameter for a byte array.
 *
 * @see java.io.DataOutputStream
 * @see java.io.DataInputStream
 * @see jdk.internal.util.ByteArray
 * @see jdk.internal.util.ByteArrayLittleEndian
 * @see com.dynatrace.hash4j.internal.ByteArrayUtil
 */
@Immutable
public final class DirectByteArrayAccess {
    private static final VarHandle SHORT = create(short[].class);
    private static final VarHandle CHAR = create(char[].class);
    private static final VarHandle INT = create(int[].class);
    private static final VarHandle FLOAT = create(float[].class);
    private static final VarHandle LONG = create(long[].class);
    private static final VarHandle DOUBLE = create(double[].class);

    private DirectByteArrayAccess() {}

    // Methods for unpacking primitive values from byte arrays starting at  a given offset.

    /**
     * {@return a {@code boolean} from the provided {@code array} at the given {@code offset}}.
     *
     * @param array
     *            to read a value from.
     * @param offset
     *            where extraction in the array should begin
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 1]
     * @see #setBoolean(byte[], int, boolean)
     */
    public static boolean getBoolean(final byte[] array, final int offset) {
        return array[offset] != 0;
    }

    /**
     * {@return a {@code char} from the provided {@code array} at the given {@code offset} using big endian order}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to get a value from.
     * @param offset
     *            where extraction in the array should begin
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 2]
     * @see #setChar(byte[], int, char)
     */
    public static char getChar(final byte[] array, final int offset) {
        return (char) CHAR.get(array, offset);
    }

    /**
     * {@return a {@code short} from the provided {@code array} at the given {@code offset} using big endian order}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to get a value from.
     * @param offset
     *            where extraction in the array should begin
     * @return a {@code short} from the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 2]
     * @see #setShort(byte[], int, short)
     */
    public static short getShort(final byte[] array, final int offset) {
        return (short) SHORT.get(array, offset);
    }

    /**
     * {@return an {@code unsigned short} from the provided {@code array} at the given {@code offset} using big endian
     * order}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to get a value from.
     * @param offset
     *            where extraction in the array should begin
     * @return an {@code int} representing an unsigned short from the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 2]
     * @see #setUnsignedShort(byte[], int, int)
     */
    public static int getUnsignedShort(final byte[] array, final int offset) {
        return Short.toUnsignedInt((short) SHORT.get(array, offset));
    }

    /**
     * {@return an {@code int} from the provided {@code array} at the given {@code offset} using big endian order}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to get a value from.
     * @param offset
     *            where extraction in the array should begin
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 4]
     * @see #setInt(byte[], int, int)
     */
    public static int getInt(final byte[] array, final int offset) {
        return (int) INT.get(array, offset);
    }

    /**
     * {@return a {@code float} from the provided {@code array} at the given {@code offset} using big endian order}.
     * <p>
     * Variants of {@linkplain Float#NaN } values are canonized to a single NaN value.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to get a value from.
     * @param offset
     *            where extraction in the array should begin
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 4]
     * @see #setFloat(byte[], int, float)
     */
    public static float getFloat(final byte[] array, final int offset) {
        // Using Float.intBitsToFloat collapses NaN values to a single
        // "canonical" NaN value
        return Float.intBitsToFloat((int) INT.get(array, offset));
    }

    /**
     * {@return a {@code float} from the provided {@code array} at the given {@code offset} using big endian order}.
     * <p>
     * Variants of {@linkplain Float#NaN } values are silently read according to their bit patterns.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to get a value from.
     * @param offset
     *            where extraction in the array should begin
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 4]
     * @see #setFloatRaw(byte[], int, float)
     */
    public static float getFloatRaw(final byte[] array, final int offset) {
        // Just gets the bits as they are
        return (float) FLOAT.get(array, offset);
    }

    /**
     * {@return a {@code long} from the provided {@code array} at the given {@code offset} using big endian order}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to get a value from.
     * @param offset
     *            where extraction in the array should begin
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 8]
     * @see #setLong(byte[], int, long)
     */
    public static long getLong(final byte[] array, final int offset) {
        return (long) LONG.get(array, offset);
    }

    /**
     * {@return a {@code double} from the provided {@code array} at the given {@code offset} using big endian order}.
     * <p>
     * Variants of {@linkplain Double#NaN } values are canonized to a single NaN value.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to get a value from.
     * @param offset
     *            where extraction in the array should begin
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 8]
     * @see #setDouble(byte[], int, double)
     * @see #getDoubleRaw(byte[], int)
     */
    public static double getDouble(final byte[] array, final int offset) {
        // Using Double.longBitsToDouble collapses NaN values to a single "canonical" NaN value
        return Double.longBitsToDouble((long) LONG.get(array, offset));
    }

    /**
     * {@return a {@code double} from the provided {@code array} at the given {@code offset} using big endian order}.
     * <p>
     * Variants of {@linkplain Double#NaN } values are silently read according to their bit patterns.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to get a value from.
     * @param offset
     *            where extraction in the array should begin
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 8]
     * @see #setDoubleRaw(byte[], int, double)
     * @see #getDouble(byte[], int)
     */
    public static double getDoubleRaw(final byte[] array, final int offset) {
        // Just gets the bits as they are
        return (double) DOUBLE.get(array, offset);
    }

    // Methods for packing primitive values into byte arrays starting at a given * offset.

    /**
     * Sets (writes) the provided {@code value} into the provided {@code array} beginning at the given {@code offset}.
     *
     * @param array
     *            to set (write) a value into
     * @param offset
     *            where setting (writing) in the array should begin
     * @param value
     *            value to set in the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length]
     * @see #getBoolean(byte[], int)
     */
    public static void setBoolean(final byte[] array, final int offset, final boolean value) {
        array[offset] = (byte) (value ? 1 : 0);
    }

    /**
     * Sets (writes) the provided {@code value} using big endian order into the provided {@code array} beginning at the
     * given {@code offset}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to set (write) a value into
     * @param offset
     *            where setting (writing) in the array should begin
     * @param value
     *            value to set in the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 2]
     * @see #getChar(byte[], int)
     */
    public static void setChar(final byte[] array, final int offset, final char value) {
        CHAR.set(array, offset, value);
    }

    /**
     * Sets (writes) the provided {@code value} using big endian order into the provided {@code array} beginning at the
     * given {@code offset}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to set (write) a value into
     * @param offset
     *            where setting (writing) in the array should begin
     * @param value
     *            value to set in the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 2]
     * @see #getShort(byte[], int)
     */
    public static void setShort(final byte[] array, final int offset, final short value) {
        SHORT.set(array, offset, value);
    }

    /**
     * Sets (writes) the provided {@code value} using big endian order into the provided {@code array} beginning at the
     * given {@code offset}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to set (write) a value into
     * @param offset
     *            where setting (writing) in the array should begin
     * @param value
     *            value to set in the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 2]
     * @see #getUnsignedShort(byte[], int)
     */
    public static void setUnsignedShort(final byte[] array, final int offset, final int value) {
        SHORT.set(array, offset, (short) (char) value);
    }

    /**
     * Sets (writes) the provided {@code value} using big endian order into the provided {@code array} beginning at the
     * given {@code offset}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to set (write) a value into
     * @param offset
     *            where setting (writing) in the array should begin
     * @param value
     *            value to set in the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 4]
     * @see #getInt(byte[], int)
     */
    public static void setInt(final byte[] array, final int offset, final int value) {
        INT.set(array, offset, value);
    }

    /**
     * Sets (writes) the provided {@code value} using big endian order into the provided {@code array} beginning at the
     * given {@code offset}.
     * <p>
     * Variants of {@linkplain Float#NaN } values are canonized to a single NaN value.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to set (write) a value into
     * @param offset
     *            where setting (writing) in the array should begin
     * @param value
     *            value to set in the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 2]
     * @see #getFloat(byte[], int)
     */
    public static void setFloat(final byte[] array, final int offset, final float value) {
        // Using Float.floatToIntBits collapses NaN values to a single
        // "canonical" NaN value
        INT.set(array, offset, Float.floatToIntBits(value));
    }

    /**
     * Sets (writes) the provided {@code value} using big endian order into the provided {@code array} beginning at the
     * given {@code offset}.
     * <p>
     * Variants of {@linkplain Float#NaN } values are silently written according to their bit patterns.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to set (write) a value into
     * @param offset
     *            where setting (writing) in the array should begin
     * @param value
     *            value to set in the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 2]
     * @see #getFloatRaw(byte[], int)
     */
    public static void setFloatRaw(final byte[] array, final int offset, final float value) {
        // Just sets the bits as they are
        FLOAT.set(array, offset, value);
    }

    /**
     * Sets (writes) the provided {@code value} using big endian order into the provided {@code array} beginning at the
     * given {@code offset}.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to set (write) a value into
     * @param offset
     *            where setting (writing) in the array should begin
     * @param value
     *            value to set in the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 4]
     * @see #getLong(byte[], int)
     */
    public static void setLong(final byte[] array, final int offset, final long value) {
        LONG.set(array, offset, value);
    }

    /**
     * Sets (writes) the provided {@code value} using big endian order into the provided {@code array} beginning at the
     * given {@code offset}.
     * <p>
     * Variants of {@linkplain Double#NaN } values are canonized to a single NaN value.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to set (write) a value into
     * @param offset
     *            where setting (writing) in the array should begin
     * @param value
     *            value to set in the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 2]
     * @see #getDouble(byte[], int)
     */
    public static void setDouble(final byte[] array, final int offset, final double value) {
        // Using Double.doubleToLongBits collapses NaN values to a single
        // "canonical" NaN value
        LONG.set(array, offset, Double.doubleToLongBits(value));
    }

    /**
     * Sets (writes) the provided {@code value} using big endian order into the provided {@code array} beginning at the
     * given {@code offset}.
     * <p>
     * Variants of {@linkplain Double#NaN } values are silently written according to their bit patterns.
     * <p>
     * There are no access alignment requirements.
     *
     * @param array
     *            to set (write) a value into
     * @param offset
     *            where setting (writing) in the array should begin
     * @param value
     *            value to set in the array
     * @throws IndexOutOfBoundsException
     *             if the provided {@code offset} is outside the range [0, array.length - 2]
     * @see #getDoubleRaw(byte[], int)
     */
    public static void setDoubleRaw(final byte[] array, final int offset, final double value) {
        // Just sets the bits as they are
        DOUBLE.set(array, offset, value);
    }

    private static VarHandle create(final Class<?> viewArrayClass) {
        return MethodHandles.byteArrayViewVarHandle(viewArrayClass, ByteOrder.BIG_ENDIAN);
    }

    public static UUID getUUID(final byte[] array, final int offset) {
        final long mostSigBits = getLong(array, offset);
        final long leastSigBits = getLong(array, offset + 8);
        return new UUID(mostSigBits, leastSigBits);
    }

    public static void setUUID(final byte[] array, final int offset, final UUID uuid) {
        setLong(array, offset, uuid.getMostSignificantBits());
        setLong(array, offset + 8, uuid.getLeastSignificantBits());
    }

    /**
     * Reads a {@code long} value from a {@link CharSequence} with given offset.
     *
     * @param cs
     *            a char sequence
     * @param off
     *            an offset
     * @return the value
     */
    public static long getLong(final CharSequence cs, final int off) {
        return ((long) cs.charAt(off) << 48) | ((long) cs.charAt(off + 1) << 32) | ((long) cs.charAt(off + 2) << 16)
                | cs.charAt(off + 3);
    }

    /**
     * Reads an {@code int} value from a {@link CharSequence} with given offset.
     *
     * @param cs
     *            a char sequence
     * @param off
     *            an offset
     * @return the value
     */
    public static int getInt(final CharSequence cs, final int off) {
        return (cs.charAt(off) << 16) | cs.charAt(off + 1);
    }

    /**
     * Copies a given number of characters from a {@link CharSequence} into a byte array.
     * 
     * 😳 CharSequence vs String cs → speed is same
     * 
     * @param cs
     *            a char sequence
     * @param offsetCharSequence
     *            an offset for the char sequence
     * @param toByteArray
     *            a byte array
     * @param offsetByteArray
     *            an offset for the byte array
     * @param numChars
     *            the number of characters to copy
     */
    public static void copyCharsToByteArray(final CharSequence cs, final int offsetCharSequence,
            final byte[] toByteArray, final int offsetByteArray, final int numChars) {
        for (int charIdx = 0; charIdx <= numChars - 4; charIdx += 4) {
            setLong(toByteArray, offsetByteArray + (charIdx << 1), getLong(cs, offsetCharSequence + charIdx));
        }

        if ((numChars & 2) != 0) {
            final int charIdx = numChars & 0xFFFF_FFFC;
            setInt(toByteArray, offsetByteArray + (charIdx << 1), getInt(cs, offsetCharSequence + charIdx));
        }

        if ((numChars & 1) != 0) {
            final int charIdx = numChars & 0xFFFF_FFFE;
            setChar(toByteArray, offsetByteArray + (charIdx << 1), cs.charAt(offsetCharSequence + charIdx));
        }
    }
}//DirectByteArrayAccess