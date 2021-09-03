package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.streams.buffer.ByteBuffers;

/**
 * Extracted from java.io.DataOutputStream
 */
@Immutable
public final class OutputStreams {

    private OutputStreams() {
    }

    public static void write(final OutputStream out, final int b) throws IOException {
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
        int remaining = byteBuffer.remaining();
        while (remaining > 0) {
            final int count = dst.write(byteBuffer);
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
