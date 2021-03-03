package de.invesdwin.util.lang;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Buffers {

    private Buffers() {
    }

    /**
     * Workaround for java 8 compiled on java 9 or higher
     */
    public static void position(final Buffer buffer, final int position) {
        buffer.position(position);
    }

    public static void get(final ByteBuffer buffer, final int position, final byte[] dst) {
        final ByteBuffer duplicate = buffer.duplicate();
        Buffers.position(duplicate, position);
        duplicate.get(dst);
    }

    public static byte[] getRemaining(final ByteBuffer buffer, final int position) {
        final ByteBuffer duplicate = buffer.duplicate();
        Buffers.position(duplicate, position);
        final byte[] dst = new byte[duplicate.remaining()];
        duplicate.get(dst);
        return dst;
    }

    public static byte[] get(final ByteBuffer buffer, final int position, final int size) {
        final ByteBuffer duplicate = buffer.duplicate();
        Buffers.position(duplicate, position);
        final byte[] dst = new byte[size];
        duplicate.get(dst);
        return dst;
    }

}
