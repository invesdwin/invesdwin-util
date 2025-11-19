package de.invesdwin.util.streams;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UTFDataFormatException;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.future.throwing.IThrowingIORunnable;
import de.invesdwin.util.concurrent.future.throwing.IThrowingTimeoutRunnable;
import de.invesdwin.util.concurrent.loop.ASpinWait;
import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.error.FastTimeoutException;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.time.duration.Duration;
import io.netty.util.concurrent.FastThreadLocal;

/**
 * Extracted from java.io.DataInputStream
 */
@Immutable
public final class InputStreams {

    public static final InputStream[] EMPTY_ARRAY = new InputStream[0];
    public static final FastThreadLocal<byte[]> LONG_BUFFER_HOLDER = new FastThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() throws Exception {
            return ByteBuffers.allocateByteArray(8);
        }
    };
    private static final FastThreadLocal<char[]> LINE_BUFFER_HOLDER = new FastThreadLocal<char[]>() {
        @Override
        protected char[] initialValue() throws Exception {
            return new char[128];
        }
    };

    private InputStreams() {}

    public static String readLine(final InputStream pIn) throws IOException {
        InputStream in = pIn;
        char[] lineBuffer = LINE_BUFFER_HOLDER.get();
        char[] buf = lineBuffer;

        int room = buf.length;
        int offset = 0;
        int c;

        loop: while (true) {
            c = in.read();
            switch (c) {
            case -1:
            case '\n':
                break loop;

            case '\r':
                final int c2 = in.read();
                if ((c2 != '\n') && (c2 != -1)) {
                    if (!(in instanceof PushbackInputStream)) {
                        in = new PushbackInputStream(in);
                    }
                    ((PushbackInputStream) in).unread(c2);
                }
                break loop;

            default:
                if (--room < 0) {
                    buf = new char[offset + 128];
                    room = buf.length - offset - 1;
                    System.arraycopy(lineBuffer, 0, buf, 0, offset);
                    lineBuffer = buf;
                    LINE_BUFFER_HOLDER.set(lineBuffer);
                }
                buf[offset++] = (char) c;
                break;
            }
        }
        if ((c == -1) && (offset == 0)) {
            return null;
        }
        return String.copyValueOf(buf, 0, offset);
    }

    public static int read(final InputStream in, final byte[] b) throws IOException {
        return in.read(b, 0, b.length);
    }

    public static int read(final InputStream in, final byte[] b, final int off, final int len) throws IOException {
        return in.read(b, off, len);
    }

    public static int read(final DataInput in, final byte[] b, final int off, final int len) {
        checkFromIndexSize(off, len, b.length);
        if (len == 0) {
            return 0;
        }

        int i = 0;
        try {
            byte c = in.readByte();
            b[off] = c;
            i = 1;
            //CHECKSTYLE:OFF
            for (; i < len; i++) {
                //CHECKSTYLE:ON
                c = in.readByte();
                if (c == -1) {
                    break;
                }
                b[off + i] = c;
            }
        } catch (final IOException ee) {
        }
        return i;
    }

    public static int skipBytes(final InputStream in, final int n) throws IOException {
        int total = 0;
        int cur = 0;

        while ((total < n) && ((cur = (int) in.skip(n - total)) > 0)) {
            total += cur;
        }

        return total;
    }

    public static boolean readBoolean(final InputStream in) throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw FastEOFException.getInstance("end reached");
        }
        return (ch != 0);
    }

    public static byte readByte(final InputStream in) throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw FastEOFException.getInstance("end reached");
        }
        return (byte) (ch);
    }

    public static int readUnsignedByte(final InputStream in) throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw FastEOFException.getInstance("end reached");
        }
        return ch;
    }

    public static short readShort(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw FastEOFException.getInstance("end reached");
        }
        return (short) ((ch1 << 8) + (ch2 << 0));
    }

    public static int readUnsignedShort(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw FastEOFException.getInstance("end reached");
        }
        return (ch1 << 8) + (ch2 << 0);
    }

    public static char readChar(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw FastEOFException.getInstance("end reached");
        }
        return (char) ((ch1 << 8) + (ch2 << 0));
    }

    public static int readInt(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        final int ch3 = in.read();
        final int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw FastEOFException.getInstance("end reached");
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    public static long readLong(final InputStream in) throws IOException {
        final byte[] readBuffer = LONG_BUFFER_HOLDER.get();
        readFullyNoTimeout(in, readBuffer, 0, 8);
        //CHECKSTYLE:OFF
        return (((long) readBuffer[0] << 56) + ((long) (readBuffer[1] & 255) << 48)
                + ((long) (readBuffer[2] & 255) << 40) + ((long) (readBuffer[3] & 255) << 32)
                + ((long) (readBuffer[4] & 255) << 24) + ((readBuffer[5] & 255) << 16) + ((readBuffer[6] & 255) << 8)
                + ((readBuffer[7] & 255) << 0));
        //CHECKSTYLE:ON
    }

    public static float readFloat(final InputStream in) throws IOException {
        return Float.intBitsToFloat(readInt(in));
    }

    public static double readDouble(final InputStream in) throws IOException {
        return Double.longBitsToDouble(readLong(in));
    }

    public static void readFullyNoTimeout(final InputStream in, final byte[] b) throws IOException {
        try {
            readFully(in, b);
        } catch (final TimeoutException e) {
            throw newTimeoutEOF(e);
        }
    }

    public static void readFully(final InputStream in, final byte[] b) throws IOException, TimeoutException {
        readFully(in, b, 0, b.length);
    }

    public static void readFullyNoTimeout(final InputStream src, final byte[] array, final int index, final int length)
            throws IOException {
        final Duration timeout = URIs.getDefaultNetworkTimeout();
        try {
            readFully(src, array, index, length, timeout);
        } catch (final TimeoutException e) {
            throw newTimeoutEOF(e);
        }
    }

    public static void readFully(final InputStream src, final byte[] array, final int index, final int length)
            throws IOException, TimeoutException {
        final Duration timeout = URIs.getDefaultNetworkTimeout();
        readFully(src, array, index, length, timeout);
    }

    public static void readFully(final InputStream src, final byte[] array, final int index, final int length,
            final Duration timeout) throws IOException, TimeoutException {
        readFully(src, array, index, length, timeout, ASpinWait::onSpinWaitStatic, InputStreams::throwOnEOF,
                InputStreams::throwOnTimeout);
    }

    public static void readFully(final InputStream src, final byte[] array, final int index, final int length,
            final Duration timeout, final Runnable onSpinWaitF, final IThrowingIORunnable onEofF,
            final IThrowingTimeoutRunnable onTimeoutF) throws IOException, TimeoutException {
        final int read = readFullyIfPossible(src, array, index, length, timeout, onSpinWaitF, onEofF, onTimeoutF);
        if (read < length) {
            throw newEOF();
        }
    }

    public static void readFullyNoTimeout(final ReadableByteChannel src, final java.nio.ByteBuffer byteBuffer)
            throws IOException {
        try {
            readFully(src, byteBuffer);
        } catch (final TimeoutException e) {
            throw newTimeoutEOF(e);
        }
    }

    public static void readFully(final ReadableByteChannel src, final java.nio.ByteBuffer byteBuffer)
            throws IOException, TimeoutException {
        final Duration timeout = URIs.getDefaultNetworkTimeout();
        readFully(src, byteBuffer, timeout);
    }

    public static void readFully(final ReadableByteChannel src, final java.nio.ByteBuffer byteBuffer,
            final Duration timeout) throws IOException, TimeoutException {
        readFully(src, byteBuffer, timeout, ASpinWait::onSpinWaitStatic, InputStreams::throwOnEOF,
                InputStreams::throwOnTimeout);
    }

    public static void readFully(final ReadableByteChannel src, final java.nio.ByteBuffer byteBuffer,
            final Duration timeout, final Runnable onSpinWaitF, final IThrowingIORunnable onEofF,
            final IThrowingTimeoutRunnable onTimeoutF) throws IOException, TimeoutException {
        final int length = byteBuffer.remaining();
        final int read = readFullyIfPossible(src, byteBuffer, timeout, onSpinWaitF, onEofF, onTimeoutF);
        if (read < length) {
            throw newEOF();
        }
    }

    public static int readFullyIfPossible(final InputStream in, final byte[] b) throws IOException, TimeoutException {
        return readFullyIfPossible(in, b, 0, b.length);
    }

    public static int readFullyIfPossible(final InputStream src, final byte[] array, final int index, final int length)
            throws IOException, TimeoutException {
        final Duration timeout = URIs.getDefaultNetworkTimeout();
        return readFullyIfPossible(src, array, index, length, timeout);
    }

    public static int readFullyIfPossible(final InputStream src, final byte[] array, final int index, final int length,
            final Duration timeout) throws IOException, TimeoutException {
        return readFullyIfPossible(src, array, index, length, timeout, ASpinWait::onSpinWaitStatic,
                InputStreams::throwOnEOF, InputStreams::throwOnTimeout);
    }

    public static int readFullyIfPossible(final InputStream src, final byte[] array, final int index, final int length,
            final Duration timeout, final Runnable onSpinWaitF, final IThrowingIORunnable onEofF,
            final IThrowingTimeoutRunnable onTimeoutF) throws IOException, TimeoutException {
        long zeroCountNanos = -1L;

        final int end = index + length;
        int remaining = length;
        while (remaining > 0) {
            final int location = end - remaining;
            final int count = src.read(array, location, remaining);
            if (count < 0) { // EOF
                onEofF.run();
                break;
            }
            if (count == 0) {
                if (timeout != null) {
                    if (zeroCountNanos == -1) {
                        zeroCountNanos = System.nanoTime();
                    } else if (timeout.isLessThanNanos(System.nanoTime() - zeroCountNanos)) {
                        //timeout exceeded
                        onTimeoutF.run();
                        return length - remaining;
                    }
                }
                onSpinWaitF.run();
            } else {
                zeroCountNanos = -1L;
                remaining -= count;
            }
        }
        return length - remaining;
    }

    public static int readFullyIfPossible(final ReadableByteChannel src, final java.nio.ByteBuffer byteBuffer)
            throws IOException, TimeoutException {
        final Duration timeout = URIs.getDefaultNetworkTimeout();
        return readFullyIfPossible(src, byteBuffer, timeout);
    }

    public static int readFullyIfPossible(final ReadableByteChannel src, final java.nio.ByteBuffer byteBuffer,
            final Duration timeout) throws IOException, TimeoutException {
        return readFullyIfPossible(src, byteBuffer, timeout, ASpinWait::onSpinWaitStatic, InputStreams::throwOnEOF,
                InputStreams::throwOnTimeout);
    }

    public static int readFullyIfPossible(final ReadableByteChannel src, final java.nio.ByteBuffer byteBuffer,
            final Duration timeout, final Runnable onSpinWaitF, final IThrowingIORunnable onEofF,
            final IThrowingTimeoutRunnable onTimeoutF) throws IOException, TimeoutException {
        long zeroCountNanos = -1L;

        int remaining = byteBuffer.remaining();
        final int length = remaining;
        final int positionBefore = byteBuffer.position();
        while (remaining > 0) {
            final int count = src.read(byteBuffer);
            if (count < 0) { // EOF
                onEofF.run();
                break;
            }
            if (count == 0) {
                if (timeout != null) {
                    if (zeroCountNanos == -1) {
                        zeroCountNanos = System.nanoTime();
                    } else if (timeout.isLessThanNanos(System.nanoTime() - zeroCountNanos)) {
                        //timeout exceeded
                        onTimeoutF.run();
                        return length - remaining;
                    }
                }
                onSpinWaitF.run();
            } else {
                zeroCountNanos = -1L;
                remaining -= count;
            }
        }
        ByteBuffers.position(byteBuffer, positionBefore);
        return length - remaining;
    }

    /**
     * Reads from the stream {@code in} a representation of a Unicode character string encoded in
     * <a href="DataInput.html#modified-utf-8">modified UTF-8</a> format; this string of characters is then returned as
     * a {@code String}. The details of the modified UTF-8 representation are exactly the same as for the
     * {@code readUTF} method of {@code DataInput}.
     *
     * @param in
     *            a data input stream.
     * @return a Unicode string.
     * @throws EOFException
     *             if the input stream reaches the end before all the bytes.
     * @throws IOException
     *             the stream has been closed and the contained input stream does not support reading after close, or
     *             another I/O error occurs.
     * @throws UTFDataFormatException
     *             if the bytes do not represent a valid modified UTF-8 encoding of a Unicode string.
     * @see java.io.DataInputStream#readUnsignedShort()
     */
    public static String readUTF(final InputStream in) throws IOException {
        final int utflen = readUnsignedShort(in);
        try (ICloseableByteBuffer bytearr = ByteBuffers.EXPANDABLE_POOL.borrowObject()) {
            bytearr.ensureCapacity(utflen);
            final char[] chararr = new char[utflen];

            int c, char2, char3;
            int count = 0;
            int chararr_count = 0;

            bytearr.putBytesTo(0, in, utflen);

            while (count < utflen) {
                c = bytearr.getByte(count) & 0xff;
                if (c > 127) {
                    break;
                }
                count++;
                chararr[chararr_count++] = (char) c;
            }

            while (count < utflen) {
                c = bytearr.getByte(count) & 0xff;
                switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    /* 0xxxxxxx */
                    count++;
                    chararr[chararr_count++] = (char) c;
                    break;
                case 12:
                case 13:
                    /* 110x xxxx 10xx xxxx */
                    count += 2;
                    if (count > utflen) {
                        throw new UTFDataFormatException("malformed input: partial character at end");
                    }
                    char2 = bytearr.getByte(count - 1);
                    if ((char2 & 0xC0) != 0x80) {
                        throw new UTFDataFormatException("malformed input around byte " + count);
                    }
                    chararr[chararr_count++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
                    break;
                case 14:
                    /* 1110 xxxx 10xx xxxx 10xx xxxx */
                    count += 3;
                    if (count > utflen) {
                        throw new UTFDataFormatException("malformed input: partial character at end");
                    }
                    char2 = bytearr.getByte(count - 2);
                    char3 = bytearr.getByte(count - 1);
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                        throw new UTFDataFormatException("malformed input around byte " + (count - 1));
                    }
                    chararr[chararr_count++] = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6)
                            | ((char3 & 0x3F) << 0));
                    break;
                default:
                    /* 10xx xxxx, 1111 xxxx */
                    throw new UTFDataFormatException("malformed input around byte " + count);
                }
            }
            // The number of chars produced may be less than utflen
            return new String(chararr, 0, chararr_count);
        }
    }

    public static <X extends RuntimeException> int checkFromIndexSize(final int fromIndex, final int size,
            final int length) {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex) {
            throw new IndexOutOfBoundsException("fromIndex=" + fromIndex + " size=" + size + " length=" + length);
        }
        return fromIndex;
    }

    public static void throwOnEOF() throws IOException {
        throw newEOF();
    }

    public static FastEOFException newEOF() {
        return FastEOFException.getInstance("readFully src.read() returned negative count");
    }

    public static FastEOFException newTimeoutEOF(final TimeoutException cause) {
        return FastEOFException.getInstance("readFully timeout exceeded", cause);
    }

    public static void throwOnTimeout() throws TimeoutException {
        throw newTimeout();
    }

    public static FastTimeoutException newTimeout() {
        return FastTimeoutException.getInstance("readFully timeout exceeded");
    }

}
