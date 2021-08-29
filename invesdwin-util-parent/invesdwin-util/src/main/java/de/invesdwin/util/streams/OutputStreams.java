package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

/**
 * Extracted from java.io.DataOutputStream
 */
@Immutable
public final class OutputStreams {

    private OutputStreams() {
    }

    /**
     * Writes the specified byte (the low eight bits of the argument <code>b</code>) to the underlying output stream. If
     * no exception is thrown, the counter <code>written</code> is incremented by <code>1</code>.
     * <p>
     * Implements the <code>write</code> method of <code>OutputStream</code>.
     *
     * @param b
     *            the <code>byte</code> to be written.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public static void write(final OutputStream out, final int b) throws IOException {
        out.write(b);
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code> to the underlying
     * output stream. If no exception is thrown, the counter <code>written</code> is incremented by <code>len</code>.
     *
     * @param b
     *            the data.
     * @param off
     *            the start offset in the data.
     * @param len
     *            the number of bytes to write.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public static void write(final OutputStream out, final byte[] b, final int off, final int len) throws IOException {
        out.write(b, off, len);
    }

    /**
     * Writes a <code>boolean</code> to the underlying output stream as a 1-byte value. The value <code>true</code> is
     * written out as the value <code>(byte)1</code>; the value <code>false</code> is written out as the value
     * <code>(byte)0</code>. If no exception is thrown, the counter <code>written</code> is incremented by
     * <code>1</code>.
     *
     * @param v
     *            a <code>boolean</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public static void writeBoolean(final OutputStream out, final boolean v) throws IOException {
        out.write(v ? 1 : 0);
    }

    /**
     * Writes out a <code>byte</code> to the underlying output stream as a 1-byte value. If no exception is thrown, the
     * counter <code>written</code> is incremented by <code>1</code>.
     *
     * @param v
     *            a <code>byte</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public static void writeByte(final OutputStream out, final int v) throws IOException {
        out.write(v);
    }

    /**
     * Writes a <code>short</code> to the underlying output stream as two bytes, high byte first. If no exception is
     * thrown, the counter <code>written</code> is incremented by <code>2</code>.
     *
     * @param v
     *            a <code>short</code> to be written.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public static void writeShort(final OutputStream out, final int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    /**
     * Writes a <code>char</code> to the underlying output stream as a 2-byte value, high byte first. If no exception is
     * thrown, the counter <code>written</code> is incremented by <code>2</code>.
     *
     * @param v
     *            a <code>char</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public static void writeChar(final OutputStream out, final int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    /**
     * Writes an <code>int</code> to the underlying output stream as four bytes, high byte first. If no exception is
     * thrown, the counter <code>written</code> is incremented by <code>4</code>.
     *
     * @param v
     *            an <code>int</code> to be written.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public static void writeInt(final OutputStream out, final int v) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    /**
     * Writes a <code>long</code> to the underlying output stream as eight bytes, high byte first. In no exception is
     * thrown, the counter <code>written</code> is incremented by <code>8</code>.
     *
     * @param v
     *            a <code>long</code> to be written.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public static void writeLong(final OutputStream out, final long v) throws IOException {
        out.write((byte) (v >>> 56));
        out.write((byte) (v >>> 48));
        out.write((byte) (v >>> 40));
        out.write((byte) (v >>> 32));
        out.write((byte) (v >>> 24));
        out.write((byte) (v >>> 16));
        out.write((byte) (v >>> 8));
        out.write((byte) (v >>> 0));
    }

    /**
     * Converts the float argument to an <code>int</code> using the <code>floatToIntBits</code> method in class
     * <code>Float</code>, and then writes that <code>int</code> value to the underlying output stream as a 4-byte
     * quantity, high byte first. If no exception is thrown, the counter <code>written</code> is incremented by
     * <code>4</code>.
     *
     * @param v
     *            a <code>float</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     * @see java.lang.Float#floatToIntBits(float)
     */
    public static void writeFloat(final OutputStream out, final float v) throws IOException {
        writeInt(out, Float.floatToIntBits(v));
    }

    /**
     * Converts the double argument to a <code>long</code> using the <code>doubleToLongBits</code> method in class
     * <code>Double</code>, and then writes that <code>long</code> value to the underlying output stream as an 8-byte
     * quantity, high byte first. If no exception is thrown, the counter <code>written</code> is incremented by
     * <code>8</code>.
     *
     * @param v
     *            a <code>double</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     * @see java.lang.Double#doubleToLongBits(double)
     */
    public static void writeDouble(final OutputStream out, final double v) throws IOException {
        writeLong(out, Double.doubleToLongBits(v));
    }

    /**
     * Writes a string to the underlying output stream as a sequence of characters. Each character is written to the
     * data output stream as if by the <code>writeChar</code> method. If no exception is thrown, the counter
     * <code>written</code> is incremented by twice the length of <code>s</code>.
     *
     * @param s
     *            a <code>String</code> value to be written.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.DataOutputStream#writeChar(int)
     * @see java.io.FilterOutputStream#out
     */
    public static void writeChars(final OutputStream out, final String s) throws IOException {
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            final int v = s.charAt(i);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 0) & 0xFF);
        }
    }

}
