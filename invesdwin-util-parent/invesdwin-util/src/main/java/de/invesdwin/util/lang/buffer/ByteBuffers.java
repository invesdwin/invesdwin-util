package de.invesdwin.util.lang.buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.math.Bytes;

@Immutable
public final class ByteBuffers {

    public static final int EXAPANDABLE_LENGTH = -1;

    public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);
    public static final AtomicBuffer EMPTY_DIRECT_BUFFER = new UnsafeBuffer(Bytes.EMPTY_ARRAY);

    private ByteBuffers() {
    }

    /**
     * Workaround for java 8 compiled on java 9 or higher
     */
    public static void position(final Buffer buffer, final int position) {
        buffer.position(position);
    }

    public static void get(final ByteBuffer buffer, final int position, final byte[] dst) {
        final ByteBuffer duplicate = buffer.duplicate();
        ByteBuffers.position(duplicate, position);
        duplicate.get(dst);
    }

    public static byte[] getRemaining(final ByteBuffer buffer, final int position) {
        final ByteBuffer duplicate = buffer.duplicate();
        ByteBuffers.position(duplicate, position);
        final byte[] dst = new byte[duplicate.remaining()];
        duplicate.get(dst);
        return dst;
    }

    public static byte[] get(final ByteBuffer buffer, final int position, final int size) {
        final ByteBuffer duplicate = buffer.duplicate();
        ByteBuffers.position(duplicate, position);
        final byte[] dst = new byte[size];
        duplicate.get(dst);
        return dst;
    }

    public static IByteBuffer allocate(final int fixedLength) {
        if (fixedLength == 0) {
            return EmptyByteBuffer.INSTANCE;
        } else if (fixedLength < 0) {
            return allocateExpandable();
        } else {
            return allocateFixed(fixedLength);
        }
    }

    public static IByteBuffer allocateFixed(final int fixedLength) {
        return wrap(new byte[fixedLength]);
    }

    public static IByteBuffer allocateExpandable() {
        return wrap(new ExpandableArrayBuffer());
    }

    public static IByteBuffer allocateNonZero(final int fixedLength) {
        if (fixedLength == 0) {
            throw new IllegalArgumentException("fixedLength should not be 0");
        } else if (fixedLength < 0) {
            return allocateExpandable();
        } else {
            return allocateFixed(fixedLength);
        }
    }

    public static IByteBuffer wrap(final byte[] bytes) {
        return wrap(new UnsafeBuffer(bytes));
    }

    public static IByteBuffer wrap(final ByteBuffer buffer) {
        return wrap(new UnsafeBuffer(buffer));
    }

    public static IByteBuffer wrap(final MutableDirectBuffer buffer) {
        return new AgronaByteBuffer(buffer);
    }

}
