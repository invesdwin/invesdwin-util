package de.invesdwin.util.streams;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.Immutable;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * Extracted from java.io.DataInputStream
 */
@Immutable
public final class InputStreams {

    private static final FastThreadLocal<byte[]> LONG_BUFFER_HOLDER = new FastThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() throws Exception {
            return new byte[8];
        }
    };

    private InputStreams() {
    }

    /**
     * Reads some number of bytes from the contained input stream and stores them into the buffer array <code>b</code>.
     * The number of bytes actually read is returned as an integer. This method blocks until input data is available,
     * end of file is detected, or an exception is thrown.
     *
     * <p>
     * If <code>b</code> is null, a <code>NullPointerException</code> is thrown. If the length of <code>b</code> is
     * zero, then no bytes are read and <code>0</code> is returned; otherwise, there is an attempt to read at least one
     * byte. If no byte is available because the stream is at end of file, the value <code>-1</code> is returned;
     * otherwise, at least one byte is read and stored into <code>b</code>.
     *
     * <p>
     * The first byte read is stored into element <code>b[0]</code>, the next one into <code>b[1]</code>, and so on. The
     * number of bytes read is, at most, equal to the length of <code>b</code>. Let <code>k</code> be the number of
     * bytes actually read; these bytes will be stored in elements <code>b[0]</code> through <code>b[k-1]</code>,
     * leaving elements <code>b[k]</code> through <code>b[b.length-1]</code> unaffected.
     *
     * <p>
     * The <code>read(b)</code> method has the same effect as: <blockquote>
     * 
     * <pre>
     * read(b, 0, b.length)
     * </pre>
     * 
     * </blockquote>
     *
     * @param b
     *            the buffer into which the data is read.
     * @return the total number of bytes read into the buffer, or <code>-1</code> if there is no more data because the
     *         end of the stream has been reached.
     * @exception IOException
     *                if the first byte cannot be read for any reason other than end of file, the stream has been closed
     *                and the underlying input stream does not support reading after close, or another I/O error occurs.
     * @see java.io.FilterInputStream#in
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public static int read(final InputStream in, final byte[] b) throws IOException {
        return in.read(b, 0, b.length);
    }

    /**
     * Reads up to <code>len</code> bytes of data from the contained input stream into an array of bytes. An attempt is
     * made to read as many as <code>len</code> bytes, but a smaller number may be read, possibly zero. The number of
     * bytes actually read is returned as an integer.
     *
     * <p>
     * This method blocks until input data is available, end of file is detected, or an exception is thrown.
     *
     * <p>
     * If <code>len</code> is zero, then no bytes are read and <code>0</code> is returned; otherwise, there is an
     * attempt to read at least one byte. If no byte is available because the stream is at end of file, the value
     * <code>-1</code> is returned; otherwise, at least one byte is read and stored into <code>b</code>.
     *
     * <p>
     * The first byte read is stored into element <code>b[off]</code>, the next one into <code>b[off+1]</code>, and so
     * on. The number of bytes read is, at most, equal to <code>len</code>. Let <i>k</i> be the number of bytes actually
     * read; these bytes will be stored in elements <code>b[off]</code> through
     * <code>b[off+</code><i>k</i><code>-1]</code>, leaving elements <code>b[off+</code><i>k</i><code>]</code> through
     * <code>b[off+len-1]</code> unaffected.
     *
     * <p>
     * In every case, elements <code>b[0]</code> through <code>b[off]</code> and elements <code>b[off+len]</code>
     * through <code>b[b.length-1]</code> are unaffected.
     *
     * @param b
     *            the buffer into which the data is read.
     * @param off
     *            the start offset in the destination array <code>b</code>
     * @param len
     *            the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or <code>-1</code> if there is no more data because the
     *         end of the stream has been reached.
     * @exception NullPointerException
     *                If <code>b</code> is <code>null</code>.
     * @exception IndexOutOfBoundsException
     *                If <code>off</code> is negative, <code>len</code> is negative, or <code>len</code> is greater than
     *                <code>b.length - off</code>
     * @exception IOException
     *                if the first byte cannot be read for any reason other than end of file, the stream has been closed
     *                and the underlying input stream does not support reading after close, or another I/O error occurs.
     * @see java.io.FilterInputStream#in
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public static int read(final InputStream in, final byte[] b, final int off, final int len) throws IOException {
        return in.read(b, off, len);
    }

    /**
     * See the general contract of the {@code readFully} method of {@code DataInput}.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @param b
     *            the buffer into which the data is read.
     * @throws NullPointerException
     *             if {@code b} is {@code null}.
     * @throws EOFException
     *             if this input stream reaches the end before reading all the bytes.
     * @throws IOException
     *             the stream has been closed and the contained input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public static void readFully(final InputStream in, final byte[] b) throws IOException {
        readFully(in, b, 0, b.length);
    }

    /**
     * See the general contract of the {@code readFully} method of {@code DataInput}.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @param b
     *            the buffer into which the data is read.
     * @param off
     *            the start offset in the data array {@code b}.
     * @param len
     *            the number of bytes to read.
     * @exception NullPointerException
     *                if {@code b} is {@code null}.
     * @exception IndexOutOfBoundsException
     *                if {@code off} is negative, {@code len} is negative, or {@code len} is greater than
     *                {@code b.length - off}.
     * @exception EOFException
     *                if this input stream reaches the end before reading all the bytes.
     * @exception IOException
     *                the stream has been closed and the contained input stream does not support reading after close, or
     *                another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public static void readFully(final InputStream in, final byte[] b, final int off, final int len)
            throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        while (n < len) {
            final int count = in.read(b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }

    /**
     * See the general contract of the <code>skipBytes</code> method of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @param n
     *            the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @exception IOException
     *                if the contained input stream does not support seek, or the stream has been closed and the
     *                contained input stream does not support reading after close, or another I/O error occurs.
     */
    public static int skipBytes(final InputStream in, final int n) throws IOException {
        int total = 0;
        int cur = 0;

        while ((total < n) && ((cur = (int) in.skip(n - total)) > 0)) {
            total += cur;
        }

        return total;
    }

    /**
     * See the general contract of the <code>readBoolean</code> method of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the <code>boolean</code> value read.
     * @exception EOFException
     *                if this input stream has reached the end.
     * @exception IOException
     *                the stream has been closed and the contained input stream does not support reading after close, or
     *                another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public static boolean readBoolean(final InputStream in) throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (ch != 0);
    }

    /**
     * See the general contract of the <code>readByte</code> method of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next byte of this input stream as a signed 8-bit <code>byte</code>.
     * @exception EOFException
     *                if this input stream has reached the end.
     * @exception IOException
     *                the stream has been closed and the contained input stream does not support reading after close, or
     *                another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public static byte readByte(final InputStream in) throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte) (ch);
    }

    /**
     * See the general contract of the <code>readUnsignedByte</code> method of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next byte of this input stream, interpreted as an unsigned 8-bit number.
     * @exception EOFException
     *                if this input stream has reached the end.
     * @exception IOException
     *                the stream has been closed and the contained input stream does not support reading after close, or
     *                another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public static int readUnsignedByte(final InputStream in) throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }

    /**
     * See the general contract of the <code>readShort</code> method of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next two bytes of this input stream, interpreted as a signed 16-bit number.
     * @exception EOFException
     *                if this input stream reaches the end before reading two bytes.
     * @exception IOException
     *                the stream has been closed and the contained input stream does not support reading after close, or
     *                another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public static short readShort(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short) ((ch1 << 8) + (ch2 << 0));
    }

    /**
     * See the general contract of the <code>readUnsignedShort</code> method of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next two bytes of this input stream, interpreted as an unsigned 16-bit integer.
     * @exception EOFException
     *                if this input stream reaches the end before reading two bytes.
     * @exception IOException
     *                the stream has been closed and the contained input stream does not support reading after close, or
     *                another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public static int readUnsignedShort(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch1 << 8) + (ch2 << 0);
    }

    /**
     * See the general contract of the <code>readChar</code> method of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next two bytes of this input stream, interpreted as a <code>char</code>.
     * @exception EOFException
     *                if this input stream reaches the end before reading two bytes.
     * @exception IOException
     *                the stream has been closed and the contained input stream does not support reading after close, or
     *                another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public static char readChar(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (char) ((ch1 << 8) + (ch2 << 0));
    }

    /**
     * See the general contract of the <code>readInt</code> method of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next four bytes of this input stream, interpreted as an <code>int</code>.
     * @exception EOFException
     *                if this input stream reaches the end before reading four bytes.
     * @exception IOException
     *                the stream has been closed and the contained input stream does not support reading after close, or
     *                another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public static int readInt(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        final int ch3 = in.read();
        final int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    /**
     * See the general contract of the <code>readLong</code> method of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next eight bytes of this input stream, interpreted as a <code>long</code>.
     * @exception EOFException
     *                if this input stream reaches the end before reading eight bytes.
     * @exception IOException
     *                the stream has been closed and the contained input stream does not support reading after close, or
     *                another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public static long readLong(final InputStream in) throws IOException {
        final byte[] readBuffer = LONG_BUFFER_HOLDER.get();
        readFully(in, readBuffer, 0, 8);
        //CHECKSTYLE:OFF
        return (((long) readBuffer[0] << 56) + ((long) (readBuffer[1] & 255) << 48)
                + ((long) (readBuffer[2] & 255) << 40) + ((long) (readBuffer[3] & 255) << 32)
                + ((long) (readBuffer[4] & 255) << 24) + ((readBuffer[5] & 255) << 16) + ((readBuffer[6] & 255) << 8)
                + ((readBuffer[7] & 255) << 0));
        //CHECKSTYLE:ON
    }

    /**
     * See the general contract of the <code>readFloat</code> method of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next four bytes of this input stream, interpreted as a <code>float</code>.
     * @exception EOFException
     *                if this input stream reaches the end before reading four bytes.
     * @exception IOException
     *                the stream has been closed and the contained input stream does not support reading after close, or
     *                another I/O error occurs.
     * @see java.io.DataInputStream#readInt()
     * @see java.lang.Float#intBitsToFloat(int)
     */
    public static float readFloat(final InputStream in) throws IOException {
        return Float.intBitsToFloat(readInt(in));
    }

    /**
     * See the general contract of the <code>readDouble</code> method of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next eight bytes of this input stream, interpreted as a <code>double</code>.
     * @exception EOFException
     *                if this input stream reaches the end before reading eight bytes.
     * @exception IOException
     *                the stream has been closed and the contained input stream does not support reading after close, or
     *                another I/O error occurs.
     * @see java.io.DataInputStream#readLong()
     * @see java.lang.Double#longBitsToDouble(long)
     */
    public static double readDouble(final InputStream in) throws IOException {
        return Double.longBitsToDouble(readLong(in));
    }

}
