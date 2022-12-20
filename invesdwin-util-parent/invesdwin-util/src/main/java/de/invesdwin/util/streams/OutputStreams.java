package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.nio.channels.WritableByteChannel;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.loop.ASpinWait;
import de.invesdwin.util.error.FastEOFException;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.time.duration.Duration;

/**
 * Extracted from java.io.DataOutputStream
 */
@Immutable
public final class OutputStreams {

    private OutputStreams() {}

    public static void write(final OutputStream out, final int b) throws IOException {
        out.write(b);
    }

    public static void write(final OutputStream out, final byte[] b) throws IOException {
        out.write(b);
    }

    public static void write(final OutputStream out, final byte[] b, final int off, final int len) throws IOException {
        out.write(b, off, len);
    }

    public static void writeBoolean(final OutputStream out, final boolean v) throws IOException {
        out.write(v ? 1 : 0);
    }

    public static void writeByte(final OutputStream out, final int v) throws IOException {
        out.write(v);
    }

    public static void writeBytes(final OutputStream out, final String s) throws IOException {
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            out.write((byte) s.charAt(i));
        }
    }

    public static void writeShort(final OutputStream out, final int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    public static void writeChar(final OutputStream out, final int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    public static void writeInt(final OutputStream out, final int v) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

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

    public static void writeFloat(final OutputStream out, final float v) throws IOException {
        writeInt(out, Float.floatToIntBits(v));
    }

    public static void writeDouble(final OutputStream out, final double v) throws IOException {
        writeLong(out, Double.doubleToLongBits(v));
    }

    public static void writeChars(final OutputStream out, final String s) throws IOException {
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            final int v = s.charAt(i);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 0) & 0xFF);
        }
    }

    public static void writeFully(final WritableByteChannel dst, final java.nio.ByteBuffer byteBuffer)
            throws IOException {
        final Duration timeout = URIs.getDefaultNetworkTimeout();
        long zeroCountNanos = -1L;

        int remaining = byteBuffer.remaining();
        final int positionBefore = byteBuffer.position();
        while (remaining > 0) {
            final int count = dst.write(byteBuffer);
            if (count < 0) { // EOF
                break;
            }
            if (count == 0 && timeout != null) {
                if (zeroCountNanos == -1) {
                    zeroCountNanos = System.nanoTime();
                } else if (timeout.isLessThanNanos(System.nanoTime() - zeroCountNanos)) {
                    throw FastEOFException.getInstance("write timeout exceeded");
                }
                ASpinWait.onSpinWaitStatic();
            } else {
                zeroCountNanos = -1L;
                remaining -= count;
            }
        }
        ByteBuffers.position(byteBuffer, positionBefore);
        if (remaining > 0) {
            throw ByteBuffers.newEOF();
        }
    }

    //CHECKSTYLE:OFF
    public static int writeUTF(final OutputStream out, final String str) throws IOException {
        //CHECKSTYLE:ON
        final int strlen = str.length();
        int utflen = strlen; // optimized for ASCII

        for (int i = 0; i < strlen; i++) {
            final int c = str.charAt(i);
            if (c >= 0x80 || c == 0) {
                utflen += (c >= 0x800) ? 2 : 1;
            }
        }

        if (utflen > 65535 || /* overflow */ utflen < strlen) {
            throw new UTFDataFormatException(tooLongMsg(str, utflen));
        }

        final IByteBuffer bytearr = ByteBuffers.EXPANDABLE_POOL.borrowObject().ensureCapacity(utflen + 2);
        try {
            int count = 0;
            bytearr.putByte(count++, (byte) ((utflen >>> 8) & 0xFF));
            bytearr.putByte(count++, (byte) ((utflen >>> 0) & 0xFF));

            int i = 0;
            for (i = 0; i < strlen; i++) { // optimized for initial run of ASCII
                final int c = str.charAt(i);
                if (c >= 0x80 || c == 0) {
                    break;
                }
                bytearr.putByte(count++, (byte) c);
            }

            //CHECKSTYLE:OFF
            for (; i < strlen; i++) {
                //CHECKSTYLE:ON
                final int c = str.charAt(i);
                if (c < 0x80 && c != 0) {
                    bytearr.putByte(count++, (byte) c);
                } else if (c >= 0x800) {
                    bytearr.putByte(count++, (byte) (0xE0 | ((c >> 12) & 0x0F)));
                    bytearr.putByte(count++, (byte) (0x80 | ((c >> 6) & 0x3F)));
                    bytearr.putByte(count++, (byte) (0x80 | ((c >> 0) & 0x3F)));
                } else {
                    bytearr.putByte(count++, (byte) (0xC0 | ((c >> 6) & 0x1F)));
                    bytearr.putByte(count++, (byte) (0x80 | ((c >> 0) & 0x3F)));
                }
            }
            out.write(bytearr.byteArray(), 0, utflen + 2);
            return utflen + 2;
        } finally {
            ByteBuffers.EXPANDABLE_POOL.returnObject(bytearr);
        }
    }

    private static String tooLongMsg(final String s, final int bits32) {
        final int slen = s.length();
        final String head = s.substring(0, 8);
        final String tail = s.substring(slen - 8, slen);
        // handle int overflow with max 3x expansion
        final long actualLength = slen + Integer.toUnsignedLong(bits32 - slen);
        return "encoded string (" + head + "..." + tail + ") too long: " + actualLength + " bytes";
    }

}
