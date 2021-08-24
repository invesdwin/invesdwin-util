package de.invesdwin.util.lang.buffer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

import javax.annotation.concurrent.Immutable;

import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.lang.Charsets;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Bytes;

@Immutable
public final class ByteBuffers {

    public static final int EXAPANDABLE_LENGTH = -1;

    public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);
    public static final AtomicBuffer EMPTY_DIRECT_BUFFER = new UnsafeBuffer(Bytes.EMPTY_ARRAY);

    /**
     * Also used by Protobuf, most common in x86/x64 systems.
     */
    public static final ByteOrder DEFAULT_ORDER = ByteOrder.LITTLE_ENDIAN;
    /**
     * What does the system actually use?
     */
    public static final ByteOrder NATIVE_ORDER = ByteOrder.nativeOrder();

    private static final ISliceInvoker SLICE_INVOKER;

    static {
        SLICE_INVOKER = newSliceInvoker();
    }

    private ByteBuffers() {
    }

    private static ISliceInvoker newSliceInvoker() {
        try {
            //java >= 13
            final Method sliceMethod = Reflections.findMethod(ByteBuffer.class, "slice", int.class, int.class);
            final MethodHandle sliceInvoker = MethodHandles.lookup().unreflect(sliceMethod);
            return (buffer, position, length) -> {
                try {
                    return (ByteBuffer) sliceInvoker.invoke(buffer, position, length);
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (final Throwable e) {
            //java < 13
            return (buffer, position, length) -> {
                final ByteBuffer duplicate = buffer.duplicate();
                position(duplicate, position);
                duplicate.limit(position + length);
                return duplicate.slice();
            };
        }
    }

    @FunctionalInterface
    private interface ISliceInvoker {
        ByteBuffer slice(ByteBuffer buffer, int position, int length);
    }

    /**
     * Workaround for java 8 compiled on java 9 or higher
     */
    public static void position(final Buffer buffer, final int position) {
        buffer.position(position);
    }

    public static ByteBuffer slice(final ByteBuffer buffer, final int position, final int length) {
        return SLICE_INVOKER.slice(buffer, position, length);
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
        return wrap(ByteBuffer.wrap(bytes));
    }

    public static IByteBuffer wrap(final ByteBuffer buffer) {
        return new JavaByteBuffer(buffer);
    }

    public static IByteBuffer wrap(final MutableDirectBuffer buffer) {
        return new AgronaByteBuffer(buffer);
    }

    /**
     * Allocate a buffer for this encoded size and use putStringAscii(string) afterwards.
     */
    public static int newStringAsciiLength(final CharSequence value) {
        return value.length();
    }

    /**
     * Allocate a buffer for the encoded size and use putBytes(stringBytes) afterwards.
     */
    public static byte[] newStringAsciiBytes(final CharSequence value) {
        final byte[] bytes = new byte[newStringAsciiLength(value)];
        final int length = value.length();
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c > 127) {
                c = '?';
            }

            bytes[i] = (byte) c;
        }
        return bytes;
    }

    /**
     * Allocate a buffer for the encoded size and use putBytes(stringBytes) afterwards.
     */
    public static byte[] newStringUtf8Bytes(final CharSequence value) {
        return Charsets.UTF_8.encode(CharBuffer.wrap(value)).array();
    }

    /**
     * Allocate a buffer for the encoded size and use putBytes(stringBytes) afterwards.
     */
    public static byte[] newStringUtf8Bytes(final String value) {
        return value.getBytes(Charsets.UTF_8);
    }

    public static String newStringUtf8(final byte[] bytes) {
        return new String(bytes, Charsets.UTF_8);
    }

    public static String newStringAscii(final byte[] bytes) {
        //actually the same
        return newStringUtf8(bytes);
    }

}
