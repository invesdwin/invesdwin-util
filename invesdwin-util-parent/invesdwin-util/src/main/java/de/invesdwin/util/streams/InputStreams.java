package de.invesdwin.util.streams;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.streams.buffer.ByteBuffers;
import io.netty.util.concurrent.FastThreadLocal;

/**
 * Extracted from java.io.DataInputStream
 */
@Immutable
public final class InputStreams {

    private static final FastThreadLocal<byte[]> LONG_BUFFER_HOLDER = new FastThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() throws Exception {
            return ByteBuffers.allocateByteArray(8);
        }
    };

    private InputStreams() {
    }

    public static int read(final InputStream in, final byte[] b) throws IOException {
        return in.read(b, 0, b.length);
    }

    public static int read(final InputStream in, final byte[] b, final int off, final int len) throws IOException {
        return in.read(b, off, len);
    }

    public static int read(final DataInput in, final byte[] b, final int off, final int len) {
        java.util.Objects.checkFromIndexSize(off, len, b.length);
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
            throw new EOFException();
        }
        return (ch != 0);
    }

    public static byte readByte(final InputStream in) throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte) (ch);
    }

    public static int readUnsignedByte(final InputStream in) throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }

    public static short readShort(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short) ((ch1 << 8) + (ch2 << 0));
    }

    public static int readUnsignedShort(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch1 << 8) + (ch2 << 0);
    }

    public static char readChar(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (char) ((ch1 << 8) + (ch2 << 0));
    }

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

    public static float readFloat(final InputStream in) throws IOException {
        return Float.intBitsToFloat(readInt(in));
    }

    public static double readDouble(final InputStream in) throws IOException {
        return Double.longBitsToDouble(readLong(in));
    }

    public static void readFully(final InputStream in, final byte[] b) throws IOException {
        readFully(in, b, 0, b.length);
    }

    public static void readFully(final InputStream src, final byte[] array, final int index, final int length)
            throws IOException {
        final int end = index + length;
        int remaining = length;
        while (remaining > 0) {
            final int location = end - remaining;
            final int count = src.read(array, location, remaining);
            if (count == -1) { // EOF
                break;
            }
            remaining -= count;
        }
        if (remaining > 0) {
            throw ByteBuffers.newPutBytesToEOF();
        }
    }

    public static void readFully(final ReadableByteChannel src, final java.nio.ByteBuffer byteBuffer)
            throws IOException {
        int remaining = byteBuffer.remaining();
        while (remaining > 0) {
            final int count = src.read(byteBuffer);
            if (count == -1) { // EOF
                break;
            }
            remaining -= count;
        }
        if (remaining > 0) {
            throw ByteBuffers.newPutBytesToEOF();
        }
    }

}
